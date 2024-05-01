import type Person from './Person';

class Partnership {
  id: string;
  type: string;
  startDate: string;
  endDate: string;
  children: Person[];

  constructor(
    id: string,
    type: string,
    startDate: string,
    endDate: string,
    children: Person[]
  ) {
    this.id = id;
    this.type = type;
    this.startDate = startDate;
    this.endDate = endDate;
    this.children = children;
  }
}

export default Partnership;
