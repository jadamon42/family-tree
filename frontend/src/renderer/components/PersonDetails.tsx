import React from 'react';
import Person from '../models/Person';

function PersonDetails({ name, sex, dob, dod }: Person) {
  return (
    <div className="person-details">
      <h2>{name}</h2>
      <div className="subtext">
        <p className="left">{sex}</p>
        <p className="right">
          {dob} - {dod || 'Present'}
        </p>
      </div>
    </div>
  );
}

PersonDetails.defaultProps = {
  dod: undefined,
};

export default PersonDetails;
