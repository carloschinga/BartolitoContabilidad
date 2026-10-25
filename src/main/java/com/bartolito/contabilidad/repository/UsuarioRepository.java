package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Repository
public class UsuarioRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;

    public List<Map<String, Object>> listarUsuariosLolfar(
            Integer usecodLOLFAR
    ) {

        String sql =
                "EXEC sp_bart_usuarios_lolfar_listar ?";

        return sigoldJdbc.queryForList(
                sql,
                usecodLOLFAR
        );

    }
    public List<Map<String, Object>> obtenerEquivalenciaUsuarioLolfar(
            String usecodLOLFAR
    ) {

        String sql =
                "EXEC sp_bart_UsuarioLolfar_ObtenerEquivalenciaSIGOLD ?";


        return sigoldJdbc.queryForList(
                sql,
                usecodLOLFAR
        );

    }
}
