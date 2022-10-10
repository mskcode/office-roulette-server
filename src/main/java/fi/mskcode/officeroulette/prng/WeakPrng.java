package fi.mskcode.officeroulette.prng;

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
    public int nextInt() {
        return source.nextInt(Integer.MAX_VALUE);
    }
}
