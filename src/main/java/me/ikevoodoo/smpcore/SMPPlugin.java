package me.ikevoodoo.smpcore;

import me.ikevoodoo.smpcore.annotations.NoInject;
import me.ikevoodoo.smpcore.annotations.Property;
import me.ikevoodoo.smpcore.callbacks.blocks.PlayerPlaceBlockCallback;
import me.ikevoodoo.smpcore.callbacks.items.PlayerUseItemCallback;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.functional.CommandCreator;
import me.ikevoodoo.smpcore.commands.functional.FunctionalCommand;
import me.ikevoodoo.smpcore.config.ConfigData;
import me.ikevoodoo.smpcore.config.ConfigHandler;
import me.ikevoodoo.smpcore.config.ConfigHelper;
import me.ikevoodoo.smpcore.config.annotations.Config;
import me.ikevoodoo.smpcore.debug.LogCollector;
import me.ikevoodoo.smpcore.handlers.*;
import me.ikevoodoo.smpcore.handlers.chat.ChatInputHandler;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.items.functional.FunctionalItem;
import me.ikevoodoo.smpcore.items.functional.ItemCreator;
import me.ikevoodoo.smpcore.listeners.*;
import me.ikevoodoo.smpcore.menus.Menu;
import me.ikevoodoo.smpcore.menus.functional.FunctionalMenu;
import me.ikevoodoo.smpcore.menus.functional.MenuCreator;
import me.ikevoodoo.smpcore.recipes.RecipeLoader;
import me.ikevoodoo.smpcore.utils.*;
import me.ikevoodoo.smpcore.utils.health.HealthHelper;
import me.ikevoodoo.smpcore.utils.random.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import static me.ikevoodoo.smpcore.senders.CustomSender.as;
import static me.ikevoodoo.smpcore.senders.SenderBuilder.createNewSender;

@SuppressWarnings("unused")
public abstract class SMPPlugin extends JavaPlugin implements CommandCreator, MenuCreator, ItemCreator {

    private EliminationHandler eliminationHandler;
    private JoinActionHandler joinActionHandler;
    private InventoryActionHandler inventoryActionHandler;
    private ResourcePackHandler resourcePackHandler;
    private ChatInputHandler chatInputHandler;
    private MenuHandler menuHandler;

    private RecipeLoader recipeLoader;
    private PlayerUseListener playerUseListener;
    private PlayerPlaceListener playerPlaceListener;
    private ConfigHelper configHelper;

    private ConfigHandler configHandler;

    private HealthHelper healthHelper;

    private MaterialUtils materialUtils;

    private File cacheFolder;

    private String serverIp;

    private final CommandSender noLogConsole = createNewSender(as().noLog().console());

    private final Random random = new Random();

    private final HashMap<String, CustomItem> customItems = new HashMap<>();


    @Override
    public final void onLoad() {
//        LogCollector.init();
        // Use log collector once it's done

        onPreload();
    }

    @Override
    public final void onEnable() {
        //createDataFolder();
        //loadJoinHandler();
        eliminationHandler = new EliminationHandler(this);

        try {
            eliminationHandler.load(FileUtils.getOrCreate(getDataFolder(), "data", "cache.bin"));
        } catch (IOException e) {
            getLogger().severe("Could not load cache data!");
        }

        joinActionHandler = new JoinActionHandler(this);
        inventoryActionHandler = new InventoryActionHandler(this);
        resourcePackHandler = new ResourcePackHandler(this);
        chatInputHandler = new ChatInputHandler(this);
        menuHandler = new MenuHandler(this);

        recipeLoader = new RecipeLoader(this);
        playerUseListener = new PlayerUseListener(this);
        playerPlaceListener = new PlayerPlaceListener(this);
        registerListeners(
                new PlayerConnectListener(this),
                playerUseListener,
                playerPlaceListener,
                new PlayerDamageListener(),
                new PlayerSleepListener(),
                new InventoryEditListener(this),
                new ChatMessageListener(this),
                new MenuUpdateListener(this),
                new ItemDamageListener(this)
        );
        configHandler = new ConfigHandler(this);
        try {
            registerDynamically();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        configHelper = new ConfigHelper(this);

        this.healthHelper = new HealthHelper(this);

        materialUtils = new MaterialUtils();
        try {
            cacheFolder = FileUtils.getOrCreate(getDataFolder(), "data", "cache");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String ip = Bukkit.getIp();
        serverIp = ip.isBlank() ? NetworkUtils.getServerIp() : ip;
        whenEnabled();
    }

    @Override
    public final void onDisable() {
        //saveJoinHandler();
        try {
            eliminationHandler.save(FileUtils.getOrCreate(getDataFolder(), "data", "cache.bin"));
        } catch (IOException e) {
            getLogger().severe("Could not save cache data!");
        }

        whenDisabled();

        var file = new File("smpcore.logs");
        try {
            Files.writeString(file.toPath(), LogCollector.getLogs(), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * No util will work at this stage
     * */
    public void onPreload() {

    }

    public void whenEnabled() {

    }

    public void whenDisabled() {

    }

    public void onReload() {

    }

    public final void reload() {
        this.reload(false);
    }

    public final void reload(boolean skipEvent) {
        reloadConfig();
        configHandler.reload();
        for(CustomItem customItem : customItems.values())
            customItem.reload();
        if(!skipEvent) this.onReload();
    }

    public final EliminationHandler getEliminationHandler() {
        return eliminationHandler;
    }

    public final JoinActionHandler getJoinActionHandler() {
        return joinActionHandler;
    }

    public final InventoryActionHandler getInventoryActionHandler() {
        return inventoryActionHandler;
    }

    public final ResourcePackHandler getResourcePackHandler() {
        return resourcePackHandler;
    }

    public final ChatInputHandler getChatInputHandler() {
        return chatInputHandler;
    }

    public final MenuHandler getMenuHandler() {
        return menuHandler;
    }

    public final RecipeLoader getRecipeLoader() {
        return recipeLoader;
    }

    public final ConfigHelper getConfigHelper() {
        return configHelper;
    }

    public final HealthHelper getHealthHelper() {
        return this.healthHelper;
    }

    public final ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public final MaterialUtils getMaterialUtils() {
        return materialUtils;
    }

    public final File getCacheFolder() {
        return cacheFolder;
    }

    public final String getServerIp() {
        return serverIp;
    }

    public final Random getRandom() {
        return random;
    }

    public final CommandSender getNoLogConsole() {
        return noLogConsole;
    }

    public final CommandSender getConsole() {
        return Bukkit.getConsoleSender();
    }

    /**
     * Register a callback for when a player uses an item.
     *
     * @param key The namespaced key of the item.
     * @param callback The callback to run.
     * */
    public final void onUse(String key, PlayerUseItemCallback callback) {
        playerUseListener.addListener(key, callback);
    }

    /**
     * Register a callback for when a player uses an item.
     *
     * @param key The namespaced key of the item.
     * @param callback The callback to run.
     * */
    public final void onUse(NamespacedKey key, PlayerUseItemCallback callback) {
        playerUseListener.addListener(key, callback);
    }

    /**
     * Register a callback for when a player places a block.
     *
     * @param key The namespaced key of the item.
     * @param callback The callback to run.
     * */
    public final void onPlace(String key, PlayerPlaceBlockCallback callback) {
        playerPlaceListener.addListener(key, callback);
    }

    public final void addCommand(String name, SMPCommand executor) {
        PluginCommand command = getCommand(name);
        assert command != null;
        command.setExecutor(executor);
        command.setTabCompleter(executor);
    }

    public final void addCommand(SMPCommand command) {
        var cmd = getCommand(command.getName());
        if(cmd != null) {
            cmd.setExecutor(command);
            cmd.setTabCompleter(command);
            return;
        }

        CommandUtils.register(command);
    }

    public final void addCommands(List<SMPCommand> commands) {
        commands.forEach(this::addCommand);
    }

    public final void addCommand(String name, SMPCommand executor, TabCompleter completer) {
        PluginCommand command = getCommand(name);
        assert command != null;
        command.setExecutor(executor);
        command.setTabCompleter(completer);
    }

    public final void registerListeners(Listener... listeners) {
        for(Listener listener : listeners)
            getServer().getPluginManager().registerEvents(listener, this);
    }

    public final void registerListeners(List<Listener> listeners) {
        listeners.forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }


    // CONFIG STUFF \\

    public final String getString(String path) {
        return getConfig().getString(path);
    }

    public final int getInt(String path) {
        return getConfig().getInt(path);
    }

    public final boolean getBoolean(String path) {
        return getConfig().getBoolean(path);
    }

    public final double getDouble(String path) {
        return getConfig().getDouble(path);
    }

    public final long getLong(String path) {
        return getConfig().getLong(path);
    }

    public final ConfigurationSection getSection(String path) {
        return getConfig().getConfigurationSection(path);
    }

    public final void set(String path, Object value) {
        getConfig().set(path, value);
    }

    public final Optional<CustomItem> getItem(String id) {
        CustomItem item = customItems.get(id);
        if(item == null)
            return Optional.empty();
        return Optional.of(item);
    }

    public final Optional<CustomItem> getItem(ItemStack stack) {
        if (stack == null) return Optional.empty();
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return Optional.empty();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Optional<Pair<String, Byte>> data = PDCUtils.get(container, PersistentDataType.BYTE);
        if (data.isEmpty()) return Optional.empty();

        return getItem(data.get().getFirst());
    }

    public final void registerItem(CustomItem item) {
        if (item == null)
            throw new IllegalStateException("Item must not be null!");
        this.customItems.put(item.getId(), item);
    }

    public final List<CustomItem> getItems() {
        return new ArrayList<>(this.customItems.values());
    }

    public final NamespacedKey makeKey(String id) {
        return new NamespacedKey(this, id);
    }

    public final FunctionalCommand createCommand() {
        return this.createCommand(this);
    }

    public final FunctionalMenu createMenu() {
        return this.createMenu(this);
    }

    public final FunctionalItem createItem() {
        return this.createItem(this);
    }

    public final void destroyMenu(String id) {
        this.menuHandler.remove(makeKey(id));
    }

    public final void destroyMenu(NamespacedKey key) {
        this.menuHandler.remove(key);
    }

    public final void destroyMenu(Menu menu) {
        this.menuHandler.remove(menu.id());
        this.playerUseListener.removeListener(menu.id());
    }

    public final void destroyItem(String id) {
        this.customItems.remove(id);
    }

    public final void destroyItem(NamespacedKey key) {
        this.destroyItem(key.getKey());
    }

    public final void destroyItem(CustomItem item) {
        this.destroyItem(item.getId());
    }


    public final boolean isInstalled(String id) {
        return Bukkit.getPluginManager().getPlugin(id) != null;
    }

    public static SMPPlugin getById(String id) {
        return Bukkit.getPluginManager().getPlugin(id) instanceof SMPPlugin smpPlugin ? smpPlugin : null;
    }

    private void registerDynamically() throws IOException, URISyntaxException {
        List<Class<?>> classes = getAllClasses(getClass().getPackage().getName());
        findConfigClasses(classes).forEach(clazz -> {
            Config config = clazz.getAnnotation(Config.class);
            ConfigData data = new ConfigData(this, config.value(), config.type(), clazz);
            getConfigHandler().registerConfig(data);
        });
        Pair<List<Listener>, List<SMPCommand>> toRegister = checkPackageForClasses(classes, Listener.class, SMPCommand.class);
        registerListeners(toRegister.getFirst());
        addCommands(toRegister.getSecond());

        findItemClasses(classes).forEach(clazz -> {
            CustomItem item = getClassInstance(clazz.asSubclass(CustomItem.class));
            assert item != null;
            registerItem(item);
        });
    }

    private <A, B> Pair<List<A>, List<B>> checkPackageForClasses(List<Class<?>> classes, Class<A> toFindA, Class<B> toFindB) {
        if(classes == null) {
            return new Pair<>(new ArrayList<>(), new ArrayList<>());
        }

        List<A> foundClassesA = new ArrayList<>();
        List<B> foundClassesB = new ArrayList<>();
        for(Class<?> clazz : classes) {
            if (toFindA.isAssignableFrom(clazz)) {
                A inst = getClassInstance(clazz.asSubclass(toFindA));
                if(inst != null)
                    foundClassesA.add(inst);
            }
            else if (toFindB.isAssignableFrom(clazz)) {
                B inst = getClassInstance(clazz.asSubclass(toFindB));
                if(inst != null)
                    foundClassesB.add(inst);
            }
        }

        return new Pair<>(foundClassesA, foundClassesB);
    }

    private List<Class<?>> findConfigClasses(List<Class<?>> classes) {
        return classes == null ? new ArrayList<>() : classes.stream().filter(clazz -> clazz.isAnnotationPresent(Config.class)).toList();
    }

    private List<Class<?>> findItemClasses(List<Class<?>> classes) {
        return classes == null ? new ArrayList<>() : classes.stream().filter(CustomItem.class::isAssignableFrom).toList();
    }

    private List<Class<?>> getAllClasses(String packageName) throws URISyntaxException, IOException {
        String packagePath = packageName.replace('.', '/');
        URL resource = getClass().getClassLoader().getResource(packagePath);
        if (resource == null) return List.of();
        URI pkg = resource.toURI();
        List<Class<?>> classes = new ArrayList<>();

        Path root;
        if(pkg.getScheme().equals("jar")) {
            try {
                root = FileSystems.getFileSystem(pkg).getPath(packagePath);
            } catch (final FileSystemNotFoundException e) {
                root = FileSystems.newFileSystem(pkg, Collections.emptyMap()).getPath(packagePath);
            }
        } else {
            root = Paths.get(pkg);
        }

        try(Stream<Path> paths = Files.walk(root)) {
            paths.filter(Files::isRegularFile).forEach(file -> {
                String path = file.toString().replace("/", ".");
                String name = path.substring(path.indexOf(packageName), path.length() - 6);
                try {
                    classes.add(Class.forName(name));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
        return classes;
    }

    private <T> T getClassInstance(Class<? extends T> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        Constructor<? extends T> constr = null;
        Object[] params = null;
        try {
            for(Constructor<?> constructor : constructors) {
                if(constructor.isAnnotationPresent(NoInject.class))
                    continue;

                if(constructor.getParameterCount() == 0 && constr == null)
                    constr = clazz.getDeclaredConstructor();

                if(constructor.getParameterCount() >= 1 && constructor.getParameterTypes()[0] == SMPPlugin.class) {
                    params = new Object[constructor.getParameterCount()];
                    params[0] = this;
                    constr = clazz.getDeclaredConstructor(SMPPlugin.class);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if(constr == null) {
            return null;
        }

        for (Parameter parameter : constr.getParameters()) {
            if(parameter.isAnnotationPresent(Property.class)) {
                Property annotation = parameter.getAnnotation(Property.class);
                String key = annotation.value().toLowerCase(Locale.ROOT);
                if(key.isEmpty())
                    throw new IllegalArgumentException("Property key cannot be empty at class " + clazz.getName() + "(" + getConstructorParams(constr) + ")");
                Object val = switch(key) {
                    case "time.ms" -> System.currentTimeMillis();
                    case "time.ns" -> System.nanoTime();
                    case "random.uuid" -> UUID.randomUUID();
                    case "server.players" -> Bukkit.getOnlinePlayers();
                    case "server.players.count" -> Bukkit.getOnlinePlayers().size();

                    default -> {
                        if(key.startsWith("server.players.uuid.")) {
                            String uuid = key.substring("server.players.uuid.".length());
                            yield Bukkit.getPlayer(UUID.fromString(uuid));
                        }

                        if(key.startsWith("server.players.")) {
                            String playerName = key.substring("server.players.".length());
                            yield Bukkit.getPlayer(playerName);
                        }


                        if(key.startsWith("server.online.uuid.")) {
                            String uuid = key.substring("server.online.uuid.".length());
                            yield Bukkit.getPlayer(UUID.fromString(uuid)) != null;
                        }

                        if(key.startsWith("server.online.")) {
                            String playerName = key.substring("server.online.".length());
                            yield Bukkit.getPlayer(playerName) != null;
                        }



                        yield null;
                    }
                };

                // TODO for the morning: Check if the value is of the same type as the parameter, if it is not, throw an exception, if it is null, just pass null
                // TODO added at 11:30PM 5/23/2022 CET time
            }
        }

        T generic;
        try {
            generic = constr.newInstance(params);
        } catch (Exception e) {
            try {
                generic = constr.newInstance();
            } catch (Exception e1) {
                e.printStackTrace();
                return null;
            }
        }


        return generic;
    }

    private String getConstructorParams(Constructor<?> constr) {
        StringBuilder sb = new StringBuilder();
        for(Parameter parameter : constr.getParameters()) {
            // annotations, is array, is primitive, is generic etc
            for(Annotation annotation : parameter.getAnnotations()) {
                sb.append(annotation.toString()).append(" ");
            }
            sb.append(parameter.getType().getName()).append(", ");
        }
        return sb.toString().trim();
    }

    private List<Class<?>> findClasses(File directory, String pkgName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if(files == null)
                return classes;
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classes.addAll(findClasses(file, pkgName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(pkgName + '.' + file.getName().substring(0, file.getName().length() - 6), false, Thread.currentThread().getContextClassLoader()));
                }
            }
        }
        return classes.stream().filter(clazz -> clazz.isAssignableFrom(Listener.class)).toList();
    }

    // Needed for later
/*
    private void createDataFolder() {
        File data = new File(getDataFolder(), "data");
        if (!data.exists()) {
            data.mkdirs();
        }
    }

    private void loadJoinHandler() {
        File joinActions = new File(getDataFolder(), "data" + File.separator + "join-actions.internal");
        HashMap<UUID, List<SerializableConsumer<UUID>>> joinActionsMap = new HashMap<>();
        if (joinActions.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(joinActions))) {
                joinActionsMap = (HashMap<UUID, List<SerializableConsumer<UUID>>>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }

        File joinActionHandlers = new File(getDataFolder(), "data" + File.separator + "join-action-handlers.internal");
        List<SerializableConsumer<Player>> joinListeners = new ArrayList<>();
        if (joinActionHandlers.exists()) {
            try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(joinActionHandlers))) {
                joinListeners = (List<SerializableConsumer<Player>>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }
        joinActionHandler = new JoinActionHandler(this, joinActionsMap, joinListeners);
    }

    private void saveJoinHandler() {
        File joinActions = new File(getDataFolder(), "data" + File.separator + "join-actions.internal");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(joinActions))) {
            oos.writeObject(joinActionHandler.getJoinActions());
        } catch (IOException e) {
            e.printStackTrace();
        }

        File joinActionList = new File(getDataFolder(), "data" + File.separator + "join-action-handlers.internal");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(joinActionList))) {
            oos.writeObject(joinActionHandler.getJoinListeners());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

}
