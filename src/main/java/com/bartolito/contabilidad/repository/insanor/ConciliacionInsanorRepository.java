package com.bartolito.contabilidad.repository.insanor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class ConciliacionInsanorRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // =========================================
    // CONCILIACIÓN - LISTAR SUCURSALES
    // =========================================
    public List<Map<String, Object>> listarSucursales() {

        String sql = "EXEC sp_bart_ins_conciliacion_listar_sucursales";

        return jdbcTemplate.queryForList(sql);
    }

    // =========================================
    // CONCILIACIÓN - LISTAR FECHAS
    // =========================================
    public List<Map<String, Object>> listarFechas(
            String codigo,
            Integer mes,
            Integer anio
    ) {

        String sql = "EXEC sp_bart_ins_conciliacion_listar_fechas ?, ?, ?";

        return jdbcTemplate.queryForList(
                sql,
                codigo,
                mes,
                anio
        );
    }

    // =========================================
    // CONCILIACIÓN - LISTAR NETO TOTAL
    // =========================================
    public List<Map<String, Object>> listarNetoTotal(
            String codigo,
            String fechaAbono
    ) {

        String sql = "EXEC sp_bart_ins_conciliacion_listar_neto_total ?, ?";

        return jdbcTemplate.queryForList(
                sql,
                codigo,
                fechaAbono
        );
    }

    // =========================================
    // CONCILIACIÓN - EXISTE CONCILIADO
    // =========================================
    public List<Map<String, Object>> existeConciliado(
            String codigo,
            String fechaAbono,
            BigDecimal monto
    ) {

        String sql = "EXEC sp_bart_ins_conciliacion_existe_conciliado ?, ?, ?";

        return jdbcTemplate.queryForList(
                sql,
                codigo,
                fechaAbono,
                monto
        );
    }

    // =========================================
    // CONCILIACIÓN - COMPROBANTES CONCILIADOS CABECERA
    // =========================================
    public List<Map<String, Object>> listarComprobantesConciliadosCabecera(
            String codigo,
            String fechaAbono,
            BigDecimal monto
    ) {

        String sql = "EXEC sp_bart_ins_conciliacion_listar_comprobante_conciliados_cabecera ?, ?, ?";

        return jdbcTemplate.queryForList(
                sql,
                codigo,
                fechaAbono,
                monto
        );
    }

    // =========================================
    // CONCILIACIÓN - COMPROBANTES CONCILIADOS DETALLE
    // =========================================
    public List<Map<String, Object>> listarComprobantesConciliadosDetalle(
            Integer id
    ) {

        String sql = "EXEC sp_bart_ins_conciliacion_listar_comprobante_conciliados_detalle ?";

        return jdbcTemplate.queryForList(
                sql,
                id
        );
    }

    // =========================================
    // CONCILIACIÓN - COMPROBANTES NO CONCILIADOS
    // =========================================
    public List<Map<String, Object>> listarComprobantesNoConciliados(
            String codigo,
            String fechaAbono,
            BigDecimal netoTotal,
            Integer unidComId
    ) {

        String sql = "EXEC sp_bart_ins_conciliacion_listar_comprobantes_no_conciliados ?, ?, ?, ?";

        return jdbcTemplate.queryForList(
                sql,
                codigo,
                fechaAbono,
                netoTotal,
                unidComId
        );
    }

    // =========================================
    // CONCILIACIÓN - CONCILIAR
    // =========================================
    public List<Map<String, Object>> conciliar(
            String codigo,
            String fechaAbono,
            BigDecimal netoTotal,
            Integer unidComId
    ) {

        String sql = "EXEC sp_bart_ins_conciliacion_conciliar ?, ?, ?, ?";

        return jdbcTemplate.queryForList(
                sql,
                codigo,
                fechaAbono,
                netoTotal,
                unidComId
        );
    }

}
