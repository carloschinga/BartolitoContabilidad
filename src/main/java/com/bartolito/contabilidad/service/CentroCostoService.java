package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.CentroCostoRepository;
import com.bartolito.contabilidad.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CentroCostoService {
    @Autowired
    private CentroCostoRepository repository;

    private final CentroCostoRepository centroCostoRepository;

    public CentroCostoService(CentroCostoRepository centroCostoRepository) {
        this.centroCostoRepository= centroCostoRepository;
    }

    public List<Map<String, Object>> listarProcesos() {
        return repository.listarProcesos();
    }

    public List<Map<String, Object>> listarActividades() {
        return repository.listarActividades();
    }

    public List<Map<String, Object>> listarTareas() {
        return repository.listarTareas();
    }

    public List<Map<String, Object>> listarProductosServicios() {
        return repository.listarProductosServicios();
    }

    public List<Map<String, Object>> listarCentrosCostoResponsabilidad() {
        return repository.listarCentrosCostoResponsabilidad();
    }

    public List<Map<String, Object>> listarActivos() {
        return repository.listarActivos();
    }

    public List<Map<String, Object>> listarCentrosCosto() {

        List<Map<String, Object>> lista =
                repository.listarCentrosCosto();

        lista.forEach(item -> {

            String centroCostoId =
                    String.valueOf(item.get("ProcId")) + "|" +
                            String.valueOf(item.get("ActivId")) + "|" +
                            String.valueOf(item.get("TareaId")) + "|" +
                            String.valueOf(item.get("ActivoId")) + "|" +
                            String.valueOf(item.get("ProdId")) + "|" +
                            String.valueOf(item.get("CenCostResp"));

            item.put("CentroCostoId", centroCostoId);

        });

        return lista;

    }

}
