import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { User } from '../../model/user.model';

import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';

@Injectable()
export class AuthService {

  private readonly authStorageKey = 'currentUser';

  private user: User = undefined;

  constructor(private httpClient: HttpClient, private router: Router, private cookieService: CookieService) {
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
      this.httpClient.post(environment.usersUrl + 'login', body).subscribe(
        user => {
          this.user = <User>user;
          this.cookieService.set('token', this.user.token);
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
    this.httpClient.post(environment.usersUrl + 'logout', {}).subscribe(s => {
      this.user = undefined;
      this.cookieService.delete('token');
      this.router.navigate(['/login']);
    });
  }

  public getAuth(): string {
    return 'bearer ' + this.cookieService.get('token');
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
          this.cookieService.set('token', this.user.token);
          resolve(true);
        },
        err => {
          resolve(false);
        }
      );
    });
  }

  private hasAuthKey() {
    console.log(this.cookieService.get('token'));
    return null !== this.cookieService.get('token') && undefined !== this.cookieService.get('token') && '' !== this.cookieService.get('token');
  }

  private getUserInfo(): Observable<any> {
    const headers = new HttpHeaders();
    headers.append('Authorization', this.getAuth());
    const options = {
      headers: headers
    };
    return this.httpClient.get(environment.usersUrl + 'profile', options);
  }

}