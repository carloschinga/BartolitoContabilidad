package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Repository
public class FondoFijoRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;

    public List<Map<String, Object>> listarReposicionFondoFijoPendientes(
            String empresaId,
            String monedaId,
            String fechaEmision) {

        String sql =
                "EXEC sp_bart_PropuestaPago_ListarReposicionFondoFijoPendientes ?, ?, ?";

        return sigoldJdbc.queryForList(
                sql,
                empresaId,
                monedaId,
                fechaEmision
        );
    }
}
