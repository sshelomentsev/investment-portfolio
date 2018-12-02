import { FormGroup, FormControl, NgForm, FormGroupDirective } from "@angular/forms";
import { ErrorStateMatcher } from '@angular/material/core';

export class PasswordValidator {

  static areEqual(formGroup: FormGroup) {
    let value = undefined;
    let isValid = true;
    for (let k in formGroup.controls) {
      if (formGroup.controls.hasOwnProperty(k)) {
        const control: FormControl = <FormControl>formGroup.controls[k];

        if (undefined == value) {
          value = control.value;
        } else {
          if (value != control.value) {
            isValid = false;
            break;
          }
        }
      }
    }

    return isValid ? null : {
      areEqual: true
    }
  }

}