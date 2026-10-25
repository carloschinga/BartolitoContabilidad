package com.bartolito.contabilidad.controller;

import com.bartolito.contabilidad.service.SolicitudPagoService;
import com.bartolito.contabilidad.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    private final UsuarioService service;
    public UsuarioController(UsuarioService service) {
        this.service = service;
    }


    @GetMapping("/listarUsuariosLolfar")
    public ResponseEntity<Map<String, Object>> listarUsuariosLolfar(

            @RequestParam Integer usecodLOLFAR

    ) {

        List<Map<String, Object>> result =
                service.listarUsuariosLolfar(
                        usecodLOLFAR
                );

        Map<String, Object> data =
                new LinkedHashMap<>();

        data.put(
                "usuarios",
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


    @GetMapping("/obtenerEquivalenciaUsuarioLolfar")
    public ResponseEntity<Map<String, Object>> obtenerEquivalenciaUsuarioLolfar(

            @RequestParam String usecodLOLFAR

    ) {


        List<Map<String, Object>> result =
                service.obtenerEquivalenciaUsuarioLolfar(
                        usecodLOLFAR
                );


        Map<String, Object> data =
                new LinkedHashMap<>();


        data.put(
                "usuarios",
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
}
