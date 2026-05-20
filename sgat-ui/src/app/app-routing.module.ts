import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AfastamentosComponent } from './afastamentos/afastamentos.component';
import { HomeComponent } from './home/home.component';

import { ClienteListComponent } from './cliente/cliente-list.component';
import { ClienteFormComponent } from './cliente/cliente-form.component';
import { ProjetoListComponent } from './projeto/projeto-list.component';
import { ProjetoFormComponent } from './projeto/projeto-form.component';
import { PerfilListComponent } from './perfil/perfil-list.component';
import { PerfilFormComponent } from './perfil/perfil-form.component';
import { FuncionarioListComponent } from './funcionario/funcionario-list.component';
import { FuncionarioFormComponent } from './funcionario/funcionario-form.component';
import { FeriadoListComponent } from './feriados/feriado-list.component';
import { FeriadoFormComponent } from './feriados/feriado-form.component';
import { FeriasLicencasListComponent } from './ferias/ferias-licencas-list.component';
import { FeriasLicencasFormComponent } from './ferias/ferias-licencas-form.component';
import { HorasTrabalhadasListComponent } from './controlar-hora-mes/horas-trabalhadas-list.component';
import { HorasTrabalhadasFormComponent } from './controlar-hora-mes/horas-trabalhadas-form.component';
import { ControlarHoraMesComponent } from './controlar-hora-mes/controlar-hora-mes.component';

const routes: Routes = [
  { path: 'clientes', component: ClienteListComponent },
  { path: 'clientes/novo', component: ClienteFormComponent },
  { path: 'clientes/editar/:id', component: ClienteFormComponent },
  { path: 'projetos', component: ProjetoListComponent },
  { path: 'projetos/novo', component: ProjetoFormComponent },
  { path: 'projetos/editar/:id', component: ProjetoFormComponent },
  { path: 'perfis', component: PerfilListComponent },
  { path: 'perfis/novo', component: PerfilFormComponent },
  { path: 'perfis/editar/:id', component: PerfilFormComponent },
  { path: 'funcionarios', component: FuncionarioListComponent },
  { path: 'funcionarios/novo', component: FuncionarioFormComponent },
  { path: 'funcionarios/editar/:id', component: FuncionarioFormComponent },
  { path: 'feriados', component: FeriadoListComponent },
  { path: 'feriados/novo', component: FeriadoFormComponent },
  { path: 'feriados/editar/:id', component: FeriadoFormComponent },
  { path: 'ferias-licencas', component: FeriasLicencasListComponent },
  { path: 'ferias-licencas/novo', component: FeriasLicencasFormComponent },
  { path: 'ferias-licencas/editar/:id', component: FeriasLicencasFormComponent },
  { path: 'horas-trabalhadas', component: HorasTrabalhadasListComponent },
  { path: 'horas-trabalhadas/novo', component: HorasTrabalhadasFormComponent },
  { path: 'horas-trabalhadas/editar/:id', component: HorasTrabalhadasFormComponent },
  { path: 'controlar-horas', component: ControlarHoraMesComponent },
  { path: 'configuracoes/afastamentos', component: AfastamentosComponent },
  { path: '', component: HomeComponent, pathMatch: 'full' }, // Default route
  { path: 'home', component: HomeComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }