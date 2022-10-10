package fi.mskcode.officeroulette.error;

import java.sql.SQLException;

public class RuntimeSqlException extends RuntimeException {

    public RuntimeSqlException(String message) {
        super(message);
    }

    public RuntimeSqlException(SQLException cause) {
        super(cause);
    }
}
