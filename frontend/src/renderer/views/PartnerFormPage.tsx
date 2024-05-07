import React, { useEffect, useState } from 'react';
import Person from '../models/Person';
import PersonFormBody from '../components/form/PersonFormBody';
import PartnershipFormBody from '../components/form/PartnershipFormBody';
import SubmitAndCancelButtons from '../components/form/SubmitAndCancelButtons';
import '../styles/FormPage.css';
import { createPerson } from '../actions/PersonActions';
import { createPartnership } from '../actions/PartnershipActions';

function PartnerFormPage() {
  const [personId, setPersonId] = useState<string>(null);
  const [partner, setPartner] = useState<Person>({
    id: undefined,
    firstName: undefined,
    middleName: undefined,
    lastName: undefined,
    sex: undefined,
    birthDate: undefined,
    deathDate: undefined,
  });
  const [partnership, setPartnership] = useState({
    id: undefined,
    type: undefined,
    startDate: undefined,
    endDate: undefined,
    partners: undefined,
    children: undefined,
  });

  const addPartnerListener = (personId: string) => {
    if (personId) {
      setPersonId(personId);
    }
  };

  useEffect(() => {
    return window.electron.ipcRenderer.on('partnership-data', addPartnerListener);
  });

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
  }

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    const createdPerson: Person = await createPerson(partner);
    const createdPartnership = await createPartnership(partnership, [createdPerson.id, personId]);
    window.electron.ipcRenderer.sendMessage('submit-partner-form', createdPartnership.id);
    window.close();
  };

  return (
    <form onSubmit={handleSubmit}>
      <h1>Create Partner</h1>
      <PartnershipFormBody
        partnership={partnership}
        handleChange={handlePartnershipChange}
      />
      <div className="separator" />
      <PersonFormBody
        person={partner}
        handleChange={handlePartnerChange}
      />
      <SubmitAndCancelButtons onCancel={window.close} onSubmit={handleSubmit} />
    </form>
  );
}

export default PartnerFormPage;
