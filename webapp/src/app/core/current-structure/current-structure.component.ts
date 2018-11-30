import { Component, OnInit } from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';

@Component({
  selector: 'app-current-structure',
  templateUrl: './current-structure.component.html',
  styleUrls: ['./current-structure.component.scss']
})
export class CurrentStructureComponent implements OnInit {

  displayedColumns: string[] = ['coin', 'amount', 'usd', 'percent'];
  dataSource = ELEMENT_DATA;

  constructor() { }

  ngOnInit() {
  }

}

export class StakingCoin {
  code: string;
  name: string;
  amountCrypto: number;
  amountFiat: number;
  percent: number;
}

const ELEMENT_DATA: StakingCoin[] = [
  {
    code: 'BTC',
    name: 'Bitcoin',
    amountCrypto: 1.12,
    amountFiat: 4824.12,
    percent: 35
  },
  {
    code: 'ETH',
    name: 'Ethereum',
    amountCrypto: 1.1237676,
    amountFiat: 4824.12,
    percent: 35
  },
  {
    code: 'XRP',
    name: 'Ripple',
    amountCrypto: 1.12,
    amountFiat: 4824.12,
    percent: 11
  }
];

