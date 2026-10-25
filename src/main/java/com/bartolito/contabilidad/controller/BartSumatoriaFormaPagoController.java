package com.bartolito.contabilidad.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bartolito.contabilidad.service.BartSumatoriaFormaPagoService;

@RestController
@RequestMapping("/api/lolfar/sumatoria-forma-pago")
public class BartSumatoriaFormaPagoController {

    private final BartSumatoriaFormaPagoService service;

    public BartSumatoriaFormaPagoController(
            BartSumatoriaFormaPagoService service) {

        this.service = service;
    }

    @GetMapping("/efectivo")
    public ResponseEntity<List<Map<String, Object>>> listarEfectivo() {

        return ResponseEntity.ok(
                service.listarEfectivo()
        );
    }
}