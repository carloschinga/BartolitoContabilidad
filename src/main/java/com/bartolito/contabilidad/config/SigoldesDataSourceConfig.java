package com.bartolito.contabilidad.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class SigoldesDataSourceConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.sigoldes")
    public DataSourceProperties sigoldesDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "sigoldesDataSource")
    public DataSource sigoldesDataSource(
            @Qualifier("sigoldesDataSourceProperties")
            DataSourceProperties properties) {

        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "sigoldesJdbcTemplate")
    public JdbcTemplate sigoldesJdbcTemplate(
            @Qualifier("sigoldesDataSource")
            DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }
}
