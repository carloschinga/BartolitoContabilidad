package com.bartolito.contabilidad.controller.insanor;


import com.bartolito.contabilidad.service.insanor.SumatoriaInsanorService;
import com.bartolito.contabilidad.util.dto.insanor.sumatoria.FechaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/insanor/sumatoria")
public class SumatoriaInsanorController {

    @Autowired
    private SumatoriaInsanorService service;

    // =========================================
    // SUMATORIA FORMA DE PAGO - GUARDAR / ACTUALIZAR
    // =========================================
    @PostMapping("/save-or-update")
    public ResponseEntity<?> saveOrUpdate(@RequestBody Map<String, Object> params) {

        Integer sumatoriaFormaPagoId = params.get("sumatoriaFormaPagoId") != null
                ? Integer.valueOf(params.get("sumatoriaFormaPagoId").toString())
                : null;

        Integer operacionId = Integer.valueOf(params.get("operacionId").toString());
        Integer siscod = Integer.valueOf(params.get("siscod").toString());
        String fechaOperacion = params.get("fechaOperacion").toString();
        Integer invnumAper = Integer.valueOf(params.get("invnumAper").toString());
        String docpag = params.get("docpag").toString();
        String docdes = params.get("docdes").toString();
        Integer cantidad = Integer.valueOf(params.get("cantidad").toString());

        BigDecimal importeTotal = new BigDecimal(params.get("importeTotal").toString());
        BigDecimal importeNC = new BigDecimal(params.get("importeNC").toString());
        BigDecimal total = new BigDecimal(params.get("total").toString());

        Boolean estado = params.get("estado") != null
                ? Boolean.valueOf(params.get("estado").toString())
                : true;

        String usuario = params.get("usuario").toString();

        List<Map<String, Object>> result = service.saveOrUpdate(
                sumatoriaFormaPagoId,
                operacionId,
                siscod,
                fechaOperacion,
                invnumAper,
                docpag,
                docdes,
                cantidad,
                importeTotal,
                importeNC,
                total,
                estado,
                usuario
        );

        return ResponseEntity.ok(result);
    }

    // =========================================
    // SUMATORIA FORMA DE PAGO - LISTAR POR OPERACIÓN
    // =========================================
    @PostMapping("/listar-sumatoria")
    public ResponseEntity<?> listarSumatoria(@RequestBody Map<String, Object> params) {

        Integer operacionId = Integer.valueOf(params.get("operacionId").toString());

        return ResponseEntity.ok(service.listarSumatoria(operacionId));
    }

    // =========================================
    // SUMATORIA FORMA DE PAGO - LISTAR EFECTIVO
    // =========================================
    @PostMapping("/listar-efectivo")
    public ResponseEntity<?> listarEfectivo(@RequestBody FechaRequest request) {
        return ResponseEntity.ok(service.listarEfectivo(request.getFechaOperacion()));
    }

    // =========================================
    // SUMATORIA FORMA DE PAGO - LISTAR HISTORIAL
    // =========================================
    @PostMapping("/listar-historial-sumatorias")
    public ResponseEntity<?> listarHistorialSumatorias(
            @RequestBody(required = false) Map<String, Object> params) {

        String fechaInicio = params != null && params.get("fechaInicio") != null
                ? params.get("fechaInicio").toString()
                : null;

        String fechaFin = params != null && params.get("fechaFin") != null
                ? params.get("fechaFin").toString()
                : null;

        return ResponseEntity.ok(
                service.listarHistorialSumatorias(fechaInicio, fechaFin)
        );
    }

}
