package me.ikevoodoo.smpcore.utils.random;

public interface RandomBase<T> {

    /**
     * Get a random value, this is implementation specific
     *
     * @return The random value
     * */
    public T random();

    /**
     * Get an array of random values, this is implementation specific
     * <br>
     * <br>
     * Passing in 0 or less will return an empty array
     * <br>
     * Passing in 1 will behave exactly like {@link #random()}
     * <br>
     * Any other number will return an array of random values
     * <br>
     * <br>
     *
     * @param amount
     *            The amount of values to get
     *
     * @return The random values
     * */
    public T[] random(int amount);

    /**
     * Random value out of the given values
     *
     * @param values
     *            The value to choose from
     *
     * @return The random value
     * */
    public T irandom(T... values);

    /**
     * Random value, excluding the passed value. For multiple inputs, use {@link #orandom(T...)}
     *
     * @param value
     *            The value to exclude from the random value
     *
     * @return The random value
     * */
    public T erandom(T value);

    /**
     * Random value, excludes the given values
     *
     * @param values
     *            The values to exclude from the random value
     *
     * @return The random value
     * */
    public T orandom(T... values);

}
