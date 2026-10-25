package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.LetrasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class LetrasService {
    @Autowired
    private LetrasRepository repository;

   @Autowired
   private FileStorageService fileStorageService;

    public List<Map<String, Object>> listarLetrasPendientes(
            String empresaId,
            String monedaId,
            String fechaVencimiento) {

        empresaId = (empresaId == null || empresaId.isBlank())
                ? null
                : empresaId.trim();

        monedaId = (monedaId == null || monedaId.isBlank())
                ? null
                : monedaId.trim();

        fechaVencimiento = (fechaVencimiento == null || fechaVencimiento.isBlank())
                ? null
                : fechaVencimiento.trim();

        return repository.listarLetrasPendientes(
                empresaId,
                monedaId,
                fechaVencimiento
        );
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> obtenerPreAsiento(Map<String, Object> request) {

        String empresaId = (String) request.get("empresaId");
        String numeroCuenta = (String) request.get("numeroCuenta");

        List<Map<String, Object>> detalle =
                (List<Map<String, Object>>) request.get("detalle");

        String xml = generarXml(detalle);

        return repository.obtenerPreAsiento(
                empresaId,
                numeroCuenta,
                xml
        );
    }
    private String generarXml(List<Map<String, Object>> detalle) {

        StringBuilder xml = new StringBuilder();

        xml.append("<Detalle>");

        for (Map<String, Object> fila : detalle) {

            xml.append("<Fila>");

            xml.append("<Documento>")
                    .append(fila.get("documento"))
                    .append("</Documento>");

            xml.append("<Monto>")
                    .append(fila.get("monto"))
                    .append("</Monto>");

            xml.append("</Fila>");
        }

        xml.append("</Detalle>");

        return xml.toString();
    }
    @SuppressWarnings("unchecked")
    public Map<String, Object> registrar(
            Map<String, Object> request
    ) throws Exception {

        // ============================================
        // DATOS PRINCIPALES
        // ============================================

        String empresaId =
                request.get("empresaId").toString();

        Integer ctaBancEmpId =
                Integer.valueOf(
                        request.get("ctaBancEmpId").toString()
                );

        String glosa =
                request.get("glosa").toString();

        String usuario =
                request.get("usuario").toString();


        // ============================================
        // DETALLE XML
        // ============================================

        Object detalleObjeto =
                request.get("detalle");

        if (detalleObjeto == null) {

            throw new IllegalArgumentException(
                    "No se recibió el detalle XML del comprobante."
            );
        }

        String detalleXml =
                detalleObjeto.toString();


        // ============================================
        // FECHA
        // ============================================

        Object fechaObjeto =
                request.get("fechaPago");

        if (fechaObjeto == null) {

            throw new IllegalArgumentException(
                    "No se recibió la fecha de pago."
            );
        }

        LocalDate localDate =
                LocalDate.parse(
                        fechaObjeto.toString()
                );

        java.sql.Date fechaSql =
                java.sql.Date.valueOf(localDate);


        // ============================================
        // DEBUG
        // ============================================

        System.out.println(
                "===================================="
        );

        System.out.println(
                "REGISTRAR COMPROBANTE FACTORY"
        );

        System.out.println(
                "EmpresaId     : " +
                        empresaId
        );

        System.out.println(
                "CtaBancEmpId  : " +
                        ctaBancEmpId
        );

        System.out.println(
                "FechaPago     : " +
                        fechaSql
        );

        System.out.println(
                "Glosa         : " +
                        glosa
        );

        System.out.println(
                "Usuario       : " +
                        usuario
        );

        System.out.println(
                "Detalle XML   : " +
                        detalleXml
        );

        System.out.println(
                "===================================="
        );


        // ============================================
        // EJECUTAR REPOSITORY
        // ============================================

        return repository.registrar(

                empresaId,
                ctaBancEmpId,
                fechaSql,
                glosa,
                usuario,
                detalleXml

        );
    }
}
