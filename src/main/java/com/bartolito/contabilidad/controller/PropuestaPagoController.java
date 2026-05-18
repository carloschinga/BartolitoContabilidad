package com.bartolito.contabilidad.controller;


import com.bartolito.contabilidad.service.PropuestaPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/propuesta_pago")
public class PropuestaPagoController {
    private final PropuestaPagoService service;

    public PropuestaPagoController(PropuestaPagoService service) {
        this.service = service;
    }
    @GetMapping("/listarPropuestaPago/{empresaId}/{estado}")
    public ResponseEntity<Map<String, Object>> listarPropuestaPago(
            @PathVariable String empresaId,

            @PathVariable String estado
    ) {

        List<Map<String, Object>> result = service.listarPropuestaPagoCabecera(
                empresaId,
                estado
        );

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("propuestas", result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/generar_propuesta_pago")
    public ResponseEntity<?> generarPropuestaPago(
            @RequestParam String empresaId,
            @RequestParam BigDecimal total,
            @RequestParam String usuario,
            @RequestBody String detalleXml
    ) {

        return ResponseEntity.ok(
                service.generarPropuestaPago(
                        empresaId,
                        total,
                        usuario,
                        detalleXml
                )
        );
    }
}
