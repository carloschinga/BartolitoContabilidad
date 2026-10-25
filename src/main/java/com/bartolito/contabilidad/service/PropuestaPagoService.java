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
            String estado,
            String monedaId,
            String fechaInicio,
            String fechaFin
    ) {

        return repository.listarPropuestaPagoCabecera(
                empresaId,
                estado,
                monedaId,
                fechaInicio,
                fechaFin
        );
    }
    public Map<String, Object> generarPropuestaPago(
            String empresaId,
            String monedaId,
            BigDecimal total,
            String usuario,
            String detalleXml
    ) {
        // ==========================================
        // VALIDACIONES
        // ==========================================

        if (empresaId == null || empresaId.trim().isEmpty()) {
            throw new RuntimeException("Empresa requerida");
        }

        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Total inválido");
        }

        if (detalleXml == null || detalleXml.trim().isEmpty()) {
            throw new RuntimeException("Detalle vacío");
        }

        // ==========================================
        // VALIDAR QUE SEA XML BIEN FORMADO
        // ==========================================
        try {
            javax.xml.parsers.DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(new org.xml.sax.InputSource(
                            new java.io.StringReader(detalleXml)
                    ));
        } catch (Exception e) {
            throw new RuntimeException("XML malformado: " + e.getMessage());
        }

        return repository.generarPropuestaPago(
                empresaId,
                monedaId,
                total,
                usuario,
                detalleXml
        );
    }
    public List<Map<String, Object>> obtenerPropuestaPago(
            Long propPagoCabId
    ) {

        return repository.obtenerPropuestaPago(
                propPagoCabId
        );
    }

    public List<Map<String, Object>> rechazarPropuestaPago(
            Long propPagoCabId,
            String usrRechaza,
            String obsvRechaza
    ) {

        return repository.rechazarPropuestaPago(
                propPagoCabId,
                usrRechaza,
                obsvRechaza
        );
    }
    public List<Map<String, Object>> listarComprobanteCompraDetalle(
            Long provId,
            Long comCompCabId
    ) {

        return repository.listarComprobanteCompraDetalle(
                provId,
                comCompCabId
        );
    }
    public List<Map<String, Object>> aprobarPropuestaPago(
            Long propPagoCabId,
            String usuario
    ) {


        return repository.aprobarPropuestaPago(
                propPagoCabId,
                usuario
        );

    }
    // ==========================================
// OBTENER RANGO APROBACION
// ==========================================

    public Map<String,Object> obtenerRangoAprobacion(

            String empresaId,

            String usuario

    ) {


        return repository.obtenerRangoAprobacion(

                empresaId,

                usuario

        );


    }
    public List<Map<String,Object>> anularDetalle(
            Long propPagoCabId,
            Integer item,
            String usuario,
            String motivo
    ){

        return repository.anularDetalle(
                propPagoCabId,
                item,
                usuario,
                motivo
        );

    }
    public void procesarDetalle(

            String usuario,
            String observacion,
            List<Map<String,Object>> detalle

    ){


        for(
                Map<String,Object> item:
                detalle
        ){


            Long cabId =
                    Long.valueOf(
                            item.get("propPagoCabId").toString()
                    );


            Integer detItem =
                    Integer.valueOf(
                            item.get("propPagoDetItem").toString()
                    );


            String estado =
                    item.get("estado").toString();



            if(
                    estado.equals("A")
            ){

                repository.aprobarDetalle(
                        cabId,
                        detItem,
                        usuario
                );

            }
            else{

                repository.anularDetalle(
                        cabId,
                        detItem,
                        usuario,
                        observacion
                );

            }


        }


    }
}
