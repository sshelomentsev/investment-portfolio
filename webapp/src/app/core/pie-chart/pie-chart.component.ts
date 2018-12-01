import { Component, OnInit, AfterViewInit, Input, ElementRef, SimpleChanges, OnChanges  } from '@angular/core';

import { Chart } from "chart.js";
import { StakingCoin } from 'src/app/model/staking-coin.model';

import { ChartColorUtil } from '../chart-color-util';
import { DataService } from 'src/app/common/data.service';

@Component({
  selector: 'pie-chart',
  templateUrl: './pie-chart.component.html',
  styleUrls: ['./pie-chart.component.scss']
})
export class PieChartComponent implements OnInit, AfterViewInit, OnChanges {

  private readonly borderWidth: number = 4;
  private readonly canvasHeight: number = 400;
  private readonly canvasWidth: number = 400;

  private chart: any = undefined;
  public hasData: boolean = true;

  @Input()
  coins: StakingCoin[] = [];

  constructor(private el: ElementRef, private dataService: DataService) { }

  ngOnInit() {
    this.updateChart(this.coins);
  }

  ngAfterViewInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['coins']) {
      const ch = changes['coins'];
      if (!this.isEqual(ch.previousValue, ch.currentValue)) {
        this.updateChart(ch.currentValue);
      }
    }
  }

  private isEqual(arr1: StakingCoin[], arr2: StakingCoin[]) {
    if (!arr1 || !arr2) {
      return false;
    }
    if (arr1.length !== arr2.length) {
      return false;
    }
    return arr1.filter((v, i) => v.amountCrypto === arr2[i].amountCrypto
      && v.amountFiat === arr2[i].amountFiat).length == arr1.length;
  }

  private createAndRenderChart(config: any): void {
    const canvas = <HTMLCanvasElement>this.el.nativeElement.querySelector('canvas');
    canvas.height = this.canvasHeight;
    canvas.width = this.canvasWidth;
    const context = canvas.getContext('2d');
    this.chart = new Chart(context, config);
    this.chart.update();
    this.chart.render();
  }

  private updateChart(data: StakingCoin[]): void {
    if (undefined == this.chart) {
      this.createChartConfig(data).then(config => {
        this.createAndRenderChart(config);
      });
    } else {
      const dataset = this.chart.data.datasets[0];
      dataset.data = data.map(item => item.amountFiat);

      this.chart.data.labels = data.map(item => item.currencyCode);
      this.chart.data.datasets[0].backgroundColor = data.map(item => ChartColorUtil.getColorCode(item.currencyCode));

      this.chart.update();
      this.chart.render();
    }
  }

  private createChartConfig(data: StakingCoin[]): Promise<any> {
    return new Promise<any>((resolve) => {
      const config = {
        type: 'doughnut',
        data: this.prepareDataset(data, this.borderWidth),
        options: {
          cutoutPercentage: 80,
          maintainAspectRatio: true,
          responsive: true,
          title: {
            display: false
          },
          animation: {
            animateRotate: false,
            animateScale: false,
            duration: 100
          },
          onHover: (event, chartSegments) => this.updateTextOnClick(event, chartSegments),
          onClick: (event, chartSegments) => this.updateTextOnClick(event, chartSegments),
          legend: {
            display: false
          },
          tooltips: {
            enabled: false
          }
        }
      };

      resolve(config);
    });
  }

  private updateTextOnClick(event, chartSegments) {
    if (chartSegments.length > 0) {
      const chart = chartSegments[0]._chart;
      const index = chartSegments[0]._index;
      const data = this.coins[index];

      const title = data.currencyName + ' (' + data.currencyCode + ')';
      const rate = '$' + this.coins[index].rate.toFixed(2);
      const marketCap = '$' + this.coins[index].marketCap;

      const ctx = chart.ctx;

      this.writeLine(ctx, title, 24, 200, 136);
      this.writeLine(ctx, 'Price', 12, 200, 190);
      this.writeLine(ctx, rate, 20, 200, 210);
      this.writeLine(ctx, 'Market cap', 12, 200, 250);
      this.writeLine(ctx, marketCap, 20, 200, 270);
    }
  }

  private writeLine(ctx, content, size, x, y) {
    ctx.fillStyle = 'black';
    ctx.textBaseline = 'middle';
    ctx.textAlign = 'center';
    ctx.font = '' + size + 'px Lato';

    ctx.fillText(content, x, y);
  }

  private prepareDataset(sourceData: StakingCoin[], borderWidth: number): any {
    const data = {
      datasets: [{
        data: sourceData.map(item => item.amountFiat),
        backgroundColor: sourceData.map(item => {
          const code = ChartColorUtil.getColorCode(item.currencyCode);
          return code;
        }),
        borderColor: new Array(sourceData.length).fill('#808080'),
        borderWidth: new Array(sourceData.length).fill(0),
        hoverBorderColor: new Array(sourceData.length).fill('#005064'),
        hoverBorderWidth: borderWidth
      }],
      labels: sourceData.map(item => item.currencyCode)
    };
    return data;
  }

}
