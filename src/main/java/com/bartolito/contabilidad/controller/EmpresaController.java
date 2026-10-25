package com.bartolito.contabilidad.controller;

import com.bartolito.contabilidad.service.ComprobanteCompraService;
import com.bartolito.contabilidad.service.EmpresaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // ============================================================
// MANTENIMIENTO PROVEEDORES CUARTA CATEGORIA
// ============================================================

    // LISTAR

    @PostMapping("/obtener-lista-proveedores-cuarta-categoria")
    public ResponseEntity<Map<String, Object>>
    listarProveedoresCuartaCategoria(
            @RequestBody(required = false)
            Map<String, Object> request) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            String filtro =
                    request == null || request.get("filtro") == null
                            ? null
                            : request.get("filtro").toString().trim();

            System.out.println("PCC - FILTRO: " + filtro);

            List<Map<String, Object>> datos =
                    service.listarProveedoresCuartaCategoria(filtro);

            response.put("resultado", "ok");
            response.put("data", datos);

            return ResponseEntity.ok(response);

        }
        catch (Exception e) {

            System.err.println("========== ERROR PCC ==========");
            e.printStackTrace();

            Throwable causa = e.getCause();

            while (causa != null) {
                System.err.println(
                        "CAUSA: " + causa.getClass().getName()
                );
                System.err.println(
                        "MENSAJE: " + causa.getMessage()
                );
                causa = causa.getCause();
            }

            if (e instanceof org.springframework.jdbc.UncategorizedSQLException) {

                org.springframework.jdbc.UncategorizedSQLException sqlEx =
                        (org.springframework.jdbc.UncategorizedSQLException) e;

                java.sql.SQLException detalle =
                        sqlEx.getSQLException();

                System.err.println("SQL ERROR CODE: "
                        + detalle.getErrorCode());

                System.err.println("SQL STATE: "
                        + detalle.getSQLState());

                System.err.println("SQL MENSAJE: "
                        + detalle.getMessage());
            }

            response.put("resultado", "error");
            response.put(
                    "mensaje",
                    "No fue posible listar los proveedores."
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }

    }


    // AGREGAR
    @PostMapping("/agregar-proveedor-cuarta-categoria")
    public ResponseEntity<Map<String, Object>>
    agregarProveedorCuartaCategoria(
            @RequestBody Map<String, Object> request) {

        return guardarProveedorCuartaCategoria(request, false);
    }

    // MODIFICAR
    @PostMapping("/modificar-proveedor-cuarta-categoria")
    public ResponseEntity<Map<String, Object>>
    modificarProveedorCuartaCategoria(
            @RequestBody Map<String, Object> request) {

        return guardarProveedorCuartaCategoria(request, true);
    }

    // METODO COMPARTIDO PARA AGREGAR Y MODIFICAR
    private ResponseEntity<Map<String, Object>>
    guardarProveedorCuartaCategoria(
            Map<String, Object> request,
            boolean modificar) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            Map<String, Object> data =
                    service.guardarProveedorCuartaCategoria(
                            modificar, request);

            response.put("resultado", "ok");
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("resultado", "error");
            response.put("mensaje", e.getMessage());

            return ResponseEntity.badRequest().body(response);

        } catch (org.springframework.dao.DataAccessException e) {
            response.put("resultado", "error");
            response.put("mensaje",
                    "No se pudo guardar. Verifique el proveedor, "
                            + "el documento y los valores de los catalogos.");

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);

        } catch (Exception e) {
            response.put("resultado", "error");
            response.put("mensaje",
                    "Error inesperado al guardar el proveedor.");

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}
