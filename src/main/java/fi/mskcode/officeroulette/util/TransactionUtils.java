package fi.mskcode.officeroulette.util;

import static java.lang.String.format;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TransactionUtils {

    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private TransactionUtils() {}

    public static void logTransactionInfo(Class<?> clazz, String methodName) {
        if (logger.isTraceEnabled()) {
            try {
                var classAndMethod = format("%s.%s()", clazz.getSimpleName(), methodName);
                var info = transactionInfo();
                logger.trace(
                        "{}: transaction active = {}, isolation = {}, name = {}",
                        classAndMethod,
                        info.active,
                        convertIsolationLevelToString(info.active, info.isolationLevelValue),
                        info.name);
            } catch (Exception ex) {
                logger.error("Getting transaction info failed", ex);
            }
        }
    }

    public static void validateTransactionActive() {
        try {
            var transaction = transactionInfo();
            if (!transaction.active) {
                throw new IllegalStateException("Required transaction missing");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to validate existence of transaction", e);
        }
    }

    public static void validateTransactionNotActive() {
        try {
            var transaction = transactionInfo();
            if (transaction.active) {
                throw new IllegalStateException("Already within transaction");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to validate non-existence of transaction", e);
        }
    }

    public record TransactionInfo(boolean active, int isolationLevelValue, String name) {
        public String isolationLevelAsString() {
            return convertIsolationLevelToString(active, isolationLevelValue);
        }
    }

    public static TransactionInfo transactionInfo() throws Exception {
        var contextClassLoader = Thread.currentThread().getContextClassLoader();
        var tsmClass = contextClassLoader.loadClass(
                "org.springframework.transaction.support.TransactionSynchronizationManager");
        return new TransactionInfo(
                invoke(tsmClass, "isActualTransactionActive", false),
                invoke(tsmClass, "getCurrentTransactionIsolationLevel", Connection.TRANSACTION_NONE),
                invoke(tsmClass, "getCurrentTransactionName", null));
    }

    @SuppressWarnings("unchecked")
    private static <T> T invoke(Class<?> clazz, String staticMethod, T defaultValue) throws Exception {
        var method = clazz.getMethod(staticMethod);
        var result = method.invoke(null);
        return result == null ? defaultValue : (T) result;
    }

    private static String convertIsolationLevelToString(boolean active, int isolationLevel) {
        return switch (isolationLevel) {
            case Connection.TRANSACTION_NONE -> active ? "DEFAULT" : "NONE";
            case Connection.TRANSACTION_READ_UNCOMMITTED -> "READ_UNCOMMITTED";
            case Connection.TRANSACTION_READ_COMMITTED -> "READ_COMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ -> "REPEATABLE_READ";
            case Connection.TRANSACTION_SERIALIZABLE -> "SERIALIZABLE";
            default -> format("UNKNOWN (%d)", isolationLevel);
        };
    }
}
