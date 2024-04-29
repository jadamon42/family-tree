import React from 'react';
import '../styles/ContextMenu.scss';

interface ContextMenuProps {
  x: number;
  y: number;
  onAddPerson?: () => void;
  onAddPartner?: () => void;
  onDeletePerson?: () => void;
}

function ContextMenu({
  x,
  y,
  onAddPerson,
  onAddPartner,
  onDeletePerson,
}: ContextMenuProps) {
  return (
    <div
      className="contextMenu"
      style={{ position: 'absolute', top: y, left: x }}
    >
      {onAddPerson && (
        <button type="button" onClick={onAddPerson}>
          Add Person
        </button>
      )}
      {onAddPartner && (
        <button type="button" onClick={onAddPartner}>
          Add Partner
        </button>
      )}
      {onDeletePerson && (
        <button type="button" onClick={onDeletePerson}>
          Delete Person
        </button>
      )}
    </div>
  );
}

ContextMenu.defaultProps = {
  onAddPerson: undefined,
  onAddPartner: undefined,
  onDeletePerson: undefined,
};

export default ContextMenu;
