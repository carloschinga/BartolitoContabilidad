package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;
import java.util.List;

@Repository
public class EmpresaRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;

    public List<Map<String, Object>> listarEmpresas() {
        String sql = "EXEC sp_bart_conta_empresa_listar";
        return sigoldJdbc.queryForList(sql);
    }

}
