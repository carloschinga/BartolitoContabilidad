package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.PropuestaPagoRepository;
import com.bartolito.contabilidad.repository.SolicitudPagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@Service
public class SolicitudPagoService {
    @Autowired
    private SolicitudPagoRepository repository;

   @Autowired
   private FileStorageService fileStorageService;

    // =========================================
    // LISTAR
    // =========================================
    public List<Map<String, Object>> listarSolicitudPago(
            String empresaId,
            String monedaId,
            String fechaInicio,
            String fechaFin,
            String estado,
            String usuario
    ) {

        return repository.listarSolicitudPago(
                empresaId,
                monedaId,
                fechaInicio,
                fechaFin,
                estado,
                usuario
        );
    }



    // =========================================
    // SERVICE
    // =========================================

    public Map<String, Object> registrarSolicitudPago(

            String empresaId,

            Integer tipSolPagoId,

            Integer tipConCajId,

            Integer conCajId,

            Long ctaCteId,

            Long provId,

            String trabId,

            String trabIdSoli,

            String descripcion,

            String fecha,

            String documento,

            MultipartFile archivo1,

            MultipartFile archivo2,

            BigDecimal total,

            String procId,

            String activId,

            String tareaId,

            String activoId,

            String prodId,

            String cenCostResp,

            String usuario
    ) {

        String rutaArchivo1 = null;
        String rutaArchivo2 = null;

        descripcion =
                (descripcion == null || descripcion.isBlank())
                        ? ""
                        : descripcion.trim();

        documento =
                (documento == null || documento.isBlank())
                        ? null
                        : documento.trim();


        trabId =
                (trabId == null || trabId.isBlank())
                        ? null
                        : trabId.trim();

        trabIdSoli =
                (trabIdSoli == null || trabIdSoli.isBlank())
                        ? null
                        : trabIdSoli.trim();

        try {

            if (documento != null && !documento.isBlank()) {

                if (archivo1 != null && !archivo1.isEmpty()) {

                    rutaArchivo1 =
                            fileStorageService.guardarArchivo(
                                    archivo1,
                                    documento,
                                    documento + "-01"
                            );
                }

                if (archivo2 != null && !archivo2.isEmpty()) {

                    rutaArchivo2 =
                            fileStorageService.guardarArchivo(
                                    archivo2,
                                    documento,
                                    documento + "-02"
                            );
                }
            }

        } catch (IOException e) {

            return Map.of(
                    "Resultado", 0,
                    "Mensaje", "Error al guardar archivos: "
                            + e.getMessage()
            );
        }

        return repository.registrarSolicitudPago(

                empresaId,

                tipSolPagoId,

                tipConCajId,

                conCajId,

                ctaCteId,

                provId,

                trabId,

                trabIdSoli,

                descripcion,

                fecha,

                documento,

                rutaArchivo1,

                rutaArchivo2,

                total,

                procId,

                activId,

                tareaId,

                activoId,

                prodId,

                cenCostResp,

                usuario
        );

    }

    // =========================================
    // ANULAR
    // =========================================
    public Map<String, Object> anularSolicitudPago(
            Long soliPagoCabId,
            String usuario
    ) {

        return repository.anularSolicitudPago(
                soliPagoCabId,
                usuario
        );
    }
    // ==========================================
// SERVICE
// ==========================================

    public List<Map<String, Object>> listarTipoSolicitudPago() {

        return repository.listarTipoSolicitudPago();

    }

    // ==========================================
// LISTAR PROVEEDORES
// ==========================================

    public List<Map<String, Object>> listarProveedor() {

        return repository.listarProveedor();

    }

    // =========================================
// SERVICE
// =========================================

    public List<Map<String, Object>> listarConceptoCaja(
            Integer tipConCajID,
            Integer tipSolCajId
    ) {

        return repository.listarConceptoCaja(
                tipConCajID,
                tipSolCajId
        );

    }

// ==========================================
// LISTAR TRABAJADORES
// ==========================================

    public List<Map<String, Object>> listarTrabajador(
            String empresaId
    ) {

        return repository.listarTrabajador(
                empresaId
        );

    }

    // =========================================
    // SERVICE
    // =========================================

    public List<Map<String, Object>> listarDepartamentoFuncional() {

        return repository.listarDepartamentoFuncional();

    }

    // =========================================
    // OBTENER
    // =========================================
    public Map<String, Object> obtenerSolicitudPago(
            Long soliPagoCabId
    ) {

        return repository.obtenerSolicitudPago(
                soliPagoCabId
        );
    }

}
