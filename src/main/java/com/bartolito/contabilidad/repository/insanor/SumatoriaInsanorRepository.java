package com.bartolito.contabilidad.repository.insanor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class SumatoriaInsanorRepository {

    private final JdbcTemplate jdbcTemplate;

    public SumatoriaInsanorRepository(
            @Qualifier("lolcliJdbcTemplate")
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    // =========================================
    // SUMATORIA FORMA DE PAGO - GUARDAR / ACTUALIZAR
    // =========================================
    public List<Map<String, Object>> saveOrUpdate(
            Integer sumatoriaFormaPagoId,
            Integer operacionId,
            Integer siscod,
            String fechaOperacion,
            Integer invnumAper,
            String docpag,
            String docdes,
            Integer cantidad,
            BigDecimal importeTotal,
            BigDecimal importeNC,
            BigDecimal total,
            Boolean estado,
            String usuario) {

        String sql = "EXEC sp_bart_ins_sumatoria_forma_pago_save_or_update ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";

        return jdbcTemplate.queryForList(sql,
                sumatoriaFormaPagoId,
                operacionId,
                siscod,
                fechaOperacion,
                invnumAper,
                docpag,
                docdes,
                cantidad,
                importeTotal,
                importeNC,
                total,
                estado,
                usuario
        );
    }

    // =========================================
    // SUMATORIA FORMA DE PAGO - LISTAR POR OPERACIÓN
    // =========================================
    public List<Map<String, Object>> listarSumatoria(Integer operacionId) {

        String sql = "EXEC sp_bart_ins_comer_listar_sumatoria ?";

        return jdbcTemplate.queryForList(sql, operacionId);
    }

    // =========================================
    // SUMATORIA FORMA DE PAGO - LISTAR EFECTIVO
    // =========================================
    public List<Map<String, Object>> listarEfectivo(LocalDate fechaOperacion) {

        String sql = "EXEC sp_bart_ins_sumatoria_forma_pago_ef ?";

        return jdbcTemplate.queryForList(sql,
                Date.valueOf(fechaOperacion));
    }

    // =========================================
    // SUMATORIA FORMA DE PAGO - LISTAR HISTORIAL
    // =========================================
    public List<Map<String, Object>> listarHistorialSumatorias(
            String fechaInicio,
            String fechaFin) {

        String sql = "EXEC sp_bart_ins_comer_listar_historial_sumatorias ?, ?";

        return jdbcTemplate.queryForList(sql, fechaInicio, fechaFin);
    }

}
