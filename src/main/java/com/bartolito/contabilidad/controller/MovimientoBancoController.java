package com.bartolito.contabilidad.controller;


import com.bartolito.contabilidad.service.ComprobanteEgresoService;
import com.bartolito.contabilidad.service.MovimientoBancoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movimiento_banco")
public class MovimientoBancoController {
    private final MovimientoBancoService service;
    private final RestTemplate restTemplate;

    public MovimientoBancoController(MovimientoBancoService service, RestTemplate restTemplate) {
        this.service = service;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/listarMovimientoBanco/{empresaId}/{periodo}/{movimiento}")
    public ResponseEntity<Map<String, Object>> listarMovimientoBanco(

            @PathVariable String empresaId,

            @PathVariable String periodo,

            @PathVariable String movimiento

    ) {


        List<Map<String, Object>> result =
                service.listarMovimientoBanco(
                        empresaId,
                        periodo,
                        movimiento
                );


        Map<String, Object> data =
                new LinkedHashMap<>();


        data.put(
                "movimientos",
                result
        );


        Map<String, Object> response =
                new LinkedHashMap<>();


        response.put(
                "resultado",
                "ok"
        );


        response.put(
                "data",
                data
        );


        return ResponseEntity.ok(response);

    }

    @GetMapping("/obtenerComprobante/{diaCabCompId}")
    public ResponseEntity<Map<String, Object>> obtenerComprobante(
            @PathVariable Long diaCabCompId
    ) {

        List<Map<String, Object>> result =
                service.obtenerComprobante(
                        diaCabCompId
                );


        Map<String, Object> data =
                new LinkedHashMap<>();


        data.put(
                "comprobante",
                result
        );


        Map<String, Object> response =
                new LinkedHashMap<>();


        response.put(
                "resultado",
                "ok"
        );


        response.put(
                "data",
                data
        );


        return ResponseEntity.ok(response);
    }
}