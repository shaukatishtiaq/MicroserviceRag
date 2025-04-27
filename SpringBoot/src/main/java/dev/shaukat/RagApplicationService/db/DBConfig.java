package dev.shaukat.RagApplicationService.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBConfig.class);

    @Value("${database.jdbc.url}")
    private String jdbcUrl;
    
    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Bean
    public HikariDataSource dataSource(){
        LOGGER.info("Creating DB Connection Using Config:\nJDBC URL: {}\nUsername: {}\nPassword: {}.",jdbcUrl, username, password);

        HikariConfig config = new HikariConfig();
        config.setPoolName("H2-Database-Connection-Pool");
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
//        config.setDriverClassName();
        config.setConnectionTimeout(30000);
        config.setMinimumIdle(3);
        config.setMaximumPoolSize(10);
        config.setIdleTimeout(10000);
        config.setConnectionInitSql("select count(1)");
        try{
            return new HikariDataSource(config);
        }catch (Exception e){
            LOGGER.error("ERROR OCCURED TRYING TO CONNECT TO DATABASE!!!", e);
        }
        return null;
    }
}
