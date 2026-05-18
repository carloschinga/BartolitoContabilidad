package com.bartolito.contabilidad.controller;

import com.bartolito.contabilidad.service.ComprobanteCompraService;
import com.bartolito.contabilidad.service.EmpresaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/empresa")
public class EmpresaController {


    private final EmpresaService service;

    public EmpresaController(EmpresaService service) {
        this.service = service;
    }

    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listarEmpresas() {

        List<Map<String, Object>> result = service.listarEmpresas();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("empresas", result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}
