package fi.mskcode.officeroulette.prng;

import static java.lang.Math.abs;
import static java.lang.String.format;

public final class IntRange {

    /** Range minimum (inclusive). */
    public final int min;

    /** Range maximum (inclusive). */
    public final int max;

    private IntRange(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException(format("max value %d smaller than min value %d", max, min));
        }
        this.min = min;
        this.max = max;
    }

    public static IntRange of(int min, int max) {
        return new IntRange(min, max);
    }

    public long distance() {
        return abs((long) max - (long) min);
    }

    public boolean hasNegativeRange() {
        return min < 0 || max < 0;
    }

    public int[] materialize() {
        if (distance() > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(format(
                    "IntRange %s cannot be materialized due to its size being bigger than %d",
                    this, Integer.MAX_VALUE));
        }

        var array = new int[(int) distance()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = min + i;
        }
        return array;
    }

    @Override
    public String toString() {
        return format("[%d - %d]", min, max);
    }
}
