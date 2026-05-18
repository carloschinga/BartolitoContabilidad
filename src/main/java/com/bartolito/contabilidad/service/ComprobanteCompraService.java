package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.ComprobanteCompraRepository;
import com.bartolito.contabilidad.repository.ConciliacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ComprobanteCompraService {
    @Autowired
    private ComprobanteCompraRepository repository;

    private final ComprobanteCompraRepository comprobanteCompraRepository;

    public ComprobanteCompraService(ComprobanteCompraRepository comprobanteCompraRepository) {
        this.comprobanteCompraRepository = comprobanteCompraRepository;
    }

    public List<Map<String, Object>> listarComprobantesCompra(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            String empresaId,
            Boolean estadoPago
    ) {
        return repository.listarComprobantesCompra(fechaInicio, fechaFin, empresaId, estadoPago);
    }

    public List<Map<String, Object>> listarComprobantesCompraxPagar(
            LocalDate fechaVenc,
            String empresaId
    ) {
        return repository.listarComprobantesCompraxPagar(fechaVenc, empresaId);
    }

    public List<Map<String, Object>> listarNCyND(
            String empresaId,
            Long provId
    ) {
        return repository.listarNCyND(empresaId, provId);
    }

    public List<Map<String, Object>> listarAdelantoComprobante(
            String empresaId,
            Long provId,
            String adeCompMovRef
    ) {

        return repository.listarAdelantoComprobante(
                empresaId,
                provId,
                adeCompMovRef
        );
    }


}
