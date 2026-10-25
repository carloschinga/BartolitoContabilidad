package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.LetrasRepository;
import com.bartolito.contabilidad.repository.SolicitudRendicionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Service
public class SolicitudRendicionService {
    @Autowired
    private SolicitudRendicionRepository  repository;

    public List<Map<String, Object>> listarSolicitudRendicionPendientes(
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

        return repository.listarSolicitudRendicionPendientes(
                empresaId,
                monedaId,
                fechaEmision
        );
    }
}
