import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { map } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { Router } from '@angular/router';

@Injectable()
export class SignupService {

  private authorized = false;

  constructor(private http: Http, private router: Router) {
  }

  signup(userData: any) {
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');
    const options = new RequestOptions({
      headers: headers
    });
    this.http.post(environment.usersUrl + 'signup', userData, options).subscribe(res => {
      if (res) {
        this.router.navigate(['/performance']);
      }
    });
  }

}