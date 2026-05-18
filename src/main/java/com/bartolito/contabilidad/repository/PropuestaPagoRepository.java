package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class PropuestaPagoRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;

    public List<Map<String, Object>> listarPropuestaPagoCabecera(
            String empresaId,
            String estado
    ) {

        String sql = "EXEC sp_bart_PropuestaPagoCabecera_Listar ?, ?";

        return sigoldJdbc.queryForList(
                sql,
                empresaId,
                estado
        );
    }

    public Map<String, Object> generarPropuestaPago(
            String empresaId,
            BigDecimal total,
            String usuario,
            String detalleXml
    ) {

        String sql = "EXEC sp_bart_PropuestaPago_Generar ?, ?, ?, ?";

        return sigoldJdbc.queryForMap(
                sql,
                empresaId,
                total,
                usuario,
                detalleXml
        );
    }
}
