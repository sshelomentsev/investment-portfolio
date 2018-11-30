import { Component, OnInit } from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import { DataService } from 'src/app/common/data.service';
import { StakingCoin } from '../../model/staking-coin.model';

@Component({
  selector: 'app-current-structure',
  templateUrl: './current-structure.component.html',
  styleUrls: ['./current-structure.component.scss']
})
export class CurrentStructureComponent implements OnInit {

  displayedColumns: string[] = ['coin', 'amount', 'usd', 'percent'];

  private data: StakingCoin[] = [];

  constructor(private dataService: DataService) { }

  ngOnInit() {
    this.dataService.getStackingCoins().then(res => {
      console.log(res);
      this.data = res.sort((a, b) => b.rate - a.rate);
    });
  }

  getStakingCoins(): StakingCoin[] {
    return this.data.filter(coin => coin.amountCrypto > 0);;
  }

  getCurrentStructure(flag: number): StakingCoin[] {
    return this.data.filter((coin, i) => (i % 2) == flag);
  }

}
