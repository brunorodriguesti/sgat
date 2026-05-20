package com.ltd.sgat.sgat.domain.horastrabalhadas;

import com.ltd.sgat.sgat.api.horastrabalhadas.dto.HorasTrabalhadasDTO;
import com.ltd.sgat.sgat.api.horastrabalhadas.dto.RelatorioHorasFuncionarioDTO;
import com.ltd.sgat.sgat.domain.cliente.ClienteRepository;
import com.ltd.sgat.sgat.domain.feriado.Feriado;
import com.ltd.sgat.sgat.domain.feriado.FeriadoRepository;
import com.ltd.sgat.sgat.domain.feriaslicencas.FeriasLicencas;
import com.ltd.sgat.sgat.domain.feriaslicencas.FeriasLicencasRepository;
import com.ltd.sgat.sgat.domain.funcionario.Funcionario;
import com.ltd.sgat.sgat.domain.funcionario.FuncionarioRepository;
import com.ltd.sgat.sgat.domain.projeto.Projeto;
import com.ltd.sgat.sgat.domain.projeto.ProjetoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HorasTrabalhadasService {

    @Autowired
    private HorasTrabalhadasRepository horasTrabalhadasRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private FeriadoRepository feriadoRepository;

    @Autowired
    private FeriasLicencasRepository feriasLicencasRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<HorasTrabalhadasDTO> findAll() {
        return horasTrabalhadasRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<HorasTrabalhadasDTO> findById(Long id) {
        return horasTrabalhadasRepository.findById(id)
                .map(this::convertToDto);
    }

    public HorasTrabalhadasDTO save(HorasTrabalhadasDTO horasTrabalhadasDTO) {
        HorasTrabalhadas horasTrabalhadas = convertToEntity(horasTrabalhadasDTO);
        HorasTrabalhadas savedHorasTrabalhadas = horasTrabalhadasRepository.save(horasTrabalhadas);
        return convertToDto(savedHorasTrabalhadas);
    }

    public List<HorasTrabalhadasDTO> saveHorasTrabalhadasList(List<HorasTrabalhadasDTO> horasTrabalhadasDTOList) {
        List<HorasTrabalhadas> horasTrabalhadasEntities = horasTrabalhadasDTOList.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        List<HorasTrabalhadas> savedEntities = horasTrabalhadasRepository.saveAll(horasTrabalhadasEntities);
        return savedEntities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<HorasTrabalhadasDTO> update(Long id, HorasTrabalhadasDTO horasTrabalhadasDTO) {
        return horasTrabalhadasRepository.findById(id)
                .map(existingHorasTrabalhadas -> {
                    existingHorasTrabalhadas.setDataTrabalho(horasTrabalhadasDTO.getDataRelatorio());
                    existingHorasTrabalhadas.setHorasTrabalhadas(horasTrabalhadasDTO.getHorasTrabalhadas());
                    existingHorasTrabalhadas.setValor(horasTrabalhadasDTO.getValor());

                    funcionarioRepository.findById(horasTrabalhadasDTO.getFuncionarioId()).ifPresent(existingHorasTrabalhadas::setFuncionario);

                    HorasTrabalhadas updatedHorasTrabalhadas = horasTrabalhadasRepository.save(existingHorasTrabalhadas);
                    return convertToDto(updatedHorasTrabalhadas);
                });
    }

    public boolean delete(Long id) {
        if (horasTrabalhadasRepository.existsById(id)) {
            horasTrabalhadasRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public void deleteHorasTrabalhadasList(List<Long> ids) {
        horasTrabalhadasRepository.deleteAllById(ids);
    }

    public List<RelatorioHorasFuncionarioDTO> gerarRelatorioHoras(LocalDate dataInicial, LocalDate dataFinal, Long clienteId, Long projetoId) {
        if (dataInicial.isAfter(dataFinal)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A data inicial não pode ser maior que a data final.");
        }

        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado."));

        // Filter employees by project and client (assuming project has a client or employees are linked to client via project)
        // For now, let's assume employees are directly linked to a project, and the project is linked to a client.
        List<Funcionario> funcionariosDoProjeto = funcionarioRepository.findByProjetoId(projetoId);

        List<RelatorioHorasFuncionarioDTO> relatorio = new ArrayList<>();

        // Get all holidays in the period
        Set<LocalDate> feriadosNoPeriodo = feriadoRepository.findByDataBetween(dataInicial, dataFinal)
                .stream()
                .map(Feriado::getData)
                .collect(Collectors.toSet());

        for (Funcionario funcionario : funcionariosDoProjeto) {
            double diasUteisNoPeriodo = 0;
            LocalDate tempDate = dataInicial;
            while (!tempDate.isAfter(dataFinal)) {
                if (tempDate.getDayOfWeek() != DayOfWeek.SATURDAY && tempDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    diasUteisNoPeriodo++;
                }
                tempDate = tempDate.plusDays(1);
            }

            // Subtract holidays
            long feriadosDoFuncionario = feriadosNoPeriodo.stream()
                    .filter(date -> !date.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !date.getDayOfWeek().equals(DayOfWeek.SUNDAY))
                    .filter(date -> date.isAfter(dataInicial.minusDays(1)) && date.isBefore(dataFinal.plusDays(1)))
                    .count();
            diasUteisNoPeriodo -= feriadosDoFuncionario;

            // Subtract vacations/licenses for this employee
            List<FeriasLicencas> feriasLicencasDoFuncionario = feriasLicencasRepository.findByFuncionarioAndPeriodo(funcionario, dataInicial, dataFinal);
            for (FeriasLicencas fl : feriasLicencasDoFuncionario) {
                LocalDate start = fl.getDataInicio().isBefore(dataInicial) ? dataInicial : fl.getDataInicio();
                LocalDate end = fl.getDataFim().isAfter(dataFinal) ? dataFinal : fl.getDataFim();

                long diasFeriasLicenca = Stream.iterate(start, date -> date.plusDays(1))
                        .limit(ChronoUnit.DAYS.between(start, end) + 1)
                        .filter(date -> date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY)
                        .filter(date -> !feriadosNoPeriodo.contains(date)) // Don't double count if it's also a holiday
                        .count();
                diasUteisNoPeriodo -= diasFeriasLicenca;
            }

            double horasTrabalhadasCalculadas = diasUteisNoPeriodo * 8; // 8 hours per day
            double valorHora = funcionario.getValorHora() != null ? funcionario.getValorHora().doubleValue() : 0.0;
            double valorTotal = horasTrabalhadasCalculadas * valorHora;

            RelatorioHorasFuncionarioDTO dto = new RelatorioHorasFuncionarioDTO();
            dto.setFuncionarioId(funcionario.getId());
            dto.setFuncionarioNome(funcionario.getNome());
            dto.setCpf(funcionario.getCpf());
            dto.setHorasTrabalhadasCalculadas(horasTrabalhadasCalculadas);
            dto.setValorHora(valorHora);
            dto.setValorTotal(valorTotal);
            relatorio.add(dto);
        }

        return relatorio;
    }

    public byte[] generatePdfReport(List<RelatorioHorasFuncionarioDTO> reportData) {
        // Placeholder for PDF generation logic
        StringBuilder pdfContent = new StringBuilder();
        pdfContent.append("Relatório de Horas Trabalhadas\n\n");
        pdfContent.append("--------------------------------------------------------------------------------\n");
        pdfContent.append(String.format("%-20s %-15s %-15s %-15s %-15s\n", "Funcionário", "CPF", "Horas", "Valor Hora", "Valor Total"));
        pdfContent.append("--------------------------------------------------------------------------------\n");

        for (RelatorioHorasFuncionarioDTO item : reportData) {
            pdfContent.append(String.format("%-20s %-15s %-15.2f %-15.2f %-15.2f\n",
                    item.getFuncionarioNome(),
                    item.getCpf(),
                    item.getHorasTrabalhadasCalculadas(),
                    item.getValorHora(),
                    item.getValorTotal()));
        }
        pdfContent.append("--------------------------------------------------------------------------------\n");

        // In a real application, you would use a PDF library (e.g., iText, Apache PDFBox) here
        // For now, we'll just return the string content as bytes.
        return pdfContent.toString().getBytes();
    }


    private HorasTrabalhadasDTO convertToDto(HorasTrabalhadas horasTrabalhadas) {
        HorasTrabalhadasDTO horasTrabalhadasDTO = modelMapper.map(horasTrabalhadas, HorasTrabalhadasDTO.class);
        if (horasTrabalhadas.getFuncionario() != null) {
            horasTrabalhadasDTO.setFuncionarioId(horasTrabalhadas.getFuncionario().getId());
            horasTrabalhadasDTO.setFuncionarioNome(horasTrabalhadas.getFuncionario().getNome());
        }
        return horasTrabalhadasDTO;
    }

    private HorasTrabalhadas convertToEntity(HorasTrabalhadasDTO horasTrabalhadasDTO) {
        HorasTrabalhadas horasTrabalhadas = modelMapper.map(horasTrabalhadasDTO, HorasTrabalhadas.class);
        if (horasTrabalhadasDTO.getFuncionarioId() != null) {
            Funcionario funcionario = funcionarioRepository.findById(horasTrabalhadasDTO.getFuncionarioId())
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
            horasTrabalhadas.setFuncionario(funcionario);
        }
        return horasTrabalhadas;
    }
}