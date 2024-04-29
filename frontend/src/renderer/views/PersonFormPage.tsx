import React, { useState } from 'react';
import Person from '../models/Person';

function PersonFormPage() {
  const [person, setPerson] = useState<Person>({
    id: Math.random().toString(),
    name: '',
    sex: '',
    dob: '',
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
      <h1>Person Form</h1>
      <form onSubmit={handleSubmit}>
        <label htmlFor="name">
          Name:
          <input
            type="text"
            id="name"
            name="name"
            value={person.name}
            onChange={handleChange}
          />
        </label>
        <label htmlFor="sex">
          Sex:
          <input
            type="text"
            id="sex"
            name="sex"
            value={person.sex}
            onChange={handleChange}
          />
        </label>
        <label htmlFor="dob">
          Date of Birth:
          <input
            type="date"
            id="dob"
            name="dob"
            value={person.dob}
            onChange={handleChange}
          />
        </label>
        <input type="submit" value="Submit" />
      </form>
    </div>
  );
}

export default PersonFormPage;
