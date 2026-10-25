package com.bartolito.contabilidad.controller;

import com.bartolito.contabilidad.service.CentroCostoService;
import com.bartolito.contabilidad.service.EmpresaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/centro_costo")
public class CentroCostoController {
    private final CentroCostoService service;

    public CentroCostoController(CentroCostoService service) {
        this.service = service;
    }

    @GetMapping("/proceso/listar")
    public ResponseEntity<Map<String, Object>> listarProcesos() {

        List<Map<String, Object>> result = service.listarProcesos();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("procesos", result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/actividad/listar")
    public ResponseEntity<Map<String, Object>> listarActividades() {

        List<Map<String, Object>> result = service.listarActividades();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("actividades", result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tarea/listar")
    public ResponseEntity<Map<String, Object>> listarTareas() {

        List<Map<String, Object>> result = service.listarTareas();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("tareas", result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/producto-servicio/listar")
    public ResponseEntity<Map<String, Object>> listarProductosServicios() {

        List<Map<String, Object>> result = service.listarProductosServicios();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("productosServicios", result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/centro-costo-responsabilidad/listar")
    public ResponseEntity<Map<String, Object>> listarCentrosCostoResponsabilidad() {

        List<Map<String, Object>> result = service.listarCentrosCostoResponsabilidad();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("centrosCostoResponsabilidad", result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/activo/listar")
    public ResponseEntity<Map<String, Object>> listarActivos() {

        List<Map<String, Object>> result = service.listarActivos();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("activos", result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/centro-costo/listar")
    public ResponseEntity<Map<String, Object>> listarCentrosCosto() {

        List<Map<String, Object>> result =
                service.listarCentrosCosto();

        Map<String, Object> data =
                new LinkedHashMap<>();

        data.put("centrosCosto", result);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);

    }
}
