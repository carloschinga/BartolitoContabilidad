package com.bartolito.contabilidad.controller;

import com.bartolito.contabilidad.service.ComprobanteCompraService;
import com.bartolito.contabilidad.service.CuentaBancariaCtaCteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cuenta_bancaria_ctacte")
public class CuentaBancariaCtaCteController {

    private final CuentaBancariaCtaCteService service;

    public CuentaBancariaCtaCteController(CuentaBancariaCtaCteService service) {
        this.service = service;
    }


    @GetMapping("/listarCB/{ctaCteId}")
    public Map<String, Object> listar(
            @PathVariable Long ctaCteId
    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Map<String, Object>> lista =
                    service.listarCuentaBancariaCtaCte(ctaCteId);

            response.put("resultado", "ok");
            response.put("data", lista);

        } catch (Exception e) {

            response.put("resultado", "error");
            response.put("mensaje", "Error al listar cuentas bancarias");
            response.put("error_tecnico", e.getMessage());
        }

        return response;
    }

    @PostMapping("/agregarCB")
    public Map<String, Object> agregar(

            @RequestParam Long ctaCteId,
            @RequestParam String ctaCteEmail,
            @RequestParam Integer tipCtaBancId,
            @RequestParam String entFinId,
            @RequestParam String monedaId,
            @RequestParam String ctaBancNumero,
            @RequestParam String ctaBancCCI,
            @RequestParam String ctaBancTitular,
            @RequestParam Boolean ctaBancPrincipal,
            @RequestParam Boolean ctaBancEstado,
            @RequestParam String usrSistema
    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            int resultado = service.agregarCuentaBancariaCtaCte(
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

            if (resultado > 0) {

                response.put("resultado", "ok");
                response.put("mensaje", "Cuenta bancaria registrada correctamente");
                response.put("filas_afectadas", resultado);

            } else {

                response.put("resultado", "error");
                response.put("mensaje", "No se pudo registrar");
            }

        } catch (Exception e) {

            response.put("resultado", "error");
            response.put("mensaje", "Error al registrar");
            response.put("error_tecnico", e.getMessage());
        }

        return response;
    }

    /* =========================================
       MODIFICAR
       ========================================= */
    @PostMapping("/modificarCB")
    public Map<String, Object> modificar(

            @RequestParam Long ctaBancId,
            @RequestParam Long ctaCteId,
            @RequestParam String ctaCteEmail,
            @RequestParam Integer tipCtaBancId,
            @RequestParam String entFinId,
            @RequestParam String monedaId,
            @RequestParam String ctaBancNumero,
            @RequestParam String ctaBancCCI,
            @RequestParam String ctaBancTitular,
            @RequestParam Boolean ctaBancPrincipal,
            @RequestParam Boolean ctaBancEstado,
            @RequestParam String usrSistema
    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            int resultado = service.modificarCuentaBancariaCtaCte(
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

            if (resultado > 0) {

                response.put("resultado", "ok");
                response.put("mensaje", "Cuenta bancaria modificada correctamente");
                response.put("filas_afectadas", resultado);

            } else {

                response.put("resultado", "error");
                response.put("mensaje", "No se pudo modificar");
            }

        } catch (Exception e) {

            response.put("resultado", "error");
            response.put("mensaje", "Error al modificar");
            response.put("error_tecnico", e.getMessage());
        }

        return response;
    }

    /* =========================================
       LISTAR CUENTA CORRIENTE
       ========================================= */
    @GetMapping("/listarCC/{personaId}")
    public Map<String, Object> listarCuentaCorriente(
            @PathVariable String personaId
    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Map<String, Object>> lista =
                    service.listarCuentaCorriente(personaId);

            response.put("resultado", "ok");
            response.put("data", lista);

        } catch (Exception e) {

            response.put("resultado", "error");
            response.put("mensaje", "Error al listar cuentas corrientes");
            response.put("error_tecnico", e.getMessage());
        }

        return response;
    }

    /* =========================================
       LISTAR TIPO PERSONA
       ========================================= */
    @GetMapping("/tipo_persona/listar")
    public Map<String, Object> listarTipoPersona() {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Map<String, Object>> lista =
                    service.listarTipoPersona();

            response.put("resultado", "ok");
            response.put("data", lista);

        } catch (Exception e) {

            response.put("resultado", "error");
            response.put("mensaje", "Error al listar tipo persona");
            response.put("error_tecnico", e.getMessage());
        }

        return response;
    }

    @GetMapping("/listarEF")
    public Map<String, Object> listarEntidadFinanciera() {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Map<String, Object>> lista =
                    service.listarEntidadFinanciera();

            response.put("resultado", "ok");
            response.put("data", lista);

        } catch (Exception e) {

            response.put("resultado", "error");
            response.put("mensaje", "Error al listar entidades financieras");
            response.put("error_tecnico", e.getMessage());

        }

        return response;

    }

    @GetMapping("/listarTCB")
    public Map<String, Object> listarTipoCuentaBancaria() {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Map<String, Object>> lista =
                    service.listarTipoCuentaBancaria();

            response.put("resultado", "ok");
            response.put("data", lista);

        } catch (Exception e) {

            response.put("resultado", "error");
            response.put("mensaje", "Error al listar tipos de cuenta bancaria");
            response.put("error_tecnico", e.getMessage());

        }

        return response;

    }
    @PostMapping("/guardarCorreo")
    public ResponseEntity<Map<String, Object>> guardarCorreo(
            @RequestBody Map<String, Object> request
    ) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        try {

            Integer ctaCteId =
                    Integer.valueOf(
                            request.get("CtaCteId").toString()
                    );

            String personaId =
                    request.get("PersonaId").toString();

            String correo =
                    request.get("Correo").toString().trim();


            service.guardarCorreo(
                    ctaCteId,
                    personaId,
                    correo
            );


            response.put(
                    "resultado",
                    "ok"
            );

            response.put(
                    "mensaje",
                    "Correo actualizado correctamente."
            );


        } catch (Exception ex) {

            response.put(
                    "resultado",
                    "error"
            );

            response.put(
                    "mensaje",
                    ex.getMessage()
            );
        }


        return ResponseEntity.ok(response);
    }
}
