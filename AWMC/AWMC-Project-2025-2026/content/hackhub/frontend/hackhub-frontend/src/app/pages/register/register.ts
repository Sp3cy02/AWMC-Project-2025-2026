import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="page">
      <div class="card">
        <h2 class="title">Registrazione</h2>

        <form (ngSubmit)="doRegister()" class="form">
          <label class="field">
            <span>Nome</span>
            <input class="input" [(ngModel)]="nome" name="nome" autocomplete="given-name" required />
          </label>

          <label class="field">
            <span>Cognome</span>
            <input class="input" [(ngModel)]="cognome" name="cognome" autocomplete="family-name" required />
          </label>

          <label class="field">
            <span>Email</span>
            <input class="input" [(ngModel)]="email" name="email" autocomplete="email" required />
          </label>

          <label class="field">
            <span>Password</span>
            <input class="input" [(ngModel)]="password" name="password" type="password" autocomplete="new-password" required />
          </label>

          <button class="btn" type="submit" [disabled]="loading">
            {{ loading ? 'Creo account...' : 'Crea account' }}
          </button>

          <button class="btn ghost" type="button" (click)="goLogin()" [disabled]="loading">
            Torna al login
          </button>

          <div class="alert ok" *ngIf="ok">{{ ok }}</div>
          <div class="alert error" *ngIf="error">{{ error }}</div>
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

    .alert { margin-top: 10px; padding: 10px; border-radius: 10px; border:1px solid; font-size: 13px; }
    .alert.ok { background:#f1fff4; border-color:#bfe7c7; }
    .alert.error { background:#fff3f3; border-color:#f0b7b7; }
  `]
})
export class RegisterComponent {
  nome = '';
  cognome = '';
  email = '';
  password = '';

  error = '';
  ok = '';
  loading = false;

  constructor(
    private auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  private getErrorMessage(e: any): string {
    if (!e) return 'Errore sconosciuto';
    if (typeof e.error === 'string') return e.error;
    if (e.error?.message) return e.error.message;
    if (e.message) return e.message;
    return 'Errore richiesta';
  }

  doRegister() {
    this.error = '';
    this.ok = '';
    this.loading = true;

    this.auth.register(this.nome.trim(), this.cognome.trim(), this.email.trim(), this.password)
      .pipe(finalize(() => {
        this.loading = false;
        // ✅ forza l’aggiornamento UI anche se Angular “non se ne accorge”
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: () => {
          this.ok = 'Registrazione completata. Ora fai login.';
          // aggiorno UI prima di navigare (così non sembra “infinito”)
          this.cdr.detectChanges();
          this.router.navigateByUrl('/login');
        },
        error: (e: any) => {
          this.error = this.getErrorMessage(e);
          this.cdr.detectChanges();
        },
      });
  }

  goLogin() {
    this.router.navigateByUrl('/login');
  }
}