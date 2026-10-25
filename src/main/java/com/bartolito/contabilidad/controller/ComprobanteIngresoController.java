package com.bartolito.contabilidad.controller;


import com.bartolito.contabilidad.service.ComprobanteEgresoService;
import com.bartolito.contabilidad.service.ComprobanteIngresoService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comprobante_ingreso")
public class ComprobanteIngresoController {
    private final ComprobanteIngresoService service;
    private final RestTemplate restTemplate;

    public ComprobanteIngresoController(ComprobanteIngresoService service, RestTemplate restTemplate) {
        this.service = service;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/generarComprobanteIngreso")
    public ResponseEntity<Map<String, Object>> generarComprobanteIngreso(
            @RequestBody Map<String, Object> request
    ) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        try {

            List<Map<String, Object>> result =
                    service.generarComprobanteIngreso(request);


            Map<String, Object> data =
                    new LinkedHashMap<>();

            data.put(
                    "comprobante",
                    result
            );


            response.put(
                    "resultado",
                    "ok"
            );

            response.put(
                    "data",
                    data
            );


        } catch (DataAccessException ex) {

            Throwable causa =
                    ex.getMostSpecificCause();


            response.put(
                    "resultado",
                    "error"
            );

            response.put(
                    "mensaje",
                    causa != null
                            ? causa.getMessage()
                            : ex.getMessage()
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