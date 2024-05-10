import type TreeSegmentPartnershipData from './TreeSegmentPartnershipData';

class TreeSegmentData {
  personId: string;
  partnerships: TreeSegmentPartnershipData[];

  constructor(personId: string) {
    this.personId = personId;
    this.partnerships = [];
  }

  setPersonId(personId: string) {
    this.personId = personId;
  }

  setPartnerships(partnerships: TreeSegmentPartnershipData[]) {
    this.partnerships = partnerships;
  }

  addOrReplacePartnership(partnership: TreeSegmentPartnershipData) {
    this.partnerships = this.partnerships.filter((p) => p.valueId !== partnership.valueId);
    this.partnerships.push(partnership);
  }
}

export default TreeSegmentData;
