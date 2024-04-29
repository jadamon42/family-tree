import React, { useEffect, useState } from 'react';
import PersonNode from '../components/PersonNode';
import PersonDetails from '../components/PersonDetails';
import '../styles/HomePage.scss';
import ContextMenu from '../components/ContextMenu';
import Person from '../models/Person';

function HomePage() {
  const [people, setPeople] = useState<Person[]>([]);
  const [selectedPerson, setSelectedPerson] = useState<Person | null>(null);
  const [contextMenu, setContextMenu] = useState<{
    x: number;
    y: number;
    type: 'background' | 'person';
    person?: Person;
  } | null>(null);

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === 'Escape') {
      setSelectedPerson(null);
      setContextMenu(null);
    }
  };

  const handleBackgroundClick = () => {
    setContextMenu(null);
  };

  const handleBackgroundRightClick = (event: React.MouseEvent) => {
    event.preventDefault();
    setContextMenu({
      x: event.clientX,
      y: event.clientY,
      type: 'background',
    });
  };

  const handlePersonClick = (event: React.MouseEvent, person: Person) => {
    event.stopPropagation();
    setSelectedPerson((prevPerson) => {
      if (prevPerson && prevPerson.id === person.id) {
        return null;
      }
      return person;
    });
  };

  const handlePersonRightClick = (event: React.MouseEvent, person: Person) => {
    event.preventDefault();
    event.stopPropagation();
    setContextMenu({
      x: event.clientX,
      y: event.clientY,
      type: 'person',
      person,
    });
  };

  const handleAddPerson = () => {
    setContextMenu(null);
    window.electron.ipcRenderer.sendMessage('open-person-form');
  };

  const handleAddPartner = () => {
    setContextMenu(null);
  };

  const handleDeletePerson = () => {
    if (contextMenu && contextMenu.person) {
      setPeople((prevPeople) =>
        prevPeople.filter((person) => person.id !== contextMenu.person?.id),
      );
    }
    if (selectedPerson && contextMenu?.person?.id === selectedPerson.id) {
      setSelectedPerson(null);
    }
    setContextMenu(null);
  };

  const newPersonListener = (person: Person) => {
    setPeople((prevPeople) => [...prevPeople, person]);
  };

  useEffect(() => {
    return window.electron.ipcRenderer.on('new-person', newPersonListener);
  }, []);

  return (
    <div
      className="fullScreenDiv"
      role="button"
      tabIndex={0}
      onClick={handleBackgroundClick}
      onContextMenu={handleBackgroundRightClick}
      onKeyDown={handleKeyDown}
    >
      {people.map((person) => (
        <PersonNode
          key={person.id}
          person={person}
          onClick={(event) => handlePersonClick(event, person)}
          onContextMenu={handlePersonRightClick}
        />
      ))}
      {contextMenu && (
        <ContextMenu
          x={contextMenu.x}
          y={contextMenu.y}
          onAddPerson={
            contextMenu.type === 'background' ? handleAddPerson : undefined
          }
          onAddPartner={
            contextMenu.type === 'person' ? handleAddPartner : undefined
          }
          onDeletePerson={
            contextMenu.type === 'person' ? handleDeletePerson : undefined
          }
        />
      )}
      <div className={`personDetailsPanel ${selectedPerson ? 'show' : ''}`}>
        {selectedPerson && (
          <PersonDetails
            id={selectedPerson.id}
            name={selectedPerson.name}
            sex={selectedPerson.sex}
            dob={selectedPerson.dob}
            dod={selectedPerson.dod}
          />
        )}
      </div>
    </div>
  );
}

export default HomePage;
