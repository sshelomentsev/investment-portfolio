import { Component, ElementRef, Input, OnInit } from '@angular/core';

import { Chart } from 'chart.js';

@Component({
  selector: 'trend-line',
  templateUrl: './trend-line.component.html',
  styleUrls: ['./trend-line.component.scss']
})
export class TrendLineComponent implements OnInit {

  private chartConfig: any = null;
  private chart: any = null;

  private dataPoints: number[];

  constructor(private el: ElementRef) { }

  ngOnInit() {
  }

  public hasDataPoints(): boolean {
    return this.dataPoints !== null && this.dataPoints !== undefined && this.dataPoints.length > 0;
  }

  @Input('points')
  set points(points: number[]) {
    const oldDataPoints = this.dataPoints;
    this.dataPoints = points;

    // the data points is updated.
    if (this.showTrendLine()) {
      if (oldDataPoints !== points) {
        // if the old data points and the new ones are different, refresh the chart
        if (oldDataPoints == null || points == null || oldDataPoints.length !== points.length) {
          // if the number of data point change, need a "hard" refresh of the chart
          this.refreshChart(true);
        } else {
          // if the number o data point are the same, only refresh the data
          this.refreshChart(false);
        }
      }
    }
  }

  get points(): number[] {
    return this.dataPoints;
  }

  private refreshChart(destroyNeeded: boolean) {
    if (destroyNeeded) {
      if (this.chart) {
        this.chart.destroy();
      }
      const canvas = <HTMLCanvasElement>this.el.nativeElement.querySelector('canvas');
      const context = canvas.getContext('2d');

      this.prepareChartConfig();
      this.chart = new Chart(context, this.chartConfig);
    } else {
      this.chartConfig.data.datasets[0].data = this.dataPoints;
      this.chart.update();
    }

  }

  public showTrendLine(): boolean {
    if (this.dataPoints) {
      return true;
    } else {
      return this.dataChanged();
    }
    return false;
  }

  private dataChanged() {
    if (this.dataPoints && this.dataPoints.length > 0) {
      const firstElem = this.dataPoints[0];
      return this.points.some(elem => elem !== firstElem);
    } else {
      return false;
    }
  }

  private prepareChartConfig() {
    this.chartConfig = {
      type: 'line',
      data: {
        labels: this.dataPoints,
        datasets: [{
          backgroundColor: 'dark',
          borderColor: 'dark',
          borderWidth: 1,
          lineTension: 0,
          pointRadius: 1,
          data: this.dataPoints,
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
