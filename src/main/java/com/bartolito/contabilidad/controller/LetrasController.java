package com.bartolito.contabilidad.controller;

import com.bartolito.contabilidad.service.HaberesService;
import com.bartolito.contabilidad.service.LetrasService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

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


}
