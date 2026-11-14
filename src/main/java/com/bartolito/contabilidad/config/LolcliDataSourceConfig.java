package com.bartolito.contabilidad.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class LolcliDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.lolcli")
    public DataSourceProperties lolcliDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "lolcliDataSource")
    public DataSource lolcliDataSource(
            @Qualifier("lolcliDataSourceProperties")
            DataSourceProperties properties) {

        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "lolcliJdbcTemplate")
    public JdbcTemplate lolcliJdbcTemplate(
            @Qualifier("lolcliDataSource")
            DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }
}
