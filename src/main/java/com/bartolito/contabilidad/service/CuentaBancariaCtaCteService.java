package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.ComprobanteCompraRepository;
import com.bartolito.contabilidad.repository.CuentaBancariaCtaCteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CuentaBancariaCtaCteService {
    @Autowired
    private CuentaBancariaCtaCteRepository repository;

    private final CuentaBancariaCtaCteRepository cuentaBancariaCtaCteRepository;

    public CuentaBancariaCtaCteService(CuentaBancariaCtaCteRepository cuentaBancariaCtaCteRepository) {
        this.cuentaBancariaCtaCteRepository = cuentaBancariaCtaCteRepository;
    }

    public List<Map<String, Object>> listarCuentaBancariaCtaCte(
            Long ctaCteId
    ) {

        return repository.listarCuentaBancariaCtaCte(
                ctaCteId
        );
    }

    public int agregarCuentaBancariaCtaCte(
            Long ctaCteId,
            String ctaBancDescripcion,
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

        return repository.agregarCuentaBancariaCtaCte(
                ctaCteId,
                ctaBancDescripcion,
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
            String ctaBancDescripcion,
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

        return repository.modificarCuentaBancariaCtaCte(
                ctaBancId,
                ctaCteId,
                ctaBancDescripcion,
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

        return repository.listarCuentaCorriente(personaId);
    }

    /* =========================================
       LISTAR TIPO PERSONA
       ========================================= */
    public List<Map<String, Object>> listarTipoPersona() {

        return repository.listarTipoPersona();
    }

    // ==================================================
    // LISTAR ENTIDAD FINANCIERA
    // ==================================================

    public List<Map<String, Object>> listarEntidadFinanciera() {

        return repository.listarEntidadFinanciera();

    }

    public List<Map<String, Object>> listarTipoCuentaBancaria() {

        return repository.listarTipoCuentaBancaria();

    }
}
