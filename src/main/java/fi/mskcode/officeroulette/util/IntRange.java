package fi.mskcode.officeroulette.util;

import static java.lang.Math.abs;
import static java.lang.String.format;

public final class IntRange {

    /** Range minimum (inclusive). */
    public final int min;

    /** Range maximum (inclusive). */
    public final int max;

    /** The length of the range; having same min and max values has still length of 1. */
    public final long length;

    /** The absolute distance between the start and end of the range. */
    public final long distance;

    private IntRange(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException(format("max value %d smaller than min value %d", max, min));
        }
        this.min = min;
        this.max = max;
        this.length = length();
        this.distance = distance();
    }

    public static IntRange of(int min, int max) {
        return new IntRange(min, max);
    }

    /** Returns the absolute distance of this instance. */
    private long distance() {
        return abs((long) max - (long) min);
    }

    /** Returns the length (size) of this instance. */
    private long length() {
        return max == min ? 1 : abs((long) max - (long) min) + 1;
    }

    /** Returns true if this instance has a negative range, otherwise false. */
    public boolean hasNegativeRange() {
        return min < 0 || max < 0;
    }

    /**
     * Materializes this instance into a concrete array.
     *
     * <p>Depending on the {@link #length()} of this range, this method can end up yielding a VERY large array.</p>
     *
     * @return An array containing all the numbers in the range.
     */
    public int[] materialize() {
        // technically Java's array can hold Integer.MAX_VALUE number of
        // elements, but realistically you'll most likely run into troubles
        // sooner than that
        if (length > Integer.MAX_VALUE) {
            throw new UnsupportedOperationException(format(
                    "IntRange %s cannot be materialized due to its size being bigger than %d",
                    this, Integer.MAX_VALUE));
        }

        var array = new int[(int) length];
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
