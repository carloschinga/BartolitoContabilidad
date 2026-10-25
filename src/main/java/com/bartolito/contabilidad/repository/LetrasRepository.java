package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
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

    public List<Map<String, Object>> listarLetrasPendientes(
            String empresaId,
            String monedaId,
            String fechaVencimiento) {

        String sql =
                "EXEC sp_bart_PropuestaPago_ListarLetrasPendientes ?, ?, ?";

        return sigoldJdbc.queryForList(
                sql,
                empresaId,
                monedaId,
                fechaVencimiento
        );
    }
    public List<Map<String, Object>> obtenerPreAsiento(
            String empresaId,
            String numeroCuenta,
            String detalleXml) {

        String sql = "EXEC sp_bart_Factory_ObtenerPreAsiento ?,?,?";

        return sigoldJdbc.queryForList(
                sql,
                empresaId,
                numeroCuenta,
                detalleXml
        );
    }

    public Map<String, Object> registrar(

            String empresaId,
            Integer ctaBancEmpId,
            java.sql.Date fechaPago,
            String glosa,
            String usuario,
            String detalleXml

    ) {

        SimpleJdbcCall call =
                new SimpleJdbcCall(sigoldJdbc)
                        .withProcedureName(
                                "sp_bart_ComprobanteEgreso_Letras_Generar"
                        )
                        .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(

                                new SqlParameter(
                                        "EmpresaId",
                                        Types.CHAR
                                ),

                                new SqlParameter(
                                        "CtaBancEmpId",
                                        Types.INTEGER
                                ),

                                new SqlParameter(
                                        "FechaPago",
                                        Types.DATE
                                ),

                                new SqlParameter(
                                        "Glosa",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "Usuario",
                                        Types.VARCHAR
                                ),

                                new SqlParameter(
                                        "DetalleXml",
                                        Types.LONGVARCHAR
                                ),

                                new SqlParameter(
                                        "Debug",
                                        Types.TINYINT
                                )
                        );


        Map<String, Object> parametros =
                new HashMap<>();


        parametros.put(
                "EmpresaId",
                empresaId
        );

        parametros.put(
                "CtaBancEmpId",
                ctaBancEmpId
        );

        parametros.put(
                "FechaPago",
                fechaPago
        );

        parametros.put(
                "Glosa",
                glosa
        );

        parametros.put(
                "Usuario",
                usuario
        );

        parametros.put(
                "DetalleXml",
                detalleXml
        );

        parametros.put(
                "Debug",
                (byte) 0
        );


        return call.execute(parametros);
    }

}
