package fi.mskcode.officeroulette.util;

import static fi.mskcode.officeroulette.util.ValidateMore.isGreaterThan;
import static java.lang.String.format;
import static org.apache.commons.lang3.Validate.notBlank;

import fi.mskcode.officeroulette.error.RuntimeSqlException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SqlService {

    private final JdbcTemplate jdbcTemplate;

    public SqlService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int count(String sql, Object... args) {
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt(1), args);
    }

    /**
     * Executes the given SQL as a batch.
     *
     * @param sql the batch query to execute
     * @param batchSize batch size
     * @param batch values for the batch query
     * @return The number of affected rows.
     */
    public int batchUpdate(String sql, int batchSize, List<Object[]> batch) {
        notBlank(sql);
        isGreaterThan(0, batchSize);

        int[][] batchUpdatedRowCounts = jdbcTemplate.batchUpdate(sql, batch, batchSize, (ps, args) -> {
            for (int i = 0; i < args.length; ++i) {
                ps.setObject(i + 1, args[i]);
            }
        });

        return Arrays.stream(batchUpdatedRowCounts)
                .mapToInt(arr -> Arrays.stream(arr).sum())
                .sum();
    }

    public int batchUpdate(String sql, List<Object[]> batch) {
        return batchUpdate(sql, 100, batch);
    }

    /**
     * Executes the given SQL and checks that exactly one row was affected, otherwise raises an exception.
     *
     * @param sql the SQL statement to execute
     * @param args parameters for the SQL statement
     *
     * @throws EmptyResultDataAccessException if the number of affected rows was zero
     * @throws IncorrectResultSizeDataAccessException if the number of affected rows is more than one
     */
    public void updateOne(String sql, Object... args) {
        int updatedRows = jdbcTemplate.update(sql, args);
        if (updatedRows == 0) {
            throw new EmptyResultDataAccessException(1);
        } else if (updatedRows != 1) {
            throw new IncorrectResultSizeDataAccessException(1, updatedRows);
        }
    }

    /**
     * Executes the given SQL and checks that one or more rows was affected, otherwise raises an exception.
     *
     * @param sql the SQL statement to execute
     * @param args parameters for the SQL statement
     * @return the (non-zero) number of rows updated
     */
    public int updateAtLeastOne(String sql, Object... args) {
        int updatedRows = jdbcTemplate.update(sql, args);
        if (updatedRows == 0) {
            throw new EmptyResultDataAccessException(1);
        }
        return updatedRows;
    }

    public long nextInSequence(String sequenceName) {
        Long value = jdbcTemplate.queryForObject("SELECT nextval('?')", Long.class, notBlank(sequenceName));
        if (value == null) {
            throw new RuntimeSqlException(format("Producing next value for sequence %s failed", sequenceName));
        }
        return value;
    }

    public static OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }

    /** Read nullable {@code UUID} and return it as an {@link Optional} {@link UUID}. */
    public static Optional<UUID> readOptionalUuid(ResultSet rs, String columnName) {
        try {
            var result = rs.getObject(columnName, UUID.class);
            return result == null ? Optional.empty() : Optional.of(result);
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }

    public static UUID readUuid(ResultSet rs, String columnName) {
        return readOptionalUuid(rs, columnName).orElse(null);
    }

    /** Read nullable {@code TIMESTAMP} and return it as an {@link Optional} {@link Instant}. */
    public static Optional<Instant> readOptionalInstant(ResultSet rs, String columnName) {
        try {
            var result = rs.getObject(columnName, OffsetDateTime.class);
            return result == null ? Optional.empty() : Optional.of(result.toInstant());
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }

    public static Instant readInstant(ResultSet rs, String columnName) {
        return readOptionalInstant(rs, columnName).orElse(null);
    }
}