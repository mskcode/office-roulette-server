package fi.mskcode.officeroulette.prng;

public interface Prng {

    /**
     * Generates {@code count} number of pseudorandom numbers within the given {@code range}.
     *
     * @param range range of the numbers
     * @param count count of numbers to generate
     * @return An int array containing the generated numbers.
     */
    int[] generateIndependentNumbers(IntRange range, int count);

    /**
     * Generates {@code count} number of unique pseudorandom numbers with the given {@code range}.
     *
     * @param range range of the numbers
     * @param count count of numbers to generate
     * @return An int array containing the generated numbers.
     */
    int[] generateUniqueNumbers(IntRange range, int count);
}
