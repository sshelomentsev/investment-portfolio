import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { map } from 'rxjs/operators';

import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';

@Injectable()
export class AuthService {

  private readonly authStorageKey = 'currentUser';

  private authorized = false;

  constructor(private http: Http, private router: Router) {

  }

  public login(username: string, password: string) {
    console.log('login');
    const url = 'http://localhost:8888/api/users/login';
    const body = {
      username: username,
      password: password
    };
    this.http.post(url, body).subscribe(user => {
      if (user) {
        console.log(user);
        localStorage.setItem(this.authStorageKey, btoa(username + ":" + password));
        this.router.navigate(['/dashboard']);
      }

      return user;
    });
  }

  public logout() {
    localStorage.removeItem(this.authStorageKey);
    this.router.navigate(['/login']);
  }

  public getAuth(): string {
    console.log(localStorage.getItem(this.authStorageKey));
    return 'Basic ' + localStorage.getItem(this.authStorageKey);
  }

  public isAuthorized() {
    return null !== localStorage.getItem(this.authStorageKey) && undefined !== localStorage.getItem(this.authStorageKey);
  }

}