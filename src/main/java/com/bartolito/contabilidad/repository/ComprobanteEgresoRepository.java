package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class ComprobanteEgresoRepository {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private JdbcTemplate sigoldJdbc;

    public List<Map<String, Object>> obtenerComprobanteEgreso(String cuenta, String periodo, String diario) {
        return sigoldJdbc.queryForList("EXEC sp_bart_ComprobanteEgreso_Obtener ?, ?, ?", cuenta, periodo, diario);
    }
    public List<Map<String, Object>> obtenerCuentaBancaria(String cuentaBanco) {
        return sigoldJdbc.queryForList("EXEC sp_bart_CuentaBancariaEmpresa_Obtener ?", cuentaBanco);
    }
    public List<Map<String, Object>> buscarPropuestaDesdeBCP(String empresaId, BigDecimal total, String detalle) {
        return sigoldJdbc.queryForList("EXEC sp_bart_PropuestaPago_BuscarDesdeBCP ?, ?, ?", empresaId, total, detalle);
    }
    public List<Map<String, Object>> buscarPropuestaHaberes(String empresaId, BigDecimal total) {
        return sigoldJdbc.queryForList("EXEC sp_bart_PropuestaPagoHaberes_Buscar ?, ?", empresaId, total);
    }
    public List<Map<String, Object>> obtenerDetallePropuesta(Integer propPagoCabId) {
        return sigoldJdbc.queryForList("EXEC sp_bart_PropuestaPagoDetalle_Obtener ?", propPagoCabId);
    }
    public List<Map<String, Object>> generarComprobanteEgreso(Map<String, Object> request) throws Exception {
        String[] obligatorios = {"PropPagoCabId", "CtaBancEmpId", "FechaPago", "Glosa", "Usuario", "NumeroPlanilla", "DetalleBancoXml"};
        for (String campo : obligatorios) {
            if (request.get(campo) == null || request.get(campo).toString().trim().isEmpty()) {
                throw new IllegalArgumentException("Falta parámetro obligatorio: " + campo);
            }
        }
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        formato.setLenient(false);
        java.util.Date fechaUtil = formato.parse(request.get("FechaPago").toString());
        java.sql.Timestamp fechaSql = new java.sql.Timestamp(fechaUtil.getTime());
        String sql = "SET NOCOUNT ON; " +
                "DECLARE @xml XML; " +
                "SET @xml = CONVERT(XML, ?); " +
                "EXEC dbo.sp_bart_ComprobanteEgreso_Generar " +
                "@PropPagoCabId=?, " +
                "@CtaBancEmpId=?, " +
                "@FechaPago=?, " +
                "@Glosa=?, " +
                "@Usuario=?, " +
                "@NumeroPlanilla=?, " +
                "@DetalleBancoXml=@xml, " +
                "@Debug=0";
        return sigoldJdbc.queryForList(sql,
                request.get("DetalleBancoXml").toString(),
                Long.valueOf(request.get("PropPagoCabId").toString()),
                Integer.valueOf(request.get("CtaBancEmpId").toString()),
                fechaSql,
                request.get("Glosa").toString(),
                request.get("Usuario").toString(),
                request.get("NumeroPlanilla").toString());
    }
    public List<Map<String, Object>> obtenerCorreosPropuesta(Integer propPagoCabId, Long movBcoCabId) {
        return sigoldJdbc.queryForList("EXEC sp_bart_PropuestaPago_Correos_Obtener ?, ?", propPagoCabId, movBcoCabId);
    }

    // Solo lectura: devuelve un movimiento real por cada comprobante ejecutado.
    public List<Map<String, Object>> obtenerCorreosReenvioValidados(Integer propPagoCabId) {
        if (propPagoCabId == null || propPagoCabId <= 0) {
            throw new IllegalArgumentException("PropPagoCabId invalido.");
        }
        return sigoldJdbc.queryForList(
                "EXEC dbo.sp_bart_PropuestaPago_Correos_Reenvio_Validar ?", propPagoCabId);
    }
    // Resumen solo lectura para la previsualización del reenvío.
    public List<Map<String, Object>> obtenerResumenEstadosReenvio(Integer propPagoCabId) {
        if (propPagoCabId == null || propPagoCabId <= 0) {
            throw new IllegalArgumentException("PropPagoCabId invalido.");
        }
        return sigoldJdbc.queryForList(
                "SELECT PropPagoDetEstado AS Estado, COUNT(*) AS Cantidad " +
                        "FROM dbo.PropuestaPagoDetalle WHERE PropPagoCabId = ? " +
                        "GROUP BY PropPagoDetEstado", propPagoCabId);
    }
}
