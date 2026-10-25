package com.bartolito.contabilidad.controller;


import com.bartolito.contabilidad.service.PropuestaPagoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/propuesta_pago")
public class PropuestaPagoController {
    private final PropuestaPagoService service;
    private final RestTemplate restTemplate;

    public PropuestaPagoController(PropuestaPagoService service, RestTemplate restTemplate) {
        this.service = service;
        this.restTemplate = restTemplate;
    }
    @GetMapping("/listarPropuestaPago/{empresaId}/{estado}/{monedaId}/{fechaInicio}/{fechaFin}")
    public ResponseEntity<Map<String, Object>> listarPropuestaPago(
            @PathVariable String empresaId,
            @PathVariable String estado,
            @PathVariable String monedaId,
            @PathVariable String fechaInicio,
            @PathVariable String fechaFin
    ) {

        List<Map<String, Object>> result =
                service.listarPropuestaPagoCabecera(
                        empresaId,
                        estado,
                        monedaId,
                        fechaInicio,
                        fechaFin
                );

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("propuestas", result);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("resultado", "ok");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/generar_propuesta_pago",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> generarPropuestaPago(

            @RequestParam String empresaId,
            @RequestParam String monedaId,
            @RequestParam BigDecimal total,
            @RequestParam String usuario,
            @RequestBody String detalleXml

    ) {
        try {

            Map<String, Object> response = service.generarPropuestaPago(
                    empresaId,
                    monedaId,
                    total,
                    usuario,
                    detalleXml
            );

            // 🔥 SP devuelve estado 0 = error de negocio, igual retorna 200
            // pero con estado=0 en el body para que el front lo maneje
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            Map<String, Object> error = new HashMap<>();
            error.put("estado", 0);
            error.put("mensaje", e.getMessage());

            return ResponseEntity
                    .badRequest()
                    .body(error);
        }
    }

    @GetMapping("/obtenerPropuestaPago/{propPagoCabId}")
    public ResponseEntity<Map<String, Object>> obtenerPropuestaPago(

            @PathVariable Long propPagoCabId
    ) {

        List<Map<String, Object>> result =
                service.obtenerPropuestaPago(
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

        return ResponseEntity.ok(
                response
        );
    }

    @PutMapping("/rechazarPropuestaPago")
    public ResponseEntity<Map<String, Object>> rechazarPropuestaPago(
            @RequestParam Long propPagoCabId,
            @RequestParam String usrRechaza,
            @RequestParam String obsvRechaza
    ) {

        List<Map<String, Object>> result =
                service.rechazarPropuestaPago(
                        propPagoCabId,
                        usrRechaza,
                        obsvRechaza
                );


        Map<String, Object> data =
                new LinkedHashMap<>();

        data.put(
                "respuesta",
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


        return ResponseEntity.ok(
                response
        );
    }


    @GetMapping(
            "/listarComprobanteCompraDetalle/{provId}/{comCompCabId}"
    )
    public ResponseEntity<Map<String, Object>>
    listarComprobanteCompraDetalle(

            @PathVariable Long provId,

            @PathVariable Long comCompCabId
    ) {

        List<Map<String, Object>> result =
                service.listarComprobanteCompraDetalle(
                        provId,
                        comCompCabId
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

        return ResponseEntity.ok(
                response
        );
    }

    @PostMapping("/aprobar/{propPagoCabId}")
    public ResponseEntity<?> aprobarPropuestaPago(

            @PathVariable Long propPagoCabId,

            @RequestBody Map<String,String> request

    ) {

        try {


            System.out.println("========== CONTROLLER ==========");
            System.out.println("ID RECIBIDO => " + propPagoCabId);
            System.out.println("BODY => " + request);


            String usuario =
                    request.get("usuario");


            System.out.println(
                    "USUARIO RECIBIDO => ["
                            + usuario
                            + "]"
            );


            List<Map<String,Object>> result =
                    service.aprobarPropuestaPago(
                            propPagoCabId,
                            usuario
                    );


            System.out.println(
                    "RESPUESTA SQL => "
                            + result
            );


            return ResponseEntity.ok(result);


        }catch(Exception e){


            System.out.println("======= ERROR REAL =======");

            e.printStackTrace();

            System.out.println("==========================");


            Map<String,Object> error =
                    new HashMap<>();


            error.put(
                    "tipo",
                    e.getClass().getName()
            );


            error.put(
                    "mensaje",
                    e.getMessage()
            );


            return ResponseEntity
                    .status(500)
                    .body(error);

        }

    }

    // ==========================================
// OBTENER RANGO APROBACION
// ==========================================

    @GetMapping("/obtenerRangoAprobacion/{empresaId}/{usuario}")
    public ResponseEntity<Map<String,Object>> obtenerRangoAprobacion(

            @PathVariable String empresaId,

            @PathVariable String usuario

    ) {


        Map<String,Object> result =
                service.obtenerRangoAprobacion(
                        empresaId,
                        usuario
                );



        Map<String,Object> data =
                new LinkedHashMap<>();


        data.put(
                "aprobador",
                result
        );



        Map<String,Object> response =
                new LinkedHashMap<>();


        response.put(
                "resultado",
                "ok"
        );


        response.put(
                "data",
                data
        );



        return ResponseEntity.ok(
                response
        );


    }
    @PutMapping("/anularDetalle")
    public ResponseEntity<?> anularDetalle(
            @RequestParam Long propPagoCabId,
            @RequestParam Integer item,
            @RequestParam String usuario,
            @RequestParam String motivo
    ){

        List<Map<String,Object>> r =
                service.anularDetalle(
                        propPagoCabId,
                        item,
                        usuario,
                        motivo
                );


        return ResponseEntity.ok(r);

    }

    // ==========================================
// PROCESAR APROBACION DETALLE
// ==========================================

    @PostMapping("/procesarDetalle")
    public ResponseEntity<?> procesarDetalle(

            @RequestBody Map<String,Object> request

    ){

        try {


            String usuario =
                    request.get("usuario").toString();


            String observacion =
                    request.get("observacion") == null
                            ? ""
                            :
                            request.get("observacion").toString();


            List<Map<String,Object>> detalle =
                    (List<Map<String,Object>>)
                            request.get("detalle");



            service.procesarDetalle(
                    usuario,
                    observacion,
                    detalle
            );



            Map<String,Object> resp =
                    new LinkedHashMap<>();


            resp.put(
                    "estado",
                    1
            );


            resp.put(
                    "mensaje",
                    "Proceso realizado correctamente"
            );



            return ResponseEntity.ok(
                    resp
            );


        }catch(Exception e){


            Map<String,Object> error =
                    new HashMap<>();


            error.put(
                    "estado",
                    0
            );


            error.put(
                    "mensaje",
                    e.getMessage()
            );


            return ResponseEntity
                    .status(500)
                    .body(error);

        }

    }
}