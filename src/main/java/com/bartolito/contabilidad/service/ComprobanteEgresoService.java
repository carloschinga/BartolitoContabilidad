
package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.ComprobanteEgresoRepository;

import java.math.BigDecimal;
import java.io.StringReader;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ComprobanteEgresoService {

    private final ComprobanteEgresoRepository repository;
    private final RestTemplate restTemplate;

    private static final String URL_NOTIFICACIONES =
            "http://181.224.248.20:8080/noti/eventos/propuesta";

    @Autowired
    public ComprobanteEgresoService(
            ComprobanteEgresoRepository repository,
            RestTemplate restTemplate) {

        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    // ==========================================
    // OBTENER COMPROBANTE DE EGRESO
    // ==========================================

    public List<Map<String, Object>> obtenerComprobanteEgreso(
            String cuenta,
            String periodo,
            String diario) {

        return repository.obtenerComprobanteEgreso(
                cuenta,
                periodo,
                diario
        );
    }

    // ==========================================
    // OBTENER CUENTA BANCARIA
    // ==========================================

    public List<Map<String, Object>> obtenerCuentaBancaria(
            String cuentaBanco) {

        return repository.obtenerCuentaBancaria(
                cuentaBanco
        );
    }

    // ==========================================
    // BUSCAR PROPUESTA DESDE BCP
    // ==========================================

    public List<Map<String, Object>> buscarPropuestaDesdeBCP(
            String empresaId,
            BigDecimal total,
            String detalle) {

        return repository.buscarPropuestaDesdeBCP(
                empresaId,
                total,
                detalle
        );
    }

    // ==========================================
    // BUSCAR PROPUESTA HABERES
    // ==========================================

    public List<Map<String, Object>> buscarPropuestaHaberes(
            String empresaId,
            BigDecimal total) {

        return repository.buscarPropuestaHaberes(
                empresaId,
                total
        );
    }

    // ==========================================
    // OBTENER DETALLE PROPUESTA
    // ==========================================

    public List<Map<String, Object>> obtenerDetallePropuesta(
            Integer propPagoCabId) {

        return repository.obtenerDetallePropuesta(
                propPagoCabId
        );
    }

    // ==========================================
    // GENERAR COMPROBANTE DE EGRESO
    // CON ENVIO AUTOMATICO RESTAURADO
    // ==========================================

    public List<Map<String, Object>> generarComprobanteEgreso(
            Map<String, Object> request) throws Exception {

        Object propuesta = request.get("PropPagoCabId");
        Object xml = request.get("DetalleBancoXml");
        if (propuesta == null || propuesta.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Falta PropPagoCabId.");
        }
        if (xml == null || xml.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Falta DetalleBancoXml.");
        }

        // CAMBIO 1: interpretar ANTES de contabilizar los items PROCESADA.
        // Si el XML es invalido, no ejecutar el procedimiento financiero.
        Set<Integer> itemsProcesados = leerItemsProcesados(xml.toString());
        Integer propPagoCabId = Integer.valueOf(propuesta.toString());

        // INTACTO: el SP financiero se ejecuta una sola vez.
        List<Map<String, Object>> resultado =
                repository.generarComprobanteEgreso(request);

        // CAMBIO 2: no publicar sin confirmacion expresa del SP.
        if (resultado == null || resultado.isEmpty()) {
            System.err.println("SIN CONFIRMACION FINANCIERA; NO SE ENVIAN CORREOS.");
            return resultado;
        }
        for (Map<String, Object> fila : resultado) {
            if (!"OK".equalsIgnoreCase(valorTexto(fila, "Resultado", ""))) {
                System.err.println("RESULTADO FINANCIERO NO CONFIRMADO; NO SE ENVIAN CORREOS.");
                return resultado;
            }
        }

        // CAMBIO 3: el envio automatico NO usa el SP de reenvio manual.
        try {
            List<Map<String, Object>> elegibles =
                    repository.obtenerCorreosPropuesta(propPagoCabId, null);

            // Validacion global que no se repita  PropPagoDetItem
            Set<Integer> idsVistos = new LinkedHashSet<>();
            for (Map<String, Object> item : elegibles) {
                Integer id = Integer.valueOf(valorTexto(item, "PropPagoDetItem", "-1"));
                if (id <= 0 || !idsVistos.add(id)) {
                    throw new IllegalStateException(
                            "Consulta de correos con item duplicado o invalido: " + id);
                }
            }

            for (Map<String, Object> item : elegibles) {
                int id = Integer.parseInt(valorTexto(item, "PropPagoDetItem", "-1"));

                // CAMBIO 4: solo C, procesados en ESTA solicitud, con movimiento.
                if (!itemsProcesados.contains(id)
                        || !"C".equalsIgnoreCase(valorTexto(item, "TipoRegistro", ""))
                        || valorTexto(item, "MovBcoCabId", "").isEmpty()) {
                    continue;
                }

                String correo = valorTexto(item, "Correo", "");
                if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                    System.err.println("CORREO INVALIDO; PROPUESTA " + propPagoCabId
                            + " ITEM " + id);
                    continue;
                }

                try {
                    // Se conserva intacto el metodo comun de publicacion.
                    Map<String, Object> respuesta = enviarEventoCorreoPago(item);
                    if (respuesta == null || !Boolean.TRUE.equals(respuesta.get("estado"))) {
                        System.err.println("PUBLICACION NO CONFIRMADA; PROPUESTA "
                                + propPagoCabId + " ITEM " + id
                                + ". Verificar /noti antes de reintentar.");
                    } else {
                        System.out.println("EVENTO ACEPTADO; PROPUESTA "
                                + propPagoCabId + " ITEM " + id);
                    }
                } catch (Exception ex) {
                    System.err.println("PUBLICACION INCIERTA; PROPUESTA "
                            + propPagoCabId + " ITEM " + id + ": " + ex.getMessage()
                            + ". Verificar /noti antes de reintentar.");
                }
            }
        } catch (Exception ex) {
            // El pago ya fue confirmado. Nunca volver a contabilizar por un correo.
            System.err.println("PAGO CONFIRMADO; NOTIFICACION OMITIDA: " + ex.getMessage());
        }
        return resultado;
    }

    // CAMBIO 5: valida el XML BCP, sin dependencias externas adicionales.
    private Set<Integer> leerItemsProcesados(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        Document doc = factory.newDocumentBuilder()
                .parse(new InputSource(new StringReader(xml)));
        if (!"Pagos".equals(doc.getDocumentElement().getTagName())) {
            throw new IllegalArgumentException("Raiz XML invalida; se espera Pagos.");
        }
        NodeList pagos = doc.getDocumentElement().getElementsByTagName("Pago");
        if (pagos.getLength() == 0) {
            throw new IllegalArgumentException("XML sin elementos Pago.");
        }
        Set<Integer> vistos = new LinkedHashSet<>();
        Set<Integer> procesados = new LinkedHashSet<>();
        for (int i = 0; i < pagos.getLength(); i++) {
            Element pago = (Element) pagos.item(i);
            NodeList items = pago.getElementsByTagName("PropPagoDetItem");
            NodeList estados = pago.getElementsByTagName("Estado");
            if (items.getLength() != 1 || estados.getLength() != 1) {
                throw new IllegalArgumentException("Pago sin item o estado unico.");
            }
            int id = Integer.parseInt(items.item(0).getTextContent().trim());
            String estado = estados.item(0).getTextContent().trim();
            if (id <= 0 || !vistos.add(id)) {
                throw new IllegalArgumentException("Item XML invalido o duplicado: " + id);
            }
            if ("PROCESADA".equalsIgnoreCase(estado)) {
                procesados.add(id);
            } else if (!"RECHAZADA".equalsIgnoreCase(estado)) {
                throw new IllegalArgumentException("Estado XML no reconocido: " + estado);
            }
        }
        return procesados;
    }

    // ==========================================
    // OBTENER CORREOS DE PROPUESTA
    // ==========================================

    public List<Map<String, Object>> obtenerCorreosPropuesta(
            Integer propPagoCabId,
            Long movBcoCabId) {

        return repository.obtenerCorreosPropuesta(
                propPagoCabId,
                movBcoCabId
        );
    }

    // ==========================================
    // ENVIAR EVENTO DE CORREO DE PAGO
    // ==========================================

    public Map<String, Object> enviarEventoCorreoPago(
            Map<String, Object> item) {

        Map<String, Object> dto =
                new LinkedHashMap<>();

        // ======================================
        // TIPO DE CORREO
        // ======================================

        dto.put("tipoCorreo", "PAGO");

        // ======================================
        // DATOS DE PROPUESTA
        // ======================================

        dto.put(
                "idPropuesta",
                Long.valueOf(
                        item.get("PropPagoCabId").toString()
                )
        );

        dto.put(
                "codigo",
                item.get("PropPagoCodi")
        );

        // ======================================
        // EMPRESA
        // ======================================

        dto.put(
                "empresa",
                item.get("EmpresaId")
        );

        dto.put(
                "empresaNombre",
                item.get("EmpresaRazSocial")
        );

        dto.put(
                "empresaRuc",
                item.get("EmpresaRUC")
        );

        // ======================================
        // MONTO
        // ======================================

        String monto =
                valorTexto(item, "MontoAbonar", "0");

        dto.put("monto", monto);

        // ======================================
        // CORREO DESTINO
        // ======================================

        String correo =
                valorTexto(item, "Correo", "");

        dto.put("correoDestino", correo);

        // ======================================
        // CORREO COPIA
        // ======================================

        dto.put("correoCopia", Boolean.TRUE.equals(item.get("ReenvioProduccion"))
                ? java.util.Collections.emptyList()
                : Arrays.asList("cchinga@unjfsc.edu.pe"));

        // ======================================
        // BENEFICIARIO
        // ======================================

        String beneficiario =
                valorTexto(item, "Beneficiario", "");

        dto.put("beneficiario", beneficiario);

        // ======================================
        // DOCUMENTO
        // ======================================

        String documento =
                valorTexto(item, "Documento", "");

        dto.put("documento", documento);

        // NC y ND: informacion adicional del comprobante.
        // Si no existen, no se agregan lineas vacias al correo.
        String nc = valorTexto(item, "NC", "");
        String nd = valorTexto(item, "ND", "");

        dto.put("NC", nc);
        dto.put("ND", nd);

        String detalleNotas =
                (nc.isEmpty() ? "" : "Nota de Crédito (NC): " + nc + "\n") +
                        (nd.isEmpty() ? "" : "Nota de Débito (ND): " + nd + "\n");

        // ======================================
        // OPERACION BANCARIA
        // ======================================

        String operacionBancaria =
                valorTexto(
                        item,
                        "OperacionBancaria",
                        ""
                );

        dto.put(
                "operacionBancaria",
                operacionBancaria
        );

        // ======================================
        // DATOS DEL PROCESO
        // ======================================

        String fechaProceso =
                valorTexto(item, "FechaProceso", "");

        String bancoDestino =
                valorTexto(item, "BancoDestino", "");

        String cuentaBeneficiario =
                valorTexto(
                        item,
                        "CuentaBeneficiario",
                        ""
                );

        dto.put(
                "fechaProceso",
                fechaProceso
        );

        dto.put(
                "bancoDestino",
                bancoDestino
        );

        dto.put(
                "cuentaBeneficiario",
                cuentaBeneficiario
        );

        // ======================================
        // MENSAJE
        // ======================================

        String mensaje =
                "Estimado(a) " + beneficiario + ":\n\n" +
                        "Se ha realizado un pago " +
                        "correspondiente a la propuesta " +
                        item.get("PropPagoCodi") + ".\n\n" +
                        "Documento: " + documento + "\n" +
                        detalleNotas +
                        "Importe: S/ " + monto + "\n" +
                        (operacionBancaria.isEmpty() ? "\n" :
                                "N.º de operación bancaria: " + operacionBancaria + "\n\n") +
                        "Este mensaje fue generado " +
                        "automáticamente por Bartolito.";

        dto.put("mensaje", mensaje);

        // ======================================
        // REGISTRO DEL ENVIO
        // ======================================

        System.out.println(
                "====================================");

        System.out.println(
                "ENVIANDO EVENTO DE CORREO");

        System.out.println(
                "URL: " + URL_NOTIFICACIONES);

        System.out.println(
                "DESTINO: " + correo);

        System.out.println(
                "PROPUESTA: " +
                        item.get("PropPagoCodi"));

        System.out.println(
                "BENEFICIARIO: " + beneficiario);

        System.out.println(
                "DOCUMENTO: " + documento);

        System.out.println(
                "MONTO: " + monto);

        System.out.println(
                "MOVIMIENTO BANCARIO: " +
                        item.get("MovBcoCabId"));

        System.out.println(
                "====================================");

        // ======================================
        // CABECERAS HTTP
        // ======================================

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        // ======================================
        // CREAR REQUEST
        // ======================================

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(
                        dto,
                        headers
                );

        // ======================================
        // ENVIAR A SERVICIO NOTIFICACIONES
        // ======================================

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        URL_NOTIFICACIONES,
                        entity,
                        Map.class
                );

        System.out.println(
                "RESPUESTA NOTIFICACIONES: " +
                        response.getBody());

        return response.getBody();
    }

    // ==========================================
    // ENVIAR CORREOS DE PROPUESTA
    // ==========================================

    public List<Map<String, Object>> enviarCorreosPropuesta(Integer propPagoCabId, Long movBcoCabId) {
        List<Map<String, Object>> validados = obtenerCorreosReenvioValidados(propPagoCabId);
        List<Map<String, Object>> seleccionados = new ArrayList<>();
        for (Map<String, Object> item : validados) {
            if (!"E".equalsIgnoreCase(valorTexto(item, "PropPagoDetEstado", ""))) continue;
            Object mov = item.get("MovBcoCabId");
            if (mov == null || !movBcoCabId.toString().equals(mov.toString())) continue;
            seleccionados.add(item);
            if (valorTexto(item, "Correo", "").isEmpty()) continue;
            try { enviarEventoCorreoPago(item); }
            catch (Exception ex) { System.err.println("Correo fallido: " + ex.getMessage()); }
        }
        return seleccionados;
    }

    // ==========================================
    // UTILIDAD PARA OBTENER TEXTO
    // ==========================================

    private String valorTexto(
            Map<String, Object> item,
            String campo,
            String defecto) {

        Object valor = item.get(campo);

        if (valor == null) {
            return defecto;
        }

        return valor.toString().trim();
    }

    // Vista previa validada por SQL. No envia ningun correo.
    public List<Map<String, Object>> obtenerCorreosReenvioValidados(Integer propPagoCabId) {
        return repository.obtenerCorreosReenvioValidados(propPagoCabId);
    }

    // Reenvio real. El pago BCP debe haber sido contrastado por el operador.
    // El bloqueo local evita ejecuciones simultaneas en ESTA instancia solamente.
    private final java.util.Set<Integer> reenviosEnCurso =
            java.util.concurrent.ConcurrentHashMap.newKeySet();

    public Map<String, Object> reenviarCorreosPropuesta(Integer propPagoCabId) {
        if (propPagoCabId == null || propPagoCabId <= 0) {
            throw new IllegalArgumentException("Propuesta invalida.");
        }
        if (!reenviosEnCurso.add(propPagoCabId)) {
            throw new IllegalStateException("Ya existe un reenvio en curso para esta propuesta.");
        }
        try {
            List<Map<String, Object>> registros = repository.obtenerCorreosReenvioValidados(propPagoCabId);
            if (registros.isEmpty()) {
                throw new IllegalStateException("No hay documentos ejecutados para reenviar.");
            }
            int elegibles = 0, aceptados = 0;
            List<Map<String, Object>> incidencias = new ArrayList<>();
            Set<String> claves = new LinkedHashSet<>();
            for (Map<String, Object> fila : registros) {
                if (!"E".equalsIgnoreCase(valorTexto(fila, "PropPagoDetEstado", ""))
                        || !"C".equalsIgnoreCase(valorTexto(fila, "TipoRegistro", ""))) continue;
                if (valorTexto(fila, "MovBcoCabId", "").isEmpty()
                        || valorTexto(fila, "DiaCabCompId", "").isEmpty()) continue;
                String item = valorTexto(fila, "PropPagoDetItem", "");
                if (item.isEmpty() || !claves.add(item)) {
                    throw new IllegalStateException("Item repetido o sin identificador en el resultado SQL.");
                }
                elegibles++;
                String destino = valorTexto(fila, "Correo", "");
                if (!destino.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                    Map<String,Object> fallo = new LinkedHashMap<>();
                    fallo.put("item", item);
                    fallo.put("motivo", "Correo no valido");
                    incidencias.add(fallo);
                    continue;
                }
                Map<String,Object> envio = new LinkedHashMap<>(fila);
                // El campo de origen contiene una factura, NO la operacion real BCP.
                // Conservar OperacionBancaria obtenida del HABER en SQL Server.
                if (valorTexto(envio, "OperacionBancaria", "").isEmpty()) {
                    throw new IllegalStateException("No se encontro el numero de planilla bancaria para el item " + item);
                }
                envio.put("ReenvioProduccion", true);
                try {
                    Map<String,Object> respuesta = enviarEventoCorreoPago(envio);
                    if (respuesta != null && Boolean.TRUE.equals(respuesta.get("estado"))) {
                        aceptados++;
                    } else {
                        Map<String,Object> fallo = new LinkedHashMap<>();
                        fallo.put("item", item);
                        fallo.put("motivo", "El servicio de notificaciones no acepto el evento");
                        incidencias.add(fallo);
                    }
                } catch (Exception ex) {
                    Map<String,Object> fallo = new LinkedHashMap<>();
                    fallo.put("item", item);
                    fallo.put("motivo", "Error al publicar el evento; verificar RabbitMQ antes de reintentar");
                    incidencias.add(fallo);
                }
            }
            Map<String,Object> resultado = new LinkedHashMap<>();
            resultado.put("documentosElegibles", elegibles);
            resultado.put("eventosAceptados", aceptados);
            resultado.put("incidencias", incidencias);
            resultado.put("estado", "PUBLICACION_FINALIZADA_VERIFICAR_ENTREGA_SMTP");
            return resultado;
        } finally {
            reenviosEnCurso.remove(propPagoCabId);
        }
    }

    // PRUEBA CONTROLADA: solo propuesta 124, un documento ejecutado,
    // destino fijo. No invoca procedimientos de generación contable.
    // Debe exponerse únicamente mediante un endpoint autenticado/autorizado.
    public Map<String, Object> probarReenvioCorreoPago(
            Integer propPagoCabId, Integer propPagoDetItem) {

        if (propPagoCabId == null || propPagoCabId != 124) {
            throw new IllegalArgumentException("Prueba permitida solo para la propuesta 124.");
        }
        if (propPagoDetItem == null || propPagoDetItem != 1) {
            throw new IllegalArgumentException("Prueba permitida solo para el ítem 1.");
        }

        List<Map<String, Object>> documentos =
                repository.obtenerCorreosReenvioValidados(propPagoCabId);
        Map<String, Object> seleccionado = null;
        for (Map<String, Object> item : documentos) {
            if (!"E".equalsIgnoreCase(valorTexto(item, "PropPagoDetEstado", ""))
                    || !"C".equalsIgnoreCase(valorTexto(item, "TipoRegistro", ""))) {
                continue;
            }
            if (String.valueOf(propPagoDetItem).equals(
                    valorTexto(item, "PropPagoDetItem", ""))) {
                if (seleccionado != null) {
                    throw new IllegalStateException("Ítem ambiguo: hay varios documentos con el mismo número.");
                }
                seleccionado = item;
            }
        }
        if (seleccionado == null) {
            throw new IllegalStateException("Documento no encontrado o no ejecutado.");
        }
        if (valorTexto(seleccionado, "MovBcoCabId", "").isEmpty()
                || valorTexto(seleccionado, "DiaCabCompId", "").isEmpty()) {
            throw new IllegalStateException("Faltan referencias bancarias o contables.");
        }

        String correoPrueba = "cchinga@unjfsc.edu.pe";
        Map<String, Object> datosPrueba = new LinkedHashMap<>(seleccionado);
        datosPrueba.put("Correo", correoPrueba);
        // No confundir el número de factura con la operación bancaria BCP.
        // Conservar la referencia real del SP, sin sustituirla por una factura.
        if (valorTexto(datosPrueba, "OperacionBancaria", "").isEmpty()) {
            throw new IllegalStateException("El numero de planilla bancaria no esta disponible.");
        }
        // El método original también coloca en CC este mismo buzón.
        Map<String, Object> respuesta = enviarEventoCorreoPago(datosPrueba);
        if (respuesta == null || !Boolean.TRUE.equals(respuesta.get("estado"))) {
            throw new IllegalStateException("El servicio de notificaciones no aceptó el evento.");
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("PropPagoCabId", propPagoCabId);
        resultado.put("PropPagoDetItem", propPagoDetItem);
        resultado.put("correoPrueba", correoPrueba);
        resultado.put("estado", "EVENTO_ACEPTADO");
        resultado.put("respuestaNotificaciones", respuesta);
        resultado.put("mensaje", "Evento aceptado por /noti; verificar log SMTP del consumidor.");
        return resultado;
    }

    public Map<String, Object> previsualizarResumenReenvio(
            Integer propPagoCabId) {

        List<Map<String, Object>> filas =
                obtenerCorreosReenvioValidados(propPagoCabId);

        List<Map<String, Object>> estados =
                repository.obtenerResumenEstadosReenvio(propPagoCabId);

        long procesados = 0;
        long rechazados = 0;

        for (Map<String, Object> estado : estados) {

            String codigo =
                    valorTexto(estado, "Estado", "");

            long cantidad =
                    Long.parseLong(
                            estado.get("Cantidad").toString()
                    );

            if ("E".equalsIgnoreCase(codigo)) {
                procesados += cantidad;
            }

            if ("X".equalsIgnoreCase(codigo)) {
                rechazados += cantidad;
            }
        }

        Map<String, Object> resumen = new LinkedHashMap<>();

        resumen.put("PropPagoCabId", propPagoCabId);
        resumen.put("procesados", procesados);
        resumen.put("rechazadosExcluidos", rechazados);
        resumen.put("elegibles", filas.size());

        resumen.put(
                "sinCorreo",
                filas.stream()
                        .filter(f ->
                                valorTexto(f, "Correo", "").isEmpty()
                        )
                        .count()
        );

        resumen.put("envioHabilitado", true);
        resumen.put("documentos", filas);

        return resumen;
    }
}