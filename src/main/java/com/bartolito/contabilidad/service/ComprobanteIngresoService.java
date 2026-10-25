package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.ComprobanteIngresoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ComprobanteIngresoService {
    @Autowired
    private ComprobanteIngresoRepository repository;

    private final ComprobanteIngresoRepository comprobanteIngresoRepository;

    public ComprobanteIngresoService(ComprobanteIngresoRepository comprobanteIngresoRepository) {
        this.comprobanteIngresoRepository = comprobanteIngresoRepository;
    }

    public List<Map<String, Object>> generarComprobanteIngreso(
            Map<String, Object> request
    ) throws Exception {

        return repository.generarComprobanteIngreso(
                request
        );

    }

}
