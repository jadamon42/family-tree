class Partnership {
  id: string;
  type: string;
  startDate: string;
  endDate: string;

  constructor(
    id: string,
    type: string,
    startDate: string,
    endDate: string,
  ) {
    this.id = id;
    this.type = type;
    this.startDate = startDate;
    this.endDate = endDate;
  }
}

export default Partnership;
