import type Person from './Person';

class Partnership {
  id: string;
  type: string;
  startDate: string;
  endDate: string;
  partners: Person[];
  children: Person[];

  constructor(
    id: string,
    type: string,
    startDate: string,
    endDate: string,
    partners: Person[],
    children: Person[]
  ) {
    this.id = id;
    this.type = type;
    this.startDate = startDate;
    this.endDate = endDate;
    this.partners = partners;
    this.children = children;
  }

  // getData() {
  //   return new PartnershipData(this.id, this.type, this.startDate, this.endDate);
  // }
}

export default Partnership;
