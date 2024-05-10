import React, { useEffect, useState } from 'react';
import Person from '../models/Person';
import PersonFormBody from '../components/form/PersonFormBody';
import SubmitAndCancelButtons from '../components/form/SubmitAndCancelButtons';
import '../styles/FormPage.css';
import { createPerson } from '../actions/PersonActions';
import { getPartnership } from '../actions/PartnershipActions';
import { Sex } from '../models/Sex';
import ParentsFormBody from '../components/form/ParentsFormBody';
import Partnership from '../models/Partnership';

function ChildFormPage() {
  const [partnership, setPartnership] = useState<Partnership>();
  const [child, setChild] = useState<Person>({
    id: undefined,
    firstName: undefined,
    middleName: undefined,
    lastName: undefined,
    sex: Sex.UNKNOWN,
    birthDate: undefined,
    deathDate: undefined,
  });

  const addChildListener = async (partnershipId: string) => {
    if (partnershipId) {
      const partnership = await getPartnership(partnershipId);
      setPartnership(partnership);
    }
  };

  useEffect(() => {
    return window.electron.ipcRenderer.on('child-data', addChildListener);
  });

  const handleChildChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setChild({
      ...child,
      [event.target.name]: event.target.value,
    });
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    await createPerson(child, partnership.id);
    window.electron.ipcRenderer.sendMessage('submit-child-form', partnership.id);
    window.close();
  };

  if (!partnership) return null;
  return (
    <form onSubmit={handleSubmit}>
      <h1>Create Person</h1>
      <ParentsFormBody
        parents={partnership.partners}
      />
      <div className="separator" />
      <PersonFormBody
        person={child}
        handleChange={handleChildChange}
      />
      <SubmitAndCancelButtons onCancel={window.close} onSubmit={handleSubmit} />
    </form>
  );
}

export default ChildFormPage;
