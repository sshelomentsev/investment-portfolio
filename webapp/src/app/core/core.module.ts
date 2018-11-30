import { NgModule, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

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

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    MaterialModule,
    SharedModule
  ],
  exports: [
    NavigationComponent,
    FooterComponent,
    PageNotFoundComponent,
    CurrentStructureComponent
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
    NotificationDialogComponent
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