package com.leansofx.qaserviceuser.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDataSourceNotNull() {
        assertNotNull(dataSource, "DataSource should not be null");
    }

    @Test
    void testJdbcTemplateNotNull() {
        assertNotNull(jdbcTemplate, "JdbcTemplate should not be null");
    }

    @Test
    void testDatabaseConnection() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Database connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
        }
    }

    @Test
    void testDatabaseQuery() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertNotNull(result, "Query result should not be null");
        assertEquals(1, result, "Query result should be 1");
    }
}
