import TreeSegmentData from '../models/TreeSegmentData';
import React from 'react';
import PersonNode from './PersonNode';
import PartnerNode from './PartnerNode';
import PartnershipChain from './PartnershipChain';
import Person from '../models/Person';

interface TreeSegmentProps {
  data: TreeSegmentData;
  onPersonClick: (event: React.MouseEvent, person: Person) => void;
  onPersonContextMenu: (event: React.MouseEvent, person: Person) => void;
}

function TreeSegment({ data, onPersonClick, onPersonContextMenu }: TreeSegmentProps) {
  // const [children, setChildren] = useState<TreeSegmentData[]>([]);

  return (
    <PartnershipChain nodeGap={20}>
      <PersonNode person={data.person} onClick={onPersonClick} onContextMenu={onPersonContextMenu} />
      {data.partnerships.map((partnership) => (
        <PartnerNode key={partnership.partner.id} person={partnership.partner} partnership={partnership.value} onClick={onPersonClick}
                     onContextMenu={onPersonContextMenu} />
      ))}
    </PartnershipChain>
  );
}

export default TreeSegment;
