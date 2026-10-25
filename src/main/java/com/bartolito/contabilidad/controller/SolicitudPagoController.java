package com.bartolito.contabilidad.controller;

import com.bartolito.contabilidad.service.PropuestaPagoService;
import com.bartolito.contabilidad.service.SolicitudPagoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/solicitud_pago")
public class SolicitudPagoController {
    private final SolicitudPagoService service;
    public SolicitudPagoController(SolicitudPagoService service) {
        this.service = service;
    }
    // =========================================
    // LISTAR
    // =========================================
    @GetMapping("/listar")
    public ResponseEntity<?> listarSolicitudPago(

            @RequestParam(required = false)
            String empresaId,

            @RequestParam(required = false)
            String monedaId,

            @RequestParam(required = false)
            String fechaInicio,

            @RequestParam(required = false)
            String fechaFin,

            @RequestParam(required = false)
            String estado,

            @RequestParam(required = false)
            String usuario
    ) {

        // =====================================
        // NORMALIZAR PARÁMETROS
        // =====================================

        empresaId = (empresaId != null && !empresaId.isBlank())
                ? empresaId.trim()
                : null;

        monedaId = (monedaId != null && !monedaId.isBlank())
                ? monedaId.trim()
                : null;

        fechaInicio = (fechaInicio != null && !fechaInicio.isBlank())
                ? fechaInicio.trim()
                : null;

        fechaFin = (fechaFin != null && !fechaFin.isBlank())
                ? fechaFin.trim()
                : null;

        estado = (estado != null && !estado.isBlank())
                ? estado.trim()
                : null;

        usuario = (usuario != null && !usuario.isBlank())
                ? usuario.trim()
                : null;


        // =====================================
        // EJECUTAR
        // =====================================

        return ResponseEntity.ok(
                service.listarSolicitudPago(
                        empresaId,
                        monedaId,
                        fechaInicio,
                        fechaFin,
                        estado,
                        usuario
                )
        );
    }

    // =========================================
    // CONTROLLER
    // =========================================

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarSolicitudPago(

            @RequestParam String empresaId,

            @RequestParam Integer tipSolPagoId,

            @RequestParam(required = false) Integer tipConCajId,

            @RequestParam(required = false) Integer conCajId,

            @RequestParam(required = false) Long ctaCteId,

            @RequestParam(required = false) Long provId,

            @RequestParam(required = false) String trabId,

            @RequestParam(required = false) String trabIdSoli,

            @RequestParam String descripcion,

            @RequestParam String fecha,

            @RequestParam(required = false) String documento,

            @RequestParam(required = false) MultipartFile archivo1,

            @RequestParam(required = false) MultipartFile archivo2,

            @RequestParam BigDecimal total,

            @RequestParam(required = false) String procId,

            @RequestParam(required = false) String activId,

            @RequestParam(required = false) String tareaId,

            @RequestParam(required = false) String activoId,

            @RequestParam(required = false) String prodId,

            @RequestParam(required = false) String cenCostResp,

            @RequestParam String usuario
    ) {

        descripcion =
                (descripcion == null || descripcion.isBlank())
                        ? ""
                        : descripcion.trim();

        documento =
                (documento == null || documento.isBlank())
                        ? null
                        : documento.trim();



        trabId =
                (trabId == null || trabId.isBlank())
                        ? null
                        : trabId.trim();

        trabIdSoli =
                (trabIdSoli == null || trabIdSoli.isBlank())
                        ? null
                        : trabIdSoli.trim();

        return ResponseEntity.ok(

                service.registrarSolicitudPago(

                        empresaId,

                        tipSolPagoId,

                        tipConCajId,

                        conCajId,

                        ctaCteId,

                        provId,

                        trabId,

                        trabIdSoli,

                        descripcion,

                        fecha,

                        documento,

                        archivo1,

                        archivo2,

                        total,

                        procId,

                        activId,

                        tareaId,

                        activoId,

                        prodId,

                        cenCostResp,

                        usuario
                )
        );

    }

    // =========================================
    // ANULAR
    // =========================================
    @PostMapping("/anular")
    public ResponseEntity<?> anularSolicitudPago(

            @RequestParam Long soliPagoCabId,

            @RequestParam String usuario
    ) {

        return ResponseEntity.ok(
                service.anularSolicitudPago(
                        soliPagoCabId,
                        usuario
                )
        );
    }


// ==========================================
// CONTROLLER
// ==========================================

    @GetMapping("/listarTSP")
    public Map<String, Object> listarTipoSolicitudPago() {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Map<String, Object>> lista =
                    service.listarTipoSolicitudPago();

            response.put("resultado", "ok");

            response.put("data", lista);

        } catch (Exception e) {

            response.put("resultado", "error");

            response.put(
                    "mensaje",
                    "Error al listar tipos de solicitud de pago"
            );

            response.put(
                    "error_tecnico",
                    e.getMessage()
            );

        }

        return response;

    }

    // ==========================================
// LISTAR PROVEEDORES
// ==========================================

    @GetMapping("/listarProveedor")
    public Map<String, Object> listarProveedor() {

        Map<String, Object> response = new HashMap<>();

        try {

            List<Map<String, Object>> lista =
                    service.listarProveedor();

            response.put("resultado", "ok");

            response.put("data", lista);

        } catch (Exception e) {

            response.put("resultado", "error");

            response.put(
                    "mensaje",
                    "Error al listar proveedores"
            );

            response.put(
                    "error_tecnico",
                    e.getMessage()
            );

        }

        return response;

    }

    // =========================================
// CONTROLLER
// =========================================

    @GetMapping("/listarConceptoCaja")
    public Map<String, Object> listarConceptoCaja(

            @RequestParam Integer tipConCajID,

            @RequestParam Integer tipSolCajId
    ) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            List<Map<String, Object>> lista =
                    service.listarConceptoCaja(
                            tipConCajID,
                            tipSolCajId
                    );

            response.put(
                    "resultado",
                    "ok"
            );

            response.put(
                    "data",
                    lista
            );

        } catch (Exception e) {

            response.put(
                    "resultado",
                    "error"
            );

            response.put(
                    "mensaje",
                    "Error al listar conceptos caja"
            );

            response.put(
                    "error_tecnico",
                    e.getMessage()
            );

        }

        return response;

    }

// ==========================================
// LISTAR TRABAJADORES
// ==========================================

    @GetMapping("/listarTrabajador")
    public Map<String, Object> listarTrabajador(
            @RequestParam String empresaId
    ) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            List<Map<String, Object>> lista =
                    service.listarTrabajador(
                            empresaId
                    );

            response.put(
                    "resultado",
                    "ok"
            );

            response.put(
                    "data",
                    lista
            );

        } catch (Exception e) {

            response.put(
                    "resultado",
                    "error"
            );

            response.put(
                    "mensaje",
                    "Error al listar trabajadores"
            );

            response.put(
                    "error_tecnico",
                    e.getMessage()
            );

        }

        return response;

    }

    // =========================================
// CONTROLLER
// =========================================

    @GetMapping("/listarDepartamentoFuncional")
    public Map<String, Object> listarDepartamentoFuncional() {

        Map<String, Object> response =
                new HashMap<>();

        try {

            List<Map<String, Object>> lista =
                    service.listarDepartamentoFuncional();

            response.put(
                    "resultado",
                    "ok"
            );

            response.put(
                    "data",
                    lista
            );

        } catch (Exception e) {

            response.put(
                    "resultado",
                    "error"
            );

            response.put(
                    "mensaje",
                    "Error al listar departamentos funcionales"
            );

            response.put(
                    "error_tecnico",
                    e.getMessage()
            );

        }

        return response;

    }

    // =========================================
// OBTENER
// =========================================
    @GetMapping("/obtener")
    public ResponseEntity<?> obtenerSolicitudPago(

            @RequestParam Long soliPagoCabId
    ) {

        return ResponseEntity.ok(
                service.obtenerSolicitudPago(
                        soliPagoCabId
                )
        );
    }


    // =========================================
// VER ARCHIVOS DE SOLICITUD
// =========================================
    @GetMapping("/verArchivo")
    public ResponseEntity<Resource> verArchivo(

            @RequestParam String archivo

    ) throws Exception {


        Path base =
                Paths.get("/opt/uploads")
                        .toAbsolutePath()
                        .normalize();


        Path ruta =
                base.resolve(archivo)
                        .normalize();


        if (!ruta.startsWith(base)) {

            return ResponseEntity
                    .badRequest()
                    .build();
        }


        Resource resource =
                new UrlResource(
                        ruta.toUri()
                );


        if (!resource.exists()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }



        // =====================================
        // DETECTAR PDF, TXT, ETC
        // =====================================

        String contentType =
                Files.probeContentType(
                        ruta
                );


        if (contentType == null) {

            contentType =
                    "application/octet-stream";
        }



        return ResponseEntity.ok()


                .contentType(

                        MediaType.parseMediaType(
                                contentType
                        )

                )


                // inline = visualizar
                // attachment = descargar

                .header(

                        HttpHeaders.CONTENT_DISPOSITION,

                        "inline; filename=\""
                                +
                                resource.getFilename()
                                +
                                "\""

                )


                .body(resource);

    }
}
