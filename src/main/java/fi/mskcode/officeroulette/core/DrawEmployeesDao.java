package fi.mskcode.officeroulette.core;

import static com.google.common.collect.ImmutableList.toImmutableList;

import fi.mskcode.officeroulette.error.NotImplementedException;
import fi.mskcode.officeroulette.util.SqlService;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DrawEmployeesDao {

    private final JdbcTemplate jdbcTemplate;
    private final SqlService sqlService;

    public DrawEmployeesDao(JdbcTemplate jdbcTemplate, SqlService sqlService) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlService = sqlService;
    }

    public void insertEmployeesToDraw(long drawId, List<UUID> employeeIds) {
        var batch = employeeIds.stream()
                .map(employeeId -> new Object[] {drawId, employeeId})
                .collect(toImmutableList());
        sqlService.batchUpdate("INSERT INTO draw_employees (draw_id, employee_id) VALUES (?,?)", batch);
    }

    public List<UUID> enumerateEmployeesParticipatingInDraw(long drawId) {
        throw new NotImplementedException();
    }

    public boolean drawContainsEmployeeId(UUID employeeId) {
        return sqlService.count("SELECT COUNT(*) FROM draw_employees WHERE employee_id = ?", employeeId) > 0;
    }
}
