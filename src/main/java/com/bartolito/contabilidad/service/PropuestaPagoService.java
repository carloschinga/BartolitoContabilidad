package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.ComprobanteCompraRepository;
import com.bartolito.contabilidad.repository.PropuestaPagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PropuestaPagoService {
    @Autowired
    private PropuestaPagoRepository repository;

    private final PropuestaPagoRepository propuestaPagoRepository;

    public PropuestaPagoService(PropuestaPagoRepository propuestaPagoRepository) {
        this.propuestaPagoRepository = propuestaPagoRepository;
    }
    public List<Map<String, Object>> listarPropuestaPagoCabecera(
            String empresaId,
            String estado
    ) {

        return repository.listarPropuestaPagoCabecera(
                empresaId,
                estado
        );
    }

    public Map<String, Object> generarPropuestaPago(
            String empresaId,
            BigDecimal total,
            String usuario,
            String detalleXml
    ) {

        return repository.generarPropuestaPago(
                empresaId,
                total,
                usuario,
                detalleXml
        );
    }
}
