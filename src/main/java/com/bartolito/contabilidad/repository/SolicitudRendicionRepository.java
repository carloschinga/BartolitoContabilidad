package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SolicitudRendicionRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;

    public List<Map<String, Object>> listarSolicitudRendicionPendientes(
            String empresaId,
            String monedaId,
            String fechaEmision) {

        String sql =
                "EXEC sp_bart_PropuestaPago_ListarSolicitudRendicionPendientes ?, ?, ?";

        return sigoldJdbc.queryForList(
                sql,
                empresaId,
                monedaId,
                fechaEmision
        );
    }

}
