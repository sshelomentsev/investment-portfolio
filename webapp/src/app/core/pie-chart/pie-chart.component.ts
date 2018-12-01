import { Component, OnInit, AfterViewInit, Input, ElementRef } from '@angular/core';

import { Chart } from "chart.js";
import { StakingCoin } from 'src/app/model/staking-coin.model';

import { PieFilterDataUtils } from './pie-filter-data-utils';
import { DataService } from 'src/app/common/data.service';

@Component({
  selector: 'pie-chart',
  templateUrl: './pie-chart.component.html',
  styleUrls: ['./pie-chart.component.scss']
})
export class PieChartComponent implements OnInit, AfterViewInit {

  private readonly borderWidth: number = 4;
  private readonly canvasHeight: number = 400;
  private readonly canvasWidth: number = 400;

  private chart: any = undefined;
  public hasData: boolean = true;

  constructor(private el: ElementRef, private dataService: DataService) { }

  ngOnInit() {
  }

  ngAfterViewInit() {
    this.updateChart();
  }

  private createAndRenderChart(config: any): void {
    const canvas = <HTMLCanvasElement>this.el.nativeElement.querySelector('canvas');
    canvas.height = this.canvasHeight;
    canvas.width = this.canvasWidth;
    const context = canvas.getContext('2d');
    context.fillText('QQQ', 10, 10);
    this.chart = new Chart(context, config);
    console.log(this.chart);
    let text = "75%",
        textX = Math.round((400 - context.measureText(text).width) / 2),
        textY = 400 / 2;

    context.fillText(text, textX, textY);
    this.chart.update();
    this.chart.render();
  }

  private updateChart(): void {
    this.dataService.getStackingCoins().then(data => {
      if (undefined == this.chart) {
        this.createChartConfig(data).then(config => {
          this.createAndRenderChart(config);
        });
      } else {
      const dataset = this.chart.data.datasets[0];
      dataset.data = data.map(item => item.amountCrypto);
      dataset.backgroundColor = data.map((item, i) => PieFilterDataUtils.colorScheme[i]);

      this.chart.data.labels = data.map(item => item.currencyCode);

      this.chart.update();
      this.chart.render();
      }
    });
  }

  private createChartConfig(data: StakingCoin[]): Promise<any> {
    return new Promise<any>((resolve) => {
      const config = {
        type: 'doughnut',
        data: PieFilterDataUtils.prepareDataset(data, this.borderWidth),
        options: {
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
          //onClick: (event, chartSegments) => this.onSegmentClickHandler(event, chartSegments),
          legend: {
            display: false
          },
          tooltips: {
            enabled: true,
            position: 'nearest',
            callbacks: {
              title: (tooltipItem, data) => data.labels[tooltipItem[0].index],
              afterTitle: (tooltipItem, data) => this.calculatePercentageForSegment(tooltipItem[0].index, data.datasets[0].data),
              label: () => ''
            }
          },
          elements: {
            center: {
              text: 'AAA',
              fontStyle: 'Arial',
              sidePadding: 20,
            }
          }
        }
      };

      resolve(config);
    });
  }

  private calculatePercentageForSegment(segmentIndex: number, data: any[]): string {
    const sum = data.reduce((a, b) => a + b);
    const count = data[segmentIndex];
    const percent = (100 * count / sum).toFixed(2);
    return percent + '%';
  }

}
