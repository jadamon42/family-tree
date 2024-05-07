import { useEffect, useRef } from 'react';
import TreeSegmentPartnershipData from '../models/TreeSegmentPartnershipData';
import Person from '../models/Person';
import PartnershipData from '../models/PartnershipData';
import PersonNode from './PersonNode';
import '../styles/PartnershipConnection.css';

interface PartnershipConnectionProps {
  data: TreeSegmentPartnershipData;
  mainNodeRect: DOMRect;
  people: Map<string, Person>
  partnerships: Map<string, PartnershipData>
  onPersonLeftClick: (event: React.MouseEvent, person: Person) => void;
  onPersonRightClick: (event: React.MouseEvent, person: Person) => void;
}

function PartnershipConnection({ data, mainNodeRect, people, partnerships, onPersonLeftClick, onPersonRightClick }: PartnershipConnectionProps) {
  const partnerNodeRef = useRef<HTMLDivElement>(null);
  const boxRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!partnerNodeRef.current || !boxRef.current || !mainNodeRect) return;

    // Calculate positions
    const partnerNodeRect = partnerNodeRef.current.getBoundingClientRect();

    // Set box position and size
    const box = boxRef.current;
    const mainNodeCenterX = mainNodeRect.x + mainNodeRect.width / 2;
    const mainNodeCenterY = mainNodeRect.y + mainNodeRect.height;
    const partnerNodeCenterX = partnerNodeRect.x + partnerNodeRect.width / 2;

    const boxWidth = Math.abs(partnerNodeCenterX - mainNodeCenterX);

    box.style.width = `${boxWidth}px`;
    box.style.height = `${mainNodeRect.height / 2}px`;
    box.style.top = `${mainNodeCenterY}px`;
    box.style.left = `${mainNodeCenterX}px`;
  }, [mainNodeRect, people, data]);

  return (
    <div>
      <div ref={partnerNodeRef}>
        <PersonNode person={people.get(data.partner.personId)} onLeftClick={onPersonLeftClick} onRightClick={onPersonRightClick} />
      </div>
      <div ref={boxRef} className="connection-line" />
    </div>
  );
}

export default PartnershipConnection;
