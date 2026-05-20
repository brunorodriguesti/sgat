package com.ltd.sgat.sgat.api.horastrabalhadas;

import com.ltd.sgat.sgat.api.horastrabalhadas.dto.HorasTrabalhadasDTO;
import com.ltd.sgat.sgat.api.horastrabalhadas.dto.RelatorioHorasFuncionarioDTO;
import com.ltd.sgat.sgat.domain.horastrabalhadas.HorasTrabalhadasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/horas-trabalhadas")
public class HorasTrabalhadasController {

    @Autowired
    private HorasTrabalhadasService horasTrabalhadasService;

    @GetMapping
    public ResponseEntity<List<HorasTrabalhadasDTO>> getAllHorasTrabalhadas() {
        List<HorasTrabalhadasDTO> horasTrabalhadas = horasTrabalhadasService.findAll();
        return ResponseEntity.ok(horasTrabalhadas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorasTrabalhadasDTO> getHorasTrabalhadasById(@PathVariable Long id) {
        return horasTrabalhadasService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<HorasTrabalhadasDTO> createHorasTrabalhadas(@RequestBody HorasTrabalhadasDTO horasTrabalhadasDTO) {
        try {
            HorasTrabalhadasDTO savedHorasTrabalhadas = horasTrabalhadasService.save(horasTrabalhadasDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedHorasTrabalhadas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<List<HorasTrabalhadasDTO>> saveHorasTrabalhadasList(@RequestBody List<HorasTrabalhadasDTO> horasTrabalhadasDTOList) {
        try {
            List<HorasTrabalhadasDTO> savedHorasTrabalhadas = horasTrabalhadasService.saveHorasTrabalhadasList(horasTrabalhadasDTOList);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedHorasTrabalhadas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<HorasTrabalhadasDTO> updateHorasTrabalhadas(@PathVariable Long id, @RequestBody HorasTrabalhadasDTO horasTrabalhadasDTO) {
        try {
            return horasTrabalhadasService.update(id, horasTrabalhadasDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHorasTrabalhadas(@PathVariable Long id) {
        if (horasTrabalhadasService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteHorasTrabalhadasList(@RequestBody List<Long> ids) {
        try {
            horasTrabalhadasService.deleteHorasTrabalhadasList(ids);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/relatorio")
    public ResponseEntity<List<RelatorioHorasFuncionarioDTO>> gerarRelatorio(
            @RequestParam("dataInicial") LocalDate dataInicial,
            @RequestParam("dataFinal") LocalDate dataFinal,
            @RequestParam("clienteId") Long clienteId,
            @RequestParam("projetoId") Long projetoId) {
        try {
            List<RelatorioHorasFuncionarioDTO> relatorio = horasTrabalhadasService.gerarRelatorioHoras(dataInicial, dataFinal, clienteId, projetoId);
            return ResponseEntity.ok(relatorio);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/relatorio/pdf")
    public ResponseEntity<byte[]> gerarRelatorioPdf(@RequestBody List<RelatorioHorasFuncionarioDTO> reportData) {
        try {
            byte[] pdfBytes = horasTrabalhadasService.generatePdfReport(reportData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "relatorio_horas.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
