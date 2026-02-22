import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home';
import { LoginComponent } from './pages/login/login';
import { RegisterComponent } from './pages/register/register';
import { CreateHackathonComponent } from './pages/create-hackathon/create-hackathon';
import { HackathonDetailComponent } from './pages/hackathon-detail/hackathon-detail';
import { MyTeamComponent } from './pages/my-team/my-team';
import { InvitiComponent } from './pages/inviti/inviti';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: '', component: HomeComponent, canActivate: [authGuard] },
  { path: 'create-hackathon', component: CreateHackathonComponent, canActivate: [authGuard] },
  { path: 'hackathons/:id', component: HackathonDetailComponent, canActivate: [authGuard] },
  { path: 'my-team', component: MyTeamComponent, canActivate: [authGuard] },
  { path: 'inviti', component: InvitiComponent, canActivate: [authGuard] },

  { path: '**', redirectTo: '' },
];