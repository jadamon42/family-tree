import Person from './Person';
import Partnership from './Partnership';
import TreeSegmentData from './TreeSegmentData';

class TreeSegmentPartnership {
  partner: Person;
  value: Partnership;
  childSegments: TreeSegmentData[] = [];

  constructor(partner: Person, value: Partnership) {
    this.partner = partner;
    this.value = value;
  }

  addChildSegment(segment: TreeSegmentData) {
    this.childSegments.push(segment);
  }
}

export default TreeSegmentPartnership;
