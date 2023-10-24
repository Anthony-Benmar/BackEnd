package com.bbva.database;
import com.bbva.fga.core.AppProperties;
import com.bbva.fga.utils.EnvironmentUtils;
import com.bbva.jetty.MainApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MyBatisConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(MyBatisConnectionFactory.class.getName());

    private static SqlSessionFactory sqlSessionFactory;

    private MyBatisConnectionFactory() {}

    public static void initialiceInstance(){
        try {
            HikariConfig config = new HikariConfig();
            Properties properties = AppProperties.getInstance();
            config.setJdbcUrl(properties.getProperty("database.url"));
            if (EnvironmentUtils.isLocalEnvironment()) {
                config.setUsername(properties.getProperty("database.username"));
                config.setPassword(properties.getProperty("database.password"));
            } else {
                config.setUsername(properties.getProperty("datasource.property.user"));
                config.setPassword(properties.getProperty("datasource.property.pwd"));
                config.addDataSourceProperty("socketFactory",
                        properties.getProperty("datasource.property.socket"));
                config.addDataSourceProperty("cloudSqlInstance",
                        properties.getProperty("database.cloud_sql_instance"));
                // config.addDataSourceProperty("ipTypes", "PUBLIC");
            }
            config.setAutoCommit(false);
            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("database.maximum_pool_size")));
            config.setMinimumIdle(
                    Integer.parseInt(properties.getProperty("database.minimum_idle"))
            );
            config.setConnectionTestQuery("SELECT 1");

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonConfigString = gson.toJson(config);
            MainApp.ROOT_LOOGER.log(Level.INFO,"DB - HIKARI CONFIG: " +  jsonConfigString);
            HikariDataSource dataSource = new HikariDataSource(config);
            MainApp.ROOT_LOOGER.log(Level.INFO,"---- TEST ----");

            TransactionFactory transactionFactory = new JdbcTransactionFactory();
            Environment environment = new Environment("mysql", transactionFactory, dataSource);
            Configuration configuration = new Configuration(environment);
            configuration.addMappers("com.bbva.database.mappers");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        } catch (Exception e) {
            MainApp.ROOT_LOOGER.log(Level.INFO,"Error connection Database: " + e.getMessage(), e);
            MainApp.ROOT_LOOGER.log(Level.SEVERE,"Error connection Database: " + e.getMessage(), e);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            sqlSessionFactory = null;
        }
    }

    public static SqlSessionFactory getInstance() {
        if (sqlSessionFactory == null){
            initialiceInstance();
        }
        return sqlSessionFactory;
    }

}