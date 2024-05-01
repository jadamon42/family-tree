import React, { useEffect, useState } from 'react';
import Person from '../models/Person';
import PersonFormBody from '../components/form/PersonFormBody';
import { Sex } from '../models/Sex';
import PartnershipFormBody from '../components/form/PartnershipFormBody';
import Partnership from '../models/Partnership';
import SubmitAndCancelButtons from '../components/form/SubmitAndCancelButtons';
import '../styles/FormPage.css';

function PartnerFormPage() {
  const [partner, setPartner] = useState<Person>({
    id: Math.random().toString(),
    firstName: undefined,
    middleName: undefined,
    lastName: undefined,
    sex: undefined,
    dob: undefined,
    dod: undefined,
    partnerships: [],
  });

  const addPartnerListener = (partnership: Partnership) => {
    if (partnership) {
      setPartner({
        ...partner,
        partnerships: [partnership],
      })
    }
  };

  useEffect(() => {
    return window.electron.ipcRenderer.on('partnership-data', addPartnerListener);
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

  const handlePartnerChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPartner({
      ...partner,
      [event.target.name]: event.target.value,
    });
  };

  const handlePartnershipChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPartner({
      ...partner,
      partnerships:  [{
        ...partner.partnerships[0],
        [event.target.name]: event.target.value,
      }],
    });
  }

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    window.electron.ipcRenderer.sendMessage('submit-partner-form', sanitizePersonInput(partner));
    window.close();
  };

  return (
    <form onSubmit={handleSubmit}>
      <h1>Create Partner</h1>
      <PartnershipFormBody
        partnership={partner.partnerships[0]}
        handleChange={handlePartnershipChange}
      />
      <div className="separator" />
      <PersonFormBody
        person={partner}
        handleChange={handlePartnerChange}
      />
      <SubmitAndCancelButtons onCancel={window.close} />
    </form>
  );
}

export default PartnerFormPage;
