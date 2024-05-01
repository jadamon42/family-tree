import React, { useEffect, useState } from 'react';
import Person from '../models/Person';
import PersonFormBody from '../components/form/PersonFormBody';
import { Sex } from '../models/Sex';
import '../styles/FormPage.css';
import SubmitAndCancelButtons from '../components/form/SubmitAndCancelButtons';

function PersonFormPage() {
  const [person, setPerson] = useState<Person>({
    id: Math.random().toString(),
    firstName: undefined,
    middleName: undefined,
    lastName: undefined,
    sex: undefined,
    dob: undefined,
    dod: undefined,
    partnerships: [],
  });

  const editPersonListener = (personToEdit: Person) => {
    if (personToEdit) setPerson(personToEdit);
  };

  useEffect(() => {
    return window.electron.ipcRenderer.on('person-data', editPersonListener);
  });

  const toTitleCase = (name: string) => {
    return name.replace(/\b\w/g, (char) => char.toUpperCase()).trim();
  }

  const sanitizePersonInput = (person: Person) => {
    return {
      ...person,
      firstName: person.firstName ? toTitleCase(person.firstName) : null,
      middleName: person.middleName ? toTitleCase(person.middleName) : null,
      lastName: person.lastName ? toTitleCase(person.lastName) : null,
      sex: person.sex === Sex.UNKNOWN ? null : person.sex
    }
  }

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPerson({
        ...person,
        [event.target.name]: event.target.value,
    });
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    window.electron.ipcRenderer.sendMessage('submit-person-form', sanitizePersonInput(person));
    window.close();
  };

  return (
    <form onSubmit={handleSubmit}>
      <h1>Create Person</h1>
      <PersonFormBody
        person={person}
        handleChange={handleChange}
      />
      <SubmitAndCancelButtons onCancel={window.close} />
    </form>
  );
}

export default PersonFormPage;
