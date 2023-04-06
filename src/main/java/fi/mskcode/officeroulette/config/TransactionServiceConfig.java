package fi.mskcode.officeroulette.config;

import fi.mskcode.officeroulette.util.TransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
class TransactionServiceConfig {

    @Bean
    public TransactionService transactionService(
            PlatformTransactionManager transactionManager,
            @Value("${spring.datasource.hikari.transaction-isolation}") String hikariDefaultIsolationLevel) {
        var defaultIsolationLevel =
                TransactionService.IsolationLevel.fromHikariIsolationLevel(hikariDefaultIsolationLevel);
        var defaultPropagationBehaviour = TransactionService.PropagationBehaviour.REQUIRED;
        return new TransactionService(transactionManager, defaultIsolationLevel, defaultPropagationBehaviour);
    }
}
