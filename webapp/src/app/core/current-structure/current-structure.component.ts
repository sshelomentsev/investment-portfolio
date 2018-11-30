import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';

import { DataService } from 'src/app/common/data.service';
import { StakingCoin } from '../../model/staking-coin.model';
import { OperationDialogComponent } from '../operation-dialog/operation-dialog.component';

@Component({
  selector: 'app-current-structure',
  templateUrl: './current-structure.component.html',
  styleUrls: ['./current-structure.component.scss']
})
export class CurrentStructureComponent implements OnInit {

  displayedColumns: string[] = ['coin', 'amount', 'usd', 'percent'];

  private data: StakingCoin[] = [];

  constructor(private dataService: DataService, public dialog: MatDialog) { }

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

  buy(event) {
    console.log(event);
    this.openDialog(event, 'buy');
  }

  sell(event) {
    console.log('sell');
    console.log(event);
    this.openDialog(event, 'sell');
  }

  openDialog(code: string, operation: string) {
    const dialogRef = this.dialog.open(OperationDialogComponent, {
      width: '400px',
      data: {code: code, operation: operation}
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }

}
