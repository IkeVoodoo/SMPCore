package me.ikevoodoo.smpcore.items.functional;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.functional.loop.FunctionalLoopBase;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.items.ItemClickResult;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import me.ikevoodoo.smpcore.text.messaging.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FunctionalItem extends PluginProvider implements FunctionalLoopBase {

    private String id;
    private Message friendlyName;
    private Supplier<Material> material;
    private Supplier<Message> name;

    private final List<Supplier<Message>> loreSuppliers = new ArrayList<>();
    private final List<BiConsumer<Player, ItemStack>> consumers = new ArrayList<>();

    protected FunctionalItem(SMPPlugin plugin) {
        super(plugin);
    }

    public FunctionalItem id(String id) {
        this.id = id;
        return this;
    }

    public FunctionalItem friendlyName(Message friendlyName) {
        this.friendlyName = friendlyName;
        return this;
    }

    public FunctionalItem material(Supplier<Material> material) {
        this.material = material;
        return this;
    }

    public FunctionalItem name(Supplier<Message> name) {
        this.name = name;
        return this;
    }

    public FunctionalItem bind(BiConsumer<Player, ItemStack> consumer) {
        this.consumers.add(consumer);
        return this;
    }

    public FunctionalItem lore(Supplier<Message> supplier) {
        this.loreSuppliers.add(supplier);
        return this;
    }

    public ItemStack asItem() {
        return this.toItem().getItemStack();
    }

    public CustomItem register() {
        CustomItem item = this.toItem();
        getPlugin().registerItem(item);
        return item;
    }

    private CustomItem toItem() {
        if (this.id == null)
            throw new IllegalStateException("Item id must not be null!");
        if (this.friendlyName == null)
            throw new IllegalStateException("Item friendlyName must not be null!");
        CustomItem item = new CustomItem(getPlugin(), this.id, this.friendlyName) {
            @Override
            public ItemStack createItem(Player player) {
                ItemStack stack = new ItemStack(material != null ? material.get() : Material.STONE);
                ItemMeta meta = stack.getItemMeta();
                if (name != null && meta != null)
                    meta.setDisplayName(name.get().text());
                stack.setItemMeta(meta);
                return stack;
            }

            @Override
            protected ItemClickResult onClick(Player player, ItemStack itemStack, Action action) {
                for (BiConsumer<Player, ItemStack> consumer : consumers)
                    consumer.accept(player, itemStack);

                return super.onClick(player, itemStack, action);
            }
        }.addKey(this.id + "_key").setLore(this::getLore);
        item.reload();
        return item;
    }

    private List<String> getLore() {
        return this.loreSuppliers
                .stream()
                .map(supplier -> supplier.get().text())
                .collect(Collectors.toList());
    }

}
