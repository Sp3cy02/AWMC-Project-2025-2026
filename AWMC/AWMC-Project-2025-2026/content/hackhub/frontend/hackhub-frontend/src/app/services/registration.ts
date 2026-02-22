import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class RegistrationService {
  private apiUrl = 'http://localhost:8080/api/registrations';

  constructor(private http: HttpClient) {}

  joinHackathon(hackathonId: number, teamId: number, userId: number) {
    return this.http.post(
      `${this.apiUrl}/join`,
      { hackathonId, teamId, userId },
      { responseType: 'text' }
    );
  }

  // team iscritti a un hackathon
  getTeamsByHackathon(hackathonId: number) {
    return this.http.get<any[]>(`${this.apiUrl}/hackathon/${hackathonId}/teams`);
  }
}