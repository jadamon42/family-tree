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

export async function createPerson(person: Person) {
  const response = await fetch('http://localhost:50000/api/person', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(sanitizePersonInput(person)),
  });

  if (!response.ok) {
    console.error('Error:', response.statusText);
  }

  return await response.json();
}

export async function getPerson(id: string) {
  const response = await fetch(`http://localhost:50000/api/person/${id}`);

  if (!response.ok) {
    console.error('Error:', response.statusText);
  }

  return await response.json();
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

  return await response.json();
}

export async function deletePerson(id: string) {
  const response = await fetch(`http://localhost:50000/api/person/${id}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    console.error('Error:', response.statusText);
  }
}
