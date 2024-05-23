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
  const parentRef = useRef(null);
  const [percentagesToCenterOfChildNode, setPercentagesToCenterOfChildNode] = useState<number[]>([]);
  const [percentageToCenterOfChildren, setPercentageToCenterOfChildren] = useState<number>(null);

  useEffect(() => {
    if (!parentRef.current) return;

    const percentages: number[] = [];
    const parentWidth = parentRef.current.offsetWidth;
    let pixelsInOnCumulativeChildTrees = 0;
    let pixelsToCenterOfCumulativeChildTrees = 0;
    for (let i = 0; i < data.children.length; i++) {
      const componentWidth = parentRef.current.children[i].offsetWidth;
      const childData = data.children[i];
      const pixelsToCenterOfChildOnPartnershipChain = nodeWidth / 2; // center of the first node
      let pixelsRemainingOnChildPartnershipChain = nodeWidth / 2
      for (let j = 0; j < childData.partnerships.length; j++) {
        pixelsRemainingOnChildPartnershipChain += gapWidth + nodeWidth;
      }

      let totalPartnershipChainWidth = pixelsToCenterOfChildOnPartnershipChain + pixelsRemainingOnChildPartnershipChain;
      const componentWhiteSpace = (componentWidth - totalPartnershipChainWidth) / 2;
      pixelsInOnCumulativeChildTrees += componentWhiteSpace + pixelsToCenterOfChildOnPartnershipChain
      percentages.push(pixelsInOnCumulativeChildTrees / parentWidth * 100);
      if (i !== data.children.length - 1) { // if not the last child
        pixelsRemainingOnChildPartnershipChain += gapWidth;
        totalPartnershipChainWidth += gapWidth;
      }
      pixelsInOnCumulativeChildTrees += pixelsRemainingOnChildPartnershipChain + componentWhiteSpace;
      pixelsToCenterOfCumulativeChildTrees += componentWhiteSpace + (totalPartnershipChainWidth / 2);
    }
    setPercentagesToCenterOfChildNode(percentages);
    setPercentageToCenterOfChildren((pixelsToCenterOfCumulativeChildTrees / parentWidth) * 100);
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
              borderLeft: 50 - percentagesToCenterOfChildNode[i] > 0 ? '2px solid black' : 'none',
              borderRight: 50 - percentagesToCenterOfChildNode[i] > 0 ? 'none' : '2px solid black',
              borderTopLeftRadius: 50 - percentagesToCenterOfChildNode[i] > 0 && (i === 0 || i === data.children.length - 1) ? '10px' : '0',
              borderTopRightRadius: 50 - percentagesToCenterOfChildNode[i] > 0 || data.children.length === 1 || (i > 0 && i < data.children.length - 1) ? '0' : '10px',
              borderTop: '2px solid black',
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
}

export default PartnershipChildren;
