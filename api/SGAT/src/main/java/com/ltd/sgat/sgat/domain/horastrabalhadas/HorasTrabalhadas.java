package com.ltd.sgat.sgat.domain.horastrabalhadas;

import com.ltd.sgat.sgat.domain.funcionario.Funcionario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "horas_trabalhadas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorasTrabalhadas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_trabalho")
    private LocalDate dataTrabalho;

    @Column(name = "data_relatorio")
    private LocalDate dataRelatorio;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "horas_trabalhadas", precision = 6, scale = 2)
    private BigDecimal horasTrabalhadas;

    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(name = "valor", precision = 11, scale = 2)
    private BigDecimal valor;

    @PrePersist
    @PreUpdate
    public void syncDates() {
        if (this.dataTrabalho != null && this.dataRelatorio == null) {
            this.dataRelatorio = this.dataTrabalho;
        } else if (this.dataRelatorio != null && this.dataTrabalho == null) {
            this.dataTrabalho = this.dataRelatorio;
        }
    }
}
