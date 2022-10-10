package fi.mskcode.officeroulette.core;

import static fi.mskcode.officeroulette.util.SqlService.readInstant;
import static fi.mskcode.officeroulette.util.SqlService.readOptionalInstant;
import static fi.mskcode.officeroulette.util.SqlService.toOffsetDateTime;

import fi.mskcode.officeroulette.time.TimeService;
import fi.mskcode.officeroulette.util.SqlService;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DrawDao {

    private final JdbcTemplate jdbcTemplate;
    private final SqlService sqlService;
    private final TimeService timeService;

    public DrawDao(JdbcTemplate jdbcTemplate, SqlService sqlService, TimeService timeService) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlService = sqlService;
        this.timeService = timeService;
    }

    public Draw insertOpenDraw() {
        var id = sqlService.nextInSequence("seq_draws_id");
        var now = timeService.now();
        sqlService.updateOne(
                "INSERT INTO draws (id, status, insert_time, close_time) VALUES (?, ?, ?, NULL)",
                id,
                Draw.Status.OPEN.name(),
                toOffsetDateTime(timeService.now()));

        return new Draw(id, Draw.Status.OPEN, now, Optional.empty());
    }

    public Optional<Draw> findDrawById(long drawId) {
        return sqlService.queryOne("SELECT * FROM draws WHERE id = ?", drawRowMapper, drawId);
    }

    public List<Draw> findDraws() {
        return jdbcTemplate.query("SELECT * FROM draws", drawRowMapper);
    }

    public void updateDrawClosed(long drawId) {
        var now = timeService.now();
        sqlService.updateOne(
                "UPDATE draws SET status = ?, close_time = ? WHERE id = ?", Draw.Status.CLOSED.name(), now, drawId);
    }

    public boolean isExistingDrawId(long drawId) {
        return sqlService.count("SELECT COUNT(*) FROM draws WHERE id = ?", drawId) > 0;
    }

    private final RowMapper<Draw> drawRowMapper = (rs, rowNum) -> new Draw(
            rs.getLong("id"),
            Draw.Status.valueOf(rs.getString("status")),
            readInstant(rs, "insert_time"),
            readOptionalInstant(rs, "close_time"));
}
