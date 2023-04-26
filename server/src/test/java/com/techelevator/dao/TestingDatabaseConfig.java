package com.techelevator.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;

@Configuration
public class TestingDatabaseConfig {

    // To use an existing PostgreSQL database, set the following environment variables.
    // Otherwise, a temporary database will be created on the local machine.
    private static final String DB_HOST =
            Objects.requireNonNullElse(System.getenv("DB_HOST"), "localhost");
    private static final String DB_PORT =
            Objects.requireNonNullElse(System.getenv("DB_PORT"), "5432");
    private static final String DB_NAME =
            Objects.requireNonNullElse(System.getenv("DB_NAME"), "tenmodbtesting");
    private static final String DB_USER =
            Objects.requireNonNullElse(System.getenv("DB_USER"), "postgres");
    private static final String DB_PASSWORD =
            Objects.requireNonNullElse(System.getenv("DB_PASSWORD"), "postgres1");

    /* Using this particular implementation of DataSource so that
     * every database interaction is part of the same database
     * session and hence the same database transaction */
    private SingleConnectionDataSource adminDataSource;

    private JdbcTemplate adminJdbcTemplate;

    /* This method creates the temporary database to be used for the tests. */
    @PostConstruct
    public void setup() {
        if (System.getenv("DB_HOST") == null) {
            adminDataSource = new SingleConnectionDataSource();
            adminDataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
            adminDataSource.setUsername("postgres");
            adminDataSource.setPassword("postgres1");
            adminJdbcTemplate = new JdbcTemplate(adminDataSource);
            adminJdbcTemplate.update("DROP DATABASE IF EXISTS \"" + DB_NAME + "\";");
            adminJdbcTemplate.update("CREATE DATABASE \"" + DB_NAME + "\";");
        }
    }

    private DataSource ds = null;
    /* Before any tests are run, this method initializes the datasource and populates the testing db. */
    @Bean
    public DataSource dataSource() throws SQLException {
        if (ds != null) return ds;
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setUrl(String.format("jdbc:postgresql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME));
        dataSource.setUsername(DB_USER);
        dataSource.setPassword(DB_PASSWORD);

        /* The following line disables autocommit for connections
         * returned by this DataSource. This allows us to rollback
         * any changes after each test */
        dataSource.setAutoCommit(false);

        // Spring provides a convenience class called ScriptUtils for running external SQL scripts.
        // You'll find the test-data.sql script file in the test/resources folder.
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-data.sql"));
        ds = dataSource;
        return ds;
    }

    /* This method runs after all the tests and removes the temporary database. */
    @PreDestroy
    public void cleanup() throws SQLException {
        if (adminDataSource != null) {
            adminJdbcTemplate.update("DROP DATABASE \"" + DB_NAME + "\";");
            adminDataSource.getConnection().close();
            adminDataSource.destroy();
        }
    }
}
