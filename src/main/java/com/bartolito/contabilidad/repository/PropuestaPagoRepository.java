package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
            String estado,
            String monedaId,
            String fechaInicio,
            String fechaFin
    ) {

        String sql =
                "EXEC sp_bart_PropuestaPagoCabecera_Listar ?, ?, ?, ?, ?";

        return sigoldJdbc.queryForList(
                sql,
                empresaId,
                estado,
                monedaId,
                fechaInicio,
                fechaFin
        );
    }

    public Map<String, Object> generarPropuestaPago(
            String empresaId,
            String monedaId,
            BigDecimal total,
            String usuario,
            String detalleXml
    ) {
        try {

            // 🔥 LIMPIAR XML
            String xmlLimpio = detalleXml
                    .replace("\n", "")
                    .replace("\r", "")
                    .replace("\t", "")
                    .replaceAll(">\\s+<", "><")
                    .trim();

            // 🔥 DECLARAR VARIABLE XML y ejecutar en un bloque T-SQL
            // JDBC no soporta CAST(? AS XML) directo — se declara variable intermedia
            String sql =
                    "DECLARE @xml XML = CAST(? AS XML); " +
                            "EXEC sp_bart_PropuestaPago_Generar ?, ?, ?, ?, @xml";

            return sigoldJdbc.queryForMap(
                    sql,
                    xmlLimpio,   // 🔥 primero el XML (para el CAST)
                    empresaId,
                    monedaId,
                    total,
                    usuario
            );

        } catch (Exception e) {

            Map<String, Object> map = new HashMap<>();
            map.put("estado", 0);
            map.put("mensaje", e.getMessage());
            return map;
        }
    }

    public List<Map<String, Object>> obtenerPropuestaPago(
            Long propPagoCabId
    ) {

        String sql =
                "EXEC sp_bart_PropuestaPago_Obtener ?";

        return sigoldJdbc.queryForList(
                sql,
                propPagoCabId
        );
    }

    public List<Map<String, Object>> rechazarPropuestaPago(
            Long propPagoCabId,
            String usrRechaza,
            String obsvRechaza
    ) {

        String sql =
                "EXEC sp_bart_PropuestaPago_Rechazar ?,?,?";

        return sigoldJdbc.queryForList(
                sql,
                propPagoCabId,
                usrRechaza,
                obsvRechaza
        );
    }

    public List<Map<String, Object>> listarComprobanteCompraDetalle(
            Long provId,
            Long comCompCabId
    ) {

        String sql =
                "EXEC sp_bart_ComprobanteCompraDetalle_Listar ?, ?";

        return sigoldJdbc.queryForList(
                sql,
                provId,
                comCompCabId
        );
    }

    public List<Map<String, Object>> aprobarPropuestaPago(
            Long propPagoCabId,
            String usuario
    ) {


        System.out.println("========== SQL APROBAR ==========");
        System.out.println("ID SQL => " + propPagoCabId);
        System.out.println("USUARIO SQL => [" + usuario + "]");


        String sql =
                "UPDATE PropuestaPagoCabecera " +
                        "SET " +
                        "PropPagoCabEstado = 'A', " +
                        "UsrAprueba = ?, " +
                        "FecAprueba = GETDATE() " +
                        "WHERE PropPagoCabId = ?; " +

                        "SELECT " +
                        "1 AS estado, " +
                        "'Propuesta aprobada correctamente' AS mensaje, " +
                        "? AS PropPagoCabId ";


        try {


            List<Map<String,Object>> respuesta =
                    sigoldJdbc.queryForList(
                            sql,
                            usuario.trim(),
                            propPagoCabId,
                            propPagoCabId
                    );


            System.out.println(
                    "RESPUESTA SQL => "
                            + respuesta
            );


            return respuesta;


        } catch(Exception e){


            System.out.println("XXXXX ERROR SQL REAL XXXXX");

            e.printStackTrace();

            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX");


            throw e;
        }

    }

    // ==========================================
// OBTENER RANGO APROBACION
// ==========================================

    public Map<String,Object> obtenerRangoAprobacion(

            String empresaId,

            String usuario

    ) {


        String sql =

                "EXEC sp_bart_PropuestaPago_ObtenerRangoAprobacion ?, ?";



        List<Map<String,Object>> lista =

                sigoldJdbc.queryForList(

                        sql,

                        empresaId,

                        usuario

                );



        if(lista.isEmpty()){


            Map<String,Object> sinPermiso =
                    new LinkedHashMap<>();


            sinPermiso.put(
                    "EsAprobador",
                    0
            );


            sinPermiso.put(
                    "Mensaje",
                    "USUARIO SIN PERMISOS DE APROBACION"
            );


            return sinPermiso;

        }



        return lista.get(0);


    }

    public List<Map<String,Object>> anularDetalle(
            Long propPagoCabId,
            Integer item,
            String usuario,
            String motivo
    ){

        String sql=
                "EXEC sp_bart_PropuestaPagoDetalle_Anular ?,?,?,?";


        return sigoldJdbc.queryForList(
                sql,
                propPagoCabId,
                item,
                usuario,
                motivo
        );

    }
    public List<Map<String,Object>> aprobarDetalle(

            Long cabId,

            Integer item,

            String usuario

    ){


        String sql =
                "EXEC sp_bart_PropuestaPagoDetalle_Aprobar ?,?,?";



        return sigoldJdbc.queryForList(

                sql,

                cabId,

                item,

                usuario

        );


    }
}
