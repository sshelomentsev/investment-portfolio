import { Component, ElementRef, Input, OnInit, SimpleChanges, OnChanges } from '@angular/core';

import { Chart } from 'chart.js';

@Component({
  selector: 'trend-line',
  templateUrl: './trend-line.component.html',
  styleUrls: ['./trend-line.component.scss']
})
export class TrendLineComponent implements OnInit, OnChanges {

  private chartConfig: any = null;
  private chart: any = null;

  constructor(private el: ElementRef) { }

  ngOnInit() {
    this.refreshChart(this.points);
  }

  @Input()
  points: number[] = [2,3,4,19,29];

  ngOnChanges(changes: SimpleChanges) {
    if (changes['points']) {
      const ch = changes['points'];
      if (!this.isEqual(ch.previousValue, ch.currentValue)) {
        this.refreshChart(ch.currentValue);
      }
    }
  }

  private isEqual(arr1: any[], arr2: any[]) {
    if (!arr1 || !arr2) {
      return false;
    }
    if (arr1.length !== arr2.length) {
      false;
    }

    return arr1.filter((v, i) => v === arr2[i]).length == arr1.length;
  }

  private refreshChart(points: number[]) {
    if (undefined == this.chart) {
      const canvas = <HTMLCanvasElement>this.el.nativeElement.querySelector('canvas');
      const context = canvas.getContext('2d');

      this.prepareChartConfig(points);
      this.chart = new Chart(context, this.chartConfig);
    } else {
      this.chartConfig.data.datasets[0].data = this.points;
      this.chart.update();
    }

  }

  private prepareChartConfig(points: number[]) {
    this.chartConfig = {
      type: 'line',
      data: {
        labels: points,
        datasets: [{
          borderColor: points[0] < points[points.length - 1] ? 'green' : 'red',
          borderWidth: 2,
          lineTension: 0,
          pointRadius: 0,
          data: points,
          fill: false
        }]
      },
      options: {
        responsive: true,
        layout: {
          padding: {
            left: 5,
            right: 5,
            top: 5,
            bottom: 5
          }
        },
        scales: {
          xAxes: [{
            display: false
          }],
          yAxes: [{
            display: false
          }]
        },
        legend: {
          display: false
        },
        tooltips: {
          title: "",
          enabled: false,
          xPadding: 1,
          yPadding: 1,
          displayColors: false
        }
      }
    };
  }


}
