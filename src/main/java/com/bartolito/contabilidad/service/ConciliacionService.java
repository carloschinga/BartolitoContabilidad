package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.ConciliacionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ConciliacionService {
    private final ConciliacionRepository conciliacionRepository;

    public ConciliacionService(ConciliacionRepository conciliacionRepository) {
        this.conciliacionRepository = conciliacionRepository;
    }

    public Map<String, Object> getSucursales() {
        String jsonStr = conciliacionRepository.obtenerSucursalesConciliacion();

        // Convertir String JSON a Map para mantener tu estructura de respuesta
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonStr, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("Sucursales", List.of()); // fallback vacío en caso de error
        }
    }

    public Map<String, Object> getFechas(String sucursal) {
        String jsonStr = conciliacionRepository.obtenerFechasConciliacion(sucursal);

        // Convertir String JSON a Map para mantener tu estructura de respuesta
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonStr, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("Fechas", List.of()); // fallback vacío en caso de error
        }
    }

    public Map<String, Object> getNetoTotal(String sucursal, String fechaAbono) {
        String jsonResult = conciliacionRepository.obtenerNetoTotal(sucursal, fechaAbono);
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResult, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("NetoTotal", List.of()); // fallback vacío en caso de error
        }
    }
    public Map<String, Object> getConciliadoExiste(String codigo, Date fechaAbono, BigDecimal monto) {
        String jsonResult = conciliacionRepository.obtenerConciliadoExiste(codigo, fechaAbono, monto);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResult, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("Existe", 0); // fallback en caso de error
        }
    }

    public Map<String, Object> conciliar(String codigo, Date fechaAbono, BigDecimal netoTotal, int unidComId) {
        // Llamar al método del repository que ejecuta el SP y devuelve JSON como String
        String jsonResult = conciliacionRepository.conciliarOperacion(codigo, fechaAbono, netoTotal, unidComId);

        try {
            // Convertir el JSON String a Map<String, Object>
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResult, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            // Retornar un mapa por defecto en caso de error
            return Map.of("estado", -1, "mensaje", "Error al conciliar operación");
        }
    }

    public Map<String, Object> getNoConciliados(String codigo, Date fechaAbono, BigDecimal netoTotal, int unidComId) {
        String jsonResult = conciliacionRepository.obtenerNoConciliados(codigo, fechaAbono, netoTotal, unidComId);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResult, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("noconciliados", List.of()); // fallback vacío
        }
    }

    public Map<String, Object> getConciliadosCabecera(String codigo, Date fechaAbono, BigDecimal monto) {
        String jsonResult = conciliacionRepository.obtenerConciliadosCabecera(codigo, fechaAbono, monto);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResult, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("conciliados_cabecera", List.of()); // fallback vacío
        }
    }

    public Map<String, Object> getConciliadosDetalle(String codigo, Date fechaAbono, BigDecimal monto) {
        String jsonResult = conciliacionRepository.obtenerConciliadosDetalle(codigo, fechaAbono, monto);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResult, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("conciliados_detalle", List.of()); // fallback vacío
        }
    }

}
