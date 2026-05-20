package com.ltd.sgat.sgat.domain.feriado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeriadoRepository extends JpaRepository<Feriado, Long> {

    @Query("SELECT f FROM Feriado f WHERE f.data BETWEEN :startDate AND :endDate")
    List<Feriado> findByDataBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}