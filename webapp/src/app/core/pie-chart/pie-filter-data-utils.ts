import { StakingCoin } from '../../model/staking-coin.model';

export class PieFilterDataUtils {

  public static readonly colorScheme = {
    'BTC': '242, 169, 0',
    'ETH': '98, 126, 234',
    'XRP': '0, 96, 151',
    'LTC': '211, 211, 211',
    'DASH': '0, 141, 228'
  };

  public static prepareDataset(sourceData: StakingCoin[], borderWidth: number): any {
    const data = {
      datasets: [{
        data: sourceData.map(item => item.amountFiat),
        backgroundColor: sourceData.map((item, i) => PieFilterDataUtils.getColorCode(item.currencyCode)),
        borderColor: new Array(sourceData.length).fill('#808080'),
        borderWidth: new Array(sourceData.length).fill(0),
        hoverBorderColor: new Array(sourceData.length).fill('#005064'),
        hoverBorderWidth: borderWidth
      }],
      labels: sourceData.map(item => item.currencyCode)
    };
    return data;
  }

  private static getColorCode(code: string) {
    return 'rgb(' + PieFilterDataUtils.colorScheme[code] + ')';
  }

}