import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

interface BreadcrumbInfo {
  category: string;
  title: string;
  icon: string;
}

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent implements OnInit {
  isCollapsed = false;
  isMobileOpen = false;
  currentDate = new Date();

  // Accordion state for menu groups
  groups: { [key: string]: boolean } = {
    horas: true,
    cadastros: true,
    configuracoes: false
  };

  currentRoute: string = '';
  pageInfo: BreadcrumbInfo = {
    category: 'Principal',
    title: 'Visão Geral',
    icon: 'bi-grid-1x2-fill'
  };

  constructor(private router: Router) {}

  ngOnInit(): void {
    const savedCollapsed = localStorage.getItem('sgat_sidebar_collapsed');
    if (savedCollapsed !== null) {
      this.isCollapsed = savedCollapsed === 'true';
    }

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.currentRoute = event.urlAfterRedirects || event.url;
        this.updatePageInfo(this.currentRoute);
        this.autoOpenParentGroup(this.currentRoute);
        this.isMobileOpen = false;
      });

    this.currentRoute = this.router.url;
    this.updatePageInfo(this.currentRoute);
    this.autoOpenParentGroup(this.currentRoute);
  }

  toggleSidebar(): void {
    this.isCollapsed = !this.isCollapsed;
    localStorage.setItem('sgat_sidebar_collapsed', String(this.isCollapsed));
  }

  toggleMobileMenu(): void {
    this.isMobileOpen = !this.isMobileOpen;
  }

  closeMobileMenu(): void {
    this.isMobileOpen = false;
  }

  toggleGroup(name: string): void {
    this.groups[name] = !this.groups[name];
  }

  private autoOpenParentGroup(url: string): void {
    if (url.includes('controlar-horas') || url.includes('horas-trabalhadas') || url.includes('ferias-licencas')) {
      this.groups['horas'] = true;
    } else if (url.includes('cliente') || url.includes('projeto') || url.includes('perfil') || url.includes('funcionario')) {
      this.groups['cadastros'] = true;
    } else if (url.includes('feriado') || url.includes('afastamentos')) {
      this.groups['configuracoes'] = true;
    }
  }

  private updatePageInfo(url: string): void {
    if (url === '/' || url === '/home' || url === '') {
      this.pageInfo = { category: 'Início', title: 'Painel Geral & Indicadores', icon: 'bi-speedometer2' };
    } else if (url.includes('/controlar-horas')) {
      this.pageInfo = { category: 'Operações', title: 'Controlar Horas Trabalhadas (CDU007)', icon: 'bi-clock-history' };
    } else if (url.includes('/horas-trabalhadas/novo') || url.includes('/horas-trabalhadas/editar')) {
      this.pageInfo = { category: 'Operações', title: 'Registro de Horas Trabalhadas', icon: 'bi-pencil-square' };
    } else if (url.includes('/horas-trabalhadas')) {
      this.pageInfo = { category: 'Operações', title: 'Horas Trabalhadas Registradas', icon: 'bi-journal-check' };
    } else if (url.includes('/ferias-licencas/novo') || url.includes('/ferias-licencas/editar')) {
      this.pageInfo = { category: 'Operações', title: 'Registro de Férias e Licença', icon: 'bi-pencil-square' };
    } else if (url.includes('/ferias-licencas')) {
      this.pageInfo = { category: 'Operações', title: 'Gestão de Férias e Licenças', icon: 'bi-calendar-event' };
    } else if (url.includes('/clientes/novo') || url.includes('/clientes/editar')) {
      this.pageInfo = { category: 'Cadastros', title: 'Formulário de Cliente', icon: 'bi-building' };
    } else if (url.includes('/clientes')) {
      this.pageInfo = { category: 'Cadastros', title: 'Clientes Cadastrados', icon: 'bi-building' };
    } else if (url.includes('/projetos/novo') || url.includes('/projetos/editar')) {
      this.pageInfo = { category: 'Cadastros', title: 'Formulário de Projeto', icon: 'bi-kanban' };
    } else if (url.includes('/projetos')) {
      this.pageInfo = { category: 'Cadastros', title: 'Projetos de Clientes', icon: 'bi-kanban' };
    } else if (url.includes('/perfis/novo') || url.includes('/perfis/editar')) {
      this.pageInfo = { category: 'Cadastros', title: 'Formulário de Perfil Profissional', icon: 'bi-person-gear' };
    } else if (url.includes('/perfis')) {
      this.pageInfo = { category: 'Cadastros', title: 'Perfis Profissionais & Taxas Padrão', icon: 'bi-person-lines-fill' };
    } else if (url.includes('/funcionarios/novo') || url.includes('/funcionarios/editar')) {
      this.pageInfo = { category: 'Cadastros', title: 'Formulário de Funcionário / Consultor', icon: 'bi-person-plus' };
    } else if (url.includes('/funcionarios')) {
      this.pageInfo = { category: 'Cadastros', title: 'Quadro de Funcionários Alocados', icon: 'bi-people-fill' };
    } else if (url.includes('/feriados/novo') || url.includes('/feriados/editar')) {
      this.pageInfo = { category: 'Configurações', title: 'Formulário de Feriado', icon: 'bi-calendar-plus' };
    } else if (url.includes('/feriados')) {
      this.pageInfo = { category: 'Configurações', title: 'Calendário de Feriados', icon: 'bi-calendar-x' };
    } else if (url.includes('/configuracoes/afastamentos')) {
      this.pageInfo = { category: 'Configurações', title: 'Regras de Afastamentos & Descontos', icon: 'bi-shield-check' };
    } else if (url.includes('/teste')) {
      this.pageInfo = { category: 'Desenvolvimento', title: 'Ambiente de Testes', icon: 'bi-wrench-adjustable' };
    } else {
      this.pageInfo = { category: 'SGAT', title: 'Sistema de Alocação de Talentos', icon: 'bi-grid-1x2-fill' };
    }
  }
}
