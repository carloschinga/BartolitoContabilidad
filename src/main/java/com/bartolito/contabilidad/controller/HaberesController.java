package com.bartolito.contabilidad.controller;

import com.bartolito.contabilidad.service.HaberesService;
import com.bartolito.contabilidad.service.SolicitudPagoService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/haberes")
public class HaberesController {
    private final HaberesService service;
    public HaberesController(HaberesService service) {
        this.service = service;
    }
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarHaberes(

            @RequestParam String empresaId,

            @RequestParam Integer tipConCajId,

            @RequestParam Integer conCajId,

            @RequestParam String periodo,

            @RequestParam String descripcion,

            @RequestParam String fecha,

            @RequestParam(required = false) String fechaProceso,

            @RequestParam String subTipo,

            @RequestParam String moneda,

            @RequestParam String cuentaCargo,

            @RequestParam BigDecimal total,

            @RequestParam(required = false) String archivoTxt,

            @RequestParam String usuario,

            @RequestParam String detalle

    ) {

        descripcion =
                descripcion == null ? "" : descripcion.trim();

        periodo =
                periodo == null ? "" : periodo.trim();

        subTipo =
                subTipo == null ? "" : subTipo.trim().toUpperCase();

        moneda =
                moneda == null ? "" : moneda.trim();

        cuentaCargo =
                cuentaCargo == null ? "" : cuentaCargo.trim();

        archivoTxt =
                (archivoTxt == null || archivoTxt.isBlank())
                        ? null
                        : archivoTxt.trim();

        return ResponseEntity.ok(

                service.registrarHaberes(

                        empresaId,

                        tipConCajId,

                        conCajId,

                        periodo,

                        descripcion,

                        fecha,

                        fechaProceso,

                        subTipo,

                        moneda,

                        cuentaCargo,

                        total,

                        archivoTxt,

                        usuario,

                        detalle
                )

        );

    }

    @GetMapping("/listar")
    public ResponseEntity<?> listar(
            @RequestParam(required = false) String empresaId,
            @RequestParam(required = false) String monedaId,
            @RequestParam(required = false) String periodo,
            @RequestParam(required = false) String estado){

        Map<String,Object> response = new LinkedHashMap<>();

        response.put("resultado", "ok");

        response.put(
                "data",
                service.listarHaberes(
                        empresaId,
                        monedaId,
                        periodo,
                        estado
                )
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/obtener/{habCabId}")
    public ResponseEntity<?> obtener(@PathVariable Long habCabId){
        Map<String,Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", service.obtenerHaberes(habCabId));
        return ResponseEntity.ok(response);

    }
}
