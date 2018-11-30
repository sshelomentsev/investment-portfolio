import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { map } from 'rxjs/operators';

import { environment } from '../../environments/environment';

@Injectable()
export class SignupService {

  private authorized = false;

  constructor(private http: Http) {
  }

  signup(userData: any) {
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');
    const options = new RequestOptions({
      headers: headers
    });
    this.http.post('http://localhost:8888/api/users/signup', userData, options).subscribe(res => {
      console.log(res);
    });
  }

}