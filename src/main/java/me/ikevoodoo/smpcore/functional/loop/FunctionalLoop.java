package me.ikevoodoo.smpcore.functional.loop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FunctionalLoop<T> {

    private final FunctionalLoopBase base;
    private final List<T> collection;

    private final List<BiConsumer<FunctionalLoopBase, T>> consumerList = new ArrayList<>();
    private final HashMap<Integer, BiConsumer<FunctionalLoopBase, T>> special = new HashMap<>();

    private final List<Function<T, Boolean>> filters = new ArrayList<>();

    protected FunctionalLoop(FunctionalLoopBase base, List<T> collection) {
        this.base = base;
        this.collection = collection;
    }

    public FunctionalLoop<T> withPriority(int index, BiConsumer<FunctionalLoopBase, T> consumer) {
        this.special.put(index, consumer);
        return this;
    }

    public FunctionalLoop<T> with(BiConsumer<FunctionalLoopBase, T> consumer) {
        this.consumerList.add(consumer);
        return this;
    }

    public FunctionalLoop<T> filter(Function<T, Boolean> filter) {
        this.filters.add(filter);
        return this;
    }

    public <B extends FunctionalLoopBase> B execute() {
        if (this.collection.isEmpty())
            return (B) this.base;

        List<T> coll = new ArrayList<>();

        this.collection.forEach(t -> {
            boolean exclude = false;

            for (Function<T, Boolean> filter : this.filters) {
                exclude = exclude || filter.apply(t);
            }

            if (!exclude) {
                coll.add(t);
            }
        });

        HashMap<Integer, BiConsumer<FunctionalLoopBase, T>> parsedSpecial = new HashMap<>();

        this.special.forEach((id, consumer) -> parsedSpecial.put(id < 0 ? coll.size() + id : id, consumer));

        for (int i = 0; i < coll.size(); i++) {
            T t = coll.get(i);

            BiConsumer<FunctionalLoopBase, T> consumer = parsedSpecial.get(i);
            if (consumer != null) {
                consumer.accept(this.base, t);
                continue;
            }

            this.consumerList.forEach(con -> con.accept(this.base, t));
        }
        return (B) this.base;
    }

}
