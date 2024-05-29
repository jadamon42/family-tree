class Person {
  id: string;
  firstName: string;
  middleName: string;
  lastName: string;
  sex: string;
  birthDate: Date;
  deathDate?: Date;

  constructor(
    id: string,
    firstName: string,
    middleName: string,
    lastName: string,
    sex: string,
    birthDate: Date,
    deathDate?: Date,
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
