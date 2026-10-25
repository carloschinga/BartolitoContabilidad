package com.bartolito.contabilidad.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;



@Repository
public class PropuestaPagoAprobadorRepository {



    @Autowired
    private JdbcTemplate sigoldJdbc;






    public List<Map<String,Object>> listar(
            String empresaId
    ){


        String sql =
                "EXEC sp_bart_PropuestaPagoAprobador_Listar ?";



        return sigoldJdbc.queryForList(
                sql,
                empresaId
        );

    }







    public Map<String,Object> obtener(
            Long aproId
    ){


        String sql =
                "EXEC sp_bart_PropuestaPagoAprobador_Obtener ?";


        return sigoldJdbc.queryForMap(
                sql,
                aproId
        );

    }



    public List<Map<String,Object>> guardar(
            Map<String,Object> datos
    ){


        String sql =
                "EXEC sp_bart_PropuestaPagoAprobador_Guardar ?,?,?,?,?,?,?";



        Object aproId =
                datos.get("aproId");



        Object empresaId =
                datos.get("empresaId");



        BigDecimal montoDesde =
                BigDecimal.ZERO;


        if(datos.get("montoDesde") != null){

            montoDesde =
                    new BigDecimal(
                            datos.get("montoDesde")
                                    .toString()
                    );

        }



        BigDecimal montoHasta = null;


        if(datos.get("montoHasta") != null){

            montoHasta =
                    new BigDecimal(
                            datos.get("montoHasta")
                                    .toString()
                    );

        }




        return sigoldJdbc.queryForList(

                sql,


                aproId,


                empresaId,


                datos.get("usrAprueba"),


                montoDesde,


                montoHasta,


                datos.get("activo"),


                datos.get("usrSistema")

        );


    }







}