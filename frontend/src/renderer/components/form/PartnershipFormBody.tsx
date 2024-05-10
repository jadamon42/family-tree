import React from 'react';
import { Box, FormControl, FormControlLabel, FormLabel, Radio, RadioGroup, TextField } from '@mui/material';
import Partnership from '../../models/Partnership';
import '../../styles/PartnershipFormBody.css';
import { PartnershipType, PartnershipTypeDisplayNames } from '../../models/PartnershipType';

interface PersonFormModalProps {
  partnership: Partnership;
  handleChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

function PartnershipFormBody({ partnership, handleChange}: PersonFormModalProps) {
  return (
    <div className="inputs-container">
        <div className="type-radials">
          <Box sx={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
            <FormControl component="fieldset">
              <FormLabel component="legend">Type</FormLabel>
              <RadioGroup row aria-label="type" name="type" value={partnership.type} onChange={handleChange}>
                <FormControlLabel value={PartnershipType.MARRIAGE} control={<Radio />} label={PartnershipTypeDisplayNames[PartnershipType.MARRIAGE]} />
              </RadioGroup>
            </FormControl>
          </Box>
        </div>
        <div className="date-inputs">
          <TextField
            id="dob"
            name="dob"
            label="Start Date"
            type="date"
            value={partnership.startDate}
            onChange={handleChange}
            InputLabelProps={{
              shrink: true,
            }}
          />
          <TextField
            id="dod"
            name="dod"
            label="End Date"
            type="date"
            value={partnership.endDate}
            onChange={handleChange}
            InputLabelProps={{
              shrink: true,
            }}
          />
        </div>
    </div>
  );
}

export default PartnershipFormBody
