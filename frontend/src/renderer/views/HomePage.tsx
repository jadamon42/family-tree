import React, { useEffect, useState } from 'react';
import PersonNode from '../components/PersonNode';
import PersonDetails from '../components/PersonDetails';
import '../styles/HomePage.css';
import ContextMenu from '../components/ContextMenu';
import Person from '../models/Person';
import { deletePerson, getPerson } from '../actions/PersonActions';

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
    window.electron.ipcRenderer.sendMessage('open-person-form');
    setContextMenu(null);
  };

  const handleAddPartner = () => {
    window.electron.ipcRenderer.sendMessage('open-partner-form', contextMenu?.person?.id);
    setContextMenu(null);
  };

  const handleEditPerson = () => {
    window.electron.ipcRenderer.sendMessage('open-person-form', contextMenu?.person);
    setContextMenu(null);
  };

  const handleDeletePerson = async () => {
    if (contextMenu && contextMenu.person) {
      await deletePerson(contextMenu.person.id);
      setPeople((prevPeople) => prevPeople.filter((person) => person.id !== contextMenu.person?.id));
    }
    if (selectedPerson && contextMenu?.person?.id === selectedPerson.id) {
      setSelectedPerson(null);
    }
    setContextMenu(null);
  };

  const putPersonListener = async (personId: string) => {
    const person = await getPerson(personId);
    if (!person) return;

    setPeople((prevPeople) => {
      const updatedPeople = prevPeople.filter((p) => p.id !== personId);
      return [...updatedPeople, person];
    });
  };

  const putPartnerListener = async (personId: string, partnerId: string) => {
    const person: Person = await getPerson(personId);
    const partner: Person = await getPerson(partnerId);
    if (!person || !partner) return;

    // const sharedPartnership = person.partnerships.find(partnership =>
    //   partner.partnerships.some(p => p.id === partnership.id)
    // );
    //
    // if (!sharedPartnership) return;
    //
    // person.partnerships = person.partnerships.map(p => p.id === sharedPartnership.id ? sharedPartnership : p);
    // partner.partnerships = partner.partnerships.map(p => p.id === sharedPartnership.id ? sharedPartnership : p);

    setPeople((prevPeople) => {
      const updatedPeople = prevPeople.filter((p) => p.id !== personId && p.id !== partnerId);
      updatedPeople.push(person);
      updatedPeople.push(partner);
      return updatedPeople;
    });
  };

  useEffect(() => {
    return window.electron.ipcRenderer.on('person-submitted', putPersonListener);
  }, []);

  useEffect(() => {
    return window.electron.ipcRenderer.on('partner-submitted', putPartnerListener);
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
            birthDate={selectedPerson.birthDate}
            deathDate={selectedPerson.deathDate}
          />
        )}
      </div>
    </div>
  );
}

export default HomePage;
