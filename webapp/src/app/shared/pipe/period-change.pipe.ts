import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'periodChange'
})
export class PeriodChangePipe implements PipeTransform {

  transform(value: number, args?: any): any {
    let res;
    if (undefined === value || null === value || 0 === value) {
      res = '0';
    } else {
      res = String(value) + '%';
    }
    if (value > 0) {
      res = '+' + res;
    }

    return res;
  }

}