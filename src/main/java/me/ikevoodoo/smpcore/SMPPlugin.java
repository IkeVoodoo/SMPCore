package me.ikevoodoo.smpcore;

import me.ikevoodoo.smpcore.annotations.NoInject;
import me.ikevoodoo.smpcore.annotations.Property;
import me.ikevoodoo.smpcore.callbacks.blocks.PlayerPlaceBlockCallback;
import me.ikevoodoo.smpcore.callbacks.items.PlayerUseItemCallback;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.config.ConfigData;
import me.ikevoodoo.smpcore.config.ConfigHandler;
import me.ikevoodoo.smpcore.config.ConfigHelper;
import me.ikevoodoo.smpcore.config.annotations.Config;
import me.ikevoodoo.smpcore.handlers.EliminationHandler;
import me.ikevoodoo.smpcore.handlers.InventoryActionHandler;
import me.ikevoodoo.smpcore.handlers.JoinActionHandler;
import me.ikevoodoo.smpcore.handlers.ResourcePackHandler;
import me.ikevoodoo.smpcore.handlers.chat.ChatInputHandler;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.listeners.*;
import me.ikevoodoo.smpcore.recipes.RecipeLoader;
import me.ikevoodoo.smpcore.utils.CommandUtils;
import me.ikevoodoo.smpcore.utils.Pair;
import me.ikevoodoo.smpcore.utils.random.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import static me.ikevoodoo.smpcore.senders.CustomSender.as;
import static me.ikevoodoo.smpcore.senders.SenderBuilder.createNewSender;

@SuppressWarnings("unused")
public abstract class SMPPlugin extends JavaPlugin {

    private EliminationHandler eliminationHandler;
    private JoinActionHandler joinActionHandler;
    private InventoryActionHandler inventoryActionHandler;
    private ResourcePackHandler resourcePackHandler;
    private ChatInputHandler chatInputHandler;

    private RecipeLoader recipeLoader;
    private PlayerUseListener playerUseListener;
    private PlayerPlaceListener playerPlaceListener;
    private ConfigHelper configHelper;

    private ConfigHandler configHandler;

    private MaterialUtils materialUtils;

    private final CommandSender noLogConsole = createNewSender(as().noLog().console());

    private final Random random = new Random();

    private final HashMap<String, CustomItem> customItems = new HashMap<>();

    @Override
    public final void onEnable() {
        eliminationHandler = new EliminationHandler(this);
        joinActionHandler = new JoinActionHandler(this);
        inventoryActionHandler = new InventoryActionHandler(this);
        resourcePackHandler = new ResourcePackHandler(this);
        chatInputHandler = new ChatInputHandler(this);

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
                new ChatMessageListener(this)
        );
        configHandler = new ConfigHandler();
        try {
            registerDynamically();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        configHelper = new ConfigHelper(this);
        materialUtils = new MaterialUtils();
        whenEnabled();
    }

    @Override
    public final void onDisable() {
        // Run pre disable code
        whenDisabled();
    }

    public void whenEnabled() {

    }

    public void whenDisabled() {

    }

    public final void reload() {
        reloadConfig();
        configHandler.reload();
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

    public final RecipeLoader getRecipeLoader() {
        return recipeLoader;
    }

    public final ConfigHelper getConfigHelper() {
        return configHelper;
    }

    public final ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public final MaterialUtils getMaterialUtils() {
        return materialUtils;
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
    }

    public final void addCommand(SMPCommand command) {
        var cmd = getCommand(command.getName());
        if(cmd != null) {
            cmd.setExecutor(command);
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

    public final <T extends CustomItem> Optional<T> getItem(String id) {
        CustomItem item = customItems.get(id);
        if(item == null)
            return Optional.empty();
        return Optional.of((T) item);
    }

    public static SMPPlugin getById(String id) {
        return Bukkit.getPluginManager().getPlugin(id) instanceof SMPPlugin smpPlugin ? smpPlugin : null;
    }

    private void registerDynamically() throws IOException, URISyntaxException {
        List<Class<?>> classes = getAllClasses(getClass().getPackage().getName());
        Pair<List<Listener>, List<SMPCommand>> toRegister = checkPackageForClasses(classes, Listener.class, SMPCommand.class);
        registerListeners(toRegister.getFirst());
        addCommands(toRegister.getSecond());

        findConfigClasses(classes).forEach(clazz -> {
            Config config = clazz.getAnnotation(Config.class);
            ConfigData data = new ConfigData(this, config.value(), config.type(), clazz);
            getConfigHandler().registerConfig(data);
        });

        findItemClasses(classes).forEach(clazz -> {
            CustomItem item = getClassInstance(clazz.asSubclass(CustomItem.class));
            assert item != null;
            customItems.put(item.getId(), item);
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
        URI pkg = getClass().getClassLoader().getResource(packagePath).toURI();
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

}
