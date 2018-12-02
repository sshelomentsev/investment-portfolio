import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { AuthService } from 'src/app/common/auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;

  hasError = false;
  errorMessage;

  constructor(private fb: FormBuilder, private authService: AuthService,
    private router: Router) { }

  ngOnInit() {
    this.createForm();
  }

  private createForm() {
    this.loginForm = this.fb.group({
      email: '',
      password: ''
    });
  }

  public onSubmit(form) {
    this.authService.login(form.value.email, form.value.password).then(res => {
      if (res.success) {
        this.router.navigate(['/performance']);
      } else {
        this.hasError = true;
        this.errorMessage = res.error;
      }
    });
  }


}
