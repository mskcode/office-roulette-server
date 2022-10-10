package fi.mskcode.officeroulette.core;

import static fi.mskcode.officeroulette.util.SqlService.readInstant;
import static fi.mskcode.officeroulette.util.SqlService.readUuid;
import static fi.mskcode.officeroulette.util.SqlService.toOffsetDateTime;
import static java.lang.String.format;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import fi.mskcode.officeroulette.util.SqlService;
import java.time.Instant;
import java.util.List;
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
                toOffsetDateTime(notNull(employmentStartTime)),
                notNull(status).name());

        return new Employee(employeeId, firstName, lastName, employmentStartTime, status);
    }

    public Optional<Employee> findEmployeeById(UUID employeeId) {
        final var sql = "SELECT * FROM employees WHERE id = ?";
        return sqlService.queryOne(sql, employeeRowMapper, employeeId);
    }

    public List<Employee> findEmployees(String nameFilter) {
        final var sql = "SELECT * FROM employees "
                + "WHERE LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?) "
                + "ORDER BY last_name ASC, first_name ASC";
        return jdbcTemplate.query(sql, employeeRowMapper, format("%%%s%%", nameFilter), format("%%%s%%", nameFilter));
    }

    private final RowMapper<Employee> employeeRowMapper = (rs, rowNum) -> new Employee(
            readUuid(rs, "id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            readInstant(rs, "employment_start_time"),
            Employee.Status.valueOf(rs.getString("status")));
}
