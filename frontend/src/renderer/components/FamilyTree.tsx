import TreeSegmentData from '../models/TreeSegmentData';
import PersonNode from './PersonNode';
import Person from '../models/Person';
import PartnershipData from '../models/PartnershipData';
import TreeSegment from './TreeSegment';

interface TreeSegmentProps {
  data: TreeSegmentData;
  people: Map<string, Person>
  partnerships: Map<string, PartnershipData>
  treePathIds: string[];
  onPersonLeftClick: (event: React.MouseEvent, person: Person) => void;
  onPersonRightClick: (event: React.MouseEvent, person: Person) => void;
  onPartnershipLeftClick: (event: React.MouseEvent, partnership: PartnershipData) => void;
  onPartnershipRightClick: (event: React.MouseEvent, partnership: PartnershipData) => void;
}

function FamilyTree({ data, people, partnerships, treePathIds, onPersonLeftClick, onPersonRightClick, onPartnershipLeftClick, onPartnershipRightClick }: TreeSegmentProps) {
  return (
    <div>
      <TreeSegment
        data={data}
        people={people}
        partnerships={partnerships}
        treePathIds={treePathIds}
        gapWidth={20}
        onPersonLeftClick={onPersonLeftClick}
        onPersonRightClick={onPersonRightClick}
        onPartnershipLeftClick={onPartnershipLeftClick}
        onPartnershipRightClick={onPartnershipRightClick}>
        <PersonNode
          key={data.personId}
          person={people.get(data.personId)}
          treePathIds={treePathIds}
          onLeftClick={onPersonLeftClick}
          onRightClick={onPersonRightClick} />
        {data.partnerships.map((partnership) => (
          <PersonNode
            key={partnership.partner.personId}
            person={people.get(partnership.partner.personId)}
            treePathIds={treePathIds}
            onLeftClick={onPersonLeftClick}
            onRightClick={onPersonRightClick} />
        ))}
      </TreeSegment>
    </div>
  );
}

export default FamilyTree;
