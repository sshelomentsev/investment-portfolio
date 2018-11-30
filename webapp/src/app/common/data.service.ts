import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Snapshot } from '../model/snapshot.model';

import { environment } from '../../environments/environment';

@Injectable()
export class DataService {

  constructor(private http: Http) {
  }

  public getSnapshots(period: string): Promise<Snapshot[]> {
    const url = this.getBaseUrl() + 'snapshots/' + period;
    return new Promise<Snapshot[]>((resolve) => {
      this.http.get(url).subscribe(res => {
        let snapshots: Snapshot[] = <Snapshot[]>res.json();
        resolve(snapshots);
      });
    });
  }

  private getBaseUrl(): string {
    return environment.apiUrl;
  }

}