package fi.mskcode.officeroulette.util;

import static java.lang.String.format;

public final class ValidateMore {
    private ValidateMore() {}

    public static int isGreaterThan(int x, int value) {
        if (value <= x) {
            throw new IllegalArgumentException(format("Value (%d) is equal or less than %d", value, x));
        }
        return value;
    }
}
