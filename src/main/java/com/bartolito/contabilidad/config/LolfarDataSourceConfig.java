package com.bartolito.contabilidad.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class LolfarDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.lolfar")
    public DataSourceProperties lolfarDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "lolfarDataSource")
    public DataSource lolfarDataSource(
            @Qualifier("lolfarDataSourceProperties")
            DataSourceProperties properties) {

        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "lolfarJdbcTemplate")
    public JdbcTemplate lolfarJdbcTemplate(
            @Qualifier("lolfarDataSource")
            DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }
}