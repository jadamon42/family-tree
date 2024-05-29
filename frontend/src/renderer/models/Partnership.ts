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

  getPartnershipData() {
    return {
      id: this.id,
      type: this.type,
      startDate: this.startDate,
      endDate: this.endDate,
      partnerIds: this.partners.map((partner: Person) => partner.id),
      childrenIds: this.children.map((child: Person) => child.id),
    };
  }
}

export default Partnership;
