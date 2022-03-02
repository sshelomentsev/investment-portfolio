import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { environment } from '../../environments/environment';
import { Router } from '@angular/router';
import { Observable, of } from 'rxjs';

@Injectable()
export class SignupService {

  private authorized = false;

  constructor(private http: HttpClient, private router: Router) {
  }

  signup(userData: any): Observable<any> {
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'application/json');
    const options = {
      headers: headers
    };
    return this.http.post(environment.usersUrl + 'signup', userData, options);
  }

}