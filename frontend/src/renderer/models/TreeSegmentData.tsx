import Person from './Person';
import type TreeSegmentPartnership from './TreeSegmentPartnership';

class TreeSegmentData {
  person: Person;
  partnerships: TreeSegmentPartnership[];

  constructor(person: Person) {
    this.person = person;
    this.partnerships = [];
  }

  addPartnership(partnership: TreeSegmentPartnership) {
    this.partnerships.push(partnership);
  }
}

export default  TreeSegmentData;
