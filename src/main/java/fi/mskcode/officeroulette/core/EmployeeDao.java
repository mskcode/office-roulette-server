package fi.mskcode.officeroulette.core;

import static fi.mskcode.officeroulette.util.SqlService.readInstant;
import static fi.mskcode.officeroulette.util.SqlService.readUuid;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import fi.mskcode.officeroulette.util.SqlService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeDao {

    private final JdbcTemplate jdbcTemplate;
    private final SqlService sqlService;

    public EmployeeDao(JdbcTemplate jdbcTemplate, SqlService sqlService) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlService = sqlService;
    }

    public Employee insertEmployee(
            UUID employeeId, String firstName, String lastName, Instant employmentStartTime, Employee.Status status) {
        final var sql = "INSERT INTO employees (id, first_name, last_name, employment_start_time, status) "
                + "VALUES (?, ?, ?, ?, ?)";
        sqlService.updateOne(
                sql,
                notNull(employeeId),
                notBlank(firstName),
                notBlank(lastName),
                notNull(employmentStartTime),
                notNull(status));

        return new Employee(employeeId, firstName, lastName, employmentStartTime, status);
    }

    public final Optional<Employee> findEmployeeById(UUID employeeId) {
        var result = jdbcTemplate.query("SELECT * FROM employees WHERE id = ?", employeeRowMapper, employeeId);
        return result.size() > 0 ? Optional.of(result.get(0)) : Optional.empty();
    }

    private final RowMapper<Employee> employeeRowMapper = (rs, rowNum) -> new Employee(
            readUuid(rs, "id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            readInstant(rs, "employment_start_time"),
            Employee.Status.valueOf(rs.getString("status")));
}
