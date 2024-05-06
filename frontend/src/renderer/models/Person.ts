class Person {
  id: string;
  firstName: string;
  middleName: string;
  lastName: string;
  sex: string;
  birthDate: string;
  deathDate?: string;

  constructor(
    id: string,
    firstName: string,
    middleName: string,
    lastName: string,
    sex: string,
    birthDate: string,
    deathDate?: string,
  ) {
    this.id = id;
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.sex = sex;
    this.birthDate = birthDate;
    this.deathDate = deathDate;
  }
}

export default Person;
