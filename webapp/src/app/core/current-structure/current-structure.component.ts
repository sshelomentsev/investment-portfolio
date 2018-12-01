import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';

import { DataService } from 'src/app/common/data.service';
import { StakingCoin } from '../../model/staking-coin.model';
import { OperationDialogComponent } from '../operation-dialog/operation-dialog.component';
import { NotificationDialogComponent } from '../notification-dialog/notification-dialog.component';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-current-structure',
  templateUrl: './current-structure.component.html',
  styleUrls: ['./current-structure.component.scss']
})
export class CurrentStructureComponent implements OnInit {

  displayedColumns: string[] = ['coin', 'amount', 'usd', 'percent'];

  private data: StakingCoin[] = [];

  private points: Map<string, number[]> = new Map();

  constructor(private dataService: DataService, public dialog: MatDialog) { }

  ngOnInit() {
    this.updateData();
  }

  getStakingCoins(): StakingCoin[] {
    return this.data;
  }

  getCurrentStructure(flag: number): StakingCoin[] {
    return this.data.filter((coin, i) => (i % 2) == flag);
  }

  buy(event) {
    this.openDialog(event, 'buy');
  }

  sell(event) {
    this.openDialog(event, 'sell');
  }

  openDialog(code: string, operation: string) {
    const dialogRef = this.dialog.open(OperationDialogComponent, {
      width: '400px',
      data: {code: code, operation: operation}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.updateData();
      } else if (typeof result === "boolean" && !result)  {
        this.openNotification('This operation can not be performed');
      }
    });
  }

  getPoints(code: string): number[] {
    if (this.points.has(code)) {
      return this.points.get(code);
    }
    return [1,2,3,4,5,6];
  }

  private updateData(): void {
    this.dataService.getStackingCoins().then(res => {
      this.dataService.getSnapshots('day').then(snapshots => {
        this.data = res.sort((a, b) => b.rate - a.rate);
        console.log(this.data);
        snapshots.forEach(s => {
          this.points.set(s.currency, s.values.map(v => v[1]));
        });
      });
    });
  }

  private openNotification(status: string) {
    this.dialog.open(NotificationDialogComponent, {
      width: '400px',
      data: {status: status}
    });
  }

}
