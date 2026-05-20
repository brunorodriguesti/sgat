package com.ltd.sgat.sgat.api.horastrabalhadas.dto;

import lombok.Data;

@Data
public class RelatorioHorasFuncionarioDTO {
    private Long funcionarioId;
    private String funcionarioNome;
    private String cpf;
    private Double horasTrabalhadasCalculadas;
    private Double valorHora;
    private Double valorTotal;
}
