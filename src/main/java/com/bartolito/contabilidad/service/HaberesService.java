package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.HaberesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Service
public class HaberesService {
    @Autowired
    private HaberesRepository repository;

   @Autowired
   private FileStorageService fileStorageService;

    public Map<String,Object> registrarHaberes(

            String empresaId,

            Integer tipConCajId,

            Integer conCajId,

            String periodo,

            String descripcion,

            String fecha,

            String fechaProceso,

            String subTipo,

            String moneda,

            String cuentaCargo,

            BigDecimal total,

            String archivoTxt,

            String usuario,

            String detalle

    ){

        descripcion =
                descripcion == null ? "" : descripcion.trim();

        periodo =
                periodo == null ? "" : periodo.trim();

        subTipo =
                subTipo == null ? "" : subTipo.trim().toUpperCase();

        moneda =
                moneda == null ? "" : moneda.trim();

        cuentaCargo =
                cuentaCargo == null ? "" : cuentaCargo.trim();

        archivoTxt =
                (archivoTxt == null || archivoTxt.isBlank())
                        ? null
                        : archivoTxt.trim();

        return repository.registrarHaberes(

                empresaId,

                tipConCajId,

                conCajId,

                periodo,

                descripcion,

                fecha,

                fechaProceso,

                subTipo,

                moneda,

                cuentaCargo,

                total,

                archivoTxt,

                usuario,

                detalle

        );

    }

    public List<Map<String,Object>> listarHaberes(
            String empresaId,
            String monedaId,
            String periodo,
            String estado){

        empresaId = (empresaId == null || empresaId.isBlank())
                ? null
                : empresaId.trim();

        monedaId = (monedaId == null || monedaId.isBlank())
                ? null
                : monedaId.trim();

        periodo = (periodo == null || periodo.isBlank())
                ? null
                : periodo.trim();

        estado = (estado == null || estado.isBlank())
                ? null
                : estado.trim();

        return repository.listarHaberes(
                empresaId,
                monedaId,
                periodo,
                estado
        );
    }
    public Map<String,Object> obtenerHaberes(Long habCabId){
        return repository.obtenerHaberes(habCabId);
    }

}
