package me.ikevoodoo.smpcore.commands.arguments;

import me.ikevoodoo.smpcore.commands.arguments.parsers.ParserRegistry;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.function.Consumer;

public class Arguments implements Iterable<Integer> {

    private static final int READING_STRING = 0;
    private static final int READING_TOKEN = 1;
    private static final int SEARCHING_TOKEN = 2;

    private final List<String> args;
    private final List<ArgumentWrapper> types;
    private final CommandSender sender;

    public Arguments(CommandSender sender, List<ArgumentWrapper> types, String[] args) {
        this.sender = sender;
        this.types = types;
        this.args = getArguments(String.join(" ", args));
    }

    public int rawLength() {
        return this.args.size();
    }

    public int typeLength() {
        return this.types.size();
    }

    public boolean match() {
        int currIndex = 0;

        for (ArgumentWrapper wrapper : types) {
            Argument arg = wrapper.getArgument();
            if(!has(arg.type())) {
                return false;
            }
            
            if(currIndex >= this.args.size()) {
                return !arg.required();
            }

            if (!is(currIndex, arg.type())) {
                if (arg.required()) {
                    return false;
                } else {
                    continue;
                }
            }
            currIndex++;
        }
        return true;
    }

    public Map<Integer, ArgumentWrapper> findDifferences(List<ArgumentWrapper> args) {
        var map = new HashMap<Integer, ArgumentWrapper>();

        var length = Math.min(this.types.size(), args.size());

        for (int i = 0; i < length; i++) {
            var arg = args.get(i);
            var current = this.types.get(i);

            var wrappedArg = arg.getArgument();
            var currentArg = current.getArgument();

            if (wrappedArg.type() != currentArg.type()) {
                map.put(i, current);
                continue;
            }

            var wrappedParsed = this.get(i, wrappedArg.type());
            var currentParsed = this.get(i, currentArg.type());

            if (!Objects.equals(wrappedParsed, currentParsed)) {
                map.put(i, current);
            }
        }

        if (length < this.types.size()) {
            for (int i = length; i < this.types.size(); i++) {
                map.put(i, this.types.get(i));
            }
        }

        return map;
    }

    public boolean isEmpty() {
        return args.isEmpty();
    }

    public boolean has(Class<?> type) {
        return ParserRegistry.get(type) != null;
    }

    public boolean is(int index, Class<?> type) {
        if (index >= args.size()) return false;
        return ParserRegistry.get(type) != null && ParserRegistry.get(type).canParse(args.get(index));
    }

    public <T> T get(int index, Class<T> type) {
        if (index < 0 || index >= this.args.size()) return null;

        var data = this.args.get(index);
        if(type == String.class)
            return type.cast(data);

        var parser = ParserRegistry.get(type);
        if (parser == null || !parser.canParse(data))
            return null;

        return parser.parse(this.sender, data);
    }

    public <T> T get(String name, Class<T> type) {
        return get(find(name), type);
    }

    public <T> T get(int index, Class<T> type, T def) {
        T v = get(index, type);
        if (v == null) return def;
        return v;
    }

    public <T> T get(String name, Class<T> type, T def) {
        return get(find(name), type, def);
    }

    public <T> List<T> getAll(Class<T> type) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < this.args.size(); i++) {
            if (is(i, type)) {
                list.add(get(i, type));
            }
        }
        return list;
    }

    public List<String> getRaw() {
        return Collections.unmodifiableList(this.args);
    }

    public ArgumentWrapper getArgument(int index) {
        return this.types.get(index);
    }

    public boolean has(String name) {
        int found = find(name);
        return found > 0 && found < args.size();
    }

    public String get(int index) {
        return index > this.args.size() ? null : this.args.get(index);
    }

    public String get(int index, String def) {
        return index >= this.args.size() || index < 0 ? def : this.args.get(index);
    }

    /**
     * TODO: Move this to a utility class
     * */
    private List<String> getArguments(String input) {
        int state = SEARCHING_TOKEN;
        StringBuilder currentToken = new StringBuilder();
        List<String> tokens = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (state) {
                case SEARCHING_TOKEN -> {
                    if (c == '"') state = READING_STRING;
                    else if(c != ' ') {
                        state = READING_TOKEN;
                        currentToken.append(c);
                    }
                }
                case READING_STRING -> {
                    if (c == '"' && input.charAt(i - 1) != '\\') {
                        state = SEARCHING_TOKEN;
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0);
                    } else {
                        if(input.charAt(i - 1) == '\\' && c == '"') {
                            currentToken.setCharAt(currentToken.length() - 1, '"');
                        }
                        else currentToken.append(c);
                    }
                }
                case READING_TOKEN -> {
                    if (c == ' ') {
                        state = SEARCHING_TOKEN;
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0);
                    } else {
                        currentToken.append(c);
                    }
                }
                default -> throw new IllegalStateException("Achievement get: How did you get here?");
            }
        }
        if(currentToken.length() > 0)
            tokens.add(currentToken.toString());

        return tokens;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<>() {
            private int pos;

            @Override
            public boolean hasNext() {
                return pos < args.size();
            }

            @Override
            public Integer next() {
                if(!hasNext()) throw new NoSuchElementException("No more elements");
                return pos++;
            }
        };
    }

    @Override
    public void forEach(Consumer<? super Integer> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Integer> spliterator() {
        return Iterable.super.spliterator();
    }

    private int find(String name) {
        int currIndex = 0;

        for (ArgumentWrapper wrapper : types) {
            Argument arg = wrapper.getArgument();
            if (arg.name().equalsIgnoreCase(name)) {
                return currIndex;
            }

            if(!has(arg.type())) {
                return -1;
            }

            if (!is(currIndex, arg.type())) {
                if (arg.required()) {
                    return -1;
                } else {
                    continue;
                }
            }
            currIndex++;
        }
        return -1;
    }
}
