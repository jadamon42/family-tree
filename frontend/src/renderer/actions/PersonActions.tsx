import Person from '../models/Person';

const toTitleCase = (name: string) => {
  return name.replace(/\b\w/g, (char) => char.toUpperCase()).trim();
}

const sanitizePersonInput = (person: Person) => {
  return {
    ...person,
    firstName: person.firstName ? toTitleCase(person.firstName) : null,
    middleName: person.middleName ? toTitleCase(person.middleName) : null,
    lastName: person.lastName ? toTitleCase(person.lastName) : null,
    // sex: person.sex === Sex.UNKNOWN ? null : person.sex
  }
}

export function mapToPerson(person: any): Person {
  return new Person(
    person.id,
    person.firstName,
    person.middleName,
    person.lastName,
    person.sex,
    person.birthDate ? new Date(person.birthDate) : undefined,
    person.deathDate ? new Date(person.deathDate) : undefined,
  );
}

export async function createPerson(person: Person, parentsPartnershipId?: string) {
  const response = await fetch('http://localhost:50000/api/person', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      ...sanitizePersonInput(person),
      parentsPartnershipId,
    }),
  });

  if (!response.ok) {
    console.error('Error:', response.statusText);
  }

  const createdPerson = await response.json();
  return mapToPerson(createdPerson);
}

export async function getPerson(id: string) {
  const response = await fetch(`http://localhost:50000/api/person/${id}`);

  if (!response.ok) {
    console.error('Error:', response.statusText);
  }

  const person = await response.json();
  return mapToPerson(person);
}

export async function getRootPeople() {
  const response = await fetch('http://localhost:50000/api/person?rootsOnly=true');

  if (!response.ok) {
    console.error('Error:', response.statusText);
  }

  const data: any[] = await response.json();
  return data.map(person => mapToPerson(person));
}

export async function updatePerson(person: Person) {
  const response = await fetch(`http://localhost:50000/api/person/${person.id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(sanitizePersonInput(person)),
  });

  if (!response.ok) {
    console.error('Error:', response.statusText);
  }

  const updatedPerson = await response.json();
  return mapToPerson(updatedPerson);
}

export async function deletePerson(id: string) {
  const response = await fetch(`http://localhost:50000/api/person/${id}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    console.error('Error:', response.statusText);
  }
}
