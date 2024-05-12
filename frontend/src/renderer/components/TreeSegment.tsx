import React, { useEffect, useRef, useState } from 'react';
import TreeSegmentData from '../models/TreeSegmentData';
import '../styles/TreeSegment.css';
import PartnershipPath from './PartnershipPath';
import PartnershipData from '../models/PartnershipData';
import Person from '../models/Person';
import PartnershipChildren from './PartnershipChildren';

interface PartnershipChainProps {
  children: React.ReactNode;
  data: TreeSegmentData;
  people: Map<string, Person>;
  partnerships: Map<string, PartnershipData>;
  treePathIds: string[];
  gap: number;
  onPersonLeftClick: (event: React.MouseEvent, person: Person) => void;
  onPersonRightClick: (event: React.MouseEvent, person: Person) => void;
  onPartnershipLeftClick: (event: React.MouseEvent, partnership: PartnershipData) => void;
  onPartnershipRightClick: (event: React.MouseEvent, partnership: PartnershipData) => void;
}

function TreeSegment({ children, data, people, partnerships, treePathIds, gap, onPersonLeftClick, onPersonRightClick, onPartnershipLeftClick, onPartnershipRightClick }: PartnershipChainProps) {
  const nodesRef = useRef(null);
  const pathsRef = useRef(null);
  const [nodeWidth, setNodeWidth] = useState(0);
  const [gapWidth, setGapWidth] = useState(0);

  useEffect(() => {
    if (nodesRef) {
      const firstNode = nodesRef.current.firstChild;
      const firstNodeWidth = firstNode.offsetWidth;

      pathsRef.current.style.paddingLeft = `${firstNodeWidth / 2}px`;
      pathsRef.current.style.paddingRight = `${firstNodeWidth / 2}px`;
      pathsRef.current.style.height = `${firstNodeWidth / 2}px`;
      setNodeWidth(firstNodeWidth);
    }
  }, [children]);

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      // gap: gap * 3,
    }}>
      <div className="partnership-chain">
        <div ref={nodesRef} className="person-nodes" style={{ gap: gap }}>
          {children}
        </div>
        <div ref={pathsRef} className="partnership-paths">
          { data.partnerships.map((partnership, i) => (
            <PartnershipPath
              key={partnership.valueId}
              data={partnership}
              partnerships={partnerships}
              nodeWidth={nodeWidth}
              gapWidth={gap}
              index={i}
              isFocused={treePathIds.includes(partnership.valueId)}
              onLeftClick={onPartnershipLeftClick}
              onRightClick={onPartnershipRightClick}/>
            ))}
        </div>
      </div>
      {/*<div className="lineage-paths">*/}
      {/*  { data.partnerships.map((partnership, i) => (*/}
      {/*    <div/>*/}
      {/*    /!* LineagePath needs to be allowed to be completely unaligned with the parent  *!/*/}
      {/*  ))}*/}
      {/*</div>*/}
      {/*<div>*/}
      {/*  { data.partnerships.map( (child, i) => (*/}
      {/*    <div/>*/}
      {/*  ))}*/}
      {/*</div>*/}
      <div className="children-chain" style={{alignItems: 'flex-start', justifyContent: 'center'}}>
        { data.partnerships.map( (partnership, i) => (
          <PartnershipChildren
            key={`${partnership.valueId}-children`}
            data={partnership}
            people={people}
            partnerships={partnerships}
            treePathIds={treePathIds}
            nodeWidth={nodeWidth}
            gapWidth={gap}
            onPersonLeftClick={onPersonLeftClick}
            onPersonRightClick={onPersonRightClick}
            onPartnershipLeftClick={onPartnershipLeftClick}
            onPartnershipRightClick={onPartnershipRightClick} />
        ))}
      </div>
    </div>
  );
}

export default TreeSegment;
