package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.LetrasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;

import java.math.BigDecimal;
import java.time.LocalDate;
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

        Map<String, Object> resultado = repository.registrar(
                empresaId,
                ctaBancEmpId,
                fechaSql,
                glosa,
                usuario,
                detalleXml
        );


        try {
            Map<?, ?> confirmacion = obtenerConfirmacionFactory(resultado);

            if (confirmacion == null) {
                System.err.println(
                        "FACTORY: no se encontró confirmación final. "
                                + "Verificar persistencia antes de cualquier reintento."
                );
                return resultado;
            }

            String estado = String.valueOf(
                    campoFactory(confirmacion, "EstadoPrueba")
            );

            String persistido = String.valueOf(
                    campoFactory(confirmacion, "Persistido")
            );

            Object movimientosObj =
                    campoFactory(confirmacion, "MovimientosGenerados");

            int movimientos = movimientosObj == null
                    ? 0
                    : Integer.parseInt(movimientosObj.toString());

            boolean confirmado =
                    "CONFIRMADO_COMMIT".equalsIgnoreCase(estado)
                            && ("true".equalsIgnoreCase(persistido)
                            || "1".equals(persistido))
                            && movimientos > 0;

            if (!confirmado) {
                return resultado;
            }

            Object propuestaObj =
                    campoFactory(confirmacion, "PropPagoCabId");

            if (propuestaObj == null) {
                System.err.println(
                        "FACTORY: pago confirmado sin PropPagoCabId. "
                                + "No se enviarán correos."
                );
                return resultado;
            }

            Integer propPagoCabId =
                    Integer.valueOf(propuestaObj.toString());

            if (propPagoCabId > 0) {
                enviarCorreosAutomaticosFactory(propPagoCabId);
            }

        } catch (Exception ex) {
            // El registro financiero ya puede estar confirmado.
            // Nunca repetir el SP por un error de notificación.
            System.err.println(
                    "FACTORY: error posterior al registro financiero: "
                            + ex.getMessage()
            );
        }

        return resultado;
    }
    @Autowired
    private ComprobanteEgresoService comprobanteEgresoService;

    public List<Map<String, Object>> previsualizarReenvioCorreos(Integer propuestaId) {
        return repository.obtenerCorreosReenvioValidados(propuestaId);
    }

    public Map<String, Object> reenviarCorreos(Integer propuestaId) {
        List<Map<String, Object>> filas = repository.obtenerCorreosReenvioValidados(propuestaId);
        if (filas.isEmpty())
            throw new IllegalStateException("No hay documentos contabilizados para reenviar.");
        for (Map<String, Object> fila : filas) {
            if (!"C".equalsIgnoreCase(String.valueOf(fila.get("TipoRegistro")))
                    && !"L".equalsIgnoreCase(String.valueOf(fila.get("TipoRegistro"))))
                throw new IllegalStateException("La propuesta contiene un tipo distinto de Factura/Letra.");
        }
        return comprobanteEgresoService.reenviarCorreosPropuesta(propuestaId);
    }
    public List<Map<String, Object>> buscarPropuesta(
            Map<String, Object> request) {

        String empresaId = String.valueOf(request.get("empresaId"));

        BigDecimal total = new BigDecimal(
                String.valueOf(request.get("total"))
        );

        String detalle = String.valueOf(request.get("detalle"));

        if (empresaId.trim().isEmpty()
                || total.compareTo(BigDecimal.ZERO) <= 0
                || !detalle.contains("<Detalle>")) {

            throw new IllegalArgumentException(
                    "Datos incompletos para buscar la propuesta."
            );
        }

        return repository.buscarPropuesta(
                empresaId,
                total,
                detalle
        );
    }

    private Object campoFactory(Map<?, ?> fila, String nombre) {
        for (Map.Entry<?, ?> entrada : fila.entrySet()) {
            if (nombre.equalsIgnoreCase(String.valueOf(entrada.getKey()))) {
                return entrada.getValue();
            }
        }
        return null;
    }

    private Map<?, ?> obtenerConfirmacionFactory(
            Map<String, Object> resultado) {

        Map<?, ?> confirmacion = null;

        for (Object valor : resultado.values()) {
            if (!(valor instanceof List)) {
                continue;
            }

            for (Object elemento : (List<?>) valor) {
                if (!(elemento instanceof Map)) {
                    continue;
                }

                Map<?, ?> fila = (Map<?, ?>) elemento;

                if (campoFactory(fila, "EstadoPrueba") != null
                        && campoFactory(fila, "Persistido") != null) {

                    if (confirmacion != null) {
                        throw new IllegalStateException(
                                "El SP devolvió más de una confirmación FACTORY."
                        );
                    }

                    confirmacion = fila;
                }
            }
        }

        return confirmacion;
    }

    private void enviarCorreosAutomaticosFactory(Integer propPagoCabId) {



        try {
            List<Map<String, Object>> registros =
                    repository.obtenerCorreosAutomaticos(propPagoCabId);

            if (registros == null || registros.isEmpty()) {
                System.err.println(
                        "FACTORY: no se encontraron documentos ejecutados para notificar. "
                                + propPagoCabId
                );
                return;
            }

            // Consultar el estado real de los ítems. E = ejecutado.
            // Los rechazados y pendientes NO deben recibir correo.
            Set<Integer> itemsEjecutados =
                    repository.obtenerItemsEjecutadosFactory(propPagoCabId);

            java.util.Set<String> itemsVistos =
                    new java.util.LinkedHashSet<>();

            // Primero validar todos los registros.
            for (Map<String, Object> fila : registros) {

                String item = String.valueOf(
                        campoFactory(fila, "PropPagoDetItem")
                );

                String tipo = String.valueOf(
                        campoFactory(fila, "TipoRegistro")
                ).toUpperCase(Locale.ROOT);



                if ("null".equals(item)
                        || !itemsVistos.add(item)) {
                    throw new IllegalStateException(
                            "Item FACTORY inválido o duplicado: " + item
                    );
                }

                if (!("C".equals(tipo)
                        || "L".equals(tipo)
                        || "R".equals(tipo)
                        || "S".equals(tipo))) {

                    throw new IllegalStateException(
                            "Tipo de documento no permitido: " + item
                    );

                }

                if (!String.valueOf(propPagoCabId).equals(
                        String.valueOf(campoFactory(fila, "PropPagoCabId")))) {
                    throw new IllegalStateException(
                            "Documento perteneciente a otra propuesta: " + item
                    );
                }

                Integer numeroItem;
                try {
                    numeroItem = Integer.valueOf(item);
                } catch (NumberFormatException ex) {
                    throw new IllegalStateException("Ítem FACTORY no numérico: " + item, ex);
                }
                if (!itemsEjecutados.contains(numeroItem)) {
                    System.out.println("FACTORY: ítem " + item
                            + " rechazado o pendiente; excluido de notificación.");
                    continue;
                }

                for (String campo : new String[]{
                        "MovBcoCabId",
                        "OperacionBancaria",
                        "Documento",
                        "MontoAbonar"
                }) {
                    Object valor = campoFactory(fila, campo);

                    if (valor == null || valor.toString().trim().isEmpty()) {
                        throw new IllegalStateException(
                                "FACTORY: falta " + campo + " en item " + item
                        );
                    }
                }
            }

            // Publicar solamente documentos válidos con correo.
            for (Map<String, Object> fila : registros) {

                String item = String.valueOf(
                        campoFactory(fila, "PropPagoDetItem")
                );

                // Segundo filtro obligatorio: jamás publicar ítems no ejecutados.
                if (!itemsEjecutados.contains(Integer.valueOf(item))) {
                    continue;
                }

                Object correoObj = campoFactory(fila, "Correo");

                String correo = correoObj == null
                        ? ""
                        : correoObj.toString().trim();

                if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                    System.err.println(
                            "FACTORY: correo inválido en item " + item
                    );
                    continue;
                }

                Map<String, Object> envio = new LinkedHashMap<>(fila);
                envio.put("ReenvioProduccion", true);

                try {
                    Map<String, Object> respuesta =
                            comprobanteEgresoService.enviarEventoCorreoPago(envio);

                    if (respuesta == null
                            || !Boolean.TRUE.equals(respuesta.get("estado"))) {
                        System.err.println(
                                "FACTORY: publicación no confirmada en item "
                                        + item + ". Revisar /noti."
                        );
                    }

                } catch (Exception ex) {
                    System.err.println(
                            "FACTORY: publicación incierta, propuesta "
                                    + propPagoCabId + ", item " + item
                                    + ": " + ex.getMessage()
                    );
                }
            }

        } catch (Exception ex) {
            System.err.println(
                    "FACTORY: no se pudo completar la notificación automática "
                            + propPagoCabId + ": " + ex.getMessage()
            );
        }
    }
}
