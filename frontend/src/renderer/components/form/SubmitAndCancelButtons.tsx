import { Button, CircularProgress } from '@mui/material';
import React, { useState } from 'react';
import '../../styles/SubmitAndCancelButtons.css';

interface SubmitAndCancelButtonsProps {
  onCancel: () => void;
  onSubmit: (event: React.FormEvent) => Promise<void>;
}

function SubmitAndCancelButtons({ onCancel, onSubmit }: SubmitAndCancelButtonsProps) {
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setIsSubmitting(true);
    await onSubmit(event);
    setIsSubmitting(false);
  };

  return (
    <div className="button-container">
      <Button variant="contained" color="secondary" type="button" className="cancel-button" onClick={onCancel}>
        Cancel
      </Button>
      <Button variant="contained" color="primary" type="submit" className="submit-button" onClick={handleSubmit} disabled={isSubmitting}>
        {isSubmitting ? <CircularProgress size={24} /> : 'Submit'}
      </Button>
    </div>
  );
}

export default SubmitAndCancelButtons;
