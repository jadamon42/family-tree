import React from 'react';
import '../styles/PersonNode.css';
import Person from '../models/Person';
import Partnership from '../models/Partnership';
import PersonNode from './PersonNode';

export interface PartnerNodeProps {
  person: Person;
  partnership?: Partnership;
  onClick: (event: React.MouseEvent, person: Person) => void;
  onContextMenu: (event: React.MouseEvent, person: Person) => void;
}

function PartnerNode({ person, onClick, onContextMenu }: PartnerNodeProps) {
  return <PersonNode person={person} onClick={onClick} onContextMenu={onContextMenu} />;
}

export default PartnerNode;
