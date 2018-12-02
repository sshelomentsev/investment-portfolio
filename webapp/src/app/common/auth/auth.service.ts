import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';

import { User } from '../../model/user.model';

import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable()
export class AuthService {

  private readonly authStorageKey = 'currentUser';

  private user: User = undefined;

  constructor(private http: Http, private router: Router) {
    this.hasAuth().then(isAuth => {
      if (!isAuth) {
        this.router.navigate([this.hasAuthKey() ? '/login' : '/signup']);
      } else {
        this.router.navigate(['/']);
      }
    });
  }

  public login(username: string, password: string): Promise<any> {
    const body = {
      username: username,
      password: password
    };
    return new Promise<any>((resolve) => {
      this.http.post(environment.usersUrl + 'login', body).subscribe(
        user => {
          this.user = <User>user.json();
          localStorage.setItem(this.authStorageKey, btoa(username + ":" + password));
          resolve({success: true});
        },
        err => {
          resolve(err.json());
        },
      );
    });
  }

  public getUserName() {
    if (undefined !== this.user) {
      return this.user.firstName + ' ' + this.user.lastName;
    }
    return undefined;
  }

  public logout() {
    this.http.post(environment.usersUrl + 'logout', {}).subscribe(s => {
      localStorage.removeItem(this.authStorageKey);
      this.user = undefined;
      this.router.navigate(['/login']);
    });
  }

  public getAuth(): string {
    return 'Basic ' + localStorage.getItem(this.authStorageKey);
  }

  public isAuthorized() {
    return undefined !== this.user;
  }

  private hasAuth(): Promise<boolean> {
    if (undefined === this.user) {
      if (!this.hasAuthKey()) {
        return new Promise<boolean>((resolve) => resolve(false));
      }
    }
    return new Promise<boolean>((resolve) => {
      this.getUserInfo().subscribe(
        user => {
          this.user = <User>user.json();
          resolve(true);
        },
        err => {
          resolve(false);
        }
      );
    });
  }

  private hasAuthKey() {
    return null !== localStorage.getItem(this.authStorageKey) && undefined !== localStorage.getItem(this.authStorageKey);
  }

  private getUserInfo(): Observable<any> {
    const headers = new Headers();
    headers.append('Authorization', this.getAuth());
    const options = new RequestOptions({
      headers: headers
    });
    return this.http.get(environment.usersUrl + 'profile', options);
  }

}