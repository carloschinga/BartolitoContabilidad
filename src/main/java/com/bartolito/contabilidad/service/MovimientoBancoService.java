package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.ComprobanteEgresoRepository;
import com.bartolito.contabilidad.repository.MovimientoBancoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MovimientoBancoService {
    @Autowired
    private MovimientoBancoRepository  repository;

    private final MovimientoBancoRepository comprobanteEgresoRepository;

    public MovimientoBancoService(MovimientoBancoRepository comprobanteEgresoRepository) {
        this.comprobanteEgresoRepository = comprobanteEgresoRepository;
    }

    public List<Map<String, Object>> listarMovimientoBanco(
            String empresaId,
            String periodo,
            String movimiento
    ) {
        return repository.listarMovimientoBanco(
                empresaId,
                periodo,
                movimiento
        );

    }
    public List<Map<String, Object>> obtenerComprobante(
            Long diaCabCompId
    ) {

        return repository.obtenerComprobante(
                diaCabCompId
        );

    }
}
