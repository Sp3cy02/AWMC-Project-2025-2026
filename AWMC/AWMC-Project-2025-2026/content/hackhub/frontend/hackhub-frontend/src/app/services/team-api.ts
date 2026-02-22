import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class TeamApiService {
  private apiUrl = 'http://localhost:8080/api/teams';

  constructor(private http: HttpClient) {}

  getMembers(teamId: number) {
    return this.http.get<any[]>(`${this.apiUrl}/${teamId}/members`);
  }
}