import { NgModule, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { MomentModule } from 'ngx-moment';

import { NgChartsModule } from 'ng2-charts';

import { throwIfAlreadyLoaded } from './module-import.guard';

import { NavigationComponent } from './navigation/navigation.component';
import { FooterComponent } from './footer/footer.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { MaterialModule } from '../shared/modules/material.module';
import { SharedModule } from '../shared/shared.module';
import { CurrentStructureComponent } from './current-structure/current-structure.component';
import { CoinCardComponent } from './coin-card/coin-card.component';
import { OperationDialogComponent } from './operation-dialog/operation-dialog.component';
import { NotificationDialogComponent } from './notification-dialog/notification-dialog.component';
import { TransactionHistoryComponent } from './transaction-history/transaction-history.component';
import { PieChartComponent } from './pie-chart/pie-chart.component';
import { TrendLineComponent } from './trend-line/trend-line.component';
import { LineChartComponent } from './line-chart/line-chart.component';
import { UserProfileComponent } from './user-profile/user-profile.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    MaterialModule,
    SharedModule,
    MomentModule,
    NgChartsModule
  ],
  exports: [
    NavigationComponent,
    FooterComponent,
    PageNotFoundComponent,
    CurrentStructureComponent,
    MomentModule,
    LineChartComponent
  ],
  declarations: [
    NavigationComponent,
    FooterComponent,
    PageNotFoundComponent,
    LoginComponent,
    SignupComponent,
    CurrentStructureComponent,
    CoinCardComponent,
    OperationDialogComponent,
    NotificationDialogComponent,
    TransactionHistoryComponent,
    PieChartComponent,
    TrendLineComponent,
    LineChartComponent,
    UserProfileComponent
  ],
  entryComponents: [
    OperationDialogComponent,
    NotificationDialogComponent
  ],
  providers: [
  ]
})

export class CoreModule {
  constructor( @Optional() @SkipSelf() parentModule: CoreModule) {
    throwIfAlreadyLoaded(parentModule, 'CoreModule');
  }
}