import { ModuleWithProviders, NgModule } from '@angular/core';
import { MaterialModule } from './modules/material.module';
import { FlexLayoutModule } from '@angular/flex-layout';

import { PasswordValidator } from './validators/password.validator';
import { PeriodChangePipe } from './pipe/period-change.pipe';

@NgModule({
  imports: [
    MaterialModule,
    FlexLayoutModule
  ],
  declarations: [
    PeriodChangePipe
  ],
  exports: [
    MaterialModule,
    FlexLayoutModule,
    PeriodChangePipe
  ]
})

export class SharedModule {
  static forRoot(): ModuleWithProviders<any> {
    return {
      ngModule: SharedModule,
      providers: []
    };
  }
}
