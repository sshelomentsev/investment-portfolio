import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormControl, FormBuilder, FormGroup, Validators } from '@angular/forms';

import { PasswordValidator, ParentErrorStateMatcher } from '../../shared/validators/password.validator';
import { SignupService } from 'src/app/common/signup.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class SignupComponent implements OnInit {

  signupForm: FormGroup;

  matchingPasswordsGroup: FormGroup;

  parentErrorStateMatcher = new ParentErrorStateMatcher();

  accountValidationMessages = {
    'firstName': [
      { type: 'required', message: 'First name is required' }
    ],
    'secondName': [
      { type: 'required', message: 'Second name is required' }
    ],
    'email': [
      { type: 'required', message: 'Email is required' },
      { type: 'pattern', message: 'Enter a valid email' }
    ],
    'createPassword': [
      { type: 'required', message: 'Password is required' },
      { type: 'minlength', message: 'Password must be at least 5 characters long' },
      { type: 'pattern', message: 'Your password must contain at least one uppercase, one lowercase, and one number' }
    ],
    'confirmPassword': [
      { type: 'required', message: 'Confirm password is required' },
      { type: 'areEqual', message: 'Password mismatch' }
    ]
  }

  constructor(private fb: FormBuilder, private signupService: SignupService) { }

  ngOnInit() {
    this.createForm();
  }

  private createForm() {
    this.matchingPasswordsGroup = new FormGroup({
      createPassword: new FormControl('', Validators.compose([
        Validators.minLength(5),
        Validators.required,
        Validators.pattern('^[a-zA-Z0-9]+$')
      ])),
      confirmPassword: new FormControl('', Validators.required)
    }, (formGroup: FormGroup) => {
      return PasswordValidator.areEqual(formGroup);
    });

    this.signupForm = this.fb.group({
      firstName: '',
      lastName: '',
      email: new FormControl('', Validators.compose([
        Validators.required,
        Validators.pattern('^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$')
      ])),
      phoneNumber: '',
      matchingPasswords: this.matchingPasswordsGroup
    });
  }

  onSubmit(form) {
    console.log(form);
    const value = form.value;
    value.password = value.matchingPasswords.createPassword;
    delete value.matchingPasswords;
    this.signupService.signup(form.value);
  }
}
