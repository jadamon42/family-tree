import type Partnership from './Partnership';

class Person {
  id: string;
  firstName: string;
  middleName: string;
  lastName: string;
  sex: string;
  dob: string;
  dod?: string;
  partnerships?: Partnership[];

  constructor(
    id: string,
    firstName: string,
    middleName: string,
    lastName: string,
    sex: string,
    dob: string,
    dod?: string,
    partnerships?: Partnership[],
  ) {
    this.id = id;
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.sex = sex;
    this.dob = dob;
    this.dod = dod;
    this.partnerships = partnerships;
  }
}

export default Person;
