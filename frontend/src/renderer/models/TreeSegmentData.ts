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

  addPartnership(partnership: TreeSegmentPartnershipData) {
    this.partnerships.push(partnership);
  }
}

export default TreeSegmentData;
