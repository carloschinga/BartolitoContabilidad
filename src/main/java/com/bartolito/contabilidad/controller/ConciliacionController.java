package com.bartolito.contabilidad.controller;

import com.bartolito.contabilidad.service.ConciliacionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/conciliacion")
public class ConciliacionController {

    private final ConciliacionService service;

    public ConciliacionController(ConciliacionService service) {
        this.service = service;
    }



    @PostMapping("/sucursales")
    public Map<String, Object> getFechas() {

        return service.getSucursales();
    }


    @GetMapping("/meses")
    public Map<String, Object> getMeses(@RequestParam(required = false) String codigo) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> lista =service.getMeses(codigo);
            response.put("resultado", "ok");
            response.put("data", lista);
        } catch (Exception e) {
            response.put("resultado", "error");
            response.put("mensaje", "Error ");
            response.put("error_tecnico", e.getMessage());
        }
        return response;
    }

    @GetMapping("/operaciones")
    public Map<String, Object> getOperaciones(@RequestParam(required = false) String codigo,@RequestParam(required = false) String mes) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> lista =service.getOperaciones(codigo,mes);
            response.put("resultado", "ok");
            response.put("data", lista);
        } catch (Exception e) {
            response.put("resultado", "error");
            response.put("mensaje", "Error ");
            response.put("error_tecnico", e.getMessage());
        }
        return response;
    }

    @GetMapping("/comprobantes")
    public Map<String, Object> getComprobantes(@RequestParam(required = false) String codigo,@RequestParam(required = false) String fechaAbono,@RequestParam(required = false) Double netoTotal,@RequestParam(required = false) Integer unidComId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> lista = service.getComprobantes(codigo,fechaAbono,netoTotal,unidComId);
            response.put("resultado", "ok");
            response.put("data", lista);

        } catch (Exception e) {
            response.put("resultado", "error");
            response.put("mensaje", "Error al obtener comprobantes");
            response.put("error_tecnico", e.getMessage());
        }

        return response;
    }


    @PostMapping("/fechas")
    public Map<String, Object> getFechas(@RequestBody Map<String, String> request) {
        String sucursal = request.get("sucursal");
        return service.getFechas(sucursal);
    }

    @PostMapping("/netototal")
    public Map<String, Object> getNetoTotal(@RequestBody Map<String, String> request) {
        String sucursal = request.get("sucursal");
        String fechaAbono = request.get("fechaAbono");
        return service.getNetoTotal(sucursal, fechaAbono);
    }

    @PostMapping("/conciliadoexiste")
    public Map<String, Object> getConciliadoExiste(@RequestBody Map<String, String> request) {
        try {
            String codigo = request.get("codigo");
            String fechaAbonoStr = request.get("fechaAbono");
            String montoStr = request.get("monto");

            // Convertir fecha y monto
            Date fechaAbono = Date.valueOf(fechaAbonoStr);
            BigDecimal monto = new BigDecimal(montoStr);

            return service.getConciliadoExiste(codigo, fechaAbono, monto);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("Existe", 0);
        }
    }

    @PostMapping("/conciliar")
    public Map<String, Object> conciliarOperacion(@RequestBody Map<String, String> request) {
        try {
            String codigo = request.get("codigo");
            String fechaAbonoStr = request.get("fechaAbono");
            String netoTotalStr = request.get("netoTotal");
            String unidComIdStr = request.get("unidComId");

            // Convertir fecha, monto y unidComId
            Date fechaAbono = Date.valueOf(fechaAbonoStr); // yyyy-MM-dd
            BigDecimal netoTotal = new BigDecimal(netoTotalStr);
            int unidComId = Integer.parseInt(unidComIdStr);

            // Llamar al service
            return service.conciliar(codigo, fechaAbono, netoTotal, unidComId);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("estado", -1, "mensaje", "Error al procesar la conciliación");
        }
    }

    @PostMapping("/noconciliados")
    public Map<String, Object> getNoConciliados(@RequestBody Map<String, String> request) {
        try {
            String codigo = request.get("codigo");
            String fechaAbonoStr = request.get("fechaAbono");
            String netoTotalStr = request.get("netoTotal");
            String unidComIdStr = request.get("unidComId");

            Date fechaAbono = Date.valueOf(fechaAbonoStr); // yyyy-MM-dd
            BigDecimal netoTotal = new BigDecimal(netoTotalStr);
            int unidComId = Integer.parseInt(unidComIdStr);

            return service.getNoConciliados(codigo, fechaAbono, netoTotal, unidComId);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("noconciliados", List.of()); // fallback vacío en caso de error
        }
    }

    @PostMapping("/conciliadoscabecera")
    public Map<String, Object> getConciliadosCabecera(@RequestBody Map<String, String> request) {
        try {
            String codigo = request.get("codigo");
            String fechaAbonoStr = request.get("fechaAbono");
            String monto = request.get("monto");


            Date fechaAbono = Date.valueOf(fechaAbonoStr); // yyyy-MM-dd
            BigDecimal dmonto = new BigDecimal(monto);


            return service.getConciliadosCabecera(codigo, fechaAbono, dmonto);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("conciliados_cabecera", List.of()); // fallback vacío en caso de error
        }
    }

    @PostMapping("/conciliadosdetalle")
    public Map<String, Object> getConciliadosDetalle(@RequestBody Map<String, String> request) {
        try {
            String codigo = request.get("codigo");
            String fechaAbonoStr = request.get("fechaAbono");
            String monto = request.get("monto");


            Date fechaAbono = Date.valueOf(fechaAbonoStr); // yyyy-MM-dd
            BigDecimal dmonto = new BigDecimal(monto);


            return service.getConciliadosDetalle(codigo, fechaAbono, dmonto);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("conciliados_detalle", List.of()); // fallback vacío en caso de error
        }
    }

    @GetMapping("/tarjetas/izipay/reporte")
    public Map<String, Object> reporteConciliacionTarjetasIzipay(
            @RequestParam(required = false) String codigo, // 🔥 nuevo parámetro opcional
            @RequestParam String fechaIni,
            @RequestParam String fechaFin) {

        Map<String, Object> response = new HashMap<>();

        try {

            Date fechaIniDate = java.sql.Date.valueOf(fechaIni);
            Date fechaFinDate = java.sql.Date.valueOf(fechaFin);

            // 🔥 normalizar codigo
            if (codigo != null && codigo.trim().isEmpty()) {
                codigo = null;
            }

            List<Map<String, Object>> lista =
                    service.reporteConciliacionTarjetasIzipay(codigo, fechaIniDate, fechaFinDate);

            response.put("resultado", "ok");
            response.put("data", lista);

        } catch (Exception e) {

            response.put("resultado", "error");
            response.put("mensaje", "Error en formato de fechas");
            response.put("error_tecnico", e.getMessage());
        }

        return response;
    }

}
