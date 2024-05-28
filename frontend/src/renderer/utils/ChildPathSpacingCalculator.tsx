import TreeSegmentPartnershipData from '../models/TreeSegmentPartnershipData';

class ChildPathSpacingCalculator {
  static getChildChainSpacing(
    parentRef: HTMLDivElement,
    data: TreeSegmentPartnershipData,
    nodeWidth: number,
    gapWidth: number
  ) {
    const percentagesToChildrenNodes: number[] = [];
    const parentWidth = parentRef.offsetWidth;
    let pixelsInOnCumulativeChildTrees = 0;
    let pixelsToCenterOfCumulativeChildTrees = 0;
    for (let i = 0; i < data.children.length; i++) {
      const componentWidth = (
        parentRef.children as HTMLCollectionOf<HTMLDivElement>
      )[i].offsetWidth;
      const childData = data.children[i];
      const pixelsToCenterOfChildOnPartnershipChain = nodeWidth / 2;
      let pixelsRemainingOnChildPartnershipChain = nodeWidth / 2;
      for (let j = 0; j < childData.partnerships.length; j++) {
        pixelsRemainingOnChildPartnershipChain += gapWidth + nodeWidth;
      }

      let totalPartnershipChainWidth = pixelsToCenterOfChildOnPartnershipChain + pixelsRemainingOnChildPartnershipChain;
      const componentWhiteSpace = (componentWidth - totalPartnershipChainWidth) / 2;
      pixelsInOnCumulativeChildTrees += componentWhiteSpace + pixelsToCenterOfChildOnPartnershipChain;
      percentagesToChildrenNodes.push(pixelsInOnCumulativeChildTrees / parentWidth * 100);
      if (i !== data.children.length - 1) {
        pixelsRemainingOnChildPartnershipChain += gapWidth;
        totalPartnershipChainWidth += gapWidth;
        pixelsInOnCumulativeChildTrees += pixelsRemainingOnChildPartnershipChain + componentWhiteSpace;
        pixelsToCenterOfCumulativeChildTrees += componentWhiteSpace + (totalPartnershipChainWidth / 2);
      } else {
        pixelsToCenterOfCumulativeChildTrees += componentWhiteSpace / 2 + pixelsToCenterOfChildOnPartnershipChain;
      }
    }
    const percentageToCenterOfChildChain = (pixelsToCenterOfCumulativeChildTrees / parentWidth) * 100
    return { percentagesToChildrenNodes, percentageToCenterOfChildChain };
  }
}

export default ChildPathSpacingCalculator;
