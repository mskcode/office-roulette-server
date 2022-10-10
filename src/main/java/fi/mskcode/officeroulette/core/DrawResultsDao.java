package fi.mskcode.officeroulette.core;

import static fi.mskcode.officeroulette.util.SqlService.readInstant;
import static fi.mskcode.officeroulette.util.SqlService.readUuid;
import static org.apache.commons.lang3.Validate.notNull;

import fi.mskcode.officeroulette.time.TimeService;
import fi.mskcode.officeroulette.util.SqlService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DrawResultsDao {

    private final JdbcTemplate jdbcTemplate;
    private final SqlService sqlService;
    private final TimeService timeService;

    public DrawResultsDao(JdbcTemplate jdbcTemplate, SqlService sqlService, TimeService timeService) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlService = sqlService;
        this.timeService = timeService;
    }

    public DrawResult insertDrawResult(long drawId, UUID winnerEmployeeId) {
        final var sql = "INSERT INTO draw_results (draw_id, winner_employee_id, result_insert_time) VALUES (?,?,?)";
        Instant now = timeService.now();
        sqlService.updateOne(sql, drawId, notNull(winnerEmployeeId), now);
        return new DrawResult(drawId, winnerEmployeeId, now);
    }

    public Optional<DrawResult> findDrawResultByDrawId(long drawId) {
        final var sql = "SELECT * FROM draw_results WHERE draw_id = ?";
        return sqlService.queryOne(sql, employeeRowMapper, drawId);
    }

    private final RowMapper<DrawResult> employeeRowMapper = (rs, rowNum) -> new DrawResult(
            rs.getLong("draw_id"), readUuid(rs, "winner_employee_id"), readInstant(rs, "result_insert_time"));
}
