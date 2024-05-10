import React from 'react';
import '../styles/ContextMenu.css';

interface ContextMenuProps {
  x: number;
  y: number;
  onAddPerson?: () => void;
  onAddPartner?: () => void;
  onEditPerson?: () => void;
  onDeletePerson?: () => void;
  onAddChild?: () => void;
}

function ContextMenu({ x, y, onAddPerson=undefined, onAddPartner=undefined, onEditPerson=undefined, onDeletePerson=undefined, onAddChild=undefined }: ContextMenuProps) {
  return (
    <div className="contextMenu" style={{ position: 'absolute', top: y, left: x }}>
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
      {onEditPerson && (
        <button type="button" onClick={onEditPerson}>
          Edit Person
        </button>
      )}
      {onDeletePerson && (
        <button type="button" onClick={onDeletePerson}>
          Delete Person
        </button>
      )}
      {onAddChild && (
        <button type="button" onClick={onAddChild}>
          Add Child
        </button>
      )}
    </div>
  );
}

export default ContextMenu;
