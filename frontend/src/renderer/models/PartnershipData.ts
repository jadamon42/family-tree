class PartnershipData {
  id: string;
  type: string;
  startDate: Date;
  endDate: Date;
  partnerIds: string[];
  childrenIds: string[];

  constructor(
    id: string,
    type: string,
    startDate: Date,
    endDate: Date,
    partnerIds: string[],
    childrenIds: string[]
  ) {
    this.id = id;
    this.type = type;
    this.startDate = startDate;
    this.endDate = endDate;
    this.partnerIds = partnerIds;
    this.childrenIds = childrenIds;
  }
}

export default PartnershipData;
