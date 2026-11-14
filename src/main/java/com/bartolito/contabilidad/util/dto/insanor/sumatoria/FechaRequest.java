package com.bartolito.contabilidad.util.dto.insanor.sumatoria;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class FechaRequest {
    private LocalDate fechaOperacion;
}
