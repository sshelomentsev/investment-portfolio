import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';

import { User } from '../../model/user.model';

import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';

@Injectable()
export class AuthService {

  private readonly authStorageKey = 'currentUser';

  private user: User;

  constructor(private http: Http, private router: Router) {
    if (this.isAuthorized()) {
      console.log('aith');
      console.log(this.getAuth());
      this.getUserInfo();
    } else {
      this.router.navigate(['/signup']);
    }
  }

  public login(username: string, password: string) {
    const body = {
      username: username,
      password: password
    };
    this.http.post(environment.usersUrl + 'login', body).subscribe(user => {
      if (user) {
        this.user = <User>user.json();
        localStorage.setItem(this.authStorageKey, btoa(username + ":" + password));
        this.router.navigate(['/performance']);
      }

      return user;
    });
  }

  public getUserName() {
    return this.user.firstName + ' ' + this.user.lastName;
  }

  public logout() {
    localStorage.removeItem(this.authStorageKey);
    this.user = undefined;
    this.router.navigate(['/login']);
  }

  public getAuth(): string {
    return 'Basic ' + localStorage.getItem(this.authStorageKey);
  }

  public isAuthorized() {
    return null !== localStorage.getItem(this.authStorageKey) && undefined !== localStorage.getItem(this.authStorageKey);
  }

  private getUserInfo() {
    const headers = new Headers();
    headers.append('Authorization', this.getAuth());
    const options = new RequestOptions({
      headers: headers
    });
    this.http.get(environment.usersUrl + 'profile', options).subscribe(user => {
      if (user) {
        this.user = <User>user.json();
      } else {
        localStorage.removeItem(this.authStorageKey);
        this.router.navigate(['/login']);
      }
    });
  }

}