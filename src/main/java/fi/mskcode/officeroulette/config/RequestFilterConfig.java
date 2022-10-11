package fi.mskcode.officeroulette.config;

import java.lang.invoke.MethodHandles;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@Configuration
class RequestFilterConfig {

    @Bean
    public AbstractRequestLoggingFilter loggingFilter() {
        // FIXME the contents/format of the message is crap and should be
        //  replaced with custom formatting
        return new AbstractRequestLoggingFilter() {
            private static final Logger logger =
                    LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

            @Override
            protected boolean shouldLog(HttpServletRequest request) {
                return true;
            }

            @Override
            protected void beforeRequest(HttpServletRequest request, String message) {
                logger.info(message);
            }

            @Override
            protected void afterRequest(HttpServletRequest request, String message) {
                logger.info(message);
            }
        };
    }
}
