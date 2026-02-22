import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';

import { AuthService } from '../../services/auth.service';
import { TeamService } from '../../services/team';
import { TeamApiService } from '../../services/team-api';

@Component({
  selector: 'app-my-team',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './my-team.html',
  styleUrls: ['./my-team.css']
})
export class MyTeamComponent {
  nomeTeam = '';
  userId = 0;
  teamId: number | null = null;

  loadingCreate = false;

  error = '';
  msg = '';

  // Membri
  members: any[] = [];
  loadingMembers = false;
  membersErr = '';

  // Inviti
  inviteEmail = '';
  loadingInvite = false;
  inviteMsg = '';
  inviteErr = '';

  constructor(
    private auth: AuthService,
    private teams: TeamService,
    private teamApi: TeamApiService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const u = this.auth.getUser();
    this.userId = u?.id ?? 0;
    this.teamId = this.teams.getTeamId();
  }

  goHome() {
    this.router.navigateByUrl('/');
  }

  create() {
    this.error = '';
    this.msg = '';

    const u = this.auth.getUser();
    if (!u?.id) {
      this.error = 'Devi essere loggato.';
      return;
    }

    if (!this.nomeTeam.trim()) {
      this.error = 'Inserisci un nome team.';
      return;
    }

    this.loadingCreate = true;

    this.teams.createTeam(u.id, this.nomeTeam.trim())
      .pipe(finalize(() => { this.loadingCreate = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: (team: any) => {
          const id = team?.id;
          if (id) {
            this.teams.saveTeamId(id);
            this.teamId = id;
            this.members = [];
            this.msg = `Team creato! Team ID = ${id} (salvato).`;
          } else {
            this.msg = 'Team creato, ma non ho ricevuto l’id.';
          }
        },
        error: (err: any) => {
          this.error = (typeof err?.error === 'string' ? err.error : 'Errore creazione team');
        }
      });
  }

  resetTeamLocal() {
    this.teams.clearTeamId();
    this.teamId = null;
    this.members = [];
    this.msg = 'Team selezionato resettato (solo lato frontend).';
    this.error = '';
    this.membersErr = '';
    this.inviteErr = '';
    this.inviteMsg = '';
  }

  loadMembers() {
    this.membersErr = '';
    this.members = [];

    if (!this.teamId) {
      this.membersErr = 'Nessun Team ID salvato.';
      return;
    }

    this.loadingMembers = true;

    this.teamApi.getMembers(this.teamId)
      .pipe(finalize(() => { this.loadingMembers = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: (res: any) => this.members = res ?? [],
        error: (err: any) => {
          this.membersErr = (typeof err?.error === 'string' ? err.error : 'Errore caricamento membri');
        }
      });
  }

  invite() {
    this.inviteMsg = '';
    this.inviteErr = '';

    if (!this.teamId) {
      this.inviteErr = 'Devi avere un Team ID (crea team o seleziona team).';
      return;
    }

    const email = this.inviteEmail.trim();
    if (!email) {
      this.inviteErr = 'Inserisci una email valida.';
      return;
    }

    this.loadingInvite = true;

    this.teams.inviteMember(this.teamId, email)
      .pipe(finalize(() => { this.loadingInvite = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: (res: any) => {
          this.inviteMsg = (typeof res === 'string' ? res : 'Invito inviato!');
          this.inviteEmail = '';
        },
        error: (e: any) => {
          this.inviteErr = (typeof e?.error === 'string' ? e.error : 'Errore invito');
        }
      });
  }

  goInviti() {
    this.router.navigateByUrl('/inviti');
  }
}