package com.ltd.sgat.sgat.domain.horastrabalhadas;

import com.ltd.sgat.sgat.domain.funcionario.Funcionario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "horas_trabalhadas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorasTrabalhadas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_trabalho", nullable = false)
    private LocalDate dataTrabalho;

    @Column(name = "horas_trabalhadas", nullable = false)
    private LocalTime horasTrabalhadas; // Or BigDecimal if representing duration

    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(name = "valor", precision = 11, scale = 2)
    private BigDecimal valor;
}