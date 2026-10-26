package com.bartolito.contabilidad.controller;

import com.bartolito.contabilidad.service.ComprobanteCompraService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comprabante_compra")
public class ComprobanteCompraController {

    private final ComprobanteCompraService service;

    public ComprobanteCompraController(ComprobanteCompraService service) {
        this.service = service;
    }


    @GetMapping("/listar/{fechaInicio}/{fechaFin}/{empresaId}")
    public ResponseEntity<Map<String, Object>> listarCompras(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin,
            @PathVariable String empresaId,
            @RequestParam(required = false) Boolean estadoPago
    ) {

        List<Map<String, Object>> result = service.listarComprobantesCompra(
                fechaInicio,
                fechaFin,
                empresaId,
                estadoPago
        );

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("compras", result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/obtenerPreAsientoPropuestaPago")
    public ResponseEntity<Map<String, Object>> obtenerPreAsientoPropuestaPago(
            @RequestBody Map<String, Object> request
    ) {

        Integer propPagoCabId =
                Integer.valueOf(
                        request.get("propPagoCabId").toString()
                );

        String numeroCuenta =
                request.get("numeroCuenta").toString();

        String detalle =
                request.get("detalle").toString();


        List<Map<String, Object>> result =
                service.obtenerPreAsientoPropuestaPago(
                        propPagoCabId,
                        numeroCuenta,
                        detalle
                );


        Map<String, Object> data = new LinkedHashMap<>();

        data.put("preAsiento", result);


        Map<String, Object> response = new LinkedHashMap<>();

        response.put("resultado", "ok");
        response.put("data", data);


        return ResponseEntity.ok(response);
    }

    @GetMapping("/listarxPagar/{fechaVenc}/{empresaId}")
    public ResponseEntity<Map<String, Object>> listarComprasxPagar(
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate fechaVenc,

            @PathVariable
            String empresaId,

            @RequestParam(required = false)
            String docId,

            @RequestParam(required = false)
            Integer monedaId
    ) {

        List<Map<String, Object>> result =
                service.listarComprobantesCompraxPagar(
                        fechaVenc,
                        empresaId,
                        docId,
                        monedaId
                );

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("compras", result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/listarNCyND/{empresaId}/{provId}")
    public ResponseEntity<Map<String, Object>> listarNCyND(
            @PathVariable String empresaId,
            @PathVariable Long provId
    ) {

        List<Map<String, Object>> result =
                service.listarNCyND(empresaId, provId);

        // Convertir identificadores BIGINT a String
        // antes de serializar la respuesta JSON.
        for (Map<String, Object> comprobante : result) {

            Object comCompCabId = comprobante.get("ComCompCabId");

            if (comCompCabId != null) {
                comprobante.put(
                        "ComCompCabId",
                        comCompCabId.toString()
                );
            }

            Object proveedorId = comprobante.get("ProvId");

            if (proveedorId != null) {
                comprobante.put(
                        "ProvId",
                        proveedorId.toString()
                );
            }
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("comprobantes", result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }



    @GetMapping("/listarAdelantoComprobante/{empresaId}/{provId}/{adeCompMovRef}")
    public ResponseEntity<Map<String, Object>> listarAdelantoComprobante(
            @PathVariable String empresaId,
            @PathVariable Long provId,
            @PathVariable String adeCompMovRef
    ) {

        List<Map<String, Object>> result = service.listarAdelantoComprobante(
                empresaId,
                provId,
                adeCompMovRef
        );

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("comprobantes", result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }


}
