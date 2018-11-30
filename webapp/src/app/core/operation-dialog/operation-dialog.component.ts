import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import { OperationData } from '../../model/operation-data.model';

@Component({
  selector: 'app-operation-dialog',
  templateUrl: './operation-dialog.component.html',
  styleUrls: ['./operation-dialog.component.scss']
})
export class OperationDialogComponent implements OnInit {

  amount: number;
  operationName: string;

  constructor(public dialogRef: MatDialogRef<OperationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: OperationData) {
      this.operationName = 'buy' == data.operation ? 'Buy' : 'Sell';
    }

  ngOnInit() {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
