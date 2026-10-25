package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Repository
public class CentroCostoRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;

    public List<Map<String, Object>> listarProcesos() {
        String sql = "EXEC sp_bart_Proceso_listar";
        return sigoldJdbc.queryForList(sql);
    }

    public List<Map<String, Object>> listarActividades() {
        String sql = "EXEC sp_bart_Actividad_listar";
        return sigoldJdbc.queryForList(sql);
    }

    public List<Map<String, Object>> listarTareas() {
        String sql = "EXEC sp_bart_Tarea_listar";
        return sigoldJdbc.queryForList(sql);
    }

    public List<Map<String, Object>> listarProductosServicios() {
        String sql = "EXEC sp_bart_ProductoServicio_listar";
        return sigoldJdbc.queryForList(sql);
    }

    public List<Map<String, Object>> listarCentrosCostoResponsabilidad() {
        String sql = "EXEC sp_bart_CentroCostoResponsabilidad_listar";
        return sigoldJdbc.queryForList(sql);
    }
    public List<Map<String, Object>> listarActivos() {
        String sql = "EXEC sp_bart_Activo_listar";
        return sigoldJdbc.queryForList(sql);
    }

    public List<Map<String, Object>> listarCentrosCosto() {
        String sql = "EXEC sp_bart_CentroCosto_listar";
        return sigoldJdbc.queryForList(sql);
    }
}
