package com.bbva.database;

import com.bbva.fga.core.AppProperties;
import com.bbva.fga.utils.EnvironmentUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyBatisConnectionFactory {

    private static final Logger LOGGER = Logger.getLogger(MyBatisConnectionFactory.class.getName());

    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(AppProperties.getInstance().getProperty("database.url"));
            if (EnvironmentUtils.isLocalEnvironment()) {
                dataSource.setUsername(AppProperties.getInstance().getProperty("database.username"));
                dataSource.setPassword(AppProperties.getInstance().getProperty("database.password"));
            } else {
                dataSource.addDataSourceProperty(
                        "user",
                        AppProperties.getInstance().getProperty("database.username"));
                dataSource.addDataSourceProperty(
                        "password",
                        AppProperties.getInstance().getProperty("database.password"));
                dataSource.addDataSourceProperty(
                        "socketFactory",
                        "com.google.cloud.sql.mysql.SocketFactory"
                );
                dataSource.addDataSourceProperty(
                        "cloudSqlInstance",
                        AppProperties.getInstance().getProperty("database.cloud_sql_instance")
                );
            }
            dataSource.setAutoCommit(false);
            dataSource.setMaximumPoolSize(
                    Integer.parseInt(AppProperties.getInstance().getProperty("database.maximum_pool_size"))
            );
            dataSource.setMinimumIdle(
                    Integer.parseInt(AppProperties.getInstance().getProperty("database.minimum_idle"))
            );
            dataSource.setConnectionTestQuery("SELECT 1");

            
            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("mysql", transactionFactory, dataSource);
            Configuration configuration = new Configuration(environment);
            configuration.addMappers("com.bbva.database.mappers");

            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            sqlSessionFactory = null;
        }
    }

    private MyBatisConnectionFactory() {}

    public static SqlSessionFactory getInstance() {
        return sqlSessionFactory;
    }
}