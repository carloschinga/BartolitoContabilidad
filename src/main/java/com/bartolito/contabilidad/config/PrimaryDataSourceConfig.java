package com.bartolito.contabilidad.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class PrimaryDataSourceConfig {

    /**
     * Propiedades de la base de datos principal
     * SIGOLDHOSPITAL_DES
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * DataSource principal de Bartolito
     */
    @Bean(name = "dataSource")
    @Primary
    public DataSource primaryDataSource(
            @Qualifier("primaryDataSourceProperties")
            DataSourceProperties properties) {

        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    /**
     * JdbcTemplate principal de Bartolito
     *
     * Todos los repositories existentes que utilizan:
     *
     * @Autowired
     * private JdbcTemplate sigoldJdbc;
     *
     * utilizarán esta conexión.
     */
    @Bean(name = "sigoldJdbc")
    @Primary
    public JdbcTemplate sigoldJdbc(
            @Qualifier("dataSource")
            DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }
}