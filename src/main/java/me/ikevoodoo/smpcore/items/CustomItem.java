package me.ikevoodoo.smpcore.items;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.functions.SerializableConsumer;
import me.ikevoodoo.smpcore.text.messaging.Message;
import me.ikevoodoo.smpcore.recipes.RecipeData;
import me.ikevoodoo.smpcore.recipes.RecipeOptions;
import me.ikevoodoo.smpcore.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public abstract class CustomItem {

    private static class CustomItemData {
        private Supplier<String> displayName = () -> null;
        private Supplier<List<String>> lore = () -> null;
        private IntSupplier customModelData = () -> -1;
    }

    private final List<NamespacedKey> keys = new ArrayList<>();
    private boolean decreaseOnUse = false;

    private final CustomItemData data = new CustomItemData();
    private Pair<NamespacedKey, Recipe> recipe;
    private RecipeData recipeData;
    private final String id;
    private final Message friendlyName;

    private SerializableConsumer<Player> unlockOnJoin;
    private Consumer<Player> unlockOnObtain;

    private Supplier<RecipeOptions> optionsSupplier = () -> new RecipeOptions(Material.STONE, 1, true);

    private final SMPPlugin plugin;

    private boolean enabled;

    protected CustomItem(SMPPlugin plugin, String id, Message friendlyName) {
        this.plugin = plugin;
        this.id = id;
        this.friendlyName = friendlyName;
    }

    public final String getId() {
        return id;
    }

    public final Message getFriendlyName() {
        return this.friendlyName;
    }

    public static void give(Player player, CustomItem customItem) {
        player.getInventory().addItem(customItem.getItemStack());
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public final ItemStack getItemStack() {
        ItemStack itemStack = createItem(null);
        if(itemStack == null)
            return null;

        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        for (NamespacedKey key : keys)
            container.set(key, PersistentDataType.INTEGER, 0);

        String displayName = data.displayName.get();
        if(displayName != null && !displayName.isBlank())
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        List<String> lore = data.lore.get();
        if(lore != null && !lore.isEmpty()) {
            lore.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));
            itemMeta.setLore(lore);
        }

        int customModelData = data.customModelData.getAsInt();
        if(customModelData != -1)
            itemMeta.setCustomModelData(customModelData);

        itemStack.setItemMeta(itemMeta);
        itemStack.setAmount(getRecipeOptions().amount());
        return itemStack;
    }

    public final ItemStack getItemStack(int amount) {
        ItemStack itemStack = getItemStack();
        if(itemStack == null)
            return null;

        itemStack.setAmount(amount);
        return itemStack;
    }

    public final RecipeOptions getRecipeOptions() {
        return optionsSupplier.get();
    }

    public final RecipeData getRecipeData() {
        return this.recipeData;
    }

    public final CustomItem unlockOnJoin() {
        clearConsumers();
        unlockOnJoin = this.plugin.getJoinActionHandler().runAlwaysOnJoin(player -> {
            NamespacedKey key = recipe == null ? null : recipe.getFirst();
            if(key == null)
                return;
            player.discoverRecipe(key);
        });
        return this;
    }

    public final CustomItem unlockOnObtain(Material... materials) {
        clearConsumers();
        unlockOnObtain = this.plugin.getInventoryActionHandler().onInventoryAction(player -> {
            Inventory inventory = player.getInventory();
            for (Material material : materials) {
                if(!inventory.contains(material)) {
                    return;
                }
            }
            NamespacedKey key = recipe == null ? null : recipe.getFirst();
            if(key == null)
                return;
            player.discoverRecipe(key);
        });
        return this;
    }

    public final boolean containsAtLeast(Player player, int amount) {
        return player.getInventory().containsAtLeast(getItemStack(), amount);
    }

    public final void remove(Player player, int amount) {
        player.getInventory().removeItem(getItemStack(amount));
    }

    public final CustomItem addKey(String key) {
        NamespacedKey namespacedKey = new NamespacedKey(this.plugin, key);
        keys.add(namespacedKey);
        this.plugin.onUse(key, (plr, item, action) -> {
            ItemClickResult result = tryClick(plr, item, action);
            if(result == null)
                return true;

            if(decreaseOnUse && result.state() == ItemClickState.SUCCESS)
                item.setAmount(item.getAmount() - 1);

            return result.cancel();
        });
        return this;
    }

    public final CustomItem setDecreaseOnUse(boolean decreaseOnUse) {
        this.decreaseOnUse = decreaseOnUse;
        return this;
    }

    public abstract ItemStack createItem(Player player);

    public final ItemClickResult tryClick(Player player, ItemStack itemStack, Action action) {
        try {
            return this.onClick(player, itemStack, action);
        } catch (Exception e) {
            return new ItemClickResult(ItemClickState.FAIL, true);
        }
    }

    protected ItemClickResult onClick(Player player, ItemStack itemStack, Action action) {
        return new ItemClickResult(ItemClickState.SUCCESS, true);
    }

    public boolean onDrop(Player player, ItemStack itemStack) {
        return false;
    }

    public Pair<NamespacedKey, Recipe> createRecipe() {
        return null;
    }

    public RecipeData createRecipeData() {
        return null;
    }

    public final void reload() {
        NamespacedKey old = recipe != null ? recipe.getFirst() : null;
        this.recipeData = createRecipeData();
        Pair<NamespacedKey, Recipe> newRecipe = createRecipe();
        if(old != null && Bukkit.getRecipe(old) != null)
            Bukkit.removeRecipe(old);

        if(newRecipe != null)
            Bukkit.addRecipe(newRecipe.getSecond());

        this.recipe = newRecipe;
    }

    public final CustomItem setDisplayName(Supplier<String> displayName) {
        if(displayName != null)
            data.displayName = displayName;
        return this;
    }

    public final CustomItem setLore(Supplier<List<String>> lore) {
        if(lore != null)
            data.lore = lore;
        return this;
    }

    public final CustomItem setCustomModelData(IntSupplier customModelData) {
        if(customModelData != null)
            data.customModelData = customModelData;
        return this;
    }

    public final CustomItem setOptions(Supplier<RecipeOptions> options) {
        if(options != null)
            optionsSupplier = options;
        return this;
    }
    
    public final CustomItem bindConfig(String path) {
        return setDisplayName(() -> getConfig().getString(path + ".displayName", getConfig().getString(path + ".name", null)))
                .setLore(() -> getConfig().getStringList(path + ".lore"))
                .setCustomModelData(() -> getConfig().getInt(path + ".customModelData", -1));
    }

    public final CustomItem bindConfigOptions(String config, String path) {
        optionsSupplier = () -> this.plugin.getRecipeLoader().getOptions(this.plugin.getConfigHandler().getYmlConfig(config).getConfigurationSection(path));
        return this;
    }

    public final CustomItem bindConfig(String path, String key) {
        return bindConfig(path + "." + key);
    }

    public final CustomItem bindConfig(ConfigurationSection section) {
        return bindConfig(section.getCurrentPath());
    }

    public final CustomItem bindConfig(ConfigurationSection section, String key) {
        return bindConfig(section.getCurrentPath(), key);
    }

    public final FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public final NamespacedKey makeKey(String key) {
        return new NamespacedKey(plugin, key);
    }

    public final SMPPlugin getPlugin() {
        return plugin;
    }

    private void clearConsumers() {
        if(unlockOnJoin != null)
            this.plugin.getJoinActionHandler().cancelAlwaysOnJoin(unlockOnJoin);
        if(unlockOnObtain != null)
            this.plugin.getInventoryActionHandler().removeInventoryAction(unlockOnObtain);

        unlockOnJoin = null;
        unlockOnObtain = null;
    }
}
