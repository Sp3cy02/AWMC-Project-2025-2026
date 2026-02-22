import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { AuthService } from '../../services/auth.service';
import { TeamService } from '../../services/team';

@Component({
  selector: 'app-inviti',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './inviti.html',
  styleUrls: ['./inviti.css']
})
export class InvitiComponent {
  loading = false;
  err = '';
  msg = '';
  invites: any[] = [];

  constructor(
    private auth: AuthService,
    private teams: TeamService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadInvites();
  }

  goHome() {
    this.router.navigateByUrl('/');
  }

  reload() {
    this.loadInvites();
  }

  private explainError(e: any): string {
    const status = e?.status;
    const body = e?.error;

    if (typeof body === 'string' && body.trim()) return `(${status}) ${body}`;
    if (body?.message) return `(${status}) ${body.message}`;
    if (status) return `(${status}) Errore richiesta`;
    return 'Errore richiesta';
  }

  private loadInvites() {
    this.err = '';
    this.msg = '';

    const userId = this.auth.getUser()?.id ?? 0;
    if (!userId) {
      this.err = 'Devi essere loggato.';
      return;
    }

    this.loading = true;

    this.teams.getMyInvitations(userId)
      .pipe(finalize(() => { this.loading = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: (res: any) => this.invites = res ?? [],
        error: (e: any) => {
          this.invites = [];
          this.err = this.explainError(e);
        }
      });
  }

  respond(inv: any, accept: boolean) {
    this.err = '';
    this.msg = '';

    const userId = this.auth.getUser()?.id ?? 0;
    if (!userId) {
      this.err = 'Devi essere loggato.';
      return;
    }

    const teamId = Number(inv?.teamId);
    if (!Number.isFinite(teamId) || teamId <= 0) {
      this.err = 'Invito non valido (teamId mancante).';
      return;
    }

    this.loading = true;

    this.teams.respondInvite(teamId, userId, accept)
      .pipe(finalize(() => { this.loading = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: (res: any) => {
          // ✅ se accetti, salva quel team per questo utente
          if (accept) {
            this.teams.saveTeamId(teamId);
          }

          this.msg = (typeof res === 'string' ? res : 'Operazione completata');
          this.loadInvites();
        },
        error: (e: any) => this.err = this.explainError(e)
      });
  }

  label(inv: any): string {
    const name = inv?.teamNome ?? 'Team';
    const id = inv?.teamId ?? '?';
    return `${name} (ID: ${id})`;
  }
}