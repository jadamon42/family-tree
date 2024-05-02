import type Partnership from './Partnership';

class Person {
  id: string;
  firstName: string;
  middleName: string;
  lastName: string;
  sex: string;
  birthDate: string;
  deathDate?: string;
  partnerships?: Partnership[];

  constructor(
    id: string,
    firstName: string,
    middleName: string,
    lastName: string,
    sex: string,
    birthDate: string,
    deathDate?: string,
    partnerships?: Partnership[],
  ) {
    this.id = id;
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.sex = sex;
    this.birthDate = birthDate;
    this.deathDate = deathDate;
    this.partnerships = partnerships;
  }
}

export default Person;
