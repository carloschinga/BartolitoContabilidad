package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.FondoFijoRepository;
import com.bartolito.contabilidad.repository.SolicitudRendicionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class FondoFijoService {
    @Autowired
    private FondoFijoRepository repository;

    public List<Map<String, Object>> listarReposicionFondoFijoPendientes(
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

        return repository.listarReposicionFondoFijoPendientes(
                empresaId,
                monedaId,
                fechaEmision
        );
    }
}
