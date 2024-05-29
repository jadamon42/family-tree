import TreeSegmentPartnershipData from '../models/TreeSegmentPartnershipData';
import FamilyTree from './FamilyTree';
import React, { useEffect, useImperativeHandle, useRef, useState } from 'react';
import Person from '../models/Person';
import PartnershipData from '../models/PartnershipData';
import ChildPathSpacingCalculator from '../utils/ChildPathSpacingCalculator';

interface PartnershipChildrenProps {
  data: TreeSegmentPartnershipData;
  people: Map<string, Person>;
  partnerships: Map<string, PartnershipData>;
  treePathIds: string[];
  nodeWidth: number;
  gapWidth: number;
  onPersonLeftClick: (event: React.MouseEvent, person: Person) => void;
  onPersonRightClick: (event: React.MouseEvent, person: Person) => void;
  onPartnershipLeftClick: (event: React.MouseEvent, partnership: PartnershipData) => void;
  onPartnershipRightClick: (event: React.MouseEvent, partnership: PartnershipData) => void;
}

const PartnershipChildren = React.forwardRef((props: PartnershipChildrenProps, ref: React.Ref<HTMLDivElement>) => {
  const {
    data,
    people,
    partnerships,
    treePathIds,
    nodeWidth,
    gapWidth,
    onPersonLeftClick,
    onPersonRightClick,
    onPartnershipLeftClick,
    onPartnershipRightClick
  } = props;
  const parentRef = useRef<HTMLDivElement>(null);
  const [percentagesToCenterOfChildNode, setPercentagesToCenterOfChildNode] = useState<number[]>([]);
  const [percentageToCenterOfChildren, setPercentageToCenterOfChildren] = useState<number>(null);

  useImperativeHandle(ref, () => parentRef.current);

  useEffect(() => {
    if (!parentRef.current) return;

    const {
      percentagesToChildrenNodes,
      percentageToCenterOfChildChain
    } = ChildPathSpacingCalculator.getChildChainSpacing(parentRef.current, data, nodeWidth, gapWidth);

    setPercentagesToCenterOfChildNode(percentagesToChildrenNodes);
    setPercentageToCenterOfChildren(percentageToCenterOfChildChain);
  }, [JSON.stringify(data.children), nodeWidth, gapWidth]);

  return (
    <div>
      <div ref={parentRef} style={{
        display: 'flex',
        flexDirection: 'row',
        gap: gapWidth,
        position: 'relative',
        justifyContent: 'center',
      }}>
        {data.children.map((child, i) => (
          <div key={`${data.valueId}-${child.personId}`} className="child-tree">
            <div style={{
              position: 'absolute',
              top: '0%',
              left: 50 - percentagesToCenterOfChildNode[i] > 0 ? `${percentagesToCenterOfChildNode[i]}%` : `${percentageToCenterOfChildren}%`,
              right : 50 - percentagesToCenterOfChildNode[i] > 0 ? `calc(${100 - percentageToCenterOfChildren}% - 2px)` : `calc(${100 - percentagesToCenterOfChildNode[i]}% - 2px)`,
              height: gapWidth,
              borderLeft: 50 - percentagesToCenterOfChildNode[i] > 0 ? (treePathIds.includes(data.valueId) && treePathIds.includes(child.personId) ? '2px solid green' : '2px solid black') : 'none',
              borderRight: 50 - percentagesToCenterOfChildNode[i] > 0 ? 'none' : (treePathIds.includes(data.valueId) && treePathIds.includes(child.personId) ? '2px solid green' : '2px solid black'),
              borderTopLeftRadius: 50 - percentagesToCenterOfChildNode[i] > 0 && (i === 0 || i === data.children.length - 1) ? '10px' : '0',
              borderTopRightRadius: 50 - percentagesToCenterOfChildNode[i] > 0 || data.children.length === 1 || (i > 0 && i < data.children.length - 1) ? '0' : '10px',
              borderTop: treePathIds.includes(data.valueId) && treePathIds.includes(child.personId) ? '2px solid green' : '2px solid black',
            }} />
            <div style={{
              paddingTop: gapWidth + 2
            }}>
              <FamilyTree
                data={child}
                people={people}
                partnerships={partnerships}
                treePathIds={treePathIds}
                onPersonLeftClick={onPersonLeftClick}
                onPersonRightClick={onPersonRightClick}
                onPartnershipLeftClick={onPartnershipLeftClick}
                onPartnershipRightClick={onPartnershipRightClick} />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
});

export default PartnershipChildren;
