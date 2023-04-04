package fi.mskcode.officeroulette.util;

import static java.lang.String.format;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.*;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;

/**
 * Transaction management with {@code @Transaction} annotations can get iffy
 * quickly since you cannot use them on non-component classes (classes that
 * don't have {@code @Component} or {@code @Service} annotation) or on
 * non-public methods.
 *
 * <p>You can read more about Spring's transaction management</p>
 *
 * <ul>
 *     <li><a href="https://docs.spring.io/spring-framework/docs/5.3.0/reference/html/data-access.html">5.3</a></li>
 *     <li><a href="https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html">Latest</a></li>
 * </ul>
 */
public class TransactionService {

    /**
     * Type safe propagation behaviour mappings.
     *
     * <p>Some of the more problematic propagation methods have been disabled for the time being.</p>
     *
     * @see TransactionDefinition
     */
    public enum PropagationBehaviour {

        /**
         * Use current transaction if there is one, otherwise create a new transaction.
         */
        REQUIRED(TransactionDefinition.PROPAGATION_REQUIRED),

        // SUPPORTS(TransactionDefinition.PROPAGATION_SUPPORTS),

        /**
         * Transaction must already exist or an exception is raised.
         */
        MANDATORY(TransactionDefinition.PROPAGATION_MANDATORY),

        /**
         * Suspends an on-going transaction if there is one and start a new one. Use this sparingly since this can
         * cause deadlocks when available database connections are low.
         */
        REQUIRES_NEW(TransactionDefinition.PROPAGATION_REQUIRES_NEW),

        // NOT_SUPPORTED(TransactionDefinition.PROPAGATION_NOT_SUPPORTED),

        /**
         * If a transaction already exists, an exception is raised.
         */
        NEVER(TransactionDefinition.PROPAGATION_NEVER);

        // NESTED(TransactionDefinition.PROPAGATION_NESTED);

        public final int value;

        PropagationBehaviour(int value) {
            this.value = value;
        }
    }

    /**
     * Type safe isolation level mappings.
     *
     * @see TransactionDefinition
     */
    public enum IsolationLevel {
        DEFAULT(TransactionDefinition.ISOLATION_DEFAULT),
        READ_UNCOMMITTED(TransactionDefinition.ISOLATION_READ_UNCOMMITTED),
        READ_COMMITTED(TransactionDefinition.ISOLATION_READ_COMMITTED),
        REPEATABLE_READ(TransactionDefinition.ISOLATION_REPEATABLE_READ),
        SERIALIZABLE(TransactionDefinition.ISOLATION_SERIALIZABLE);

        public final int value;

        IsolationLevel(int value) {
            this.value = value;
        }

        /** Constructs value from an integer value. */
        public static IsolationLevel fromInt(int value) {
            for (var level : IsolationLevel.values()) {
                if (level.value == value) {
                    return level;
                }
            }
            throw new IllegalArgumentException(format("Unsupported isolation level value %d", value));
        }

        /** Constructs value from Hikari isolation level string. */
        public static IsolationLevel fromHikariIsolationLevel(String hikariIsolationLevel) {
            notBlank(hikariIsolationLevel);
            var convertedIsolationLevel =
                    convertHikariTransactionIsolationLevelToSpringEquivalent(hikariIsolationLevel);
            var isolationLevelValue = convertTransactionDefinitionValue(convertedIsolationLevel);
            return TransactionService.IsolationLevel.fromInt(isolationLevelValue);
        }

        /**
         * Converts HikariCP isolation level string to the Spring equivalent.
         *
         * @see com.zaxxer.hikari.util.IsolationLevel
         */
        private static String convertHikariTransactionIsolationLevelToSpringEquivalent(
                String hikariTransactionIsolationLevel) {
            return hikariTransactionIsolationLevel.replace("TRANSACTION_", "ISOLATION_");
        }

        /** Searches TransactionDefinition class for the given field name and returns its int value. */
        private static int convertTransactionDefinitionValue(String str) {
            for (var field : TransactionDefinition.class.getFields()) {
                if (field.getName().equals(str)) {
                    try {
                        return field.getInt(null);
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            }
            throw new IllegalArgumentException(format("Transaction definition %s does not exist", str));
        }
    }

    /**
     * A result yielding computation.
     *
     * @param <V> the result type of the method call
     *
     * @see TransactionVoidCallable
     * @see java.util.concurrent.Callable
     */
    @FunctionalInterface
    public interface TransactionResultCallable<V> {
        V call() throws Exception;
    }

    /**
     * A non-result yielding computation.
     *
     * @see TransactionResultCallable
     */
    @FunctionalInterface
    public interface TransactionVoidCallable {
        void call() throws Exception;
    }

    public static class TransactionProperties {

        public final Optional<PropagationBehaviour> propagationBehaviour;
        public final Optional<IsolationLevel> isolationLevel;
        public final Optional<String> name;
        public final Optional<Integer> timeout;

        private TransactionProperties(
                PropagationBehaviour propagationBehaviour,
                IsolationLevel isolationLevel,
                String name,
                Integer timeout) {
            this.propagationBehaviour = Optional.ofNullable(propagationBehaviour);
            this.isolationLevel = Optional.ofNullable(isolationLevel);
            this.name = Optional.ofNullable(name);
            this.timeout = Optional.ofNullable(timeout);
        }

        public static TransactionProperties defaults() {
            return new TransactionProperties(null, null, null, null);
        }

        public static TransactionProperties of(
                PropagationBehaviour propagationBehaviour,
                IsolationLevel isolationLevel,
                String name,
                Integer timeout) {
            return new TransactionProperties(propagationBehaviour, isolationLevel, name, timeout);
        }

        public static TransactionProperties of(PropagationBehaviour propagationBehaviour) {
            return new TransactionProperties(propagationBehaviour, null, null, null);
        }
    }

    private static final Logger logger =
            LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PlatformTransactionManager transactionManager;
    private final IsolationLevel defaultIsolationLevel;
    private final PropagationBehaviour defaultPropagationBehaviour;

    /** Constructs a new instance with given transaction manager and isolation level. */
    public TransactionService(PlatformTransactionManager transactionManager, IsolationLevel defaultIsolationLevel) {
        this.transactionManager = notNull(transactionManager);
        this.defaultIsolationLevel = notNull(defaultIsolationLevel);
        this.defaultPropagationBehaviour = PropagationBehaviour.REQUIRED;
        // TODO transactionManager validateExistingTransactions
    }

    /** Constructs a new instance with {@code READ_COMMITTED} isolation level. */
    public TransactionService(PlatformTransactionManager transactionManager) {
        this(transactionManager, IsolationLevel.READ_COMMITTED);
    }

    /**
     * Executes the given function with given transaction properties.
     *
     * @param func function to execute
     * @param properties transaction properties
     * @return result the function yielded.
     * @param <T> result type of the function {@code func}
     */
    public <T> T tx(TransactionResultCallable<T> func, TransactionProperties properties) {
        return _tx(func, properties);
    }

    /** Same as {@link #tx(TransactionResultCallable, TransactionProperties)} without yielding a result. */
    public void tx(TransactionVoidCallable func, TransactionProperties properties) {
        var wrapper = (TransactionResultCallable<Void>) () -> {
            func.call();
            return null;
        };
        _tx(wrapper, properties);
    }

    /**
     * Implementation proper for the transaction mechanism.
     *
     * @see org.springframework.transaction.support.TransactionTemplate#execute(TransactionCallback)
     */
    private <T> T _tx(TransactionResultCallable<T> func, TransactionProperties properties) {
        var transactionDefinition = newTransactionDefinition(properties);
        var status = transactionManager.getTransaction(transactionDefinition);
        T result;
        try {
            result = func.call();
        } catch (RuntimeException | Error e) {
            rollbackOnException(status, e);
            throw e;
        } catch (Throwable e) {
            rollbackOnException(status, e);
            throw new UndeclaredThrowableException(e, "Transaction callback threw undeclared checked exception");
        }

        // do NOT rollback if commit raises an exception;
        // this could happen but rolling back is not an option
        transactionManager.commit(status);

        return result;
    }

    /**
     * Executes the given callable inside a transaction.
     *
     * <p>Uses a pre-existing transaction or creates a new one when there is none.</p>
     *
     * <p>Can be used instead of <code>@Transactional(propagation = Propagation.REQUIRED)</code> annotation.</p>
     *
     * @param func function to execute
     * @return The result of the callable function.
     * @param <T>
     */
    public <T> T txRequired(TransactionResultCallable<T> func) {
        return tx(func, TransactionProperties.of(PropagationBehaviour.REQUIRED));
    }

    /** Same as {@link #txRequired(TransactionResultCallable)} without yielding a result. */
    public void txRequired(TransactionVoidCallable func) {
        tx(func, TransactionProperties.of(PropagationBehaviour.REQUIRED));
    }

    /**
     * Executes the given function inside a new transaction.
     *
     * <p>If there is a pre-existing transaction on-going, it is suspended and a new one is started. You should be
     * careful with this method since it can cause deadlocks when database connection pool resources are low.</p>
     *
     * <p>Can be used instead of <code>@Transactional(propagation = Propagation.REQUIRES_NEW)</code> annotation.</p>
     *
     * @param func function to execute
     * @return The result of the callable function.
     * @param <T>
     */
    public <T> T txRequiresNew(TransactionResultCallable<T> func) {
        return tx(func, TransactionProperties.of(PropagationBehaviour.REQUIRES_NEW));
    }

    public void txRequiresNew(TransactionVoidCallable func) {
        tx(func, TransactionProperties.of(PropagationBehaviour.REQUIRES_NEW));
    }

    /**
     * Executes the given function when there is not a pre-existing transaction.
     *
     * <p>Every SQL statement will be its own transaction - unless you explicitly create a new transaction within the
     * callable function.</p>
     *
     * <p>Can be used instead of <code>@Transactional(propagation = Propagation.NEVER)</code> annotation.</p>
     *
     * <p>Instead of using this method, you might want to use {@link TransactionUtils#validateTransactionNotActive()}
     * static method to verify that there is no pre-existing transaction.</p>
     *
     * @param func function to execute
     * @return The result of the callable function.
     * @param <T>
     */
    public <T> T txNever(TransactionResultCallable<T> func) {
        return tx(func, TransactionProperties.of(PropagationBehaviour.NEVER));
    }

    /** Same as {@link #txNever(TransactionResultCallable)} without yielding a result. */
    public void txNever(TransactionVoidCallable func) {
        tx(func, TransactionProperties.of(PropagationBehaviour.NEVER));
    }

    /**
     * Executes the given function when there is a pre-existing transaction.
     *
     * <p>Can be used instead of <code>@Transactional(propagation = Propagation.MANDATORY)</code> annotation.</p>
     *
     * <p>Instead of using this method, you might want to use {@link TransactionUtils#validateTransactionActive()}
     * static method to verify that there is a pre-existing transaction.</p>
     *
     * @param func function to execute
     * @return The result of the callable function.
     * @param <T>
     */
    public <T> T txMandatory(TransactionResultCallable<T> func) {
        return tx(func, TransactionProperties.of(PropagationBehaviour.MANDATORY));
    }

    /** Same as {@link #txMandatory(TransactionResultCallable)} without yielding a result. */
    public void txMandatory(TransactionVoidCallable func) {
        tx(func, TransactionProperties.of(PropagationBehaviour.MANDATORY));
    }

    /**
     * Implementation copied from TransactionTemplate#rollbackOnException(TransactionStatus, Throwable).
     *
     * @see org.springframework.transaction.support.TransactionTemplate#rollbackOnException(TransactionStatus, Throwable)
     */
    private void rollbackOnException(TransactionStatus status, Throwable e) throws TransactionException {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "Rolling back transaction due to exception {}: {}",
                    e.getClass().getSimpleName(),
                    e.getMessage());
        }
        try {
            this.transactionManager.rollback(status);
        } catch (TransactionSystemException ex2) {
            logger.error("Application exception overridden by rollback exception", e);
            ex2.initApplicationException(e);
            throw ex2;
        } catch (RuntimeException | Error ex2) {
            logger.error("Application exception overridden by rollback exception", e);
            throw ex2;
        }
        // there is no catch-block for Exception since rollback() has not declared raising it; so basically the
        // exceptions it can raise are one of TransactionException (which TransactionSystemException is an
        // implementation of), RuntimeException or Error
    }

    private DefaultTransactionDefinition newTransactionDefinition(TransactionProperties properties) {
        var selectedPropagationBehaviour = properties.propagationBehaviour.orElse(defaultPropagationBehaviour);
        var selectedIsolationLevel = properties.isolationLevel.orElse(defaultIsolationLevel);
        var selectedName = properties.name.orElse(null); // can be null without issue
        var selectedTimeout = properties.timeout.orElse(TransactionDefinition.TIMEOUT_DEFAULT);

        var definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(selectedPropagationBehaviour.value);
        definition.setIsolationLevel(selectedIsolationLevel.value);
        // noinspection DataFlowIssue
        definition.setName(selectedName);
        definition.setTimeout(selectedTimeout);
        return definition;
    }
}
