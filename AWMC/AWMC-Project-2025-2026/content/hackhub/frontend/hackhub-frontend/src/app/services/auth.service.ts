import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

export type UserResponse = {
  id: number;
  nome: string;
  cognome: string;
  email: string;
};

export type AuthResponse = {
  token: string;
  user: UserResponse;
};

const TOKEN_KEY = 'hackhub_token';
const USER_KEY = 'hackhub_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  // -------------------------
  // HTTP
  // -------------------------

  login(email: string, password: string) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { email, password }).pipe(
      tap((res) => {
        this.setSession(res.token, res.user);
      })
    );
  }

  register(nome: string, cognome: string, email: string, password: string) {
    return this.http.post<UserResponse>(`${this.apiUrl}/register`, { nome, cognome, email, password });
  }

  // -------------------------
  // SESSION STORAGE
  // -------------------------

  private setSession(token: string, user: UserResponse) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  getUser(): UserResponse | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as UserResponse;
    } catch {
      return null;
    }
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout() {
    //  logout deve togliere sessione, NON i teamId “per utente”
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }
}