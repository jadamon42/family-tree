import React from 'react';
import '../styles/PersonNode.scss';
import Person from '../models/Person';

interface PersonNodeProps {
  person: Person;
  onClick: (event: React.MouseEvent) => void;
  onContextMenu: (event: React.MouseEvent, person: Person) => void;
}

function PersonNode({ person, onClick, onContextMenu }: PersonNodeProps) {
  const longestName: string = person.name.split(' ').reduce((a: string, b: string) => (a.length > b.length ? a : b));
  const fontSize: string = `${Math.min(1, 10 / longestName.length)}em`;

  return (
    <button
      type="button"
      className="person"
      onClick={onClick}
      onContextMenu={(event) => onContextMenu(event, person)}
      style={{ fontSize }}
    >
      {person.name}
    </button>
  );
}

export default PersonNode;
