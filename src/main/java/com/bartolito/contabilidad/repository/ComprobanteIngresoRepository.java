package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class ComprobanteIngresoRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;

    public List<Map<String, Object>> generarComprobanteIngreso(
            Map<String, Object> request
    ) throws Exception {


        // ============================================
        // FECHA
        // ============================================

        SimpleDateFormat formato =
                new SimpleDateFormat("dd/MM/yyyy");


        java.util.Date fechaUtil =
                formato.parse(
                        request.get("FechaIngreso").toString()
                );


        // ============================================
        // FIX: usar java.sql.Date explícitamente.
        //
        // Antes se usaba "Date" (java.util.Date, por el
        // import de arriba), y el driver JDBC lo enviaba a
        // SQL Server como el tipo moderno "datetime2", que
        // no calza con el parámetro @FechaIngreso DATETIME
        // del procedimiento -> "Error al convertir el tipo
        // de datos datetime2 a datetime".
        //
        // java.sql.Date sí se mapea correctamente a DATETIME.
        // ============================================

        java.sql.Date fechaSql =
                new java.sql.Date(
                        fechaUtil.getTime()
                );


        // ============================================
        // DEBUG
        // ============================================

        Object debug =
                request.get("Debug");


        if (debug == null) {

            debug = 0;

        }


        // ============================================
        // DATOS
        // ============================================

        Object empresaId =
                request.get("EmpresaId");


        Object ctaBancEmpId =
                request.get("CtaBancEmpId");


        Object monto =
                request.get("Monto");


        Object operacion =
                request.get("Operacion");


        Object glosa =
                request.get("Glosa");


        Object tipConCajId =
                request.get("TipConCajId");


        Object conCajId =
                request.get("ConCajId");


        Object usuario =
                request.get("Usuario");


        // ============================================
        // DEBUG CONSOLA
        // ============================================

        System.out.println(
                "===================================="
        );

        System.out.println(
                "GENERAR COMPROBANTE INGRESO"
        );

        System.out.println(
                "EmpresaId     : " +
                        empresaId
        );

        System.out.println(
                "CtaBancEmpId  : " +
                        ctaBancEmpId
        );

        System.out.println(
                "FechaIngreso  : " +
                        request.get("FechaIngreso")
        );

        System.out.println(
                "Monto         : " +
                        monto
        );

        System.out.println(
                "Operacion     : [" +
                        operacion +
                        "]"
        );

        System.out.println(
                "Glosa         : " +
                        glosa
        );

        System.out.println(
                "TipConCajId   : " +
                        tipConCajId
        );

        System.out.println(
                "ConCajId      : " +
                        conCajId
        );

        System.out.println(
                "Usuario       : " +
                        usuario
        );

        System.out.println(
                "Debug         : " +
                        debug
        );

        System.out.println(
                "===================================="
        );


        // ============================================
        // SQL
        // ============================================

        String sql =
                "EXEC sp_bart_ComprobanteIngreso_Generar " +

                        "@EmpresaId=?," +

                        "@CtaBancEmpId=?," +

                        "@FechaIngreso=?," +

                        "@Monto=?," +

                        "@Operacion=?," +

                        "@Glosa=?," +

                        "@TipConCajId=?," +

                        "@ConCajId=?," +

                        "@Usuario=?," +

                        "@Debug=?";


        // ============================================
        // EJECUTAR SP
        // ============================================

        return sigoldJdbc.queryForList(

                sql,

                empresaId,

                ctaBancEmpId,

                fechaSql,

                monto,

                operacion,

                glosa,

                tipConCajId,

                conCajId,

                usuario,

                debug

        );

    }

}