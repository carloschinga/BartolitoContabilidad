package com.bartolito.contabilidad.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bartolito.contabilidad.repository.BartSumatoriaFormaPagoRepository;

@Service
public class BartSumatoriaFormaPagoService {

    private final BartSumatoriaFormaPagoRepository repository;

    public BartSumatoriaFormaPagoService(
            BartSumatoriaFormaPagoRepository repository) {

        this.repository = repository;
    }

    public List<Map<String, Object>> listarEfectivo() {

        return repository.listarEfectivo();
    }
}