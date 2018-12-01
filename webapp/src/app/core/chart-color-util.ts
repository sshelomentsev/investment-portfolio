export class ChartColorUtil {

  private static readonly colorScheme = {
    'BTC': '242, 169, 0',
    'ETH': '98, 126, 234',
    'XRP': '0, 96, 151',
    'LTC': '211, 211, 211',
    'DASH': '0, 141, 228'
  };

  public static getColorCode(code: string) {
    return 'rgb(' + this.colorScheme[code] + ')';
  }

  public static getColorCodeWithOpacity(code: string, opacity: number) {
    return 'rgba(' + this.colorScheme[code] + ', ' + String(opacity) + ')';
  }

}