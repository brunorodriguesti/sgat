package com.ltd.sgat.sgat.api.projeto;

import com.ltd.sgat.sgat.api.projeto.dto.ProjetoDTO;
import com.ltd.sgat.sgat.domain.projeto.ProjetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    @GetMapping
    public ResponseEntity<List<ProjetoDTO>> getAllProjetos(@RequestParam(required = false) Long clienteId) {
        List<ProjetoDTO> projetos;
        if (clienteId != null) {
            projetos = projetoService.findByClienteId(clienteId);
        } else {
            projetos = projetoService.findAll();
        }
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoDTO> getProjetoById(@PathVariable Long id) {
        return projetoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProjetoDTO> createProjeto(@RequestBody ProjetoDTO projetoDTO) {
        try {
            ProjetoDTO savedProjeto = projetoService.save(projetoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProjeto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetoDTO> updateProjeto(@PathVariable Long id, @RequestBody ProjetoDTO projetoDTO) {
        try {
            return projetoService.update(id, projetoDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjeto(@PathVariable Long id) {
        if (projetoService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
