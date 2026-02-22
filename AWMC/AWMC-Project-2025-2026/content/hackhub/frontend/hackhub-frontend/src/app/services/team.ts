import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class TeamService {
  private apiUrl = 'http://localhost:8080/api/teams';

  constructor(
    private http: HttpClient,
    private auth: AuthService
  ) {}

  // =============================
  // TEAM STORAGE PER UTENTE
  // =============================

  private getStorageKey(): string | null {
    const user = this.auth.getUser();
    if (!user?.id) return null;
    return `hackhub_team_id_${user.id}`;
  }

  saveTeamId(teamId: number) {
    const key = this.getStorageKey();
    if (!key) return;
    localStorage.setItem(key, String(teamId));
  }

  getTeamId(): number | null {
    const key = this.getStorageKey();
    if (!key) return null;

    const value = localStorage.getItem(key);
    const num = value ? Number(value) : NaN;

    return Number.isFinite(num) && num > 0 ? num : null;
  }

  clearTeamId() {
    const key = this.getStorageKey();
    if (!key) return;
    localStorage.removeItem(key);
  }

  // =============================
  // API CALLS
  // =============================

  createTeam(creatorUserId: number, nomeTeam: string) {
    return this.http.post<any>(`${this.apiUrl}/create`, { creatorUserId, nomeTeam });
  }

  inviteMember(teamId: number, emailUtente: string) {
    return this.http.post(
      `${this.apiUrl}/invite`,
      { teamId, emailUtente },
      { responseType: 'text' }
    );
  }

  respondInvite(teamId: number, userId: number, accetta: boolean) {
    return this.http.post(
      `${this.apiUrl}/respond-invite`,
      { teamId, userId, accetta },
      { responseType: 'text' }
    );
  }

  getMyInvitations(userId: number) {
    return this.http.get<any[]>(
      `${this.apiUrl}/invitations/by-user?userId=${userId}`
    );
  }
}