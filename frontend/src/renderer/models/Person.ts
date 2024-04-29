import type Partnership from './Partnership';

class Person {
  id: string;
  name: string;
  sex: string;
  dob: string;
  dod?: string;
  partnerships?: Partnership[];

  constructor(
    id: string,
    name: string,
    sex: string,
    dob: string,
    dod?: string,
    partnerships?: Partnership[],
  ) {
    this.id = id;
    this.name = name;
    this.sex = sex;
    this.dob = dob;
    this.dod = dod;
    this.partnerships = partnerships;
  }
}

export default Person;
