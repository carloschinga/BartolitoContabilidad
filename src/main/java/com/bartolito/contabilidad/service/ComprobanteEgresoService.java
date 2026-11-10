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

    @org.springframework.beans.factory.annotation.Value("${bartolito.correo.prueba.masiva.habilitada:false}")
    private boolean pruebaMasivaHabilitada;

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

                // CAMBIO 4: notificar C, S, R y L, solo si el BCP procesó ESTE ítem.
                String tipoRegistro = valorTexto(item, "TipoRegistro", "")
                        .toUpperCase(java.util.Locale.ROOT);
                boolean tipoPermitido = "C".equals(tipoRegistro)
                        || "S".equals(tipoRegistro)
                        || "R".equals(tipoRegistro)
                        || "L".equals(tipoRegistro);
                if (!itemsProcesados.contains(id)
                        || !tipoPermitido
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
            // El SP valida el asiento, el movimiento y los items ejecutados.
            List<Map<String, Object>> registros = repository.obtenerCorreosReenvioValidados(propPagoCabId);
            if (registros == null || registros.isEmpty()) {
                throw new IllegalStateException("No hay documentos ejecutados para reenviar.");
            }

            // Fase 1: validar TODO antes de publicar el primer evento.
            Set<String> claves = new LinkedHashSet<>();
            List<Map<String, Object>> pendientes = new ArrayList<>();
            List<Map<String, Object>> incidencias = new ArrayList<>();
            for (Map<String, Object> fila : registros) {
                String item = valorTexto(fila, "PropPagoDetItem", "");
                String tipo = valorTexto(fila, "TipoRegistro", "").toUpperCase(java.util.Locale.ROOT);
                if (item.isEmpty() || !claves.add(item)) {
                    throw new IllegalStateException("Item repetido o sin identificador: " + item);
                }
                if (!String.valueOf(propPagoCabId).equals(valorTexto(fila, "PropPagoCabId", ""))
                        || !"E".equalsIgnoreCase(valorTexto(fila, "PropPagoDetEstado", ""))
                        || !("C".equals(tipo) || "L".equals(tipo) || "R".equals(tipo) || "S".equals(tipo))) {
                    throw new IllegalStateException("Item no ejecutado o tipo no permitido: " + item);
                }
                for (String campo : new String[]{"MovBcoCabId", "DiaCabCompId", "DiaDetItem",
                        "OperacionBancaria", "Documento", "MontoAbonar"}) {
                    if (valorTexto(fila, campo, "").isEmpty()) {
                        throw new IllegalStateException("Falta " + campo + " en el item " + item);
                    }
                }
                String destino = valorTexto(fila, "Correo", "");
                if (!destino.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                    Map<String, Object> incidencia = new LinkedHashMap<>();
                    incidencia.put("item", item);
                    incidencia.put("motivo", "Correo ausente o no valido");
                    incidencias.add(incidencia);
                    continue;
                }
                Map<String, Object> envio = new LinkedHashMap<>(fila);
                envio.put("ReenvioProduccion", true); // No agregar CC de pruebas.
                pendientes.add(envio);
            }

            // Fase 2: publicar exclusivamente los items prevalidos con destinatario.
            int aceptados = 0;
            for (Map<String, Object> envio : pendientes) {
                String item = valorTexto(envio, "PropPagoDetItem", "");
                try {
                    Map<String, Object> respuesta = enviarEventoCorreoPago(envio);
                    if (respuesta != null && Boolean.TRUE.equals(respuesta.get("estado"))) {
                        aceptados++;
                    } else {
                        Map<String, Object> incidencia = new LinkedHashMap<>();
                        incidencia.put("item", item);
                        incidencia.put("motivo", "Publicacion no confirmada; verificar /noti antes de reintentar");
                        incidencias.add(incidencia);
                    }
                } catch (Exception ex) {
                    Map<String, Object> incidencia = new LinkedHashMap<>();
                    incidencia.put("item", item);
                    incidencia.put("motivo", "Publicacion incierta; verificar /noti antes de reintentar");
                    incidencias.add(incidencia);
                    System.err.println("Reenvio incierto, propuesta " + propPagoCabId
                            + ", item " + item + ": " + ex.getMessage());
                }
            }
            Map<String, Object> resultado = new LinkedHashMap<>();
            resultado.put("documentosElegibles", registros.size());
            resultado.put("documentosConCorreoValido", pendientes.size());
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
    // PRUEBA CONTROLADA: no genera comprobantes ni modifica SQL Server.
    // El destinatario es fijo y se exige activacion explicita en properties.
    public Map<String, Object> probarCorreosEjecutados(Integer propuestaId,
                                                       boolean confirmarEnvio) {
        if (!pruebaMasivaHabilitada) {
            throw new IllegalStateException("Prueba de correos deshabilitada en el servidor.");
        }
        if (propuestaId == null || propuestaId <= 0) {
            throw new IllegalArgumentException("PropPagoCabId invalido.");
        }
        List<Map<String, Object>> filas = repository.obtenerCorreosPropuesta(propuestaId, null);
        if (filas.isEmpty()) {
            throw new IllegalStateException("No hay pagos ejecutados y validados para la propuesta.");
        }
        if (filas.size() > 30) {
            throw new IllegalStateException("Prueba limitada a 30 documentos por propuesta.");
        }
        Set<String> vistos = new LinkedHashSet<>();
        List<Map<String, Object>> detalle = new ArrayList<>();
        int aceptados = 0;
        for (Map<String, Object> fila : filas) {
            String item = valorTexto(fila, "PropPagoDetItem", "");
            String tipo = valorTexto(fila, "TipoRegistro", "").toUpperCase(java.util.Locale.ROOT);
            if (!vistos.add(item) || item.isEmpty()) {
                throw new IllegalStateException("Item duplicado o sin identificador: " + item);
            }
            if (!("C".equals(tipo) || "S".equals(tipo) || "R".equals(tipo) || "L".equals(tipo))
                    || valorTexto(fila, "MovBcoCabId", "").isEmpty()) {
                throw new IllegalStateException("Pago no elegible en item " + item);
            }
            Map<String, Object> estado = new LinkedHashMap<>();
            estado.put("item", item);
            estado.put("tipo", tipo);
            estado.put("documento", valorTexto(fila, "Documento", ""));
            estado.put("correoPrueba", "cchinga@unjfsc.edu.pe");
            if (!confirmarEnvio) {
                estado.put("estado", "PREVISUALIZADO_NO_ENVIADO");
            } else {
                Map<String, Object> copia = new LinkedHashMap<>(fila);
                copia.put("Correo", "cchinga@unjfsc.edu.pe");
                copia.put("ReenvioProduccion", true); // Evita CC adicional.
                try {
                    Map<String, Object> respuesta = enviarEventoCorreoPago(copia);
                    if (respuesta != null && Boolean.TRUE.equals(respuesta.get("estado"))) {
                        estado.put("estado", "EVENTO_ACEPTADO");
                        aceptados++;
                    } else {
                        estado.put("estado", "EVENTO_NO_CONFIRMADO");
                    }
                } catch (Exception ex) {
                    estado.put("estado", "PUBLICACION_INCIERTA_VERIFICAR_NOTI");
                    estado.put("motivo", ex.getMessage());
                }
            }
            detalle.add(estado);
        }
        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("PropPagoCabId", propuestaId);
        resumen.put("modo", confirmarEnvio ? "ENVIO_PRUEBA" : "PREVISUALIZACION");
        resumen.put("destinatario", "cchinga@unjfsc.edu.pe");
        resumen.put("documentos", detalle.size());
        resumen.put("eventosAceptados", aceptados);
        resumen.put("detalle", detalle);
        resumen.put("advertencia", "Evento aceptado no equivale a entrega SMTP. No reintentar sin revisar /noti.");
        return resumen;
    }

}

