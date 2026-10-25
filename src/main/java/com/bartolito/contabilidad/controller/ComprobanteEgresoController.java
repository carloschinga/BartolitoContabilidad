package com.bartolito.contabilidad.controller;


import com.bartolito.contabilidad.service.ComprobanteEgresoService;
import com.bartolito.contabilidad.service.PropuestaPagoService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comprobante_egreso")
public class ComprobanteEgresoController {
    private final ComprobanteEgresoService service;
    private final RestTemplate restTemplate;

    public ComprobanteEgresoController(ComprobanteEgresoService service, RestTemplate restTemplate) {
        this.service = service;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/obtenerComprobanteEgreso/{cuenta}/{periodo}/{diario}")
    public ResponseEntity<Map<String, Object>> obtenerComprobanteEgreso(

            @PathVariable String cuenta,

            @PathVariable String periodo,

            @PathVariable String diario

    ) {


        List<Map<String, Object>> result =
                service.obtenerComprobanteEgreso(
                        cuenta,
                        periodo,
                        diario
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

    @GetMapping("/obtenerCuentaBancaria/{cuentaBanco}")
    public ResponseEntity<Map<String, Object>> obtenerCuentaBancaria(

            @PathVariable String cuentaBanco

    ) {


        List<Map<String, Object>> result =
                service.obtenerCuentaBancaria(
                        cuentaBanco
                );


        Map<String, Object> data =
                new LinkedHashMap<>();


        data.put(
                "cuenta",
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
    @PostMapping("/buscarPropuestaDesdeBCP")
    public ResponseEntity<Map<String, Object>> buscarPropuestaDesdeBCP(
            @RequestBody Map<String, Object> request
    ) {

        try {

            System.out.println("====================================");
            System.out.println("CONTROLLER - BUSCAR PROPUESTA BCP");
            System.out.println("REQUEST:");
            System.out.println(request);
            System.out.println("====================================");


            String empresaId =
                    request.get("empresaId").toString();


            BigDecimal total =
                    new BigDecimal(
                            request.get("total").toString()
                    );


            String detalle =
                    request.get("detalle").toString();


            List<Map<String, Object>> result =
                    service.buscarPropuestaDesdeBCP(
                            empresaId,
                            total,
                            detalle
                    );


            Map<String, Object> data =
                    new LinkedHashMap<>();


            data.put(
                    "propuesta",
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


            System.out.println("====================================");
            System.out.println("RESPUESTA:");
            System.out.println(response);
            System.out.println("====================================");


            return ResponseEntity.ok(response);


        } catch (Exception e) {

            System.err.println("====================================");
            System.err.println("ERROR CONTROLLER BUSCAR PROPUESTA BCP");
            System.err.println("====================================");

            e.printStackTrace();

            System.err.println("====================================");


            Map<String, Object> response =
                    new LinkedHashMap<>();


            response.put(
                    "resultado",
                    "error"
            );


            response.put(
                    "mensaje",
                    e.getMessage()
            );


            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
    @PostMapping("/buscarPropuestaHaberes")
    public ResponseEntity<Map<String, Object>> buscarPropuestaHaberes(

            @RequestBody Map<String, Object> request

    ) {

        String empresaId =
                request.get("empresaId").toString();

        BigDecimal total =
                new BigDecimal(
                        request.get("total").toString()
                );

        List<Map<String, Object>> result =
                service.buscarPropuestaHaberes(

                        empresaId,

                        total

                );

        Map<String, Object> data =
                new LinkedHashMap<>();

        data.put(
                "propuesta",
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

    @GetMapping("/obtenerDetallePropuesta/{propPagoCabId}")
    public ResponseEntity<Map<String, Object>> obtenerDetallePropuesta(

            @PathVariable Integer propPagoCabId

    ) {


        List<Map<String, Object>> result =
                service.obtenerDetallePropuesta(
                        propPagoCabId
                );


        Map<String, Object> data =
                new LinkedHashMap<>();


        data.put(
                "detalle",
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
    @PostMapping("/generarComprobanteEgreso")
    public ResponseEntity<Map<String, Object>> generarComprobanteEgreso(
            @RequestBody Map<String, Object> request
    ) {

        Map<String, Object> response = new LinkedHashMap<>();

        try {

            // ==========================================
            // 1. VALIDAR PARÁMETROS OBLIGATORIOS
            // ==========================================

            String[] camposObligatorios = {
                    "PropPagoCabId",
                    "CtaBancEmpId",
                    "FechaPago",
                    "Glosa",
                    "Usuario",
                    "NumeroPlanilla",
                    "DetalleBancoXml"
            };

            for (String campo : camposObligatorios) {

                Object valor = request.get(campo);

                if (valor == null ||
                        valor.toString().trim().isEmpty()) {

                    response.put("resultado", "error");
                    response.put(
                            "mensaje",
                            "Falta el parámetro obligatorio: " + campo
                    );

                    return ResponseEntity.badRequest().body(response);
                }
            }

            // ==========================================
            // 2. VALIDAR FORMATO DEL XML BCP
            // ==========================================

            String detalleBancoXml =
                    request.get("DetalleBancoXml").toString().trim();

            try {

                javax.xml.parsers.DocumentBuilderFactory factory =
                        javax.xml.parsers.DocumentBuilderFactory.newInstance();

                factory.setFeature(
                        "http://apache.org/xml/features/disallow-doctype-decl",
                        true
                );

                factory.setFeature(
                        "http://xml.org/sax/features/external-general-entities",
                        false
                );

                factory.setFeature(
                        "http://xml.org/sax/features/external-parameter-entities",
                        false
                );

                factory.setXIncludeAware(false);
                factory.setExpandEntityReferences(false);

                javax.xml.parsers.DocumentBuilder builder =
                        factory.newDocumentBuilder();

                org.w3c.dom.Document documento =
                        builder.parse(
                                new org.xml.sax.InputSource(
                                        new java.io.StringReader(detalleBancoXml)
                                )
                        );

                if (!"Pagos".equals(
                        documento.getDocumentElement().getTagName())) {

                    throw new IllegalArgumentException(
                            "La raíz del XML debe ser <Pagos>."
                    );
                }

                org.w3c.dom.NodeList pagos =
                        documento.getDocumentElement()
                                .getElementsByTagName("Pago");

                if (pagos.getLength() == 0) {

                    throw new IllegalArgumentException(
                            "El XML BCP no contiene pagos."
                    );
                }

                java.util.Set<Integer> items =
                        new java.util.HashSet<>();

                for (int i = 0; i < pagos.getLength(); i++) {

                    org.w3c.dom.Element pago =
                            (org.w3c.dom.Element) pagos.item(i);

                    String itemTexto = pago
                            .getElementsByTagName("PropPagoDetItem")
                            .item(0)
                            .getTextContent()
                            .trim();

                    String estado = pago
                            .getElementsByTagName("Estado")
                            .item(0)
                            .getTextContent()
                            .trim()
                            .toUpperCase(java.util.Locale.ROOT);

                    int item = Integer.parseInt(itemTexto);

                    if (item <= 0 || !items.add(item)) {

                        throw new IllegalArgumentException(
                                "Ítem BCP inválido o duplicado: " + itemTexto
                        );
                    }

                    if (!"PROCESADA".equals(estado) &&
                            !"RECHAZADA".equals(estado)) {

                        throw new IllegalArgumentException(
                                "Estado BCP no permitido: " + estado
                        );
                    }
                }

            } catch (Exception ex) {

                response.put("resultado", "error");
                response.put(
                        "mensaje",
                        "DetalleBancoXml inválido: " + ex.getMessage()
                );

                return ResponseEntity.badRequest().body(response);
            }

            // El servidor fija el modo; no confiar en el valor del navegador.
            request.put("Debug", 0);

            // ==========================================
            // 4. EJECUTAR SERVICE
            // ==========================================

            List<Map<String, Object>> result =
                    service.generarComprobanteEgreso(request);

            // El SP puede haber confirmado COMMIT aunque la respuesta JDBC falle.
            // Solo indicar éxito si el SP devuelve explícitamente Resultado=OK.
            boolean confirmado = false;
            if (result != null && !result.isEmpty()) {
                Object estado = result.get(0).get("Resultado");
                confirmado = estado != null &&
                        "OK".equalsIgnoreCase(estado.toString().trim());
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("comprobante", result);
            data.put("simulacion", false);

            response.put("resultado",
                    confirmado ? "ok" : "verificacion_pendiente");
            response.put("mensaje", confirmado
                    ? "El procedimiento confirmó la generación."
                    : "No se recibió confirmación explícita Resultado=OK. " +
                    "Consulte los movimientos en SQL Server antes de reintentar.");
            response.put("data", data);
            return ResponseEntity.ok(response);

        } catch (DataAccessException ex) {

            Throwable causa = ex.getMostSpecificCause();

            response.put("resultado", "error");
            response.put(
                    "mensaje",
                    causa != null ? causa.getMessage() : ex.getMessage()
            );

            return ResponseEntity.status(500).body(response);

        } catch (Exception ex) {

            response.put("resultado", "error");
            response.put("mensaje", ex.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/obtenerCorreosPropuesta/{propPagoCabId}/{movBcoCabId}")
    public ResponseEntity<Map<String, Object>> obtenerCorreosPropuesta(
            @PathVariable Integer propPagoCabId,
            @PathVariable Long movBcoCabId
    ) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        try {

            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "OBTENER CORREOS PROPUESTA"
            );

            System.out.println(
                    "PropPagoCabId = " + propPagoCabId
            );

            System.out.println(
                    "MovBcoCabId = " + movBcoCabId
            );

            System.out.println(
                    "======================================"
            );

            List<Map<String, Object>> result =
                    service.obtenerCorreosPropuesta(
                            propPagoCabId,
                            movBcoCabId
                    );

            response.put(
                    "resultado",
                    "ok"
            );

            response.put(
                    "data",
                    result
            );

            return ResponseEntity.ok(response);

        } catch (Exception ex) {

            ex.printStackTrace();

            response.put(
                    "resultado",
                    "error"
            );

            response.put(
                    "mensaje",
                    ex.getMessage()
            );

            return ResponseEntity
                    .internalServerError()
                    .body(response);
        }
    }

    @GetMapping("/probarEnvioCorreosPropuesta/{propPagoCabId}/{movBcoCabId}")
    public ResponseEntity<Map<String, Object>> probarEnvioCorreosPropuesta(
            @PathVariable Integer propPagoCabId,
            @PathVariable Long movBcoCabId
    ) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        try {

            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "PROBAR ENVIO CORREOS PROPUESTA"
            );

            System.out.println(
                    "PropPagoCabId = " + propPagoCabId
            );

            System.out.println(
                    "MovBcoCabId = " + movBcoCabId
            );

            System.out.println(
                    "======================================"
            );


            List<Map<String, Object>> result =
                    service.enviarCorreosPropuesta(
                            propPagoCabId,
                            movBcoCabId
                    );


            response.put(
                    "resultado",
                    "ok"
            );

            response.put(
                    "mensaje",
                    "Método ejecutado correctamente."
            );

            response.put(
                    "cantidad",
                    result.size()
            );

            response.put(
                    "data",
                    result
            );


            return ResponseEntity.ok(response);


        } catch (Exception ex) {

            ex.printStackTrace();

            response.put(
                    "resultado",
                    "error"
            );

            response.put(
                    "mensaje",
                    ex.getMessage()
            );

            return ResponseEntity
                    .internalServerError()
                    .body(response);
        }
    }


    // Consulta segura: NO envia correos ni ejecuta el SP financiero.
    @GetMapping("/previsualizarReenvioCorreos/{propPagoCabId}")
    public ResponseEntity<Map<String, Object>> previsualizarReenvioCorreos(
            @PathVariable Integer propPagoCabId) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> filas = service.obtenerCorreosReenvioValidados(propPagoCabId);
            respuesta.put("resultado", "ok");
            respuesta.put("cantidad", filas.size());
            respuesta.put("data", filas);
            respuesta.put("mensaje", "Consulta validada. No se enviaron correos.");
            return ResponseEntity.ok(respuesta);
        } catch (Exception ex) {
            respuesta.put("resultado", "error");
            respuesta.put("mensaje", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    // Endpoint reservado: se mantiene cerrado hasta verificar PDF y permisos.
    @PostMapping("/reenviarCorreosPropuesta")
    public ResponseEntity<Map<String, Object>> reenviarCorreosPropuesta(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        if (httpRequest.getUserPrincipal() == null) {
            respuesta.put("resultado", "prohibido");
            respuesta.put("mensaje", "Debe iniciar sesion para reenviar correos.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
        }
        try {
            if (request == null || !Boolean.TRUE.equals(request.get("ConfirmacionBCP"))) {
                throw new IllegalArgumentException("Debe confirmar previamente la conciliacion del PDF BCP.");
            }
            Object valor = request.get("PropPagoCabId");
            if (valor == null) {
                throw new IllegalArgumentException("Falta PropPagoCabId.");
            }
            Integer propPagoCabId = Integer.valueOf(valor.toString());
            Map<String, Object> resultado = service.reenviarCorreosPropuesta(propPagoCabId);
            respuesta.put("resultado", "ok");
            respuesta.put("data", resultado);
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException ex) {
            respuesta.put("resultado", "error");
            respuesta.put("mensaje", ex.getMessage());
            return ResponseEntity.badRequest().body(respuesta);
        } catch (IllegalStateException ex) {
            respuesta.put("resultado", "bloqueado");
            respuesta.put("mensaje", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
        } catch (Exception ex) {
            respuesta.put("resultado", "error");
            respuesta.put("mensaje", "No se pudo validar el reenvio.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }
    // Prueba real: SOLO propuesta 124 / item 1 / buzón fijo.
    // El servicio exige además bartolito.correo.prueba.habilitada=true.
    @PostMapping("/probarReenvioCorreoPago")
    public ResponseEntity<Map<String, Object>> probarReenvioCorreoPago(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        if (httpRequest.getUserPrincipal() == null) {
            respuesta.put("resultado", "prohibido");
            respuesta.put("mensaje", "Usuario no autorizado para prueba de correo.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(respuesta);
        }
        try {
            if (request == null || request.get("PropPagoCabId") == null
                    || request.get("PropPagoDetItem") == null) {
                throw new IllegalArgumentException("Faltan PropPagoCabId y PropPagoDetItem.");
            }
            Integer id = Integer.valueOf(request.get("PropPagoCabId").toString());
            Integer item = Integer.valueOf(request.get("PropPagoDetItem").toString());
            respuesta.put("resultado", "ok");
            respuesta.put("data", service.probarReenvioCorreoPago(id, item));
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException ex) {
            respuesta.put("resultado", "error");
            respuesta.put("mensaje", ex.getMessage());
            return ResponseEntity.badRequest().body(respuesta);
        } catch (IllegalStateException ex) {
            respuesta.put("resultado", "bloqueado");
            respuesta.put("mensaje", ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
        } catch (Exception ex) {
            respuesta.put("resultado", "error");
            respuesta.put("mensaje", "No se pudo publicar el evento de prueba.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }
}

