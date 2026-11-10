package com.bartolito.contabilidad.controller;

import com.bartolito.contabilidad.service.HaberesService;
import com.bartolito.contabilidad.service.LetrasService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/letras")
public class LetrasController {
    private final LetrasService service;
    public LetrasController(LetrasService service) {
        this.service = service;
    }


    @GetMapping("/listarLetrasPendientes")
    public ResponseEntity<?> listarLetrasPendientes(
            @RequestParam(required = false) String empresaId,
            @RequestParam(required = false) String monedaId,
            @RequestParam(required = false) String fechaVencimiento) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("resultado", "ok");

        response.put(
                "data",
                service.listarLetrasPendientes(
                        empresaId,
                        monedaId,
                        fechaVencimiento
                )
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/obtenerPreAsiento")
    public ResponseEntity<?> obtenerPreAsiento(
            @RequestBody Map<String, Object> request) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("resultado", "ok");
        response.put("data", service.obtenerPreAsiento(request));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(
            @RequestBody Map<String, Object> request) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        try {

            response.put("resultado", "ok");
            response.put(
                    "data",
                    service.registrar(request)
            );

            return ResponseEntity.ok(response);

        } catch (DataAccessException ex) {

            Throwable causa =
                    ex.getMostSpecificCause();

            response.put("resultado", "error");

            response.put(
                    "mensaje",
                    causa != null
                            ? causa.getMessage()
                            : ex.getMessage()
            );

            return ResponseEntity.ok(response);

        } catch (Exception ex) {

            response.put("resultado", "error");

            response.put(
                    "mensaje",
                    ex.getMessage()
            );

            return ResponseEntity.ok(response);
        }
    }
    @GetMapping("/previsualizarReenvioCorreos/{propPagoCabId}")
    public ResponseEntity<Map<String, Object>> previsualizarReenvioCorreos(
            @PathVariable Integer propPagoCabId, HttpServletRequest httpRequest) {
        Map<String, Object> r = new LinkedHashMap<>();
        if (httpRequest.getUserPrincipal() == null) {
            r.put("resultado", "prohibido");
            r.put("mensaje", "Se requiere sesión autenticada.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(r);
        }
        try {
            List<Map<String, Object>> filas = service.previsualizarReenvioCorreos(propPagoCabId);
            r.put("resultado", "ok");
            r.put("cantidad", filas.size());
            r.put("data", filas);
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            r.put("resultado", "error");
            r.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(r);
        }
    }

    @PostMapping("/reenviarCorreos")
    public ResponseEntity<Map<String, Object>> reenviarCorreos(
            @RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        Map<String, Object> r = new LinkedHashMap<>();
        if (httpRequest.getUserPrincipal() == null) {
            r.put("resultado", "prohibido");
            r.put("mensaje", "Se requiere sesión autenticada.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(r);
        }
        try {
            if (request == null || !Boolean.TRUE.equals(request.get("ConfirmacionFactory")))
                throw new IllegalArgumentException("Debe confirmar el reenvío Factory.");
            Object id = request.get("PropPagoCabId");
            if (id == null) throw new IllegalArgumentException("Falta PropPagoCabId.");
            r.put("data", service.reenviarCorreos(Integer.valueOf(id.toString())));
            r.put("resultado", "ok");
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
            r.put("resultado", "error");
            r.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(r);
        } catch (IllegalStateException e) {
            r.put("resultado", "bloqueado");
            r.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(r);
        } catch (Exception e) {
            r.put("resultado", "error");
            r.put("mensaje", "Publicación incierta. Verifique /noti antes de repetir.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(r);
        }
    }

    @PostMapping("/buscarPropuesta")
    public ResponseEntity<Map<String, Object>> buscarPropuesta(
            @RequestBody Map<String, Object> request) {

        try {

            List<Map<String, Object>> propuesta =
                    service.buscarPropuesta(request);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("propuesta", propuesta);

            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("resultado", "ok");
            respuesta.put("data", data);

            return ResponseEntity.ok(respuesta);

        } catch (Exception ex) {

            Map<String, Object> respuesta = new LinkedHashMap<>();
            respuesta.put("resultado", "error");
            respuesta.put("mensaje", ex.getMessage());

            return ResponseEntity.badRequest().body(respuesta);
        }
    }

}
