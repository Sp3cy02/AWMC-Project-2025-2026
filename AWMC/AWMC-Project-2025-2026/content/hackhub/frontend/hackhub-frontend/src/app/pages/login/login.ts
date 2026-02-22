import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page">
      <div class="card">
        <h2 class="title">Login</h2>

        <form (ngSubmit)="doLogin()" class="form">
          <label class="field">
            <span>Email</span>
            <input class="input" [(ngModel)]="email" name="email" placeholder="Email" required />
          </label>

          <label class="field">
            <span>Password</span>
            <input class="input" [(ngModel)]="password" name="password" placeholder="Password" type="password" required />
          </label>

          <button class="btn" type="submit" [disabled]="loading">
            {{ loading ? 'Accesso...' : 'Entra' }}
          </button>

          <button class="btn ghost" type="button" (click)="goRegister()" [disabled]="loading">
            Registrati
          </button>

          <p class="err" *ngIf="error">{{ error }}</p>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .page { min-height: 100vh; display:flex; align-items:center; justify-content:center; padding: 18px; font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial; background:#fafafa; }
    .card { width: 420px; max-width: 100%; background:#fff; border:1px solid #e6e6e6; border-radius: 14px; padding: 16px; box-shadow: 0 1px 10px rgba(0,0,0,.05); }
    .title { margin: 0 0 12px; font-size: 22px; font-weight: 900; }
    .form { display:grid; gap: 10px; }
    .field { display:grid; gap: 6px; }
    .input { border:1px solid #ddd; border-radius: 10px; padding: 10px 12px; }
    .btn { border:1px solid #222; background:#222; color:#fff; padding: 10px 12px; border-radius: 10px; cursor:pointer; }
    .btn.ghost { background:#fff; color:#222; }
    .btn:disabled { opacity:.6; cursor:not-allowed; }
    .err { color:#b00020; margin: 6px 0 0; }
  `]
})
export class LoginComponent {
  email = '';
  password = '';
  loading = false;
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  private getErrorMessage(e: any): string {
    if (!e) return 'Errore sconosciuto';
    if (typeof e.error === 'string') return e.error;
    if (e.error?.message) return e.error.message;
    if (e.message) return e.message;
    return 'Errore richiesta';
  }

  doLogin() {
    this.error = '';
    this.loading = true;

    this.auth.login(this.email.trim(), this.password).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl('/');
      },
      error: (e: any) => {
        this.loading = false;
        this.error = this.getErrorMessage(e);
      }
    });
  }

  goRegister() {
    this.router.navigateByUrl('/register');
  }
}