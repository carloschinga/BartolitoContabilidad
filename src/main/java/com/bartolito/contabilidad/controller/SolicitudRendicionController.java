package com.bartolito.contabilidad.controller;

import com.bartolito.contabilidad.service.LetrasService;
import com.bartolito.contabilidad.service.SolicitudRendicionService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/rendicion")
public class SolicitudRendicionController {
    private final SolicitudRendicionService service;
    public SolicitudRendicionController(SolicitudRendicionService service) {
        this.service = service;
    }

    @GetMapping("/listarSolicitudRendicionPendientes")
    public ResponseEntity<?> listarSolicitudRendicionPendientes(
            @RequestParam(required = false) String empresaId,
            @RequestParam(required = false) String monedaId,
            @RequestParam(required = false) String fechaEmision) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("resultado", "ok");

        response.put(
                "data",
                service.listarSolicitudRendicionPendientes(
                        empresaId,
                        monedaId,
                        fechaEmision
                )
        );

        return ResponseEntity.ok(response);
    }


}
