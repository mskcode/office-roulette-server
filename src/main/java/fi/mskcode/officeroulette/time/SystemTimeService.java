package fi.mskcode.officeroulette.time;

import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class SystemTimeService implements TimeService {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
