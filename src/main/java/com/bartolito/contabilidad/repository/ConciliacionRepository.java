package com.bartolito.contabilidad.repository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.Query;

@Repository
public class ConciliacionRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;

    public String obtenerSucursalesConciliacion() {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_bart_conciliacion_sel_sucurzal");
        return (String) query.getSingleResult(); // devuelve directamente el JSON
    }
    public List<Map<String, Object>> obtenerMesesConciliacion(String codigo) {
        String sql = "EXEC sp_bart_conciliacion_sel_meses ?";
        return sigoldJdbc.queryForList(sql, codigo);
    }
    public List<Map<String, Object>> obtenerOperacionesConciliacion(String codigo, String mes) {
        String sql = "EXEC sp_bart_conciliacion_sel_operaciones ?,? ";
        return sigoldJdbc.queryForList(sql, codigo,mes);
    }
    public List<Map<String, Object>> obtenerComprobantesConciliacion(String codigo,String fechaAbono,Double netoTotal,Integer unidComId) {
        String sql = "EXEC sp_bart_conciliacion_sel_tarjetasizipay_comprobante_grilla ?, ?, ?, ?";

        return sigoldJdbc.queryForList(sql,codigo,fechaAbono,netoTotal,unidComId);
    }

    public String obtenerFechasConciliacion(String sucursal) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_bart_conciliacion_sel_fechas");

        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.setParameter(1, sucursal);

        List<Object> resultados = query.getResultList(); // ✅ múltiples filas

        JSONArray json = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Object obj : resultados) {
            JSONObject jsonObj = new JSONObject();
            String fechaFormateada;

            if (obj instanceof Date) {
                fechaFormateada = sdf.format((Date) obj);
            } else if (obj instanceof java.sql.Date) {
                fechaFormateada = sdf.format((java.sql.Date) obj);
            } else if (obj instanceof java.sql.Timestamp) {
                fechaFormateada = sdf.format(new Date(((java.sql.Timestamp) obj).getTime()));
            } else {
                fechaFormateada = obj.toString();
            }

            jsonObj.put("fecha", fechaFormateada);
            json.put(jsonObj);
        }

        JSONObject result = new JSONObject();
        result.put("Fechas", json);
        return result.toString();
    }

    public String obtenerNetoTotal(String sucursal, String fechaAbono) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_bart_conciliacion_sel_netototal");

        // Registrar parámetros del SP
        query.registerStoredProcedureParameter("Codigo", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("Fecha_Abono", java.sql.Date.class, ParameterMode.IN);

        // Asignar valores
        query.setParameter("Codigo", sucursal);
        query.setParameter("Fecha_Abono", java.sql.Date.valueOf(fechaAbono)); // Convierte String a java.sql.Date

        // Ejecutar y devolver resultado como String
        return (String) query.getSingleResult();
    }

    public String obtenerConciliadoExiste(String codigo, Date fechaAbono, BigDecimal monto) {
        // Crear la consulta al procedimiento almacenado
        StoredProcedureQuery query = em.createStoredProcedureQuery("sp_bart_conciliacion_sel_conciliado_existe");

        // Registrar los parámetros de entrada
        query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, BigDecimal.class, ParameterMode.IN);

        // Asignar valores a los parámetros
        query.setParameter(1, codigo);
        query.setParameter(2, fechaAbono);
        query.setParameter(3, monto);

        // Ejecutar el procedimiento y obtener el resultado
        Object resultado = query.getSingleResult(); // Retorna un JSON string

        // Si el SP devuelve directamente JSON (FOR JSON PATH), podemos retornarlo tal cual
        // Para asegurarnos de que sea un JSONObject
        JSONObject resultJson;
        if (resultado instanceof String) {
            resultJson = new JSONObject((String) resultado);
        } else {
            resultJson = new JSONObject();
            resultJson.put("Existe", resultado);
        }

        return resultJson.toString();
    }

    public String conciliarOperacion(String codigo, Date fechaAbono, BigDecimal netoTotal, int unidComId) {
        try {
            // Crear la consulta al procedimiento almacenado
            StoredProcedureQuery query = em.createStoredProcedureQuery("sp_bart_conciliacion_sel_conciliar");

            // Registrar los parámetros de entrada
            query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);     // @Codigo
            query.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);       // @FechaAbono
            query.registerStoredProcedureParameter(3, BigDecimal.class, ParameterMode.IN); // @NetoTotal
            query.registerStoredProcedureParameter(4, Integer.class, ParameterMode.IN);    // @UnidComId

            // Asignar valores a los parámetros
            query.setParameter(1, codigo);
            query.setParameter(2, fechaAbono);
            query.setParameter(3, netoTotal);
            query.setParameter(4, unidComId);

            // Ejecutar el procedimiento y obtener el resultado
            // Ahora el SP devuelve directamente un JSON como String
            String resultadoJson = (String) query.getSingleResult();

            // Retornar el JSON tal cual
            return resultadoJson;

        } catch (Exception e) {
            // En caso de error, retornar JSON con estado -1 y mensaje
            JSONObject errorJson = new JSONObject();
            errorJson.put("estado", -1);
            errorJson.put("mensaje", "Error al conciliar: " + e.getMessage());
            return errorJson.toString();
        }
    }


    public String obtenerNoConciliados(String codigo, Date fechaAbono, BigDecimal netoTotal, int unidComId) {
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery(
                    "sp_bart_conciliacion_sel_tarjetasizipay_comprobante_no_conciliados"
            );

            query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(3, BigDecimal.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(4, Integer.class, ParameterMode.IN);

            query.setParameter(1, codigo);
            query.setParameter(2, fechaAbono);
            query.setParameter(3, netoTotal);
            query.setParameter(4, unidComId);

            // Ejecuta y obtiene todos los resultados (posibles fragmentos)
            query.execute();
            @SuppressWarnings("unchecked")
            List<Object> rows = query.getResultList();

            if (rows == null || rows.isEmpty()) {
                return "{\"noconciliados\":[]}";
            }

            // Concatenar todos los fragmentos en un solo StringBuilder
            StringBuilder sb = new StringBuilder();
            for (Object row : rows) {
                if (row == null) continue;
                String part = row.toString();
                // limpiar caracteres nulos invisibles
                part = part.replace("\u0000", "");
                sb.append(part);
            }

            String json = sb.toString().trim();

            // Reparación defensiva: mantener desde la primera '{' hasta la última '}' (evita truncos)
            int first = json.indexOf('{');
            int last = json.lastIndexOf('}');
            if (first >= 0 && last > first) {
                json = json.substring(first, last + 1);
            }

            // opcional: log pequeño para depurar si sigue fallando
            // System.out.println("JSON length=" + json.length());
            // System.out.println("JSON head=" + json.substring(0, Math.min(200, json.length())));

            return json;

        } catch (Exception e) {
            JSONObject errorJson = new JSONObject();
            errorJson.put("estado", -1);
            errorJson.put("mensaje", "Error al obtener no conciliados: " + e.getMessage());
            return errorJson.toString();
        }
    }

    public String obtenerConciliadosCabecera(String codigo, Date fechaAbono, BigDecimal monto) {
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery(
                    "sp_bart_conciliacion_sel_tarjetasizipay_comprobante_conciliados_cabecera"
            );

            query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(3, BigDecimal.class, ParameterMode.IN);

            query.setParameter(1, codigo);
            query.setParameter(2, fechaAbono);
            query.setParameter(3, monto);

            query.execute();

            @SuppressWarnings("unchecked")
            List<Object> rows = query.getResultList();

            if (rows == null || rows.isEmpty()) {
                return "{\"conciliados_cabecera\":[]}";
            }

            StringBuilder sb = new StringBuilder();
            for (Object row : rows) {
                if (row == null) continue;
                String part = row.toString();
                part = part.replace("\u0000", ""); // limpiar nulos invisibles
                sb.append(part);
            }

            String json = sb.toString().trim();

            // reparo defensivo: tomar solo desde la primera { hasta la última }
            int first = json.indexOf('{');
            int last = json.lastIndexOf('}');
            if (first >= 0 && last > first) {
                json = json.substring(first, last + 1);
            }

            return json;

        } catch (Exception e) {
            JSONObject errorJson = new JSONObject();
            errorJson.put("estado", -1);
            errorJson.put("mensaje", "Error al obtener conciliados cabecera: " + e.getMessage());
            return errorJson.toString();
        }
    }

    public String obtenerConciliadosDetalle(String codigo, Date fechaAbono, BigDecimal monto) {
        try {
            StoredProcedureQuery query = em.createStoredProcedureQuery(
                    "sp_bart_conciliacion_sel_tarjetasizipay_comprobante_conciliados_detalle"
            );

            query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(2, Date.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(3, BigDecimal.class, ParameterMode.IN);

            query.setParameter(1, codigo);
            query.setParameter(2, fechaAbono);
            query.setParameter(3, monto);

            query.execute();

            @SuppressWarnings("unchecked")
            List<Object> rows = query.getResultList();

            if (rows == null || rows.isEmpty()) {
                return "{\"conciliados_cabecera\":[]}";
            }

            StringBuilder sb = new StringBuilder();
            for (Object row : rows) {
                if (row == null) continue;
                String part = row.toString();
                part = part.replace("\u0000", ""); // limpiar nulos invisibles
                sb.append(part);
            }

            String json = sb.toString().trim();

            // reparo defensivo: tomar solo desde la primera { hasta la última }
            int first = json.indexOf('{');
            int last = json.lastIndexOf('}');
            if (first >= 0 && last > first) {
                json = json.substring(first, last + 1);
            }

            return json;

        } catch (Exception e) {
            JSONObject errorJson = new JSONObject();
            errorJson.put("estado", -1);
            errorJson.put("mensaje", "Error al obtener conciliados cabecera: " + e.getMessage());
            return errorJson.toString();
        }
    }

    public List<Map<String, Object>> reporteConciliacionTarjetasIzipay(String codigo, Date fechaIni, Date fechaFin) {

        String sql = "EXEC sp_bart_conciliacion_sel_tarjetasizipay_comprobante_reporte ?, ?, ?";

        return sigoldJdbc.queryForList(sql, codigo, fechaIni, fechaFin);
    }

}
