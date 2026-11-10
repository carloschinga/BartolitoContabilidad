package com.bartolito.contabilidad.service.insanor;

import com.bartolito.contabilidad.repository.insanor.ConciliacionInsanorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ConciliacionInsanorService {

    @Autowired
    private ConciliacionInsanorRepository repository;

    // =========================================
    // CONCILIACIÓN - LISTAR SUCURSALES
    // =========================================
    public List<Map<String, Object>> listarSucursales() {

        return repository.listarSucursales();
    }

    // =========================================
    // CONCILIACIÓN - LISTAR FECHAS
    // =========================================
    public List<Map<String, Object>> listarFechas(
            String codigo,
            Integer mes,
            Integer anio
    ) {

        return repository.listarFechas(
                codigo,
                mes,
                anio
        );
    }

    // =========================================
    // CONCILIACIÓN - LISTAR NETO TOTAL
    // =========================================
    public List<Map<String, Object>> listarNetoTotal(
            String codigo,
            String fechaAbono
    ) {

        return repository.listarNetoTotal(
                codigo,
                fechaAbono
        );
    }

    // =========================================
    // CONCILIACIÓN - EXISTE CONCILIADO
    // =========================================
    public List<Map<String, Object>> existeConciliado(
            String codigo,
            String fechaAbono,
            BigDecimal monto
    ) {

        return repository.existeConciliado(
                codigo,
                fechaAbono,
                monto
        );
    }

    // =========================================
    // CONCILIACIÓN - COMPROBANTES CONCILIADOS CABECERA
    // =========================================
    public List<Map<String, Object>> listarComprobantesConciliadosCabecera(
            String codigo,
            String fechaAbono,
            BigDecimal monto
    ) {

        return repository.listarComprobantesConciliadosCabecera(
                codigo,
                fechaAbono,
                monto
        );
    }

    // =========================================
    // CONCILIACIÓN - COMPROBANTES CONCILIADOS DETALLE
    // =========================================
    public List<Map<String, Object>> listarComprobantesConciliadosDetalle(
            Integer id
    ) {

        return repository.listarComprobantesConciliadosDetalle(id);
    }

    // =========================================
    // CONCILIACIÓN - COMPROBANTES NO CONCILIADOS
    // =========================================
    public List<Map<String, Object>> listarComprobantesNoConciliados(
            String codigo,
            String fechaAbono,
            BigDecimal netoTotal,
            Integer unidComId
    ) {

        return repository.listarComprobantesNoConciliados(
                codigo,
                fechaAbono,
                netoTotal,
                unidComId
        );
    }

    // =========================================
    // CONCILIACIÓN - CONCILIAR
    // =========================================
    public List<Map<String, Object>> conciliar(
            String codigo,
            String fechaAbono,
            BigDecimal netoTotal,
            Integer unidComId
    ) {

        return repository.conciliar(
                codigo,
                fechaAbono,
                netoTotal,
                unidComId
        );
    }
}
