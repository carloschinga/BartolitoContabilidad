package com.bartolito.contabilidad.service;


import com.bartolito.contabilidad.repository.PropuestaPagoAprobadorRepository;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;



@Service
public class PropuestaPagoAprobadorService {


    private final PropuestaPagoAprobadorRepository repository;



    public PropuestaPagoAprobadorService(
            PropuestaPagoAprobadorRepository repository
    ){

        this.repository = repository;

    }




    public List<Map<String,Object>> listar(
            String empresaId
    ){

        return repository.listar(
                empresaId
        );

    }





    public Map<String,Object> obtener(
            Long aproId
    ){

        return repository.obtener(
                aproId
        );

    }






    public List<Map<String,Object>> guardar(
            Map<String,Object> datos
    ){

        return repository.guardar(
                datos
        );

    }


}