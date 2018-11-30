import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { StakingCoin } from 'src/app/model/staking-coin.model';

@Component({
  selector: 'coin-card',
  templateUrl: './coin-card.component.html',
  styleUrls: ['./coin-card.component.scss']
})
export class CoinCardComponent implements OnInit {

  @Input()
  coin: StakingCoin;

  @Output()
  onBuy: EventEmitter<string> = new EventEmitter<string>();

  @Output()
  onSell: EventEmitter<string> = new EventEmitter<string>();

  constructor() { }

  ngOnInit() {
  }

  buy() {
    console.log('buy ' + this.coin.currencyCode);
    this.onBuy.emit(this.coin.currencyCode);
  }

  sell(value) {
    console.log('sell ' + this.coin.currencyCode);
    this.onSell.emit(this.coin.currencyCode);
  }

}
