import TreeSegmentPartnershipData from '../models/TreeSegmentPartnershipData';
import FamilyTree from './FamilyTree';
import React, { useEffect, useRef, useState } from 'react';
import Person from '../models/Person';
import PartnershipData from '../models/PartnershipData';

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

function PartnershipChildren({ data, people, partnerships, treePathIds, nodeWidth, gapWidth, onPersonLeftClick, onPersonRightClick, onPartnershipLeftClick, onPartnershipRightClick }: PartnershipChildrenProps) {
  // const nodesRef = useRef(null);
  //
  // useEffect(() => {
  //   if (nodesRef) {
  //     const firstNode = nodesRef.current.firstChild;
  //     const firstNodeWidth = firstNode.offsetWidth;
  //
  //     pathsRef.current.style.paddingLeft = `${firstNodeWidth / 2}px`;
  //     pathsRef.current.style.paddingRight = `${firstNodeWidth / 2}px`;
  //     pathsRef.current.style.height = `${firstNodeWidth / 2}px`;
  //     setNodeWidth(firstNodeWidth);
  //   }
  // }, [children]);


  /*
 first family tree left edge is 10% in from the parent's left edge
 last family tree left edge is 90% in from the parent's left edge

 50 - 10 -> positive so left: 10% right: 50%
 50 - 90 -> negative so left: 50% right: 90%

   */

  const parentRef = useRef(null);
  const [percentagesToCenterOfChildNode, setPercentagesToCenterOfChildNode] = useState<number[]>([]);
  const [percentageToCenterOfChildren, setPercentageToCenterOfChildren] = useState<number>(null);

  useEffect(() => {
    if (!parentRef.current) return;

    const percentages: number[] = [];
    const parentWidth = parentRef.current.offsetWidth;
    let pixelsToCenterOfChild = 0;
    let pixelsToCenterOfChildren = 0;
    for (let i = 0; i < data.children.length; i++) {
      const childData = data.children[i];
      pixelsToCenterOfChild += nodeWidth / 2;
      percentages.push((pixelsToCenterOfChild / parentWidth) * 100);
      pixelsToCenterOfChildren  += nodeWidth / 2;
      if (i !== data.children.length - 1) {
        pixelsToCenterOfChild += (nodeWidth / 2) + gapWidth;
        for (let j = 0; j < childData.partnerships.length; j++) {
          pixelsToCenterOfChild += nodeWidth + gapWidth;
        }
        pixelsToCenterOfChildren += gapWidth / 2;
      }
    }
    setPercentagesToCenterOfChildNode(percentages);
    setPercentageToCenterOfChildren((pixelsToCenterOfChildren / parentWidth) * 100);
  }, [JSON.stringify(data.children), nodeWidth, gapWidth]);

  return (
    <div>
      <div ref={parentRef} style={{
        display: 'flex',
        flexDirection: 'row',
        gap: '20px',
        position: 'relative',
        justifyContent: 'center',
        marginRight: gapWidth / 2,
        marginLeft: gapWidth / 2
      }}>
        {data.children.map((child, i) => (
          <div key={`${data.valueId}-${child.personId}`} className="child-tree">
            <div style={{
              position: 'absolute',
              top: '0%',
              left: 50 - percentagesToCenterOfChildNode[i] > 0 ? `${percentagesToCenterOfChildNode[i]}%` : `${percentageToCenterOfChildren}%`,
              right : 50 - percentagesToCenterOfChildNode[i] > 0 ? `${100 - percentageToCenterOfChildren}%` : `${100 - percentagesToCenterOfChildNode[i]}%`,
              height: '20px',
              borderLeft: 50 - percentagesToCenterOfChildNode[i] > 0 ? '2px solid black' : 'none',
              borderRight: 50 - percentagesToCenterOfChildNode[i] > 0 ? 'none' : '2px solid black',
              borderTopLeftRadius: 50 - percentagesToCenterOfChildNode[i] > 0 && (i === 0 || i === data.children.length - 1) ? '10px' : '0',
              borderTopRightRadius: 50 - percentagesToCenterOfChildNode[i] > 0 || data.children.length === 1 || (i > 0 && i < data.children.length - 1) ? '0' : '10px',
              borderTop: '2px solid black',
            }} />
            <div style={{
              paddingTop: '22px' // same as height above + width of border
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
}

export default PartnershipChildren;
