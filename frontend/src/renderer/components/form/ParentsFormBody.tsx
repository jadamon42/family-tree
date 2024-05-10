import Person from '../../models/Person';
import { TextField } from '@mui/material';
import '../../styles/ParentsFormBody.css';

interface ParentsFormBodyProps {
  parents: Person[];
}

function ParentsFormBody({ parents }: ParentsFormBodyProps) {
  return (
    <div className="inputs-container">
      <div className="parent-inputs">
        <TextField
          id="father"
          name="father"
          label="Father"
          value={`${parents[0].firstName} ${parents[0].lastName}` || ''}
          disabled={true}
        />
        <TextField
          id="mother"
          name="mother"
          label="Mother"
          value={`${parents[1].firstName} ${parents[1].lastName}` || ''}
          disabled={true}
        />
      </div>
    </div>
  );
}

export default ParentsFormBody;
