package com.bartolito.contabilidad.dto;

import java.math.BigDecimal;

public class PagoProveedorRequest {
    private String pagoProvNombre;
    private BigDecimal pagoProvMonto;
    private Integer pagoProvEstadoId;
    private Integer usecreabart; // Usado para Crear
    private Integer usemodibart; // Usado para Actualizar

    // Getters y Setters
    public String getPagoProvNombre() { return pagoProvNombre; }
    public void setPagoProvNombre(String pagoProvNombre) { this.pagoProvNombre = pagoProvNombre; }
    public BigDecimal getPagoProvMonto() { return pagoProvMonto; }
    public void setPagoProvMonto(BigDecimal pagoProvMonto) { this.pagoProvMonto = pagoProvMonto; }
    public Integer getPagoProvEstadoId() { return pagoProvEstadoId; }
    public void setPagoProvEstadoId(Integer pagoProvEstadoId) { this.pagoProvEstadoId = pagoProvEstadoId; }
    public Integer getUsecreabart() { return usecreabart; }
    public void setUsecreabart(Integer usecreabart) { this.usecreabart = usecreabart; }
    public Integer getUsemodibart() { return usemodibart; }
    public void setUsemodibart(Integer usemodibart) { this.usemodibart = usemodibart; }

}
