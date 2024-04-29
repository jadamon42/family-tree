import React from 'react';
import '../styles/Person.scss';

export interface Person {
  id: string;
  name: string;
  sex: string;
  dob: string;
  dod?: string;
  partnerships?: string[];
}

interface PersonNodeProps {
  person: Person;
  onClick: (event: React.MouseEvent) => void;
  onContextMenu: (event: React.MouseEvent, person: Person) => void;
}

function PersonNode({ person, onClick, onContextMenu }: PersonNodeProps) {
  return (
    <button
      type="button"
      className="person"
      onClick={onClick}
      onContextMenu={(event) => onContextMenu(event, person)}
    >
      {person.name}
    </button>
  );
}

export default PersonNode;
