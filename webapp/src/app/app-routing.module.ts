import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { LoginComponent } from './core/login/login.component';
import { CanActivateAuthGuard } from './can-activate.authguard';
import { SignupComponent } from './core/signup/signup.component';
import { FundPerformanceComponent } from './fund-performance/fund-performance.component';
import { TransactionHistoryComponent } from './core/transaction-history/transaction-history.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: '/',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'signup',
    component: SignupComponent
  },
  {
    path: 'performance',
    component: FundPerformanceComponent
  },
  {
    path: 'transactions',
    component: TransactionHistoryComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
