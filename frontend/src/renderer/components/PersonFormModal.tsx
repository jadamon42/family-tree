import React from 'react';
import { Button, FormControl, FormControlLabel, FormLabel, Radio, RadioGroup, TextField } from '@mui/material';
import Person from '../models/Person';
import '../styles/PersonFormModal.scss';
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
      <form onSubmit={handleSubmit}>
        <TextField id="name" name="name" label="Name" value={person.name} onChange={handleChange} required />
        <FormControl component="fieldset">
          <FormLabel component="legend" className="form-label">
            Sex
          </FormLabel>
          <RadioGroup aria-label="sex" name="sex" value={person.sex} onChange={handleChange}>
            <FormControlLabel value={Sex.MALE} control={<Radio />} label={SexDisplayNames[Sex.MALE]} />
            <FormControlLabel value={Sex.FEMALE} control={<Radio />} label={SexDisplayNames[Sex.FEMALE]} />
          </RadioGroup>
        </FormControl>
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
