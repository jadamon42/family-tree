import React, { useEffect, useState } from 'react';
import Person from '../models/Person';
import PersonFormBody from '../components/form/PersonFormBody';
import '../styles/FormPage.css';
import SubmitAndCancelButtons from '../components/form/SubmitAndCancelButtons';
import { createPerson, updatePerson } from '../actions/PersonActions';

function PersonFormPage() {
  const [person, setPerson] = useState<Person>({
    id: undefined,
    firstName: undefined,
    middleName: undefined,
    lastName: undefined,
    sex: 'UNKNOWN',
    birthDate: undefined,
    deathDate: undefined,
  });

  const editPersonListener = (personToEdit: Person) => {
    if (personToEdit) setPerson(personToEdit);
  };

  useEffect(() => {
    return window.electron.ipcRenderer.on('person-data', editPersonListener);
  });

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPerson({
        ...person,
        [event.target.name]: event.target.value,
    });
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    let createdPerson: Person;
    if (person.id) {
      createdPerson = await updatePerson(person);
    } else {
      createdPerson = await createPerson(person);
    }
    setPerson(createdPerson);
    window.electron.ipcRenderer.sendMessage('submit-person-form', createdPerson.id);
    window.close();
  };

  return (
    <form onSubmit={handleSubmit}>
      <h1>Create Person</h1>
      <PersonFormBody
        person={person}
        handleChange={handleChange}
      />
      <SubmitAndCancelButtons onCancel={window.close} onSubmit={handleSubmit} />
    </form>
  );
}

export default PersonFormPage;
