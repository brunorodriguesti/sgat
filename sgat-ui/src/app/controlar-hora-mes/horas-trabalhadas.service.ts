import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface HorasTrabalhadas {
  id?: number;
  dataTrabalho: string; // Representando LocalDate como string (YYYY-MM-DD)
  horasTrabalhadas: string; // Representando LocalTime como string (HH:mm:ss)
  funcionarioId: number;
  funcionarioNome?: string; // Para exibir o nome do funcionário
  valor: number;
}

export interface RelatorioHorasFuncionario {
  funcionarioId: number;
  funcionarioNome: string;
  cpf: string;
  horasTrabalhadasCalculadas: number;
  valorHora: number;
  valorTotal: number;
}

@Injectable({
  providedIn: 'root'
})
export class HorasTrabalhadasService {
  private apiUrl = 'http://localhost:8081/api/horas-trabalhadas';

  constructor(private http: HttpClient) { }

  getAllHorasTrabalhadas(): Observable<HorasTrabalhadas[]> {
    return this.http.get<HorasTrabalhadas[]>(this.apiUrl);
  }

  getHorasTrabalhadasById(id: number): Observable<HorasTrabalhadas> {
    return this.http.get<HorasTrabalhadas>(`${this.apiUrl}/${id}`);
  }

  createHorasTrabalhadas(horasTrabalhadas: HorasTrabalhadas): Observable<HorasTrabalhadas> {
    return this.http.post<HorasTrabalhadas>(this.apiUrl, horasTrabalhadas);
  }

  saveHorasTrabalhadasList(horasTrabalhadasList: HorasTrabalhadas[]): Observable<HorasTrabalhadas[]> {
    return this.http.post<HorasTrabalhadas[]>(`${this.apiUrl}/batch`, horasTrabalhadasList);
  }

  updateHorasTrabalhadas(id: number, horasTrabalhadas: HorasTrabalhadas): Observable<HorasTrabalhadas> {
    return this.http.put<HorasTrabalhadas>(`${this.apiUrl}/${id}`, horasTrabalhadas);
  }

  deleteHorasTrabalhadas(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  deleteHorasTrabalhadasList(ids: number[]): Observable<void> {
    return this.http.request<void>('delete', `${this.apiUrl}/batch`, { body: ids });
  }

  gerarRelatorioHoras(dataInicial: string, dataFinal: string, clienteId: number, projetoId: number): Observable<RelatorioHorasFuncionario[]> {
    let params = new HttpParams();
    params = params.append('dataInicial', dataInicial);
    params = params.append('dataFinal', dataFinal);
    params = params.append('clienteId', clienteId.toString());
    params = params.append('projetoId', projetoId.toString());

    return this.http.get<RelatorioHorasFuncionario[]>(`${this.apiUrl}/relatorio`, { params });
  }

  generatePdfReport(reportData: RelatorioHorasFuncionario[]): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/relatorio/pdf`, reportData, { responseType: 'blob' });
  }
}
