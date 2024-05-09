import React, { useEffect, useState } from 'react';
import PersonDetails from '../components/PersonDetails';
import '../styles/HomePage.css';
import ContextMenu from '../components/ContextMenu';
import Person from '../models/Person';
import { deletePerson, getPerson, getRootPeople } from '../actions/PersonActions';
import { BounceLoader } from 'react-spinners';
import PartnershipData from '../models/PartnershipData';
import TreeSegmentData from '../models/TreeSegmentData';
import { getPartnership, getPartnerships } from '../actions/PartnershipActions';
import TreeSegmentPartnershipData from '../models/TreeSegmentPartnershipData';
import TreeSegment from '../components/TreeSegment';
import Partnership from '../models/Partnership';

function HomePage() {
  const [people, setPeople] = useState<Map<string, Person>>(new Map());
  const [partnerships, setPartnerships] = useState<Map<string, PartnershipData>>(new Map());
  const [segments, setSegments] = useState<TreeSegmentData[]>([]);
  const [selectedPerson, setSelectedPerson] = useState<Person | null>(null);
  const [contextMenu, setContextMenu] = useState<{
    x: number;
    y: number;
    type: 'background' | 'person';
    person?: Person;
  } | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [zoom, setZoom] = useState(1);
  const [posX, setPosX] = useState(0);
  const [posY, setPosY] = useState(0);
  const [isDragging, setIsDragging] = useState(false);
  const [treePathIds, setTreePathIds] = useState<string[]>([]);

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === 'Escape') {
      setSelectedPerson(null);
      setContextMenu(null);
    }
  };

  const handleWheel = (event: React.WheelEvent) => {
    const scale = event.deltaY < 0 ? 1.03 : 0.97;
    setZoom(prevZoom => Math.max(prevZoom * scale, 0.1));
  };

  const handleMouseDown = (event: React.MouseEvent) => {
    setIsDragging(true);
  };

  const handleMouseMove = (event: React.MouseEvent) => {
    if (isDragging) {
      setPosX(prevPosX => prevPosX + event.movementX);
      setPosY(prevPosY => prevPosY + event.movementY);
    }
  };

  const handleMouseUp = () => {
    setIsDragging(false);
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

  const handlePartnershipClick = (event: React.MouseEvent, partnership: PartnershipData) => {
    event.stopPropagation();
    if (treePathIds.includes(partnership.id)) {
      setTreePathIds([]);
    } else {
      setTreePathIds([partnership.id]);
    }
    setContextMenu(null);
  }

  const handlePartnershipRightClick = (event: React.MouseEvent, partnership: PartnershipData) => {
    event.preventDefault();
    event.stopPropagation();
  }

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
    if (selectedPerson && contextMenu?.person?.id === selectedPerson.id) {
      setSelectedPerson(null);
    }
    setContextMenu(null);
    if (contextMenu && contextMenu.person) {
      setIsLoading(true);
      await deletePerson(contextMenu.person.id);
      await load();
    }
  };

  const refocusSegment = (segment: TreeSegmentData, newPersonId: string) => {
    let newFocusPerson = null;
    let sharedPartnership = null;

    for(const partnership of segment.partnerships) {
      if (partnership.partner.personId === newPersonId) {
        sharedPartnership = partnership;
        newFocusPerson = partnership.partner;
        break;
      }
    }

    if (sharedPartnership && newFocusPerson) {
      const oldFocusPerson = new TreeSegmentData(segment.personId)
      oldFocusPerson.setPartnerships(segment.partnerships.filter(p => p.partner.personId !== newPersonId));
      sharedPartnership.setPartner(oldFocusPerson);
      segment.setPersonId(newFocusPerson.personId);
      segment.setPartnerships(newFocusPerson.partnerships);
      segment.addPartnership(sharedPartnership);
    }
  }

  const mergeSegment = (segment: TreeSegmentData, existingSegment: TreeSegmentData): boolean => {
    let merged = false;
    if (segment.personId === existingSegment.personId) {
      for(const partnership of segment.partnerships) {
        const existingPartnership = existingSegment.partnerships.find(p => p.valueId === partnership.valueId);
        if (!existingPartnership) {
          existingSegment.addPartnership(partnership);
        }
      }
      merged = true;
    } else if (segment.partnerships.length === 1 && segment.partnerships[0].partner.personId === existingSegment.personId) {
      // Right now segment will only have at most one partnership
      // TODO: we can't refocus if they're a child
      refocusSegment(segment, existingSegment.personId);
      merged = mergeSegment(segment, existingSegment);
    } else {
      for(const existingPartnership of existingSegment.partnerships) {
        // if (segment.partnerships.length === 1 && segment.partnerships[0].partner.personId === existingPartnership.partner.personId) {
        //   refocusSegment(segment, existingPartnership.partner.personId);
        // }
        merged = mergeSegment(segment, existingPartnership.partner);
        if (merged) {
          break;
        }
        for(const child of existingPartnership.children) {
          merged = mergeSegment(segment, child);
          if (merged) {
            break;
          }
        }
        if (merged) {
          break;
        }
      }
    }
    return merged;
  }

  const mergeToSegments = (segment: TreeSegmentData, existingSegments: TreeSegmentData[]) => {
    for(const existingSegment of existingSegments) {
      if (mergeSegment(segment, existingSegment)) {
        return;
      }
    }
    existingSegments.push(segment);
  }

  const buildTreeSegment = (partnership: Partnership): TreeSegmentData => {
    const partnershipData = new PartnershipData(partnership.id, partnership.type, partnership.startDate, partnership.endDate);
    setPartnerships((prevPartnerships) => new Map([...prevPartnerships, [partnership.id, partnershipData]]));
    const segment = new TreeSegmentData(null);
    const partners = partnership.partners.sort((a, b) => a.sex.toUpperCase() === 'MALE' ? 1: -1);
    partners.forEach((partner, index) => {
      setPeople((prevPeople) => new Map([...prevPeople, [partner.id, partner]]));
      if (index === 0) {
        segment.setPersonId(partner.id);
      } else {
        const segmentPartnership = new TreeSegmentPartnershipData(partnership.id);
        segmentPartnership.setPartner(new TreeSegmentData(partner.id));

        // TODO: Sort children by birth date as date instead of as string
        const children = partnership.children.sort((a, b) => a.birthDate.localeCompare(b.birthDate));
        children.forEach((child) => {
          setPeople((prevPeople) => new Map([...prevPeople, [child.id, child]]));
          const childSegment = new TreeSegmentData(child.id);
          segmentPartnership.addChild(childSegment);
        });

        segment.addPartnership(segmentPartnership);
      }
    });
    return segment;
  }

  const fetchPartnershipsAndBuildTreeSegments = async (retryCount = 0) => {
    try {
      const segmentsToAdd: TreeSegmentData[] = [];
      const partnerships = await getPartnerships();
      for(const partnership of partnerships) {
        const segment = buildTreeSegment(partnership);
        mergeToSegments(segment, segmentsToAdd);
      }
      const rootPeople = await getRootPeople();
      for (const person of rootPeople) {
        setPeople((prevPeople) => new Map([...prevPeople, [person.id, person]]));
        const segment = new TreeSegmentData(person.id);
        mergeToSegments(segment, segmentsToAdd);
      }
      setSegments(segmentsToAdd);
    } catch (error) {
      if (retryCount < 10) {
        console.error('Failed to fetch partnerships. Retrying...');
        await new Promise((resolve) => setTimeout(resolve, 4000));
        await fetchPartnershipsAndBuildTreeSegments(retryCount + 1);
      } else {
        console.error('Failed to fetch partnerships after 10 retries.');
      }
    }
  }

  const load = async () => {
    await fetchPartnershipsAndBuildTreeSegments();
    setIsLoading(false);
  }

  const putPersonListener = async (personId: string) => {
    const person = await getPerson(personId);
    if (!person) return;

    setPeople((prevPeople) => new Map([...prevPeople, [person.id, person]]));
    setSegments((prevSegments) => {
      const segment = new TreeSegmentData(person.id);
      const segmentsCopy = [...prevSegments];
      mergeToSegments(segment, segmentsCopy);
      return segmentsCopy;
    });
  };

  const putPartnerListener = async (partnershipId: string) => {
    const partnership = await getPartnership(partnershipId);
    if (!partnership) return;

    for (const partner of partnership.partners) {
      setPeople((prevPeople) => new Map([...prevPeople, [partner.id, partner]]));
    }

    setPartnerships((prevPartnerships) => new Map([...prevPartnerships, [partnership.id, partnership]]));
    setSegments((prevSegments) => {
      const segment = buildTreeSegment(partnership);
      const segmentsCopy = [...prevSegments];
      mergeToSegments(segment, segmentsCopy);
      return segmentsCopy;
    });
  };

  useEffect(() => {
    load();
  }, []);

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
      onWheel={handleWheel}
      onMouseDown={handleMouseDown}
      onMouseMove={handleMouseMove}
      onMouseUp={handleMouseUp}
    >
      <div
        className="zoomable"
        style={{ transform: `scale(${zoom}) translate(${posX}px, ${posY}px)` }}
      >
        {isLoading && (
          <div>
            <BounceLoader color={'#123abc'} loading={isLoading} size={150} />
            <p>Loading Family Tree...</p>
          </div>
        )}
        {!isLoading && segments.length > 0 && segments.map((segment) => (
          <TreeSegment
            key={segment.personId}
            data={segment}
            people={people}
            partnerships={partnerships}
            treePathIds={treePathIds}
            onPersonLeftClick={handlePersonClick}
            onPersonRightClick={handlePersonRightClick}
            onPartnershipLeftClick={handlePartnershipClick}
            onPartnershipRightClick={handlePartnershipRightClick}
          />
        ))}
      </div>
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
  )
    ;
}

export default HomePage;
