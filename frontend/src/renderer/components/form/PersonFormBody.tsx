import React from 'react';
import { FormControlLabel, Radio, RadioGroup, TextField } from '@mui/material';
import Person from '../../models/Person';
import '../../styles/PersonFormBody.css';
import { Sex, SexDisplayNames } from '../../models/Sex';

interface PersonFormModalProps {
  person: Person;
  handleChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

function PersonFormBody({ person, handleChange }: PersonFormModalProps) {
  return (
    <div className="inputs-container">
        <div className="name-inputs">
          <TextField id="firstName" name="firstName" label="First Name" value={person.firstName} onChange={handleChange}
                     required InputLabelProps={{ shrink: !!person.firstName }}/>
          <TextField id="middleName" name="middleName" label="Middle Name" value={person.middleName}
                     onChange={handleChange} InputLabelProps={{ shrink: !!person.middleName }} />
          <TextField id="lastName" name="lastName" label="Last Name" value={person.lastName} onChange={handleChange}
                     required InputLabelProps={{ shrink: !!person.lastName }} />
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
    </div>
  );
}

export default PersonFormBody;
