package me.ikevoodoo.smpcore.functional.loop;

import me.ikevoodoo.smpcore.functional.FunctionalBase;

import java.util.List;

public interface FunctionalLoopBase extends FunctionalBase {

    default <T> FunctionalLoop<T> each(List<T> collection) {
        return new FunctionalLoop<>(this, collection);
    }

    default <T> FunctionalLoop<T> each(T... items) {
        return this.each(List.of(items));
    }

}
