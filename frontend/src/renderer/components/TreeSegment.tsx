import TreeSegmentData from '../models/TreeSegmentData';
import { useEffect, useRef, useState } from 'react';
import PersonNode from './PersonNode';
import Person from '../models/Person';
import PartnershipData from '../models/PartnershipData';
import PartnershipConnection from './PartnershipConnection';
import '../styles/TreeSegment.css';

interface TreeSegmentProps {
  data: TreeSegmentData;
  people: Map<string, Person>
  partnerships: Map<string, PartnershipData>
  onPersonLeftClick: (event: React.MouseEvent, person: Person) => void;
  onPersonRightClick: (event: React.MouseEvent, person: Person) => void;
}

function TreeSegment({ data, people, partnerships, onPersonLeftClick, onPersonRightClick }: TreeSegmentProps) {
  const nodeRef = useRef<HTMLDivElement>(null);
  const [nodeRect, setNodeRect] = useState<DOMRect>(null);

  useEffect(() => {
    if (nodeRef.current) {
      setNodeRect(nodeRef.current.getBoundingClientRect());
    }
  }, [people, partnerships]);

  return (
    <div className="segment">
      <div ref={nodeRef}>
        <PersonNode person={people.get(data.personId)} onLeftClick={onPersonLeftClick} onRightClick={onPersonRightClick} />
      </div>
      {data.partnerships.map((partnership) => (
        <PartnershipConnection
          key={partnership.valueId}
          data={partnership}
          mainNodeRect={nodeRect}
          people={people}
          partnerships={partnerships}
          onPersonLeftClick={onPersonLeftClick}
          onPersonRightClick={onPersonRightClick}
        />
      ))}
    </div>
  );
}

export default TreeSegment;
