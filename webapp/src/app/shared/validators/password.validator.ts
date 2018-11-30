import { FormGroup, FormControl, NgForm, FormGroupDirective } from "@angular/forms";
import { ErrorStateMatcher } from '@angular/material/core';


export class ParentErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = !!(form && form.submitted);
    const controlTouched = !!(control && (control.dirty || control.touched));
    const controlInvalid = !!(control && control.invalid);
    const parentInvalid = !!(control && control.parent && control.parent.invalid && (control.parent.dirty || control.parent.touched));

    return isSubmitted || (controlTouched && (controlInvalid || parentInvalid));
  }
}

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