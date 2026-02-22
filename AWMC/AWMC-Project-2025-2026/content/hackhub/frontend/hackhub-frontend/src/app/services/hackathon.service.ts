import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class HackathonService {
  private apiUrl = 'http://localhost:8080/api/hackathons';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<any[]>(this.apiUrl);
  }

  getById(id: number) {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  create(dto: any) {
    return this.http.post<any>(`${this.apiUrl}/create`, dto);
  }

  nextPhase(hackathonId: number, organizerId: number) {
    return this.http.post(`${this.apiUrl}/${hackathonId}/next-phase?organizerId=${organizerId}`, null, {
      responseType: 'text',
    });
  }

  payPrize(hackathonId: number, organizerId: number) {
    return this.http.post(`${this.apiUrl}/${hackathonId}/pay-prize?organizerId=${organizerId}`, null, {
      responseType: 'text',
    });
  }

  proclaimWinner(body: any) {
    return this.http.post(`${this.apiUrl}/proclaim-winner`, body, { responseType: 'text' });
  }
}