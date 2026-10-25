package com.bartolito.contabilidad.service;

import com.bartolito.contabilidad.repository.SolicitudPagoRepository;
import com.bartolito.contabilidad.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository repository;

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Map<String, Object>> listarUsuariosLolfar(
            Integer usecodLOLFAR
    ) {

        return repository.listarUsuariosLolfar(
                usecodLOLFAR
        );

    }

    public List<Map<String, Object>> obtenerEquivalenciaUsuarioLolfar(
            String usecodLOLFAR
    ) {


        return repository.obtenerEquivalenciaUsuarioLolfar(
                usecodLOLFAR
        );


    }
}
