package com.ltd.sgat.sgat.domain.feriaslicencas;

import com.ltd.sgat.sgat.domain.funcionario.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeriasLicencasRepository extends JpaRepository<FeriasLicencas, Long> {

    @Query("SELECT fl FROM FeriasLicencas fl WHERE fl.funcionario = :funcionario AND ((fl.dataInicio <= :dataFinal AND fl.dataFim >= :dataInicial))")
    List<FeriasLicencas> findByFuncionarioAndPeriodo(
            @Param("funcionario") Funcionario funcionario,
            @Param("dataInicial") LocalDate dataInicial,
            @Param("dataFinal") LocalDate dataFinal);
}