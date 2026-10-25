package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Repository
public class MovimientoBancoRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;

    public List<Map<String, Object>> listarMovimientoBanco(
            String empresaId,
            String periodo,
            String movimiento
    ) {
        String sql =
                "EXEC sp_bart_MovimientoBanco_Listar ?, ?, ?";

        return sigoldJdbc.queryForList(
                sql,
                empresaId,
                periodo,
                movimiento
        );


    }
    public List<Map<String, Object>> obtenerComprobante(
            Long diaCabCompId
    ) {

        String sql =
                "EXEC sp_bart_MovimientoBanco_Comprobante_Obtener ?";

        return sigoldJdbc.queryForList(
                sql,
                diaCabCompId
        );

    }

}
