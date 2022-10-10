package fi.mskcode.officeroulette.prng;

public interface Prng {

    /** Returns a non-negative value in range of [0-2147483647] (i.e. the maximum positive range of int type). */
    int nextInt();
}
