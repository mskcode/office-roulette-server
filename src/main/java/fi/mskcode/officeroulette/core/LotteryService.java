package fi.mskcode.officeroulette.core;

import fi.mskcode.officeroulette.prng.IntRange;
import fi.mskcode.officeroulette.prng.Prng;
import fi.mskcode.officeroulette.prng.WeakPrng;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LotteryService {

    public UUID selectWinningEmployee(List<UUID> employeeIds) {
        var prng = initializePrng();
        var winnerIndex = prng.generateIndependentNumbers(IntRange.of(0, employeeIds.size() - 1), 1)[0];
        return employeeIds.get(winnerIndex);
    }

    private Prng initializePrng() {
        var seed = Instant.now().toEpochMilli();
        return new WeakPrng(seed);
    }
}
