import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';

import { HackathonService } from '../../services/hackathon.service';
import { AuthService } from '../../services/auth.service';
import { RegistrationService } from '../../services/registration';
import { TeamService } from '../../services/team';

@Component({
  selector: 'app-hackathon-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './hackathon-detail.html',
  styleUrls: ['./hackathon-detail.css']
})
export class HackathonDetailComponent {
  hackathon: any = null;

  loading = false;
  error = '';

  loadingAction = false;
  actionMsg = '';
  actionErr = '';

  winningTeamId: number | null = null;

  // ✅ lista team iscritti
  registeredTeams: any[] = [];
  loadingTeams = false;
  teamsErr = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private hackathons: HackathonService,
    private auth: AuthService,
    private regs: RegistrationService,
    private teams: TeamService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.reload();
  }

  private getHackathonId(): number {
    return Number(this.route.snapshot.paramMap.get('id'));
  }

  reload() {
    const id = this.getHackathonId();
    this.loading = true;
    this.error = '';

    this.hackathons.getById(id)
      .pipe(finalize(() => { this.loading = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: (res) => {
          this.hackathon = res;
          this.loadRegisteredTeams(); // ✅ carica anche i team iscritti
        },
        error: (e) => this.error = (typeof e?.error === 'string' ? e.error : 'Errore caricamento hackathon')
      });
  }

  loadRegisteredTeams() {
    if (!this.hackathon?.id) return;

    this.loadingTeams = true;
    this.teamsErr = '';

    this.regs.getTeamsByHackathon(this.hackathon.id)
      .pipe(finalize(() => { this.loadingTeams = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: (res) => this.registeredTeams = res ?? [],
        error: (e) => this.teamsErr = (typeof e?.error === 'string' ? e.error : 'Errore caricamento team iscritti')
      });
  }

  // --- Organizer actions ---
  nextPhase() {
    this.runAction(() => this.hackathons.nextPhase(this.hackathon.id, this.getUserId()));
  }

  payPrize() {
    this.runAction(() => this.hackathons.payPrize(this.hackathon.id, this.getUserId()));
  }

  proclaimWinner() {
    if (!this.winningTeamId || this.winningTeamId <= 0) {
      this.actionErr = 'Inserisci un Winning Team ID valido.';
      this.actionMsg = '';
      return;
    }

    this.runAction(() => this.hackathons.proclaimWinner({
      hackathonId: this.hackathon.id,
      organizerId: this.getUserId(),
      winningTeamId: this.winningTeamId
    }));
  }

  // --- Participant action ---
  joinHackathon() {
    this.actionErr = '';
    this.actionMsg = '';

    const teamId = this.teams.getTeamId();
    const userId = this.getUserId();

    if (!teamId) {
      this.actionErr = 'Nessun Team ID salvato. Vai su My Team.';
      return;
    }

    this.runAction(() => this.regs.joinHackathon(this.hackathon.id, teamId, userId));
  }

  private runAction(call: () => any) {
    this.loadingAction = true;
    this.actionErr = '';
    this.actionMsg = '';

    call()
      .pipe(finalize(() => { this.loadingAction = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: (res: any) => {
          this.actionMsg = (typeof res === 'string' ? res : 'Operazione completata');
          this.reload();
        },
        error: (e: any) => {
          this.actionErr = (typeof e?.error === 'string' ? e.error : 'Operazione fallita');
        }
      });
  }

  private getUserId(): number {
    const u = this.auth.getUser();
    return u?.id ?? 0;
  }

  goHome() {
    this.router.navigateByUrl('/');
  }

  formatEuro(value: any): string {
    const n = Number(value);
    if (!Number.isFinite(n)) return '—';
    return new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' }).format(n);
  }

  formatDateTime(value: any): string {
    if (!value) return '—';
    const d = new Date(value);
    if (isNaN(d.getTime())) return String(value);
    return new Intl.DateTimeFormat('it-IT', { dateStyle: 'medium', timeStyle: 'short' }).format(d);
  }

  teamLabel(t: any): string {
    return t?.nomeTeam ?? t?.nome ?? '(senza nome)';
  }
}