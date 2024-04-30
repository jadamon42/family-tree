import React, { useEffect, useState } from 'react';
import Person from '../models/Person';
import PersonFormModal from '../components/PersonFormModal';

function PersonFormPage() {
  const [person, setPerson] = useState<Person>({
    id: Math.random().toString(),
    name: '',
    sex: '',
    dob: '',
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

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    window.electron.ipcRenderer.sendMessage('submit-person-form', person);
    window.close();
  };

  return (
    <div>
      <h1>Create Person</h1>
      <PersonFormModal
        person={person}
        handleChange={handleChange}
        handleSubmit={handleSubmit}
        handleCancel={window.close}
      />
    </div>
  );
}

export default PersonFormPage;
