package com.audit_service.config;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;
import org.javers.spring.auditable.AuthorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class JaversConfig {

    private static final Logger logger = LoggerFactory.getLogger(JaversConfig.class);

    @Bean
    public Javers javers(DataSource dataSource) {
        logger.info("Initializing JaVers with H2 database...");

        // Create transaction-aware ConnectionProvider using Spring's DataSourceUtils
        ConnectionProvider connectionProvider = new ConnectionProvider() {
            @Override
            public Connection getConnection() {
                // Use DataSourceUtils to get transaction-aware connection
                return DataSourceUtils.getConnection(dataSource);
            }
        };

        // Build SQL repository - H2 dialect with schema already created
        JaversSqlRepository sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(connectionProvider)
                .withDialect(DialectName.H2)
                .withSchemaManagementEnabled(false)  // We manage schema manually
                .build();

        // Build and return Javers instance
        Javers javers = JaversBuilder
                .javers()
                .registerJaversRepository(sqlRepository)
                .build();

        logger.info("JaVers initialized successfully with H2!");
        return javers;
    }

    @Bean
    public AuthorProvider authorProvider() {
        return () -> "system";
    }
}
