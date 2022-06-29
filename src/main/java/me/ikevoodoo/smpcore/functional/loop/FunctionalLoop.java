package me.ikevoodoo.smpcore.functional.loop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

public class FunctionalLoop<T> {

    private final FunctionalLoopBase base;
    private final List<T> collection;

    private final List<BiConsumer<FunctionalLoopBase, T>> consumerList = new ArrayList<>();
    private final HashMap<Integer, BiConsumer<FunctionalLoopBase, T>> special = new HashMap<>();


    protected FunctionalLoop(FunctionalLoopBase base, List<T> collection) {
        this.base = base;
        this.collection = collection;
    }

    public FunctionalLoop<T> withPriority(int index, BiConsumer<FunctionalLoopBase, T> consumer) {
        this.special.put(index < 0 ? this.collection.size() + index : index, consumer);
        return this;
    }

    public FunctionalLoop<T> with(BiConsumer<FunctionalLoopBase, T> consumer) {
        this.consumerList.add(consumer);
        return this;
    }

    public <B extends FunctionalLoopBase> B execute() {
        if (this.collection.isEmpty())
            return (B) this.base;
        for (int i = 0; i < this.collection.size(); i++) {
            T t = this.collection.get(i);

            BiConsumer<FunctionalLoopBase, T> consumer = this.special.get(i);
            if (consumer != null) {
                consumer.accept(this.base, t);
                continue;
            }

            this.consumerList.forEach(con -> con.accept(this.base, t));
        }
        return (B) this.base;
    }

}
