import { Injectable } from '@angular/core';
import { Http, Headers, Response, RequestOptions} from '@angular/http';

import { Snapshot } from '../model/snapshot.model';
import { Transaction } from '../model/transaction.model';

import { environment } from '../../environments/environment';

import { Observable } from 'rxjs';
import { AuthService } from './auth/auth.service';
import { StakingCoin } from '../model/staking-coin.model';

@Injectable()
export class DataService {

  constructor(private http: Http, private authService: AuthService) {
  }

  public getSnapshots(period: string): Promise<Snapshot[]> {
    const url = environment.apiUrl + 'snapshots/' + period;
    return new Promise<Snapshot[]>((resolve) => {
      this.get(url).subscribe(res => {
        resolve(<Snapshot[]>res.json());
      });
    });
  }

  public getStackingCoins(): Promise<StakingCoin[]> {
    const url = environment.apiUrl + 'portfolio';
    return new Promise<StakingCoin[]>((resolve) => {
      this.get(url).subscribe(res => {
        const coins: StakingCoin[] = <StakingCoin[]>res.json();
        const sum = coins.map(coin => coin.amountFiat).reduce((a, b) => a += b);
        coins.forEach(coin => coin.percent = (coin.amountFiat / sum) * 100);
        resolve(coins);
      });
    });
  }

  public buyCoins(currency: string, amount: number): Promise<string> {
    return this.operateCoins(currency, amount, 'buy');
  }

  public sellCoins(currency: string, amount: number): Promise<string> {
    return this.operateCoins(currency, amount, 'sell');
  }

  public operateCoins(currency: string, amount: number, operation: string): Promise<any> {
    const url = environment.apiUrl + 'coins/' + operation;
    const info = {
      currency: currency,
      amount: amount
    };
    return new Promise<any>((resolve) => {
      this.post(url, info).subscribe(res => {
        resolve(res.json());
      })
    });
  }

  public getTransactions(): Promise<Transaction[]> {
    const url = environment.apiUrl + 'coins/transactions';
    return new Promise<any>((resolve) => {
      this.get(url).subscribe(res => resolve(res.json()));
    });
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