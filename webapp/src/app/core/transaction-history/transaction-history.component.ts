import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from 'src/app/common/data.service';
import { MatPaginator, MatSort, MatTableDataSource } from '@angular/material';
import { Transaction } from 'src/app/model/transaction.model';

@Component({
  selector: 'app-transaction-history',
  templateUrl: './transaction-history.component.html',
  styleUrls: ['./transaction-history.component.scss']
})
export class TransactionHistoryComponent implements OnInit {

  displayedColumns: string[] = ['amount', 'currency', 'operation', 'time'];
  transactions: MatTableDataSource<Transaction>;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  constructor(private dataService: DataService) { }

  ngOnInit() {
    this.dataService.getTransactions().then(res => {
      this.transactions = new MatTableDataSource(res);
      this.transactions.paginator = this.paginator;
      this.transactions.sort = this.sort;
    });
  }

  applyFilter(filterValue: string) {
    this.transactions.filter = filterValue.trim().toLowerCase();

    if (this.transactions.paginator) {
      this.transactions.paginator.firstPage();
    }
  }

}
