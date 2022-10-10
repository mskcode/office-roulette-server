package fi.mskcode.officeroulette.core;

import java.time.Instant;
import java.util.UUID;

public record DrawResult(long drawId, UUID winnerEmployeeId, Instant resultInsertTime) {}
