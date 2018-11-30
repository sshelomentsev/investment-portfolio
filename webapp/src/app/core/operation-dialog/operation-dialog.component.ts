import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import { OperationData } from '../../model/operation-data.model';
import { DataService } from 'src/app/common/data.service';

@Component({
  selector: 'app-operation-dialog',
  templateUrl: './operation-dialog.component.html',
  styleUrls: ['./operation-dialog.component.scss']
})
export class OperationDialogComponent {

  amount: number;
  operationName: string;

  constructor(public dialogRef: MatDialogRef<OperationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: OperationData,
    private dataService: DataService) {
      this.operationName = 'buy' == data.operation ? 'Buy' : 'Sell';
    }

  onNoClick(): void {
    this.dialogRef.close();
  }

  operate(): void {
    this.dataService.operateCoins(this.data.code, 1.0 * this.amount, this.data.operation).then(res => {
      this.dialogRef.close(res.success);
    });
  }

}
