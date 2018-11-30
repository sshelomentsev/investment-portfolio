import { Component, OnInit } from '@angular/core';
import { DataService } from 'src/app/common/data.service';

@Component({
  selector: 'app-currency-card',
  templateUrl: './currency-card.component.html',
  styleUrls: ['./currency-card.component.scss']
})
export class CurrencyCardComponent implements OnInit {

  constructor(private dataService: DataService) { }

  ngOnInit() {
    console.log('on init');
    this.dataService.getSnapshots('week').then(res => {
      console.log(res);
    });
  }

}
