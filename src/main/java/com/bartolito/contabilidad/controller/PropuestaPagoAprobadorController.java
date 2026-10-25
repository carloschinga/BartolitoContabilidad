package com.bartolito.contabilidad.controller;


import com.bartolito.contabilidad.service.PropuestaPagoAprobadorService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/propuesta_pago_aprobador")
public class PropuestaPagoAprobadorController {


    private final PropuestaPagoAprobadorService service;


    public PropuestaPagoAprobadorController(
            PropuestaPagoAprobadorService service
    ){
        this.service = service;
    }

    @GetMapping({
            "/listar",
            "/listar/{empresaId}"
    })
    public ResponseEntity<Map<String,Object>> listar(
            @PathVariable(required = false) String empresaId
    ){


        if(
                empresaId == null ||
                        empresaId.equals("0")
        ){

            empresaId = null;

        }


        List<Map<String,Object>> result =
                service.listar(empresaId);



        Map<String,Object> data =
                new LinkedHashMap<>();


        data.put(
                "aprobadores",
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


        return ResponseEntity.ok(response);

    }






    @GetMapping("/obtener/{aproId}")
    public ResponseEntity<Map<String,Object>> obtener(
            @PathVariable Long aproId
    ){


        Map<String,Object> result =
                service.obtener(aproId);


        Map<String,Object> data =
                new LinkedHashMap<>();

        data.put(
                "aprobador",
                result
        );


        Map<String,Object> response =
                new LinkedHashMap<>();

        response.put("resultado","ok");
        response.put("data",data);


        return ResponseEntity.ok(response);

    }







    @PostMapping("/guardar")
    public ResponseEntity<Map<String,Object>> guardar(
            @RequestBody Map<String,Object> datos
    ){


        List<Map<String,Object>> result =
                service.guardar(datos);



        Map<String,Object> data =
                new LinkedHashMap<>();


        data.put(
                "registro",
                result
        );


        Map<String,Object> response =
                new LinkedHashMap<>();

        response.put("resultado","ok");
        response.put("data",data);


        return ResponseEntity.ok(response);

    }


}