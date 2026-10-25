package com.bartolito.contabilidad.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bartolito.contabilidad.service.ConciliacionBancariaService;

@RestController
@RequestMapping("/api/conciliacion-bancaria")
public class ConciliacionBancariaController {

    private final ConciliacionBancariaService service;

    public ConciliacionBancariaController(
            ConciliacionBancariaService service) {

        this.service = service;
    }

    @GetMapping("/cuentas-bancarias")
    public ResponseEntity<List<Map<String, Object>>>
    listarCuentasBancarias() {

        return ResponseEntity.ok(
                service.listarCuentasBancarias()
        );
    }

    @GetMapping("/asientos")
    public ResponseEntity<List<Map<String, Object>>> listarAsientos(

            @RequestParam String cuenta,
            @RequestParam Integer mes,
            @RequestParam Integer ano) {

        return ResponseEntity.ok(
                service.listarAsientos(
                        cuenta,
                        mes,
                        ano
                )
        );
    }
}