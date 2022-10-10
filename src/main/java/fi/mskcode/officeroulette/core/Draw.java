package fi.mskcode.officeroulette.core;

import java.time.Instant;
import java.util.Optional;

public record Draw(long id, Status status, Instant insertTime, Optional<Instant> drawTime) {

    public enum Status {
        OPEN,
        CLOSED
    }
}
