import React from 'react';
import '../styles/PersonNode.css';
import Person from '../models/Person';

export interface PersonNodeProps {
  person: Person;
  onClick: (event: React.MouseEvent, person: Person) => void;
  onContextMenu: (event: React.MouseEvent, person: Person) => void;
}

const getLongestName = (person: Person): string => {
  const firstName = person.firstName || '';
  const lastName = person.lastName || '';
  return firstName.length > lastName.length ? firstName : lastName;
}

function PersonNode({ person, onClick, onContextMenu }: PersonNodeProps) {
  const longestName: string = getLongestName(person);
  const fontSize = `${Math.min(1, 10 / longestName.length)}em`;

  return (
    <button
      type="button"
      className="person"
      onClick={(event) => onClick(event, person)}
      onContextMenu={(event) => onContextMenu(event, person)}
      style={{ fontSize }}
    >
      {person.firstName} {person.lastName}
    </button>
  );
}

export default PersonNode;
