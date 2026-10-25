package com.bartolito.contabilidad.controller;

import com.bartolito.contabilidad.service.FondoFijoService;
import com.bartolito.contabilidad.service.SolicitudRendicionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/fondofijo")
public class FondoFijoController {
    private final FondoFijoService service;
    public FondoFijoController(FondoFijoService service) {
        this.service = service;
    }

    @GetMapping("/listarReposicionFondoFijoPendientes")
    public ResponseEntity<?> listarReposicionFondoFijoPendientes(
            @RequestParam(required = false) String empresaId,
            @RequestParam(required = false) String monedaId,
            @RequestParam(required = false) String fechaEmision) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("resultado", "ok");

        response.put(
                "data",
                service.listarReposicionFondoFijoPendientes(
                        empresaId,
                        monedaId,
                        fechaEmision
                )
        );

        return ResponseEntity.ok(response);
    }

}
