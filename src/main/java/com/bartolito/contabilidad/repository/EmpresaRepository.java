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



    // ============================================================
    // MANTENIMIENTO PROVEEDORES CUARTA CATEGORIA
    // ============================================================

    // LISTAR
    public List<Map<String, Object>> listarProveedoresCuartaCategoria(
            String filtro) {

        String sql =
                "EXEC dbo.sp_bart_ProveedorCuartaCategoria_Listar @Filtro = ?";

        String filtroLimpio =
                (filtro == null || filtro.trim().isEmpty())
                        ? null
                        : filtro.trim();

        return sigoldJdbc.queryForList(sql, filtroLimpio);
    }

    // AGREGAR

    public Map<String, Object> agregarProveedorCuartaCategoria(
            Long provId,
            Integer regPenId,
            Integer tipComAfpId,
            java.sql.Date provFecNac,
            String provNumAFP,
            String provNumDoc,
            Integer provEstado,
            String usuario) {

        return ejecutarGuardarProveedorCuartaCategoria(
                "dbo.sp_bart_ProveedorCuartaCategoria_Agregar",
                provId,
                regPenId,
                tipComAfpId,
                provFecNac,
                provNumAFP,
                provNumDoc,
                provEstado,
                usuario
        );
    }

    // MODIFICAR


    public Map<String, Object> modificarProveedorCuartaCategoria(
            Long provId,
            Integer regPenId,
            Integer tipComAfpId,
            java.sql.Date provFecNac,
            String provNumAFP,
            String provNumDoc,
            Integer provEstado,
            String usuario) {

        return ejecutarGuardarProveedorCuartaCategoria(
                "dbo.sp_bart_ProveedorCuartaCategoria_Modificar",
                provId,
                regPenId,
                tipComAfpId,
                provFecNac,
                provNumAFP,
                provNumDoc,
                provEstado,
                usuario
        );
    }


    private Map<String, Object> ejecutarGuardarProveedorCuartaCategoria(
            String procedimiento,
            Long provId,
            Integer regPenId,
            Integer tipComAfpId,
            java.sql.Date provFecNac,
            String provNumAFP,
            String provNumDoc,
            Integer provEstado,
            String usuario) {

        // Solo se permite ejecutar los dos SP conocidos.
        if (!"dbo.sp_bart_ProveedorCuartaCategoria_Agregar".equals(procedimiento)
                && !"dbo.sp_bart_ProveedorCuartaCategoria_Modificar".equals(procedimiento)) {
            throw new IllegalArgumentException("Procedimiento no permitido.");
        }

        String sql = "{call " + procedimiento + "(?,?,?,?,?,?,?,?)}";

        return sigoldJdbc.execute(
                (java.sql.Connection con) -> {

                    java.sql.CallableStatement cs = con.prepareCall(sql);

                    if (provId == null) {
                        cs.setNull(1, java.sql.Types.BIGINT);
                    } else {
                        cs.setLong(1, provId);
                    }

                    if (regPenId == null) {
                        cs.setNull(2, java.sql.Types.INTEGER);
                    } else {
                        cs.setInt(2, regPenId);
                    }

                    if (tipComAfpId == null) {
                        cs.setNull(3, java.sql.Types.INTEGER);
                    } else {
                        cs.setInt(3, tipComAfpId);
                    }

                    if (provFecNac == null) {
                        cs.setNull(4, java.sql.Types.DATE);
                    } else {
                        cs.setDate(4, provFecNac);
                    }

                    cs.setString(5, provNumAFP);
                    cs.setString(6, provNumDoc);

                    if (provEstado == null) {
                        cs.setNull(7, java.sql.Types.INTEGER);
                    } else {
                        cs.setInt(7, provEstado);
                    }

                    cs.setString(8, usuario);

                    return cs;
                },
                (java.sql.CallableStatement cs) -> {

                    boolean tieneResultado = cs.execute();

                    while (true) {

                        if (tieneResultado) {

                            try (java.sql.ResultSet rs = cs.getResultSet()) {

                                if (rs != null && rs.next()) {

                                    Map<String, Object> resultado =
                                            new java.util.LinkedHashMap<>();

                                    resultado.put("estado", rs.getObject("estado"));
                                    resultado.put("mensaje", rs.getString("mensaje"));

                                    return resultado;
                                }
                            }
                        }

                        int filasActualizadas = cs.getUpdateCount();

                        if (!tieneResultado && filasActualizadas == -1) {
                            break;
                        }

                        tieneResultado = cs.getMoreResults();
                    }

                    throw new java.sql.SQLException(
                            "El procedimiento no devolvio el resultado esperado."
                    );
                }
        );
    }


}
