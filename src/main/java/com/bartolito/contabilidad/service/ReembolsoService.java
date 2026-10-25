package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.ReembolsoRepository;
import com.bartolito.contabilidad.repository.SolicitudRendicionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class ReembolsoService {
    @Autowired
    private ReembolsoRepository repository;

    public List<Map<String, Object>> listarReembolsoPendientes(
            String empresaId,
            String monedaId,
            String fechaEmision) {

        empresaId = (empresaId == null || empresaId.isBlank())
                ? null
                : empresaId.trim();

        monedaId = (monedaId == null || monedaId.isBlank())
                ? null
                : monedaId.trim();

        fechaEmision = (fechaEmision == null || fechaEmision.isBlank())
                ? null
                : fechaEmision.trim();

        return repository.listarReembolsoPendientes(
                empresaId,
                monedaId,
                fechaEmision
        );
    }

}
