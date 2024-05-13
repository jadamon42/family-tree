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
  const parentChainRef = useRef(null);
  const pathsRef = useRef(null);
  const childrenChainRef = useRef(null);
  const [nodeWidth, setNodeWidth] = useState(0);
  const [percentagesInForLineDownFromPartnership, setPercentagesInForLineDownFromPartnership] = useState<number[]>([]);
  const [percentagesInForLineUpFromChildren, setPercentagesInForLineUpFromChildren] = useState<number[]>([]);

  useEffect(() => {
    if (parentChainRef) {
      const firstNode = parentChainRef.current.firstChild;
      const firstNodeWidth = firstNode.offsetWidth;

      pathsRef.current.style.paddingLeft = `${firstNodeWidth / 2}px`;
      pathsRef.current.style.paddingRight = `${firstNodeWidth / 2}px`;
      pathsRef.current.style.height = `${firstNodeWidth / 2}px`;
      setNodeWidth(firstNodeWidth);
    }
  }, [children]);

  useEffect(() => {
    const percentagesInForLineDownFromPartnership: number[] = [];
    const percentagesInForLineUpFromChildren: number[] = [];
    const parentWidth = parentChainRef.current.offsetWidth;
    const childrenWidth = childrenChainRef.current.offsetWidth;
    const marginWidth = (childrenWidth - parentWidth) / 2;
    const borderPercent = (2 / childrenWidth) * 100;

    let lengthInOnParent = marginWidth;
    let lengthInOnChildren = 0;
    for (let i = 0; i < data.partnerships.length; i++) {
      lengthInOnParent += nodeWidth + (gap / 2);
      percentagesInForLineDownFromPartnership.push((lengthInOnParent / childrenWidth) * 100);
      lengthInOnParent += (gap / 2);

      const partnership = data.partnerships[i];
      const children = partnership.children;
      let childSegmentWidth = gap / 2;
      for (let j = 0; j < children.length; j++) {
        childSegmentWidth += nodeWidth;
        if (j !== children.length - 1) {
          for (let k = 0; k < children[j].partnerships.length; k++) {
            childSegmentWidth += gap + nodeWidth;
          }
          childSegmentWidth += gap;
        }
      }
      childSegmentWidth += gap / 2;
      percentagesInForLineUpFromChildren.push(((lengthInOnChildren + (childSegmentWidth / 2)) / childrenWidth) * 100);
      lengthInOnChildren += childSegmentWidth;
    }

    setPercentagesInForLineUpFromChildren(percentagesInForLineDownFromPartnership);
    setPercentagesInForLineDownFromPartnership(percentagesInForLineUpFromChildren);

  }, [JSON.stringify(data), nodeWidth, gap]);

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      // gap: gap * 3,
    }}>
      <div className="partnership-chain">
        <div ref={parentChainRef} className="person-nodes" style={{ gap: gap }}>
          {children}
        </div>
        <div ref={pathsRef} className="partnership-paths">
          { data.partnerships.map((partnership, i) => (
            <div>
              <PartnershipPath
                key={partnership.valueId}
                data={partnership}
                partnerships={partnerships}
                nodeWidth={nodeWidth}
                gapWidth={gap}
                index={i}
                isFocused={treePathIds.includes(partnership.valueId)}
                onLeftClick={onPartnershipLeftClick}
                onRightClick={onPartnershipRightClick} />
            </div>
          ))}
        </div>
      </div>
      <div ref={childrenChainRef} className="children-chain"
           style={{ alignItems: 'flex-start', justifyContent: 'center', position: 'relative', margin: 'auto' }}>
        {data.partnerships.map((partnership, i) => (
          <div>
            {partnership.children.length > 0 &&
              <div>
                <div style={{
                  position: 'absolute',
                  left: 50 - percentagesInForLineDownFromPartnership[i] > 0 ? `${percentagesInForLineDownFromPartnership[i]}%` : `${percentagesInForLineUpFromChildren[i]}%`,
                  width:  `${Math.abs(percentagesInForLineUpFromChildren[i] - percentagesInForLineDownFromPartnership[i])}%`,
                  height: gap,
                  borderRight: 50 - percentagesInForLineDownFromPartnership[i] > 0 ? '2px solid black' : 'none',
                  borderLeft: 50 - percentagesInForLineDownFromPartnership[i] > 0 ? 'none' : '2px solid black',
                  borderBottomRightRadius: 50 - percentagesInForLineDownFromPartnership[i] > 0 ? '10px' : '0',
                  borderBottomLeftRadius: 50 - percentagesInForLineDownFromPartnership[i] > 0 || percentagesInForLineDownFromPartnership[i] - percentagesInForLineUpFromChildren[i] === 0 ? '0' : '10px',
                  borderBottom: '2px solid black',
                }} />
                <div style={{
                  position: 'absolute',
                  marginTop: gap,
                  height: gap,
                  left: `${percentagesInForLineDownFromPartnership[i]}%`,
                  borderLeft: 50 - percentagesInForLineDownFromPartnership[i] > 0 || percentagesInForLineDownFromPartnership[i] - percentagesInForLineUpFromChildren[i] === 0 ? '2px solid black' : 'none',
                  borderRight: 50 - percentagesInForLineDownFromPartnership[i] > 0 || percentagesInForLineDownFromPartnership[i] - percentagesInForLineUpFromChildren[i] === 0 ? 'none' : '2px solid black',
                }} />
              </div>}
            {partnership.children.length > 0 &&
            <div style={{ marginTop: `${gap * 2}px` }}>
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
            </div> }
          </div>
        ))}
      </div>
    </div>
  );
}

export default TreeSegment;
