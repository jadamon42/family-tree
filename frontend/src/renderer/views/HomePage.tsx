import React, { useEffect, useState } from 'react';
import PersonNode from '../components/PersonNode';
import PersonDetails from '../components/PersonDetails';
import '../styles/HomePage.css';
import ContextMenu from '../components/ContextMenu';
import Person from '../models/Person';
import Partnership from '../models/Partnership';

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
    setContextMenu(null);
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
    const partnership = new Partnership(Math.random().toString(), undefined, undefined, undefined, []);
    putPersonListener({
      ...contextMenu.person,
      partnerships: [...contextMenu.person.partnerships, partnership],
    })
    window.electron.ipcRenderer.sendMessage('open-partner-form', partnership);
    setContextMenu(null);
  };

  const handleEditPerson = () => {
    window.electron.ipcRenderer.sendMessage('open-person-form', contextMenu?.person);
    setContextMenu(null);
  };

  const handleDeletePerson = () => {
    if (contextMenu && contextMenu.person) {
      setPeople((prevPeople) => prevPeople.filter((person) => person.id !== contextMenu.person?.id));
    }
    if (selectedPerson && contextMenu?.person?.id === selectedPerson.id) {
      setSelectedPerson(null);
    }
    setContextMenu(null);
  };

  const putPersonListener = (person: Person) => {
    setPeople((prevPeople) => {
      const updatedPeople = prevPeople.filter((p) => p.id !== person.id);
      return [...updatedPeople, person];
    });
  };

  useEffect(() => {
    return window.electron.ipcRenderer.on('person-submitted', putPersonListener);
  }, []);

  useEffect(() => {
    return window.electron.ipcRenderer.on('partner-submitted', putPersonListener);
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
          onAddPerson={contextMenu.type === 'background' ? handleAddPerson : undefined}
          onAddPartner={contextMenu.type === 'person' ? handleAddPartner : undefined}
          onEditPerson={contextMenu.type === 'person' ? handleEditPerson : undefined}
          onDeletePerson={contextMenu.type === 'person' ? handleDeletePerson : undefined}
        />
      )}
      <div className={`personDetailsPanel ${selectedPerson ? 'show' : ''}`}>
        {selectedPerson && (
          <PersonDetails
            id={selectedPerson.id}
            firstName={selectedPerson.firstName}
            middleName={selectedPerson.middleName}
            lastName={selectedPerson.lastName}
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
