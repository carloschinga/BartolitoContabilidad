package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HaberesRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;

    public Map<String,Object> registrarHaberes(

            String empresaId,

            Integer tipConCajId,

            Integer conCajId,

            String periodo,

            String descripcion,

            String fecha,

            String fechaProceso,

            String subTipo,

            String moneda,

            String cuentaCargo,

            BigDecimal total,

            String archivoTxt,

            String usuario,

            String detalle

    ){

        try{

            String sql=

                    "EXEC sp_bart_Haberes_Guardar "
                            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";

            List<Map<String,Object>> resultado=

                    sigoldJdbc.queryForList(

                            sql,

                            empresaId,

                            tipConCajId,

                            conCajId,

                            periodo,

                            descripcion,

                            fecha,

                            fechaProceso,

                            subTipo,

                            moneda,

                            cuentaCargo,

                            total,

                            archivoTxt,

                            usuario,

                            detalle

                    );

            return resultado.isEmpty()

                    ? Map.of(

                    "Resultado",0,

                    "Mensaje","Sin respuesta"

            )

                    : resultado.get(0);

        }
        catch(Exception e){

            return Map.of(

                    "Resultado",0,

                    "Mensaje",e.getMessage()

            );

        }

    }

    public List<Map<String,Object>> listarHaberes(
            String empresaId,
            String monedaId,
            String periodo,
            String estado){

        String sql = "EXEC sp_bart_Haberes_Listar ?, ?, ?, ?";

        return sigoldJdbc.queryForList(
                sql,
                empresaId,
                monedaId,
                periodo,
                estado
        );
    }

    public Map<String,Object> obtenerHaberes(Long habCabId){
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(sigoldJdbc)
                .withProcedureName("sp_bart_Haberes_Obtener")
                .returningResultSet(
                        "cabecera",
                        new ColumnMapRowMapper()
                )
                .returningResultSet(
                        "detalle",
                        new ColumnMapRowMapper()
                );

        Map<String,Object> in = new HashMap<>();
        in.put("HabCabId", habCabId);
        Map<String,Object> out = jdbcCall.execute(in);
        Map<String,Object> resultado = new LinkedHashMap<>();
        List<Map<String,Object>> cabecera =(List<Map<String,Object>>) out.get("cabecera");
        List<Map<String,Object>> detalle = (List<Map<String,Object>>) out.get("detalle");
        resultado.put("cabecera",cabecera.isEmpty()? null: cabecera.get(0));
        resultado.put("detalle", detalle );
        return resultado;

    }

}
