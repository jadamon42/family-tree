import React, { useState } from 'react';
import { Person } from './PersonNode';

interface PersonFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onAddPerson: (person: Person) => void;
}

function PersonFormModal({
  isOpen,
  onClose,
  onAddPerson,
}: PersonFormModalProps) {
  const [personForm, setPersonForm] = useState({ name: '', sex: '', dob: '' });

  const handleFormChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPersonForm({
      ...personForm,
      [event.target.name]: event.target.value,
    });
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    onAddPerson({ id: Math.random().toString(), ...personForm });
    onClose();
  };

  if (!isOpen) {
    return null;
  }

  return (
    <div className="modal">
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="name"
          value={personForm.name}
          onChange={handleFormChange}
          placeholder="Name"
        />
        <input
          type="text"
          name="sex"
          value={personForm.sex}
          onChange={handleFormChange}
          placeholder="Sex"
        />
        <input
          type="date"
          name="dob"
          value={personForm.dob}
          onChange={handleFormChange}
          placeholder="Date of Birth"
        />
        <button type="submit">Add Person</button>
        <button type="button" onClick={onClose}>
          Cancel
        </button>
      </form>
    </div>
  );
}

export default PersonFormModal;
