import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HorasTrabalhadas, HorasTrabalhadasService } from './horas-trabalhadas.service';
import { Funcionario, FuncionarioService } from '../funcionario/funcionario.service';

@Component({
  selector: 'app-horas-trabalhadas-form',
  templateUrl: './horas-trabalhadas-form.component.html',
  styleUrls: ['./horas-trabalhadas-form.component.css']
})
export class HorasTrabalhadasFormComponent implements OnInit {
  horasTrabalhadas: HorasTrabalhadas = {
    dataTrabalho: '',
    horasTrabalhadas: 0, // Alterado para 0
    funcionarioId: 0,
    valor: 0
  };
  funcionarios: Funcionario[] = [];
  isEditMode: boolean = false;

  constructor(
    private horasTrabalhadasService: HorasTrabalhadasService,
    private funcionarioService: FuncionarioService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.loadFuncionarios();

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.horasTrabalhadasService.getHorasTrabalhadasById(+id).subscribe(
          (data) => {
            this.horasTrabalhadas = data;
          },
          (error) => {
            console.error('Erro ao carregar horas trabalhadas para edição:', error);
          }
        );
      }
    });
  }

  loadFuncionarios(): void {
    this.funcionarioService.getAllFuncionarios().subscribe(
      (data) => {
        this.funcionarios = data;
      },
      (error) => {
        console.error('Erro ao carregar funcionários:', error);
      }
    );
  }

  saveHorasTrabalhadas(): void {
    if (this.isEditMode) {
      this.horasTrabalhadasService.updateHorasTrabalhadas(this.horasTrabalhadas.id!, this.horasTrabalhadas).subscribe(
        () => {
          this.router.navigate(['/horas-trabalhadas']);
        },
        (error) => {
          console.error('Erro ao atualizar horas trabalhadas:', error);
        }
      );
    } else {
      this.horasTrabalhadasService.createHorasTrabalhadas(this.horasTrabalhadas).subscribe(
        () => {
          this.router.navigate(['/horas-trabalhadas']);
        },
        (error) => {
          console.error('Erro ao criar horas trabalhadas:', error);
        }
      );
    }
  }

  cancel(): void {
    this.router.navigate(['/horas-trabalhadas']);
  }
}
