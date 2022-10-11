package fi.mskcode.officeroulette.prng;

public final class FisherYatesShuffle {
    private FisherYatesShuffle() {}

    @FunctionalInterface
    public interface RandomIntProvider {
        int randomInt(int max);
    }

    public static void shuffleFromBeginning(int[] uniqueNumbers, int count, RandomIntProvider randomIntProvider) {
        for (int i = 0; i < count; i++) {
            var randomArrayElement = i + randomIntProvider.randomInt(uniqueNumbers.length - i);
            swap(uniqueNumbers, i, randomArrayElement);
        }
    }

    private static void swap(int[] array, int indexA, int indexB) {
        int tmp = array[indexA];
        array[indexA] = array[indexB];
        array[indexB] = tmp;
    }
}
