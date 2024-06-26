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
    private Supplier<ItemStack> item;
    private Supplier<Message> name;

    private final List<Supplier<Message>> loreSuppliers = new ArrayList<>();
    private final List<FunctionalItemClickHandler> consumers = new ArrayList<>();

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
        this.item = () -> new ItemStack(material.get());
        return this;
    }

    public FunctionalItem item(Supplier<ItemStack> material) {
        this.item = material;
        return this;
    }

    public FunctionalItem name(Supplier<Message> name) {
        this.name = name;
        return this;
    }

    public FunctionalItem bind(FunctionalItemClickHandler handler) {
        this.consumers.add(handler);
        return this;
    }

    public FunctionalItem bind(BiConsumer<Player, ItemStack> consumer) {
        return this.bind((player, stack, result) -> {
            consumer.accept(player, stack);
            return result;
        });
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
                ItemStack stack = FunctionalItem.this.item.get();
                if (stack == null) stack = new ItemStack(Material.STONE);
                ItemMeta meta = stack.getItemMeta();
                if (name != null && meta != null)
                    meta.setDisplayName(name.get().legacyText());
                stack.setItemMeta(meta);
                return stack;
            }

            @Override
            protected ItemClickResult onClick(Player player, ItemStack itemStack, Action action) {
                var result = super.onClick(player, itemStack, action);
                for (var consumer : consumers) {
                    result = consumer.onClick(player, itemStack, result);
                }

                return result;
            }
        }.addKey(this.id + "_key").setLore(this::getLore);
        item.reload();
        return item;
    }

    private List<String> getLore() {
        return this.loreSuppliers
                .stream()
                .map(supplier -> supplier.get().legacyText())
                .collect(Collectors.toList());
    }

}
