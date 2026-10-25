package com.bartolito.contabilidad.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bartolito.contabilidad.repository.ConciliacionBancariaRepository;

@Service
public class ConciliacionBancariaService {

    private final ConciliacionBancariaRepository repository;

    public ConciliacionBancariaService(
            ConciliacionBancariaRepository repository) {

        this.repository = repository;
    }

    public List<Map<String, Object>> listarCuentasBancarias() {

        return repository.listarCuentasBancarias();
    }

    public List<Map<String, Object>> listarAsientos(
            String cuenta,
            Integer mes,
            Integer ano) {

        return repository.listarAsientos(
                cuenta,
                mes,
                ano
        );
    }
}