import React, { useEffect, useState } from 'react';
import Person from '../models/Person';
import PersonForm from '../components/PersonForm';
import { Sex } from '../models/Sex';
import PartnershipForm from '../components/PartnershipForm';
import Partnership from '../models/Partnership';
import '../styles/PartnerFormPage.css';

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
  const [partnership, setPartnership] = useState<Partnership>(null);

  const addPartnerListener = (partnership: Partnership) => {
    if (partnership) {
      setPartnership(partnership);
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
    setPartnership({
      ...partnership,
      [event.target.name]: event.target.value,
    });
    setPartner({
      ...partner,
      partnerships:  [partnership],
    });
  }

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    window.electron.ipcRenderer.sendMessage('submit-partner-form', sanitizePersonInput(partner));
    window.close();
  };

  return (
    <div>
      <h1>Create Partner</h1>
      <PartnershipForm
        partnership={partnership}
        handleChange={handlePartnershipChange}
      />
      <div className="separator" />
      <PersonForm
        person={partner}
        handleChange={handlePartnerChange}
        handleSubmit={handleSubmit}
        handleCancel={window.close}
        // maybe this shouldn't have submit or cancel
      />
    </div>
  );
}

export default PartnerFormPage;
