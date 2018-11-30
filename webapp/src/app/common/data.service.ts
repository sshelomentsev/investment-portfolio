import { Injectable } from '@angular/core';
import { Http, Headers, Response, RequestOptions} from '@angular/http';

import { Snapshot } from '../model/snapshot.model';

import { environment } from '../../environments/environment';

import { Observable } from 'rxjs';
import { AuthService } from './auth/auth.service';

@Injectable()
export class DataService {

  constructor(private http: Http, private authService: AuthService) {
  }

  public getSnapshots(period: string): Promise<Snapshot[]> {
    const url = this.getBaseUrl() + 'snapshots/' + period;
    return new Promise<Snapshot[]>((resolve) => {
      this.get(url).subscribe(res => {
        resolve(<Snapshot[]>res.json());
      });
    });
  }

  private getBaseUrl(): string {
    return environment.apiUrl;
  }

  private post(url: string, body: any): Observable<Response> {
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append('Authorization', this.authService.getAuth());
    const options = new RequestOptions({
      headers: headers
    });
    return this.http.post(url, body, options);
  }

  private get(url: string): Observable<Response> {
    const headers = new Headers();
    headers.append('Authorization', this.authService.getAuth());
    const options = new RequestOptions({
      headers: headers
    });
    return this.http.get(url, options);
  }

}