import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';

@Injectable()
export class AuthService {

  constructor(private http: HttpClient) {

  }

  login(username: string, password: string) {
    return this.http.post('http://localhost:8888/api/v1/users/auth', { username, password })
      .pipe(map(user => {
        if (user) {
          console.log(user);
          //user.authdata = window.btoa(username + ':' + password);
          localStorage.setItem('currentUser', JSON.stringify(user));
        }

        return user;
      }));
  }

  logout() {
    localStorage.removeItem('currentUser');
  }

  isAuthorized() {
    return true;
  }

}