import { Component, AfterViewInit, ViewChild } from '@angular/core';
import { DataService } from 'src/app/common/data.service';
import { BaseChartDirective } from 'ng2-charts';

import { ChartColorUtil } from '../chart-color-util';

import * as moment from 'moment';

@Component({
  selector: 'line-chart',
  templateUrl: './line-chart.component.html',
  styleUrls: ['./line-chart.component.scss']
})
export class LineChartComponent implements AfterViewInit {

  @ViewChild(BaseChartDirective)
  public chart: BaseChartDirective;

  public ready = false;

  public readonly lineChartLegend: boolean = true;
  public readonly lineChartType: string = 'line';

  public lineChartData: Array<any> = [];
  public lineChartLabels: Array<any> = [];
  public lineChartColors: Array<any> = [];
  public legendData: any[] = [];

  public linkControls = [
    {
      name: '1D',
      arg: 'day',
      selected: false
    },
    {
      name: '1W',
      arg: 'week',
      selected: true
    },
    {
      name: '1M',
      arg: 'month',
      selected: false
    }
  ];



  constructor(private dataService: DataService) {
  }

  ngAfterViewInit() {
    this.updateData('week');
  }

  private updateData(period: string) {
    this.dataService.getSnapshots(period).then(res => {
      // sort currencies in rate order
      res = res.sort((a, b) => b.values[a.values.length-1][1] - a.values[b.values.length-1][1]);
      this.lineChartData = [];
      this.legendData = [];
      res.forEach((d, index) => {
        let data = {
          label: d.currency,
          data: d.values.map(v => v[1]),
          pointRadius: 1,
          pointStyle: 'line'
        };
        this.lineChartData.push(data);
        let color = {
          backgroundColor: ChartColorUtil.getColorCodeWithOpacity(d.currency, 0.2),
          borderColor: ChartColorUtil.getColorCode(d.currency),
          pointBackgroundColor: ChartColorUtil.getColorCodeWithOpacity(d.currency, 0.2),
          pointBorderColor: ChartColorUtil.getColorCode(d.currency),
          pointHoverBackgroundColor: '#fff',
          pointHoverBorderColor: ChartColorUtil.getColorCode[d.currency]
        };
        this.lineChartColors.push(color);
        this.legendData.push({
          text: d.currency,
          clazz: 'legend-' + d.currency.toLowerCase(),
          index: index
        });
      });
      this.lineChartLabels = res[0].values.map(v => this.getLabel(v[0], period));
      if (undefined !== this.chart) {
        setTimeout(() => {
          this.chart.ngOnDestroy();        
          //this.chart.chart = this.chart.getChartBuilder(this.chart.ctx);
        }, 0);
      }
      this.ready = true;
    });
  }

  private getLabel(ts: number, period: string) {
    if ('day' == period) {
      return moment(ts).format('HH:mm');
    } else if ('week' == period) {
      return moment(ts).format('DD MMM HH:mm');
    }
    return moment(ts).format('DD MMM');
  }

  public changePeriod(e, period: string) {
    this.updateData(period);
    this.linkControls.forEach(c => c.selected = c.arg === period);
  }

  private legendCallback = (function (self) {
    function handle(chart) {
      return chart.legend.legendItems;
    }

    return function (chart) {
      return handle(chart);
    }
  })(this);

  public lineChartOptions: any = {
    responsive: true,
    maintainAspectRatio: true,
    legendCallback: this.legendCallback,
    scales: {
      xAxes: [{
        ticks: {
          autoSkip: true,
          maxRotation: 0,
          maxTicksLimit: 20
        }
      }],
      yAxes: [{
        type: 'logarithmic',
        ticks: {
          autoSkip: true,
          callback: function (label, index, labels) {
            if (label >= 1000) {
              return label / 1000 + 'k';
            }
            return label;
          }
        },
        afterBuildTicks: function(chart) {
          const values = chart.ticks;
          const min = values[values.length - 1];
          const max = values[0];
          const vals = [];
          let v = min;
          while (v < max) {
            v *= 10;
            vals.push(v);
          }
          const div = Math.floor(vals[vals.length - 1] / max);
          for (let i = 2; i < div; i++) {
            vals.push(i * vals[vals.length - 1]);
          }
          vals.push(max);
          chart.ticks = vals;
        }
      }]
    },
    tooltips: {
      enabled: true,
      mode: 'x',
      callbacks: {
        title: function (item, data) {
          return item[0].xLabel;
        },
        label: function (item, data) {
          const date = item.xLabel;
          const label = data.datasets[item.datasetIndex].label + ': $' + data.datasets[item.datasetIndex].data[item.index].toFixed(2);
          return label;
        }
      }
    }
  };

  public updateDataset(e, index) {
    const isHidden = null == this.chart.chart.getDatasetMeta(index).hidden
      ? false : this.chart.chart.getDatasetMeta(index).hidden;

    this.chart.chart.getDatasetMeta(index).hidden = !isHidden;
    this.chart.chart.update();

    const hiddenClass = 'legend-control-hidden';
    const classList = e.target.classList;
    if (classList.contains(hiddenClass)) {
      classList.remove(hiddenClass);
    } else {
      classList.add(hiddenClass);
    }
  };

}
