import type TreeSegmentData from './TreeSegmentData';

class TreeSegmentPartnershipData {
  valueId: string;
  partner: TreeSegmentData;
  children: TreeSegmentData[];

  constructor(partnershipId: string) {
    this.valueId = partnershipId;
    this.partner = null;
    this.children = [];
  }

  setPartner(partner: TreeSegmentData) {
    this.partner = partner;
  }

  addChild(child: TreeSegmentData) {
    this.children.push(child);
  }
}

export default TreeSegmentPartnershipData;
