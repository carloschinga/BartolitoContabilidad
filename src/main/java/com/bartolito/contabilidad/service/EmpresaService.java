package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.ConciliacionRepository;
import com.bartolito.contabilidad.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository repository;

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository= empresaRepository;
    }

    public List<Map<String, Object>> listarEmpresas() {
        return repository.listarEmpresas();
    }


}
