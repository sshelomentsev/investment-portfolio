import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { AuthService } from './common/auth/auth.service';
import { DashboardComponent } from './dashboard/dashboard.component';

import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';

import { CoreModule } from './core/core.module';
import { FundPerformanceComponent } from './fund-performance/fund-performance.component';
import { SharedModule } from './shared/shared.module';
import { CanActivateAuthGuard } from './can-activate.authguard';
import { DataService } from './common/data.service';
import { SignupService } from './common/signup.service';
import { CookieService } from 'ngx-cookie-service';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    FundPerformanceComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    SharedModule,
    CoreModule,
    HttpModule,
    HttpClientModule
  ],
  providers: [
    AuthService,
    CanActivateAuthGuard,
    DataService,
    SignupService,
    CookieService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
