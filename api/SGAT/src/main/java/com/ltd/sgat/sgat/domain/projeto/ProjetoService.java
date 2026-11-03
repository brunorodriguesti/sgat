package com.ltd.sgat.sgat.domain.projeto;

import com.ltd.sgat.sgat.api.projeto.dto.ProjetoDTO;
import com.ltd.sgat.sgat.domain.cliente.Cliente;
import com.ltd.sgat.sgat.domain.cliente.ClienteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<ProjetoDTO> findAll() {
        return projetoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Novo método para buscar projetos por clienteId
    public List<ProjetoDTO> findByClienteId(Long clienteId) {
        return projetoRepository.findByClienteId(clienteId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<ProjetoDTO> findById(Long id) {
        return projetoRepository.findById(id)
                .map(this::convertToDto);
    }

    public ProjetoDTO save(ProjetoDTO projetoDTO) {
        Projeto projeto = convertToEntity(projetoDTO);
        Projeto savedProjeto = projetoRepository.save(projeto);
        return convertToDto(savedProjeto);
    }

    public Optional<ProjetoDTO> update(Long id, ProjetoDTO projetoDTO) {
        return projetoRepository.findById(id)
                .map(existingProjeto -> {
                    existingProjeto.setNome(projetoDTO.getNome());
                    clienteRepository.findById(projetoDTO.getClienteId()).ifPresent(existingProjeto::setCliente);
                    Projeto updatedProjeto = projetoRepository.save(existingProjeto);
                    return convertToDto(updatedProjeto);
                });
    }

    public boolean delete(Long id) {
        if (projetoRepository.existsById(id)) {
            projetoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private ProjetoDTO convertToDto(Projeto projeto) {
        ProjetoDTO projetoDTO = modelMapper.map(projeto, ProjetoDTO.class);
        if (projeto.getCliente() != null) {
            projetoDTO.setClienteId(projeto.getCliente().getId());
            projetoDTO.setClienteNome(projeto.getCliente().getNome());
        }
        return projetoDTO;
    }

    private Projeto convertToEntity(ProjetoDTO projetoDTO) {
        Projeto projeto = modelMapper.map(projetoDTO, Projeto.class);
        if (projetoDTO.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(projetoDTO.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            projeto.setCliente(cliente);
        }
        return projeto;
    }
}
