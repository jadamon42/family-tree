import type Person from './Person';

class Partnership {
  id: string;
  type: string;
  startDate: Date;
  endDate: Date;
  partners: Person[];
  children: Person[];

  constructor(
    id: string,
    type: string,
    startDate: Date,
    endDate: Date,
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
}

export default Partnership;
