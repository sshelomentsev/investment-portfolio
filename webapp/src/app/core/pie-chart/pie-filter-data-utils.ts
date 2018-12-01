import { StakingCoin } from '../../model/staking-coin.model';

export class PieFilterDataUtils {

  public static readonly colorScheme: string[] = [
      "#abc233", "#64bbe3", "#faa43a", "#60bd68", "#f17cb0", "#b2912f", "#b276b2", "#decf3f", "#f15854", "#1f79a7",
      "#2484c1", "#65a620", "#d8d23a", "#e98125", "#d0743c", "#635222", "#6ada6a", "#0c6197", "#7d9058", "#207f33",
      "#44b9b0", "#bca44a", "#e4a14b", "#a3acb2", "#8cc3e9", "#69a6f9", "#5b388f", "#546e91", "#8bde95", "#d2ab58",
      "#273c71", "#98bf6e", "#4daa4b", "#98abc5", "#cc1010", "#31383b", "#006391", "#c2643f", "#b0a474", "#a5a39c",
      "#a9c2bc", "#22af8c", "#7fcecf", "#987ac6", "#3d3b87", "#b77b1c", "#c9c2b6", "#807ece", "#8db27c", "#be66a2",
      "#9ed3c6", "#00644b", "#005064", "#77979f", "#77e079", "#9c73ab", "#1f79a7", "#7b6888", "#a05d56", "#961a1a"
  ];

  public static prepareDataset(sourceData: StakingCoin[], borderWidth: number): any {
    const data = {
        datasets: [{
            data: sourceData.map(item => item.amountCrypto),
            backgroundColor: sourceData.map((item, i) => PieFilterDataUtils.colorScheme[i]),
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