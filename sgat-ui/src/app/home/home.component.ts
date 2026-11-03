import { Component, OnInit } from '@angular/core';
import { ClienteService } from '../cliente/cliente.service';
import { ProjetoService } from '../projeto/projeto.service';
import { FuncionarioService } from '../funcionario/funcionario.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  totalClientes: number | null = null;
  totalProjetos: number | null = null;
  totalFuncionarios: number | null = null;
  isLoading = true;

  constructor(
    private clienteService: ClienteService,
    private projetoService: ProjetoService,
    private funcionarioService: FuncionarioService
  ) {}

  ngOnInit(): void {
    this.carregarMetricas();
  }

  carregarMetricas(): void {
    this.isLoading = true;

    this.clienteService.getAllClientes().subscribe({
      next: (clientes) => this.totalClientes = clientes ? clientes.length : 0,
      error: () => this.totalClientes = 0
    });

    this.projetoService.getAllProjetos().subscribe({
      next: (projetos) => this.totalProjetos = projetos ? projetos.length : 0,
      error: () => this.totalProjetos = 0
    });

    this.funcionarioService.getAllFuncionarios().subscribe({
      next: (funcionarios) => {
        this.totalFuncionarios = funcionarios ? funcionarios.length : 0;
        this.isLoading = false;
      },
      error: () => {
        this.totalFuncionarios = 0;
        this.isLoading = false;
      }
    });
  }
}
