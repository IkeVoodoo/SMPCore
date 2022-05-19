package me.ikevoodoo.smpcore.functions;

public interface CatchFunction<T, V> {

    V accept(T value) throws Exception;

}
