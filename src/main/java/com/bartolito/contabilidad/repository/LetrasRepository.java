package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.*;

@Repository
public class LetrasRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;

    public List<Map<String, Object>> listarLetrasPendientes(String empresaId, String monedaId, String fechaVencimiento) {
        return sigoldJdbc.queryForList("EXEC sp_bart_PropuestaPago_ListarLetrasPendientes ?, ?, ?",
                empresaId, monedaId, fechaVencimiento);
    }

    public List<Map<String, Object>> obtenerPreAsiento(String empresaId, String numeroCuenta, String detalleXml) {
        return sigoldJdbc.queryForList("EXEC sp_bart_Factory_ObtenerPreAsiento ?,?,?",
                empresaId, numeroCuenta, detalleXml);
    }

    public Map<String, Object> registrar(String empresaId, Integer ctaBancEmpId, java.sql.Date fechaPago,
                                         String glosa, String usuario, String detalleXml) {
        SimpleJdbcCall call = new SimpleJdbcCall(sigoldJdbc)
                .withProcedureName("sp_bart_ComprobanteEgreso_Letras_Generar")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("EmpresaId", Types.CHAR),
                        new SqlParameter("CtaBancEmpId", Types.INTEGER),
                        new SqlParameter("FechaPago", Types.DATE),
                        new SqlParameter("Glosa", Types.VARCHAR),
                        new SqlParameter("Usuario", Types.VARCHAR),
                        new SqlParameter("DetalleXml", Types.LONGVARCHAR),
                        new SqlParameter("Debug", Types.TINYINT));

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("EmpresaId", empresaId);
        parametros.put("CtaBancEmpId", ctaBancEmpId);
        parametros.put("FechaPago", fechaPago);
        parametros.put("Glosa", glosa);
        parametros.put("Usuario", usuario);
        parametros.put("DetalleXml", detalleXml);
        parametros.put("Debug", (byte) 0);
        Map<String, Object> resultado = call.execute(parametros);

        System.out.println("========== RESULTADO FACTORY ==========");

        resultado.forEach((clave, valor) -> {
            System.out.println("CLAVE: " + clave);
            System.out.println("VALOR: " + valor);
        });

        System.out.println("======================================");

        return resultado;
    }

    public List<Map<String, Object>> obtenerCorreosReenvioValidados(Integer propuestaId) {
        if (propuestaId == null || propuestaId <= 0) {
            throw new IllegalArgumentException("Propuesta inválida.");
        }
        return sigoldJdbc.queryForList("EXEC dbo.sp_bart_PropuestaPago_Correos_Reenvio_Validar ?", propuestaId);
    }

    public List<Map<String, Object>> buscarPropuesta(String empresaId, BigDecimal total, String detalle) {
        return sigoldJdbc.queryForList("EXEC dbo.sp_bart_PropuestaPago_BuscarDesdeBCP ?, ?, ?",
                empresaId, total, detalle);
    }

    public List<Map<String, Object>> obtenerCorreosAutomaticos(Integer propPagoCabId) {
        if (propPagoCabId == null || propPagoCabId <= 0) {
            throw new IllegalArgumentException("PropPagoCabId inválido para envío automático FACTORY.");
        }
        return sigoldJdbc.queryForList("EXEC dbo.sp_bart_PropuestaPago_Correos_Obtener ?, ?",
                propPagoCabId, null);
    }

    // Solo lectura: confirma cuáles ítems están efectivamente ejecutados.
    public Set<Integer> obtenerItemsEjecutadosFactory(Integer propPagoCabId) {
        if (propPagoCabId == null || propPagoCabId <= 0) {
            throw new IllegalArgumentException("Propuesta inválida.");
        }
        List<Integer> items = sigoldJdbc.queryForList(
                "SELECT PropPagoDetItem FROM dbo.PropuestaPagoDetalle " +
                        "WHERE PropPagoCabId = ? AND PropPagoDetEstado = 'E'",
                Integer.class, propPagoCabId);
        return new HashSet<>(items);
    }
}
