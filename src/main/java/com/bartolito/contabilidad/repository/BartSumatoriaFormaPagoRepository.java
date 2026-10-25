package com.bartolito.contabilidad.repository;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BartSumatoriaFormaPagoRepository {

    private final JdbcTemplate jdbcTemplate;

    public BartSumatoriaFormaPagoRepository(
            @Qualifier("lolfarJdbcTemplate")
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> listarEfectivo() {

        String sql = "EXEC dbo.sp_bart_sumatoria_forma_pago_ef";

        return jdbcTemplate.queryForList(sql);
    }
}