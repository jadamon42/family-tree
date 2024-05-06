import React, { useEffect, useState } from 'react';
import PersonDetails from '../components/PersonDetails';
import '../styles/HomePage.css';
import ContextMenu from '../components/ContextMenu';
import Person from '../models/Person';
import { deletePerson, getPerson, getRootPeople } from '../actions/PersonActions';
import { BounceLoader } from 'react-spinners';
import TreeSegmentData from '../models/TreeSegmentData';
import TreeSegmentPartnership from '../models/TreeSegmentPartnership';
import TreeSegment from '../components/TreeSegment';

function HomePage() {
  const [roots, setRoots] = useState<Person[]>([]);
  const [treeSegments, setTreeSegments] = useState<TreeSegmentData[]>([]);
  const [processedPeopleIds, setProcessedPeopleIds] = useState<string[]>([]);
  const [people, setPeople] = useState<Person[]>([]);
  const [selectedPerson, setSelectedPerson] = useState<Person | null>(null);
  const [contextMenu, setContextMenu] = useState<{
    x: number;
    y: number;
    type: 'background' | 'person';
    person?: Person;
  } | null>(null);
  const [isLoading, setIsLoading] = useState(true);

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
    const fetchRootPeople = async (retryCount = 0) => {
      try {
        const rootPeople = await getRootPeople();
        setRoots(rootPeople);
        setIsLoading(false)
      } catch (error) {
        if (retryCount < 10) {
          console.error('Failed to fetch root people. Retrying...');
          setTimeout(() => fetchRootPeople(retryCount + 1), 4000);
        } else {
          console.error('Failed to fetch root people after 10 retries.');
        }
      }
    };
    // const buildSegment = async (person: Person) => {
    //   const newSegment = new TreeSegmentData(person);
    //   for (const partnership of person.partnerships) {
    //     const partner: Person = await getPartner(person.id, partnership.id)
    //     const newPartnership = new TreeSegmentPartnership(partner, partnership);
    //     newPartnership.
    //     newSegment.addPartnership(newPartnership);
    //   }
    //   return newSegment;
    // }
    // const buildTreeData = () => {
    //   for (const root of roots) {
    //     if (processedPeopleIds.includes(root.id)) {
    //       continue;
    //     }
    //     const newSegment: TreeSegmentData = buildSegment(root);
    //     setProcessedPeopleIds([...processedPeopleIds, root.id]);
    //   }
    // }
    fetchRootPeople();
    // buildTreeData();
  }, []);

  useEffect(() => {
    return window.electron.ipcRenderer.on('person-submitted', putPersonListener);
  }, []);

  useEffect(() => {
    return window.electron.ipcRenderer.on('partner-submitted', putPartnerListener);
  }, []);

  const blah = (people: Person[]) => {
    const retval = new TreeSegmentData(people[0]);
    const partnership = new TreeSegmentPartnership(people[1], people[1].partnerships[0])
    console.log(people[0].partnerships[0])
    retval.addPartnership(partnership);
    return retval;
  }

  return (
    <div
      className="fullScreenDiv"
      role="button"
      tabIndex={0}
      onClick={handleBackgroundClick}
      onContextMenu={handleBackgroundRightClick}
      onKeyDown={handleKeyDown}
    >
      {isLoading && (
        <div>
          <BounceLoader color={'#123abc'} loading={isLoading} size={150} />
          <p>Loading Family Tree...</p>
        </div>
      )}
      {/*{people.map((person) => (*/}
      {/*  <PersonNode*/}
      {/*    key={person.id}*/}
      {/*    person={person}*/}
      {/*    onClick={(event) => handlePersonClick(event, person)}*/}
      {/*    onContextMenu={handlePersonRightClick}*/}
      {/*  />*/}
      {/*))}*/}
      { !isLoading &&
        <TreeSegment data={blah(people)} onPersonClick={handlePersonClick} onPersonContextMenu={handlePersonRightClick} />
      }
      {/*<FamilyTree rootPeople={people} onPersonClick={handlePersonClick} onPersonContextMenu={handlePersonRightClick} />*/}
      {contextMenu && !isLoading && (
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
