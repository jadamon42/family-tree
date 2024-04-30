import React from 'react';
import { Avatar, Paper, Typography } from '@mui/material';
import Person from '../models/Person';
import { SexDisplayNames } from '../models/Sex';
import '../styles/PersonDetails.css';

function PersonDetails({ name, sex, dob, dod=undefined }: Person) {
  return (
    <Paper className="person-details" elevation={3}>
      <div className="header">
        <Avatar alt={name} src="/static/images/avatar/1.jpg" />
        <Typography variant="h4" component="h2" gutterBottom>
          {name}
        </Typography>
      </div>
      <Typography variant="body1" component="p" gutterBottom>
        <strong>Sex:</strong> {SexDisplayNames[sex as keyof typeof SexDisplayNames] || 'Unknown'}
      </Typography>
      <Typography variant="body1" component="p" gutterBottom>
        <strong>Date of Birth:</strong> {dob || 'Unknown'}
      </Typography>
      <Typography variant="body1" component="p" gutterBottom>
        <strong>Date of Death:</strong> {dob ? dod || 'Present' : 'Unknown'}
      </Typography>
    </Paper>
  );
}

export default PersonDetails;
