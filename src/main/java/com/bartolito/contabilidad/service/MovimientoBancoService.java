package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.ComprobanteEgresoRepository;
import com.bartolito.contabilidad.repository.MovimientoBancoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.bartolito.contabilidad.repository.BartSumatoriaFormaPagoRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;



@Service
public class MovimientoBancoService {
    @Autowired
    private MovimientoBancoRepository  repository;

    @Autowired
    private BartSumatoriaFormaPagoRepository liquidacionRepository;

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
            Long diaCabCompId) {

        List<Map<String, Object>> comprobante =
                repository.obtenerComprobante(diaCabCompId);

        if (comprobante.isEmpty()) {
            return comprobante;
        }

        String glosa = String.valueOf(
                comprobante.get(0).getOrDefault("DiaCabGlosa", "")
        );

        if (!glosa.trim().equalsIgnoreCase("DEPOSITO BANCARIO")) {
            return comprobante;
        }

        List<Map<String, Object>> movimientos =
                repository.obtenerMontoComprobante(diaCabCompId);

        // No asignar fechas si no se identifica un único movimiento.
        if (movimientos.size() != 1) {
            return comprobante;
        }

        Object montoObj = movimientos.get(0).get("MontoBanco");

        if (montoObj == null) {
            return comprobante;
        }

        BigDecimal monto = new BigDecimal(montoObj.toString());

        List<Map<String, Object>> liquidaciones =
                liquidacionRepository.buscarLiquidacionPorMonto(monto);

        // Evitar asignaciones ambiguas.
        if (liquidaciones.size() != 1) {
            return comprobante;
        }

        Map<String, Object> liquidacion = liquidaciones.get(0);

        Object fecha = liquidacion.get("fecha_operacion");
        Object idLiquidacion = liquidacion.get("sumatoria_forma_pago_id");

        List<Map<String, Object>> resultado = new java.util.ArrayList<>();

        for (Map<String, Object> fila : comprobante) {

            Map<String, Object> nuevaFila =
                    new LinkedHashMap<>(fila);

            nuevaFila.put("FechaLiquidacion", fecha);
            nuevaFila.put("SumatoriaFormaPagoId", idLiquidacion);

            resultado.add(nuevaFila);
        }

        return resultado;
    }
}
