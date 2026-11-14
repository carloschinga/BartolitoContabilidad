package com.bartolito.contabilidad.service.insanor;

import com.bartolito.contabilidad.repository.insanor.SumatoriaInsanorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class SumatoriaInsanorService {

    @Autowired
    private SumatoriaInsanorRepository repository;

    // =========================================
    // SUMATORIA FORMA DE PAGO - GUARDAR / ACTUALIZAR
    // =========================================
    public List<Map<String, Object>> saveOrUpdate(
            Integer sumatoriaFormaPagoId,
            Integer operacionId,
            Integer siscod,
            String fechaOperacion,
            Integer invnumAper,
            String docpag,
            String docdes,
            Integer cantidad,
            BigDecimal importeTotal,
            BigDecimal importeNC,
            BigDecimal total,
            Boolean estado,
            String usuario) {

        return repository.saveOrUpdate(
                sumatoriaFormaPagoId,
                operacionId,
                siscod,
                fechaOperacion,
                invnumAper,
                docpag,
                docdes,
                cantidad,
                importeTotal,
                importeNC,
                total,
                estado,
                usuario
        );
    }

    // =========================================
    // SUMATORIA FORMA DE PAGO - LISTAR POR OPERACIÓN
    // =========================================
    public List<Map<String, Object>> listarSumatoria(Integer operacionId) {
        return repository.listarSumatoria(operacionId);
    }

    // =========================================
    // SUMATORIA FORMA DE PAGO - LISTAR EFECTIVO
    // =========================================
    public List<Map<String, Object>> listarEfectivo(LocalDate fechaOperacion) {
        return repository.listarEfectivo(fechaOperacion);
    }

    // =========================================
    // SUMATORIA FORMA DE PAGO - LISTAR HISTORIAL
    // =========================================
    public List<Map<String, Object>> listarHistorialSumatorias(
            String fechaInicio,
            String fechaFin) {

        return repository.listarHistorialSumatorias(fechaInicio, fechaFin);
    }
}
