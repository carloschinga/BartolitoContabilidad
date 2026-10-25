package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.ConciliacionRepository;
import com.bartolito.contabilidad.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository repository;

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository= empresaRepository;
    }

    public List<Map<String, Object>> listarEmpresas() {
        return repository.listarEmpresas();
    }


// ============================================================
    // PROVEEDORES CUARTA CATEGORIA - LISTAR
    // ============================================================

    public List<Map<String, Object>> listarProveedoresCuartaCategoria(
            String filtro) {

        return empresaRepository.listarProveedoresCuartaCategoria(filtro);
    }

    // ============================================================
    // PROVEEDORES CUARTA CATEGORIA - AGREGAR
    // ============================================================

    public Map<String, Object> agregarProveedorCuartaCategoria(
            Long provId,
            Integer regPenId,
            Integer tipComAfpId,
            Date provFecNac,
            String provNumAFP,
            String provNumDoc,
            Integer provEstado,
            String usuario) {

        return empresaRepository.agregarProveedorCuartaCategoria(
                provId,
                regPenId,
                tipComAfpId,
                provFecNac,
                provNumAFP,
                provNumDoc,
                provEstado,
                usuario
        );
    }

    // ============================================================
    // PROVEEDORES CUARTA CATEGORIA - MODIFICAR
    // ============================================================

    public Map<String, Object> modificarProveedorCuartaCategoria(
            Long provId,
            Integer regPenId,
            Integer tipComAfpId,
            Date provFecNac,
            String provNumAFP,
            String provNumDoc,
            Integer provEstado,
            String usuario) {

        return empresaRepository.modificarProveedorCuartaCategoria(
                provId,
                regPenId,
                tipComAfpId,
                provFecNac,
                provNumAFP,
                provNumDoc,
                provEstado,
                usuario
        );
    }

    // ============================================================
    // METODO COMPARTIDO PARA AGREGAR Y MODIFICAR
    // ============================================================

    public Map<String, Object> guardarProveedorCuartaCategoria(
            boolean modificar,
            Map<String, Object> request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "No se recibieron datos del proveedor."
            );
        }

        Long provId = obtenerLong(request, "provId", true);

        Integer regPenId = obtenerInteger(request, "regPenId");
        Integer tipComAfpId = obtenerInteger(request, "tipComAfpId");

        Date provFecNac = null;

        String fecha = obtenerTexto(request, "provFecNac");

        if (fecha != null) {
            try {
                provFecNac = Date.valueOf(fecha);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "La fecha de nacimiento debe tener formato yyyy-MM-dd."
                );
            }
        }

        String provNumAFP = obtenerTexto(request, "provNumAFP");
        String provNumDoc = obtenerTexto(request, "provNumDoc");
        Integer provEstado = obtenerInteger(request, "provEstado");
        String usuario = obtenerTexto(request, "usuario");

        if (modificar) {
            return modificarProveedorCuartaCategoria(
                    provId,
                    regPenId,
                    tipComAfpId,
                    provFecNac,
                    provNumAFP,
                    provNumDoc,
                    provEstado,
                    usuario
            );
        }

        return agregarProveedorCuartaCategoria(
                provId,
                regPenId,
                tipComAfpId,
                provFecNac,
                provNumAFP,
                provNumDoc,
                provEstado,
                usuario
        );
    }

    private String obtenerTexto(
            Map<String, Object> request,
            String campo) {

        Object valor = request.get(campo);

        if (valor == null) {
            return null;
        }

        String texto = valor.toString().trim();

        return texto.isEmpty() ? null : texto;
    }

    private Long obtenerLong(
            Map<String, Object> request,
            String campo,
            boolean obligatorio) {

        String valor = obtenerTexto(request, campo);

        if (valor == null) {
            if (obligatorio) {
                throw new IllegalArgumentException(
                        "El campo " + campo + " es obligatorio."
                );
            }
            return null;
        }

        try {
            return Long.valueOf(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " debe ser numerico."
            );
        }
    }

    private Integer obtenerInteger(
            Map<String, Object> request,
            String campo) {

        String valor = obtenerTexto(request, campo);

        if (valor == null) {
            return null;
        }

        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " debe ser numerico."
            );
        }
    }
}
