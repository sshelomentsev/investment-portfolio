import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { map } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';

@Injectable()
export class SignupService {

  private authorized = false;

  constructor(private http: Http, private router: Router) {
  }

  signup(userData: any): Observable<any> {
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');
    const options = new RequestOptions({
      headers: headers
    });
    return this.http.post(environment.usersUrl + 'signup', userData, options);
  }

}