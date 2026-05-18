package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public class ComprobanteCompraRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;


    public List<Map<String, Object>> listarComprobantesCompra(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            String empresaId,
            Boolean estadoPago
    ) {

        String sql = "EXEC sp_bart_conta_comprobanteCompraCabecera_x_empresa_listar ?, ?, ?, ?";

        return sigoldJdbc.queryForList(
                sql,
                java.sql.Date.valueOf(fechaInicio),
                java.sql.Date.valueOf(fechaFin),
                empresaId,
                estadoPago
        );
    }
    public List<Map<String, Object>> listarComprobantesCompraxPagar(
            LocalDate fechaVenc,
            String empresaId
    ) {

        String sql = "EXEC sp_bart_conta_comprobanteCompraCabecera_x_empresa_porpagar_listar ?, ?";

        List<Map<String, Object>> lista = sigoldJdbc.queryForList(
                sql,
                java.sql.Date.valueOf(fechaVenc),
                empresaId
        );

        // 🔥 convertir IDs grandes a String
        lista.forEach(item -> {

            Object comCompCabId = item.get("ComCompCabId");

            if (comCompCabId != null) {
                item.put("ComCompCabId", comCompCabId.toString());
            }

        });

        return lista;
    }
    public List<Map<String, Object>> listarNCyND(
            String empresaId,
            Long provId
    ) {

        String sql = "EXEC sp_bart_conta_comprobanteCompraCabecera_NCyND_listar ?, ?";

        return sigoldJdbc.queryForList(
                sql,
                empresaId,
                provId
        );
    }

    public List<Map<String, Object>> listarAdelantoComprobante(
            String empresaId,
            Long provId,
            String adeCompMovRef
    ) {

        String sql = "EXEC sp_bart_conta_comprobante_adelanto_listar ?, ?, ?";

        return sigoldJdbc.queryForList(
                sql,
                provId,
                adeCompMovRef,
                empresaId
        );
    }

}
