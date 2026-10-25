package com.bartolito.contabilidad.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Repository
public class CuentaBancariaCtaCteRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate sigoldJdbc;

    public List<Map<String, Object>> listarCuentaBancariaCtaCte(
            Long ctaCteId
    ) {

        String sql = "EXEC dbo.sp_bart_CuentaBancariaCtaCte_Listar ?";

        return sigoldJdbc.queryForList(
                sql,
                ctaCteId
        );
    }

    public int agregarCuentaBancariaCtaCte(
            Long ctaCteId,
            String ctaCteEmail,
            Integer tipCtaBancId,
            String entFinId,
            String monedaId,
            String ctaBancNumero,
            String ctaBancCCI,
            String ctaBancTitular,
            Boolean ctaBancPrincipal,
            Boolean ctaBancEstado,
            String usrSistema
    ) {

        String sql = "EXEC dbo.sp_bart_CuentaBancariaCtaCte_Agregar ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";

        return sigoldJdbc.queryForObject(
                sql,
                Integer.class,
                ctaCteId,
                ctaCteEmail,
                tipCtaBancId,
                entFinId,
                monedaId,
                ctaBancNumero,
                ctaBancCCI,
                ctaBancTitular,
                ctaBancPrincipal,
                ctaBancEstado,
                usrSistema
        );
    }

    /* =========================================
       MODIFICAR
       ========================================= */
    public int modificarCuentaBancariaCtaCte(
            Long ctaBancId,
            Long ctaCteId,
            String ctaCteEmail,
            Integer tipCtaBancId,
            String entFinId,
            String monedaId,
            String ctaBancNumero,
            String ctaBancCCI,
            String ctaBancTitular,
            Boolean ctaBancPrincipal,
            Boolean ctaBancEstado,
            String usrSistema
    ) {

        String sql = "EXEC dbo.sp_bart_CuentaBancariaCtaCte_Modificar ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?";

        return sigoldJdbc.queryForObject(
                sql,
                Integer.class,
                ctaBancId,
                ctaCteId,
                ctaCteEmail,
                tipCtaBancId,
                entFinId,
                monedaId,
                ctaBancNumero,
                ctaBancCCI,
                ctaBancTitular,
                ctaBancPrincipal,
                ctaBancEstado,
                usrSistema
        );
    }

    /* =========================================
      LISTAR CUENTA CORRIENTE
      ========================================= */
    public List<Map<String, Object>> listarCuentaCorriente(
            String personaId
    ) {

        String sql = "EXEC dbo.sp_bart_CuentaCorriente_Listar ?";

        return sigoldJdbc.queryForList(
                sql,
                personaId
        );
    }

    /* =========================================
       LISTAR TIPO PERSONA
       ========================================= */
    public List<Map<String, Object>> listarTipoPersona() {

        String sql = "EXEC dbo.sp_bart_TipoPersona_Listar";

        return sigoldJdbc.queryForList(sql);
    }



    // ==================================================
    // LISTAR ENTIDAD FINANCIERA
    // ==================================================

    public List<Map<String, Object>> listarEntidadFinanciera() {

        String sql = "EXEC sp_bart_EntidadFinanciera_Listar";

        return sigoldJdbc.queryForList(sql);

    }


    public List<Map<String, Object>> listarTipoCuentaBancaria() {

        String sql = "EXEC sp_bart_TipoCuentaBancaria_listar";

        return sigoldJdbc.queryForList(sql);

    }

    public void guardarCorreo(
            Integer ctaCteId,
            String personaId,
            String correo
    ) {

        String sql =
                "EXEC sp_bart_CuentaCorriente_Correo_Guardar ?, ?, ?";

        sigoldJdbc.update(
                sql,
                ctaCteId,
                personaId,
                correo
        );
    }

}
