package com.bartolito.contabilidad.controller.insanor;

import com.bartolito.contabilidad.repository.insanor.ConciliacionRepository;
import com.bartolito.contabilidad.service.insanor.ConciliacionService;
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
@RequestMapping("/api/insanor/conciliacion")
public class ConciliacionController {

    @Autowired
    private ConciliacionService service;

    // =========================================
    // CONCILIACIÓN - LISTAR SUCURSALES
    // =========================================
    @PostMapping("/listar-sucursales")
    public ResponseEntity<?> listarSucursales() {

        List<Map<String, Object>> result = service.listarSucursales();

        return ResponseEntity.ok(result);
    }

    // =========================================
    // CONCILIACIÓN - LISTAR FECHAS
    // =========================================
    @PostMapping("/listar-fechas")
    public ResponseEntity<?> listarFechas(
            @RequestBody Map<String, Object> request
    ) {

        String codigo = request.get("codigo") != null
                ? request.get("codigo").toString()
                : null;

        Integer mes = request.get("mes") != null
                ? Integer.parseInt(request.get("mes").toString())
                : null;

        Integer anio = request.get("anio") != null
                ? Integer.parseInt(request.get("anio").toString())
                : null;

        List<Map<String, Object>> result = service.listarFechas(
                codigo,
                mes,
                anio
        );

        return ResponseEntity.ok(result);
    }

    // =========================================
    // CONCILIACIÓN - LISTAR NETO TOTAL
    // =========================================
    @PostMapping("/listar-neto-total")
    public ResponseEntity<?> listarNetoTotal(
            @RequestBody Map<String, Object> request
    ) {

        String codigo = request.get("codigo") != null
                ? request.get("codigo").toString()
                : null;

        String fechaAbono = request.get("fechaAbono") != null
                ? request.get("fechaAbono").toString()
                : null;

        List<Map<String, Object>> result = service.listarNetoTotal(
                codigo,
                fechaAbono
        );

        return ResponseEntity.ok(result);
    }

    // =========================================
    // CONCILIACIÓN - EXISTE CONCILIADO
    // =========================================
    @PostMapping("/existe-conciliado")
    public ResponseEntity<?> existeConciliado(
            @RequestBody Map<String, Object> request
    ) {

        String codigo = request.get("codigo") != null
                ? request.get("codigo").toString()
                : null;

        String fechaAbono = request.get("fechaAbono") != null
                ? request.get("fechaAbono").toString()
                : null;

        BigDecimal monto = request.get("monto") != null
                ? new BigDecimal(request.get("monto").toString())
                : null;

        List<Map<String, Object>> result = service.existeConciliado(
                codigo,
                fechaAbono,
                monto
        );

        return ResponseEntity.ok(result);
    }

    // =========================================
    // CONCILIACIÓN - COMPROBANTES CONCILIADOS CABECERA
    // =========================================
    @PostMapping("/listar-comprobantes-conciliados-cabecera")
    public ResponseEntity<?> listarComprobantesConciliadosCabecera(
            @RequestBody Map<String, Object> request
    ) {

        String codigo = request.get("codigo") != null
                ? request.get("codigo").toString()
                : null;

        String fechaAbono = request.get("fechaAbono") != null
                ? request.get("fechaAbono").toString()
                : null;

        BigDecimal monto = request.get("monto") != null
                ? new BigDecimal(request.get("monto").toString())
                : null;

        List<Map<String, Object>> result = service.listarComprobantesConciliadosCabecera(
                codigo,
                fechaAbono,
                monto
        );

        return ResponseEntity.ok(result);
    }

    // =========================================
    // CONCILIACIÓN - COMPROBANTES CONCILIADOS DETALLE
    // =========================================
    @PostMapping("/listar-comprobantes-conciliados-detalle")
    public ResponseEntity<?> listarComprobantesConciliadosDetalle(
            @RequestBody Map<String, Object> request
    ) {

        Integer id = Integer.parseInt(request.get("id").toString());

        List<Map<String, Object>> result = service.listarComprobantesConciliadosDetalle(
                id
        );

        return ResponseEntity.ok(result);
    }

    // =========================================
    // CONCILIACIÓN - COMPROBANTES NO CONCILIADOS
    // =========================================
    @PostMapping("/listar-comprobantes-no-conciliados")
    public ResponseEntity<?> listarComprobantesNoConciliados(
            @RequestBody Map<String, Object> request
    ) {

        String codigo = request.get("codigo") != null
                ? request.get("codigo").toString()
                : null;

        String fechaAbono = request.get("fechaAbono") != null
                ? request.get("fechaAbono").toString()
                : null;

        BigDecimal netoTotal = request.get("netoTotal") != null
                ? new BigDecimal(request.get("netoTotal").toString())
                : null;

        Integer unidComId = request.get("unidComId") != null
                ? Integer.parseInt(request.get("unidComId").toString())
                : null;

        List<Map<String, Object>> result = service.listarComprobantesNoConciliados(
                codigo,
                fechaAbono,
                netoTotal,
                unidComId
        );

        return ResponseEntity.ok(result);
    }

    // =========================================
    // CONCILIACIÓN - CONCILIAR
    // =========================================
    @PostMapping("/conciliar")
    public ResponseEntity<?> conciliar(
            @RequestBody Map<String, Object> request
    ) {

        String codigo = request.get("codigo") != null
                ? request.get("codigo").toString()
                : null;

        String fechaAbono = request.get("fechaAbono") != null
                ? request.get("fechaAbono").toString()
                : null;

        BigDecimal netoTotal = request.get("netoTotal") != null
                ? new BigDecimal(request.get("netoTotal").toString())
                : null;

        Integer unidComId = request.get("unidComId") != null
                ? Integer.parseInt(request.get("unidComId").toString())
                : null;

        List<Map<String, Object>> result = service.conciliar(
                codigo,
                fechaAbono,
                netoTotal,
                unidComId
        );

        return ResponseEntity.ok(result);
    }

}
