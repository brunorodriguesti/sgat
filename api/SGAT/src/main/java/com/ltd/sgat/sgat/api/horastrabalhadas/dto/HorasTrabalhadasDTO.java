package com.ltd.sgat.sgat.api.horastrabalhadas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorasTrabalhadasDTO {
    private Long id;
    private LocalDate dataRelatorio;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalTime horasTrabalhadas;
    private Long funcionarioId;
    private String funcionarioNome; // Para exibir o nome do funcionário
    private BigDecimal valor;
}
