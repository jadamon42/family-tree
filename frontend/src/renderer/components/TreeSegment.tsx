import TreeSegmentData from '../models/TreeSegmentData';
import PersonNode from './PersonNode';
import Person from '../models/Person';
import PartnershipData from '../models/PartnershipData';
import PartnershipChain from './PartnershipChain';

interface TreeSegmentProps {
  data: TreeSegmentData;
  people: Map<string, Person>
  partnerships: Map<string, PartnershipData>
  onPersonLeftClick: (event: React.MouseEvent, person: Person) => void;
  onPersonRightClick: (event: React.MouseEvent, person: Person) => void;
}

function TreeSegment({ data, people, partnerships, onPersonLeftClick, onPersonRightClick }: TreeSegmentProps) {
  return (
    <div>
      <PartnershipChain data={data} partnerships={partnerships} gap={20}>
        <PersonNode
          key={data.personId}
          person={people.get(data.personId)}
          onLeftClick={onPersonLeftClick}
          onRightClick={onPersonRightClick} />
        {data.partnerships.map((partnership) => (
          <PersonNode
            key={partnership.partner.personId}
            person={people.get(partnership.partner.personId)}
            onLeftClick={onPersonLeftClick}
            onRightClick={onPersonRightClick} />
        ))}
      </PartnershipChain>
      {/* add child segments */}
    </div>
  );
}

export default TreeSegment;
