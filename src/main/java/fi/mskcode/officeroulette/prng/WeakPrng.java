package fi.mskcode.officeroulette.prng;

import static java.lang.String.format;

import fi.mskcode.officeroulette.util.IntRange;
import java.util.Arrays;
import java.util.Random;

/**
 * Do NOT use this for any serious random number generation needs. Java's {@link java.util.Random} is not
 * cryptographically secure and this is here just a placeholder implementation.
 */
public class WeakPrng implements Prng {

    private final Random source;

    public WeakPrng(long seed) {
        this.source = new Random(seed);
    }

    @Override
    public int[] generateIndependentNumbers(IntRange range, int count) {
        if (range.hasNegativeRange()) {
            throw new IllegalArgumentException("WeakPrng does not support negative ranges");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be at least 1");
        }

        var array = new int[count];
        for (int i = 0; i < count; ++i) {
            array[i] = source.nextInt(range.min, range.max + 1);
        }
        return array;
    }

    @Override
    public int[] generateUniqueNumbers(IntRange range, int count) {
        if (range.hasNegativeRange()) {
            throw new IllegalArgumentException("WeakPrng does not support negative ranges");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be at least 1");
        }
        if (range.length < count) {
            throw new IllegalArgumentException(format("Range %s doesn't hold %d unique numbers", range, count));
        }

        var uniqueNumbers = range.materialize();
        FisherYatesShuffle.shuffleFromBeginning(uniqueNumbers, count, (max) -> source.nextInt(max + 1));
        return Arrays.copyOf(uniqueNumbers, count);
    }
}
