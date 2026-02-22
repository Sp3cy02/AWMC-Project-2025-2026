import { Component, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { HackathonService } from '../../services/hackathon.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-create-hackathon',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="page">
      <header class="topbar">
        <div class="brand" (click)="goHome()">HackHub</div>
        <button class="btn ghost" (click)="goHome()">← Home</button>
      </header>

      <div class="card">
        <h2 class="title">Crea Hackathon</h2>

        <form (submit)="create($event)" class="form">
          <label class="field">
            <span>Nome</span>
            <input class="input" [(ngModel)]="nome" name="nome" placeholder="Nome evento" required />
          </label>

          <label class="field">
            <span>Regolamento</span>
            <textarea class="input" [(ngModel)]="regolamento" name="regolamento" placeholder="Testo regolamento"></textarea>
          </label>

          <label class="field">
            <span>Luogo</span>
            <input class="input" [(ngModel)]="luogo" name="luogo" placeholder="Luogo" />
          </label>

          <div class="row">
            <label class="field">
              <span>Premio (€)</span>
              <input class="input" [(ngModel)]="premio" name="premio"
                     type="number" min="0" step="1" inputmode="numeric"
                     placeholder="Es: 1000" />
              <small class="muted">Anteprima: {{ formatEuro(premio) }}</small>
            </label>

            <label class="field">
              <span>Dimensione max team</span>
              <input class="input" [(ngModel)]="dimensioneMassimaTeam" name="dimensioneMassimaTeam"
                     type="number" min="1" step="1" inputmode="numeric"
                     placeholder="Es: 4" />
            </label>
          </div>

          <div class="row">
            <label class="field">
              <span>Data inizio</span>
              <input class="input" [(ngModel)]="dataInizio" name="dataInizio" type="datetime-local" />
            </label>
            <label class="field">
              <span>Data fine</span>
              <input class="input" [(ngModel)]="dataFine" name="dataFine" type="datetime-local" />
            </label>
          </div>

          <label class="field">
            <span>Scadenza iscrizione</span>
            <input class="input" [(ngModel)]="scadenzaIscrizione" name="scadenzaIscrizione" type="datetime-local" />
          </label>

          <div class="row">
            <label class="field">
              <span>Judge ID</span>
              <input class="input" [(ngModel)]="judgeId" name="judgeId"
                     type="number" min="1" step="1" inputmode="numeric"
                     placeholder="Es: 3" required />
            </label>

            <label class="field">
              <span>Mentor IDs</span>
              <input class="input" [(ngModel)]="mentorIdsText" name="mentorIdsText"
                     placeholder="Es: 2 oppure 2,3" required />
              <small class="muted">Almeno 1 mentore richiesto dal backend.</small>
            </label>
          </div>

          <button class="btn" type="submit" [disabled]="loading">
            {{ loading ? 'Creazione...' : 'Crea' }}
          </button>
        </form>

        @if (error) { <div class="alert error">{{ error }}</div> }
      </div>
    </div>
  `,
  styles: [`
    .page { max-width: 820px; margin: 0 auto; padding: 18px; font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial; }
    .topbar { display:flex; align-items:center; justify-content:space-between; margin-bottom: 14px; }
    .brand { font-weight: 800; font-size: 22px; cursor:pointer; }
    .card { background:#fff; border:1px solid #e6e6e6; border-radius: 12px; padding: 14px; box-shadow: 0 1px 8px rgba(0,0,0,.04); }
    .title { margin: 0 0 10px; font-size: 20px; font-weight: 800; }
    .form { display:grid; gap: 12px; }
    .row { display:grid; grid-template-columns: 1fr 1fr; gap: 12px; }
    @media (max-width: 760px){ .row{ grid-template-columns: 1fr; } }
    .field { display:grid; gap: 6px; }
    .input { border:1px solid #ddd; border-radius: 10px; padding: 9px 10px; }
    .muted { color:#666; font-size: 12px; }
    .btn { border:1px solid #222; background:#222; color:#fff; padding: 9px 12px; border-radius: 10px; cursor:pointer; width: fit-content; }
    .btn.ghost { background:#fff; color:#222; }
    .btn:disabled { opacity:.6; cursor:not-allowed; }
    .alert { margin-top: 12px; padding: 10px; border-radius: 10px; border:1px solid; }
    .alert.error { background:#fff3f3; border-color:#f0b7b7; }
  `]
})
export class CreateHackathonComponent {
  nome = '';
  regolamento = '';
  luogo = '';

  premio: number | null = null;
  dimensioneMassimaTeam: number | null = null;

  dataInizio = '';
  dataFine = '';
  scadenzaIscrizione = '';

  judgeId: number | null = null;
  mentorIdsText = '';

  loading = false;
  error = '';

  constructor(
    private hackathonService: HackathonService,
    private auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  private normalizeDateTime(dt: string): string | null {
    if (!dt) return null;
    return dt.length === 16 ? `${dt}:00` : dt;
  }

  private parseIds(text: string): number[] {
    return text
      .split(',')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => Number(s))
      .filter(n => Number.isFinite(n) && n > 0);
  }

  formatEuro(value: any): string {
    const n = Number(value);
    if (!Number.isFinite(n)) return '—';
    return new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' }).format(n);
  }

  create(event: Event) {
    event.preventDefault();

    this.loading = true;
    this.error = '';

    const user = this.auth.getUser();
    if (!user?.id) {
      this.loading = false;
      this.error = 'Utente non trovato in sessione. Rifai login.';
      this.cdr.detectChanges();
      return;
    }

    const mentorIds = this.parseIds(this.mentorIdsText);
    if (mentorIds.length === 0) {
      this.loading = false;
      this.error = 'Inserisci almeno 1 Mentor ID (es: 2 oppure 2,3).';
      this.cdr.detectChanges();
      return;
    }

    if (!this.judgeId) {
      this.loading = false;
      this.error = 'Judge ID obbligatorio.';
      this.cdr.detectChanges();
      return;
    }

    const dto = {
      organizerId: user.id,
      nome: this.nome,
      regolamento: this.regolamento,
      luogo: this.luogo,
      premio: this.premio !== null ? Number(this.premio) : 0,
      dimensioneMassimaTeam: this.dimensioneMassimaTeam !== null ? Number(this.dimensioneMassimaTeam) : 0,
      judgeId: this.judgeId,
      mentorIds: mentorIds,
      dataInizio: this.normalizeDateTime(this.dataInizio),
      dataFine: this.normalizeDateTime(this.dataFine),
      scadenzaIscrizione: this.normalizeDateTime(this.scadenzaIscrizione),
    };

    this.hackathonService.create(dto)
      .pipe(finalize(() => { this.loading = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: () => this.router.navigateByUrl('/'),
        error: (e) => this.error = (typeof e?.error === 'string' ? e.error : 'Errore creazione hackathon'),
      });
  }

  goHome() {
    this.router.navigateByUrl('/');
  }
}