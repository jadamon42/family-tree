import type Person from './Person';

class Partnership {
  id: string;
  type: string;
  children: Person[];

  constructor(id: string, type: string, children: Person[]) {
    this.id = id;
    this.type = type;
    this.children = children;
  }
}

export default Partnership;
