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
import { CurrencyCardComponent } from './currency-card/currency-card.component';
import { SharedModule } from '../shared/modules/shared.module';
import { CurrentStructureComponent } from './current-structure/current-structure.component';

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
    CurrencyCardComponent
  ],
  declarations: [
    NavigationComponent,
    FooterComponent,
    PageNotFoundComponent,
    LoginComponent,
    SignupComponent,
    CurrencyCardComponent,
    CurrentStructureComponent
  ],
  entryComponents: [

  ],
  providers: [
  ]
})

export class CoreModule {
  constructor( @Optional() @SkipSelf() parentModule: CoreModule) {
    throwIfAlreadyLoaded(parentModule, 'CoreModule');
  }
}