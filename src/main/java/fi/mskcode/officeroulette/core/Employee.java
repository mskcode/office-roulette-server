package fi.mskcode.officeroulette.core;

import java.time.Instant;
import java.util.UUID;

public record Employee(UUID id, String firstName, String lastName, Instant employmentStartTime, Status status) {

    public enum Status {
        ACTIVE,
        INACTIVE;
    }
}
