import React from 'react';
import { Button, FormControlLabel, Radio, RadioGroup, TextField } from '@mui/material';
import Person from '../models/Person';
import '../styles/PersonFormModal.css';
import { Sex, SexDisplayNames } from '../models/Sex';

interface PersonFormModalProps {
  person: Person;
  handleChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  handleSubmit: (event: React.FormEvent) => void;
  handleCancel: () => void;
}

function PersonFormModal({ person, handleChange, handleSubmit, handleCancel }: PersonFormModalProps) {
  return (
    <div className="form-container">
      <h1>Create Person</h1>
      <form onSubmit={handleSubmit}>
        <div className="name-inputs">
          <TextField id="firstName" name="firstName" label="First Name" value={person.firstName} onChange={handleChange}
                     required />
          <TextField id="middleName" name="middleName" label="Middle Name" value={person.middleName}
                     onChange={handleChange} />
          <TextField id="lastName" name="lastName" label="Last Name" value={person.lastName} onChange={handleChange}
                     required />
        </div>
        <div className="sex-radials">
          <RadioGroup row aria-label="sex" name="sex" value={person.sex} onChange={handleChange}>
            <FormControlLabel value={Sex.MALE} control={<Radio />} label={SexDisplayNames[Sex.MALE]} />
            <FormControlLabel value={Sex.FEMALE} control={<Radio />} label={SexDisplayNames[Sex.FEMALE]} />
            <FormControlLabel value={Sex.UNKNOWN} control={<Radio />} label={SexDisplayNames[Sex.UNKNOWN]} />
          </RadioGroup>
        </div>
        <div className="date-inputs">
          <TextField
            id="dob"
            name="dob"
            label="Date of Birth"
            type="date"
            value={person.dob}
            onChange={handleChange}
            InputLabelProps={{
              shrink: true,
            }}
          />
          <TextField
            id="dod"
            name="dod"
            label="Date of Death"
            type="date"
            value={person.dod}
            onChange={handleChange}
            InputLabelProps={{
              shrink: true,
            }}
          />
        </div>
        <div className="button-container">
          <Button variant="contained" color="secondary" type="button" className="cancel-button" onClick={handleCancel}>
            Cancel
          </Button>
          <Button variant="contained" color="primary" type="submit" className="submit-button">
            Submit
          </Button>
        </div>
      </form>
    </div>
  );
}

export default PersonFormModal;
