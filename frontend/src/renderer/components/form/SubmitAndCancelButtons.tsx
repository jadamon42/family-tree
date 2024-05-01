import { Button } from '@mui/material';
import React from 'react';
import '../../styles/SubmitAndCancelButtons.css';

interface SubmitAndCancelButtonsProps {
  onCancel: () => void;
}

function SubmitAndCancelButtons({ onCancel }: SubmitAndCancelButtonsProps) {
  return (
    <div className="button-container">
      <Button variant="contained" color="secondary" type="button" className="cancel-button" onClick={onCancel}>
        Cancel
      </Button>
      <Button variant="contained" color="primary" type="submit" className="submit-button">
        Submit
      </Button>
    </div>
  );
}

export default SubmitAndCancelButtons;
