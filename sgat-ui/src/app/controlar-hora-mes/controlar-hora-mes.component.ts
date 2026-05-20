import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Cliente, ClienteService } from '../cliente/cliente.service';
import { Projeto, ProjetoService } from '../projeto/projeto.service';
import { HorasTrabalhadasService, RelatorioHorasFuncionario, HorasTrabalhadas } from './horas-trabalhadas.service';

interface RelatorioHorasFuncionarioUI extends RelatorioHorasFuncionario {
  id?: number; // Add id for existing records
  editableHoras?: number; // Para permitir edição manual
  selected?: boolean; // Para seleção de exclusão
}

@Component({
  selector: 'app-controlar-hora-mes',
  templateUrl: './controlar-hora-mes.component.html',
  styleUrls: ['./controlar-hora-mes.component.css']
})
export class ControlarHoraMesComponent implements OnInit {
  filtroForm!: FormGroup;
  clientes: Cliente[] = [];
  projetos: Projeto[] = [];
  relatorioHoras: RelatorioHorasFuncionarioUI[] = [];
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private projetoService: ProjetoService,
    private horasTrabalhadasService: HorasTrabalhadasService
  ) { }

  ngOnInit(): void {
    this.filtroForm = this.fb.group({
      dataInicial: ['', Validators.required],
      dataFinal: ['', Validators.required],
      clienteId: ['', Validators.required],
      projetoId: ['', Validators.required]
    });

    this.loadClientes();
    this.onChanges();
  }

  onChanges(): void {
    this.filtroForm.get('clienteId')?.valueChanges.subscribe(clienteId => {
      if (clienteId) {
        this.loadProjetosByCliente(clienteId);
      } else {
        this.projetos = [];
        this.filtroForm.get('projetoId')?.setValue('');
      }
    });
  }

  loadClientes(): void {
    this.clienteService.getAllClientes().subscribe({
      next: (data) => this.clientes = data,
      error: (err) => {
        console.error('Erro ao carregar clientes', err);
        this.errorMessage = 'Erro ao carregar clientes.';
      }
    });
  }

  loadProjetosByCliente(clienteId: number): void {
    this.projetoService.getAllProjetos().subscribe({
      next: (data) => {
        this.projetos = data.filter(p => p.clienteId === clienteId);
        this.filtroForm.get('projetoId')?.setValue(''); // Reset project selection
      },
      error: (err) => {
        console.error('Erro ao carregar projetos por cliente', err);
        this.errorMessage = 'Erro ao carregar projetos.';
      }
    });
  }

  gerarRelatorio(): void {
    this.errorMessage = '';
    if (this.filtroForm.invalid) {
      this.errorMessage = 'Por favor, preencha todos os campos do filtro.';
      return;
    }

    const { dataInicial, dataFinal, clienteId, projetoId } = this.filtroForm.value;

    if (new Date(dataInicial) > new Date(dataFinal)) {
      this.errorMessage = 'A data inicial não pode ser maior que a data final.';
      return;
    }

    this.horasTrabalhadasService.gerarRelatorioHoras(dataInicial, dataFinal, clienteId, projetoId).subscribe({
      next: (data) => {
        this.relatorioHoras = data.map(item => ({
          ...item,
          editableHoras: item.horasTrabalhadasCalculadas, // Initialize editable hours
          selected: false // Initialize selected state
        }));
        if (this.relatorioHoras.length === 0) {
          this.errorMessage = 'Nenhum funcionário encontrado para os filtros selecionados.';
        }
      },
      error: (err) => {
        console.error('Erro ao gerar relatório', err);
        this.errorMessage = err.error?.message || 'Erro ao gerar relatório de horas.';
      }
    });
  }

  onHorasEditadas(funcionario: RelatorioHorasFuncionarioUI): void {
    if (funcionario.editableHoras !== undefined && funcionario.valorHora !== undefined) {
      funcionario.valorTotal = funcionario.editableHoras * funcionario.valorHora;
    }
  }

  salvarHoras(): void {
    this.errorMessage = '';
    if (this.relatorioHoras.length === 0) {
      this.errorMessage = 'Não há horas para salvar.';
      return;
    }

    const { dataFinal } = this.filtroForm.value; // Use dataFinal as dataTrabalho

    const horasParaSalvar: HorasTrabalhadas[] = this.relatorioHoras.map(f => ({
      id: f.id, // Include ID if it exists for update
      dataTrabalho: dataFinal, // As per documentation, "data trabalho final"
      horasTrabalhadas: f.editableHoras ? f.editableHoras.toString() : f.horasTrabalhadasCalculadas.toString(), // Use editable if present, else calculated
      funcionarioId: f.funcionarioId,
      valor: f.valorTotal
    }));

    this.horasTrabalhadasService.saveHorasTrabalhadasList(horasParaSalvar).subscribe({
      next: (response) => {
        console.log('Horas salvas com sucesso!', response);
        this.errorMessage = 'Horas salvas com sucesso!';
        // Update IDs of saved items in the UI if they were new
        response.forEach(savedItem => {
          const uiItem = this.relatorioHoras.find(item => item.funcionarioId === savedItem.funcionarioId);
          if (uiItem && !uiItem.id) {
            uiItem.id = savedItem.id;
          }
        });
      },
      error: (err) => {
        console.error('Erro ao salvar horas', err);
        this.errorMessage = err.error?.message || 'Erro ao salvar horas.';
      }
    });
  }

  editarHoras(): void {
    // Editing is done inline, this button might trigger a save or just be a visual cue
    this.errorMessage = 'Edição é feita diretamente na tabela. Use o botão Salvar para persistir as mudanças.';
  }

  excluirHoras(): void {
    const selectedFuncionarios = this.relatorioHoras.filter(f => f.selected);
    if (selectedFuncionarios.length === 0) {
      this.errorMessage = 'Selecione ao menos um funcionário para excluir.';
      return;
    }

    const idsToDelete = selectedFuncionarios.filter(f => f.id).map(f => f.id as number);
    const localDeleteCount = selectedFuncionarios.filter(f => !f.id).length;

    if (idsToDelete.length > 0) {
      this.horasTrabalhadasService.deleteHorasTrabalhadasList(idsToDelete).subscribe({
        next: () => {
          console.log('Horas excluídas com sucesso!', idsToDelete);
          this.relatorioHoras = this.relatorioHoras.filter(f => !f.selected);
          this.errorMessage = `Horas excluídas com sucesso. ${localDeleteCount} itens não salvos removidos localmente.`;
        },
        error: (err) => {
          console.error('Erro ao excluir horas', err);
          this.errorMessage = err.error?.message || 'Erro ao excluir horas.';
        }
      });
    } else if (localDeleteCount > 0) {
      this.relatorioHoras = this.relatorioHoras.filter(f => !f.selected);
      this.errorMessage = `${localDeleteCount} itens não salvos removidos localmente.`;
    } else {
      this.errorMessage = 'Nenhum item selecionado para exclusão possui ID salvo no backend.';
    }
  }

  imprimirRelatorio(): void {
    this.errorMessage = '';
    if (this.relatorioHoras.length === 0) {
      this.errorMessage = 'Não há relatório para imprimir.';
      return;
    }

    // Prepare data for PDF generation (backend expects RelatorioHorasFuncionario, not RelatorioHorasFuncionarioUI)
    const reportDataForPdf: RelatorioHorasFuncionario[] = this.relatorioHoras.map(item => ({
      funcionarioId: item.funcionarioId,
      funcionarioNome: item.funcionarioNome,
      cpf: item.cpf,
      horasTrabalhadasCalculadas: item.horasTrabalhadasCalculadas,
      valorHora: item.valorHora,
      valorTotal: item.valorTotal
    }));

    this.horasTrabalhadasService.generatePdfReport(reportDataForPdf).subscribe({
      next: (response: Blob) => {
        const fileURL = URL.createObjectURL(response);
        window.open(fileURL, '_blank');
        this.errorMessage = 'PDF gerado com sucesso!';
      },
      error: (err) => {
        console.error('Erro ao gerar PDF', err);
        this.errorMessage = err.error?.message || 'Erro ao gerar PDF do relatório.';
      }
    });
  }

  toggleSelectAll(event: any): void {
    const checked = event.target.checked;
    this.relatorioHoras.forEach(f => f.selected = checked);
  }
}
