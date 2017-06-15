package com.aquariumpain.dialect.database;

import com.knockturnmc.api.util.sql.SqlDatasource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionPool implements SqlDatasource {

    private final HikariDataSource dataSource;

    public DatabaseConnectionPool(DatabaseProperties properties) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setJdbcUrl(properties.getDatasourceUrl());
        hikariConfig.setUsername(properties.getDatasourceUser());
        hikariConfig.setPassword(properties.getDatasourcePassword());
        hikariConfig.setConnectionInitSql(properties.getConnectionInitSql());
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
