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
    private List<Argument> types;
    private final CommandSender sender;

    public Arguments(CommandSender sender, String[] args) {
        this.args = getArguments(String.join(" ", args));
        this.sender = sender;
    }

    public boolean match(List<Argument> args) {
        types = args;
        int currIndex = 0;

        for (Argument arg : args) {
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

    private int find(String name) {
        int currIndex = 0;

        for (Argument arg : types) {
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

    public boolean isEmpty() {
        return args.isEmpty();
    }

    public boolean has(Class<?> type) {
        return ParserRegistry.get(type) != null;
    }

    public boolean is(int index, Class<?> type) {
        return ParserRegistry.get(type) != null && ParserRegistry.get(type).canParse(args.get(index));
    }

    public <T> T get(int index, Class<T> type) {
        if(type == String.class)
            return type.cast(args.get(index));
        return ParserRegistry.get(type).parse(sender, args.get(index));
    }

    public <T> T get(String name, Class<T> type) {
        return get(find(name), type);
    }

    public <T> List<T> getAll(Class<T> type) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            if (is(i, type)) {
                list.add(get(i, type));
            }
        }
        return list;
    }

    public boolean has(String name) {
        int found = find(name);
        return found > 0 && found < args.size();
    }

    public String get(int index) {
        return get(index, String.class);
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
}
