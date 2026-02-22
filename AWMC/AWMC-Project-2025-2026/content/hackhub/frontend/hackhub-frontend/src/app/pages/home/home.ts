import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { HackathonService } from '../../services/hackathon.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page">
      <header class="topbar">
        <div class="brand" (click)="goHome()">HackHub</div>

        <div class="user" *ngIf="user">
          <div class="user-name">{{ user.nome }} {{ user.cognome }}</div>
          <div class="user-meta">ID: {{ user.id }} • {{ user.email }}</div>
        </div>

        <div class="actions">
          <button class="btn ghost" (click)="goMyTeam()">My Team</button>
          <button class="btn ghost" (click)="goInviti()">Inviti</button>
          <button class="btn ghost" (click)="goCreate()">+ Crea Hackathon</button>
          <button class="btn" (click)="logout()">Logout</button>
        </div>
      </header>

      <div class="row">
        <button class="btn ghost" (click)="load()" [disabled]="loading">
          {{ loading ? 'Carico...' : 'Ricarica elenco' }}
        </button>
        <span class="muted" *ngIf="loading">Sto chiamando GET /api/hackathons…</span>
      </div>

      <div class="alert error" *ngIf="error">{{ error }}</div>

      <div class="grid" *ngIf="!loading && hackathons.length > 0">
        <div class="card" *ngFor="let h of hackathons; trackBy: trackById">
          <div class="card-top">
            <div>
              <div class="title">{{ h.nome }}</div>
              <div class="meta">
                <span class="pill">{{ h.stato }}</span>
                <span class="muted">ID: {{ h.id }}</span>
              </div>
            </div>
            <button class="btn ghost" (click)="openDetail(h.id)">Dettagli →</button>
          </div>

          <div class="kv">
            <div><span class="k">Luogo</span><span class="v">{{ h.luogo || '—' }}</span></div>
            <div><span class="k">Premio</span><span class="v">{{ formatEuro(h.premio) }}</span></div>
            <div><span class="k">Max team</span><span class="v">{{ h.dimensioneMassimaTeam ?? '—' }}</span></div>
          </div>
        </div>
      </div>

      <div class="card empty" *ngIf="!loading && hackathons.length === 0 && !error">
        Nessun hackathon trovato.
      </div>
    </div>
  `,
  styles: [`
    .page { max-width: 1100px; margin: 0 auto; padding: 18px; font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial; background:#fafafa; min-height: 100vh; }
    .topbar { display:flex; align-items:center; gap: 14px; justify-content: space-between; margin-bottom: 14px; flex-wrap: wrap; }
    .brand { font-weight: 900; font-size: 22px; cursor:pointer; }
    .user { padding: 8px 10px; border: 1px solid #e6e6e6; border-radius: 12px; background:#fff; min-width: 240px; }
    .user-name { font-weight: 900; }
    .user-meta { color:#666; font-size: 12px; }

    .actions { display:flex; gap: 10px; flex-wrap: wrap; }
    .row { display:flex; align-items:center; gap: 10px; margin: 10px 0 14px; flex-wrap: wrap; }

    .btn { border:1px solid #222; background:#222; color:#fff; padding: 10px 12px; border-radius: 10px; cursor:pointer; }
    .btn.ghost { background:#fff; color:#222; }
    .btn:disabled { opacity:.6; cursor:not-allowed; }

    .muted { color:#666; font-size: 13px; }

    .grid { display:grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
    @media (max-width: 1000px){ .grid{ grid-template-columns: repeat(2, 1fr); } }
    @media (max-width: 700px){ .grid{ grid-template-columns: 1fr; } }

    .card { background:#fff; border:1px solid #e6e6e6; border-radius: 14px; padding: 14px; box-shadow: 0 1px 10px rgba(0,0,0,.04); }
    .card.empty { text-align:center; color:#666; }

    .card-top { display:flex; justify-content: space-between; gap: 10px; align-items:flex-start; margin-bottom: 10px; }
    .title { font-size: 16px; font-weight: 900; margin-bottom: 6px; }
    .meta { display:flex; gap: 10px; align-items:center; }
    .pill { padding: 4px 10px; border-radius: 999px; background:#f2f2f2; font-size: 12px; }

    .kv { display:grid; gap: 8px; margin-top: 6px; }
    .kv > div { display:flex; justify-content: space-between; gap: 16px; }
    .k { color:#666; }
    .v { font-weight: 800; text-align:right; }

    .alert { margin-top: 10px; padding: 10px; border-radius: 10px; border:1px solid; }
    .alert.error { background:#fff3f3; border-color:#f0b7b7; }
  `]
})
export class HomeComponent {
  hackathons: any[] = [];
  loading = false;
  error = '';

  constructor(
    private hackathonService: HackathonService,
    private auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  get user() {
    return this.auth.getUser();
  }

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading = true;
    this.error = '';

    this.hackathonService.getAll()
      .pipe(finalize(() => { this.loading = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: (res: any) => this.hackathons = res ?? [],
        error: (err: any) => {
          this.hackathons = [];
          this.error = (typeof err?.error === 'string' ? err.error : 'Errore caricamento hackathon');
        }
      });
  }

  trackById(_: number, item: any) {
    return item?.id;
  }

  openDetail(id: number) {
    this.router.navigateByUrl(`/hackathons/${id}`);
  }

  logout() {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }

  goCreate() {
    this.router.navigateByUrl('/create-hackathon');
  }

  goMyTeam() {
    this.router.navigateByUrl('/my-team');
  }

  goInviti() {
    this.router.navigateByUrl('/inviti');
  }

  goHome() {
    this.router.navigateByUrl('/');
  }

  formatEuro(value: any): string {
    const n = Number(value);
    if (!Number.isFinite(n)) return '—';
    return new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' }).format(n);
  }
}