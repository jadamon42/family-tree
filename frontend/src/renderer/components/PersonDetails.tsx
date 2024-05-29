import React from 'react';
import { Avatar, Paper, Typography } from '@mui/material';
import Person from '../models/Person';
import { SexDisplayNames } from '../models/Sex';
import '../styles/PersonDetails.css';

function PersonDetails({ firstName, middleName, lastName, sex, birthDate, deathDate=undefined }: Person) {
  const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'long', day: 'numeric' };
  const formattedBirthDate = birthDate ? new Intl.DateTimeFormat('en-US', options).format(birthDate) : 'Unknown';
  const formattedDeathDate = birthDate ? (deathDate ? new Intl.DateTimeFormat('en-US', options).format(deathDate) : 'Present') : 'Unknown';

  return (
    <Paper className="person-details" elevation={3}>
      <div className="header">
        <Avatar alt={firstName} src="/static/images/avatar/1.jpg" />
        <Typography variant="h4" component="h2" gutterBottom>
          {firstName} {middleName ? `${middleName} ` : ''}{lastName}
        </Typography>
      </div>
      <Typography variant="body1" component="p" gutterBottom>
        <strong>Sex:</strong> {SexDisplayNames[sex as keyof typeof SexDisplayNames] || 'Unknown'}
      </Typography>
      <Typography variant="body1" component="p" gutterBottom>
        <strong>Date of Birth:</strong> {formattedBirthDate}
      </Typography>
      <Typography variant="body1" component="p" gutterBottom>
        <strong>Date of Death:</strong> {formattedDeathDate}
      </Typography>
    </Paper>
  );
}

export default PersonDetails;
