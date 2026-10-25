package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SolicitudPagoRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;

    // =========================================
// LISTAR
// =========================================
    public List<Map<String, Object>> listarSolicitudPago(
            String empresaId,
            String monedaId,
            String fechaInicio,
            String fechaFin,
            String estado,
            String usuario
    ) {

        // =====================================
        // NORMALIZAR PARÁMETROS
        // =====================================

        empresaId = (empresaId != null && !empresaId.isBlank())
                ? empresaId.trim()
                : null;

        monedaId = (monedaId != null && !monedaId.isBlank())
                ? monedaId.trim()
                : null;

        fechaInicio = (fechaInicio != null && !fechaInicio.isBlank())
                ? fechaInicio.trim()
                : null;

        fechaFin = (fechaFin != null && !fechaFin.isBlank())
                ? fechaFin.trim()
                : null;

        estado = (estado != null && !estado.isBlank())
                ? estado.trim()
                : null;

        usuario = (usuario != null && !usuario.isBlank())
                ? usuario.trim()
                : null;


        // =====================================
        // DEBUG
        // =====================================

        System.out.println("====================================");
        System.out.println("LISTAR SOLICITUD PAGO");
        System.out.println("EmpresaId   : [" + empresaId + "]");
        System.out.println("MonedaId    : [" + monedaId + "]");
        System.out.println("FechaInicio : [" + fechaInicio + "]");
        System.out.println("FechaFin    : [" + fechaFin + "]");
        System.out.println("Estado      : [" + estado + "]");
        System.out.println("Usuario     : [" + usuario + "]");
        System.out.println("====================================");


        // =====================================
        // SQL
        // =====================================

        String sql =
                "EXEC dbo.sp_bart_SolicitudPago_Listar ?, ?, ?, ?, ?, ?";


        return sigoldJdbc.queryForList(
                sql,
                empresaId,
                monedaId,
                fechaInicio,
                fechaFin,
                estado,
                usuario
        );
    }
    // =========================================
    // REGISTRAR
    // =========================================


    public Map<String, Object> registrarSolicitudPago(

            String empresaId,

            Integer tipSolPagoId,

            Integer tipConCajId,

            Integer conCajId,

            Long ctaCteId,

            Long provId,

            String trabId,

            String trabIdSoli,

            String descripcion,

            String fecha,

            String documento,

            String archivo1,

            String archivo2,

            BigDecimal total,

            String procId,

            String activId,

            String tareaId,

            String activoId,

            String prodId,

            String cenCostResp,

            String usuario
    ) {

        try {

            // =====================================
            // VALIDACIONES
            // =====================================

            descripcion =
                    (descripcion == null || descripcion.isBlank())
                            ? ""
                            : descripcion.trim();

            documento =
                    (documento == null || documento.isBlank())
                            ? null
                            : documento.trim();

            archivo1 =
                    (archivo1 == null || archivo1.isBlank())
                            ? null
                            : archivo1.trim();

            archivo2 =
                    (archivo2 == null || archivo2.isBlank())
                            ? null
                            : archivo2.trim();

            trabId =
                    (trabId == null || trabId.isBlank())
                            ? null
                            : trabId.trim();

            trabIdSoli =
                    (trabIdSoli == null || trabIdSoli.isBlank())
                            ? null
                            : trabIdSoli.trim();

            procId =
                    (procId == null || procId.isBlank())
                            ? null
                            : procId.trim();

            activId =
                    (activId == null || activId.isBlank())
                            ? null
                            : activId.trim();

            tareaId =
                    (tareaId == null || tareaId.isBlank())
                            ? null
                            : tareaId.trim();

            activoId =
                    (activoId == null || activoId.isBlank())
                            ? null
                            : activoId.trim();

            prodId =
                    (prodId == null || prodId.isBlank())
                            ? null
                            : prodId.trim();

            cenCostResp =
                    (cenCostResp == null || cenCostResp.isBlank())
                            ? null
                            : cenCostResp.trim();

            // =====================================
            // SQL
            // =====================================

            String sql =
                    "EXEC sp_bart_SolicitudPago_Registrar "
                            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";

            List<Map<String, Object>> resultado =
                    sigoldJdbc.queryForList(
                            sql,

                            empresaId,

                            tipSolPagoId,

                            tipConCajId,

                            conCajId,

                            ctaCteId,

                            provId,

                            trabId,

                            trabIdSoli,

                            descripcion,

                            fecha,

                            documento,

                            archivo1,

                            archivo2,

                            total,

                            procId,

                            activId,

                            tareaId,

                            activoId,

                            prodId,

                            cenCostResp,

                            usuario
                    );

            return resultado.isEmpty()
                    ? Map.of(
                    "Resultado", 0,
                    "Mensaje", "Sin respuesta"
            )
                    : resultado.get(0);

        } catch (Exception e) {

            return Map.of(
                    "Resultado", 0,
                    "Mensaje", e.getMessage()
            );
        }

    }

    // =========================================
    // ANULAR
    // =========================================
    public Map<String, Object> anularSolicitudPago(
            Long soliPagoCabId,
            String usuario
    ) {

        String sql = "EXEC sp_bart_SolicitudPago_Anular ?, ?";

        return sigoldJdbc.queryForMap(
                sql,
                soliPagoCabId,
                usuario
        );
    }

    // ==========================================
// REPOSITORY
// ==========================================

    public List<Map<String, Object>> listarTipoSolicitudPago() {

        String sql = "EXEC sp_bart_TipoSolicitudPago_Listar";

        return sigoldJdbc.queryForList(sql);

    }

    // ==========================================
// LISTAR PROVEEDORES
// ==========================================

    public List<Map<String, Object>> listarProveedor() {

        String sql = "EXEC sp_bart_Proveedor_Listar";

        return sigoldJdbc.queryForList(sql);

    }
    // =========================================
// REPOSITORY
// =========================================

    public List<Map<String, Object>> listarConceptoCaja(
            Integer tipConCajID,
            Integer tipSolCajId
    ) {

        String sql =
                "EXEC sp_bart_ConceptoCaja_Listar ?, ?";

        return sigoldJdbc.queryForList(
                sql,
                tipConCajID,
                tipSolCajId
        );

    }

// ==========================================
// LISTAR TRABAJADORES
// ==========================================

    public List<Map<String, Object>> listarTrabajador(
            String empresaId
    ) {

        String sql =
                "EXEC sp_bart_Trabajador_Listar ?";

        return sigoldJdbc.queryForList(
                sql,
                empresaId
        );

    }

    // =========================================
// REPOSITORY
// =========================================

    public List<Map<String, Object>> listarDepartamentoFuncional() {

        String sql =
                "EXEC sp_bart_DepartamentoFuncional_Listar";

        return sigoldJdbc.queryForList(sql);

    }

    // =========================================
// OBTENER SOLICITUD
// =========================================
    public Map<String, Object> obtenerSolicitudPago(
            Long soliPagoCabId
    ) {

        Map<String, Object> response =
                new HashMap<>();

        // =====================================
        // CABECERA
        // =====================================

        String sqlCab =
                "EXEC sp_bart_SolicitudPago_Obtener ?";

        List<Map<String, Object>> cabecera =
                sigoldJdbc.queryForList(
                        sqlCab,
                        soliPagoCabId
                );

        // =====================================
        // DETALLE
        // =====================================

        String sqlDet =
                "EXEC sp_bart_SolicitudPagoDetalle_Listar ?";

        List<Map<String, Object>> detalle =
                sigoldJdbc.queryForList(
                        sqlDet,
                        soliPagoCabId
                );

        // =====================================
        // RESPONSE
        // =====================================

        response.put(
                "cabecera",
                cabecera.isEmpty()
                        ? null
                        : cabecera.get(0)
        );

        response.put(
                "detalle",
                detalle
        );

        return response;
    }

}
