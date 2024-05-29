import React from 'react';
import '../styles/PersonNode.css';
import Person from '../models/Person';

interface PersonNodeProps {
  person: Person;
  treePathIds: string[];
  onLeftClick: (event: React.MouseEvent, person: Person) => void;
  onRightClick: (event: React.MouseEvent, person: Person) => void;
}

const getLongestName = (person: Person): string => {
  const firstName = person.firstName || '';
  const lastName = person.lastName || '';
  return firstName.length > lastName.length ? firstName : lastName;
}

function PersonNode({ person, treePathIds, onLeftClick, onRightClick }: PersonNodeProps) {
  const longestName: string = getLongestName(person);
  const fontSize = `${Math.min(1, 10 / longestName.length)}em`;
  // const [focused, setFocused] = useState(false);

  // const customClick = (event: React.MouseEvent) => {
  //   setFocused(true);
  //   onLeftClick(event, person);
  // }

  return (
    <button
      type="button"
      className="person"
      onClick={(event) => onLeftClick(event, person)}
      onContextMenu={(event) => onRightClick(event, person)}
      style={{ fontSize, border: treePathIds.includes(person.id) ? '2px solid green' : '' }}
    >
      {person.firstName} {person.lastName}
    </button>
  );
}

export default PersonNode;
