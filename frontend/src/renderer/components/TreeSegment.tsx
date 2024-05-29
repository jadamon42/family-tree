import React, { useEffect, useRef, useState } from 'react';
import TreeSegmentData from '../models/TreeSegmentData';
import '../styles/TreeSegment.css';
import PartnershipPath from './PartnershipPath';
import PartnershipData from '../models/PartnershipData';
import Person from '../models/Person';
import PartnershipChildren from './PartnershipChildren';
import ChildPathSpacingCalculator from '../utils/ChildPathSpacingCalculator';

interface PartnershipChainProps {
  children: React.ReactNode;
  data: TreeSegmentData;
  people: Map<string, Person>;
  partnerships: Map<string, PartnershipData>;
  treePathIds: string[];
  gapWidth: number;
  onPersonLeftClick: (event: React.MouseEvent, person: Person) => void;
  onPersonRightClick: (event: React.MouseEvent, person: Person) => void;
  onPartnershipLeftClick: (event: React.MouseEvent, partnership: PartnershipData) => void;
  onPartnershipRightClick: (event: React.MouseEvent, partnership: PartnershipData) => void;
}

function TreeSegment({ children, data, people, partnerships, treePathIds, gapWidth, onPersonLeftClick, onPersonRightClick, onPartnershipLeftClick, onPartnershipRightClick }: PartnershipChainProps) {
  const parentChainRef = useRef(null);
  const pathsRef = useRef(null);
  const childrenChainRef = useRef(null);
  const [childrenRefs, setChildrenRefs] = useState(data.partnerships.map(() => React.createRef<HTMLDivElement>()));
  const [nodeWidth, setNodeWidth] = useState(0);
  const [percentagesInForLineUpFromChildren, setPercentagesInForLineUpFromChildren] = useState<number[]>([]);
  const [percentagesInForLineDownFromPartnership, setPercentagesInForLineDownFromPartnership] = useState<number[]>([]);

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
    setChildrenRefs(data.partnerships.map(() => React.createRef<HTMLDivElement>()));
  }, [JSON.stringify(data.partnerships)]);

  useEffect(() => {
    if (!childrenChainRef.current) return;

    const percentagesInForLineDownFromPartnership: number[] = [];
    const percentagesInForLineUpFromChildren: number[] = [];
    const parentWidth = parentChainRef.current.offsetWidth;
    const childrenWidth = childrenChainRef.current.offsetWidth;
    const marginWidth = (childrenWidth - parentWidth) / 2;

    let lengthInOnParent = marginWidth;
    let lengthInOnChildren = 0;
    for (let i = 0; i < data.partnerships.length; i++) {
      if (!childrenRefs || !childrenRefs[i] || !childrenRefs[i].current) continue;
      lengthInOnParent += nodeWidth + (gapWidth / 2);
      percentagesInForLineDownFromPartnership.push((lengthInOnParent / childrenWidth) * 100);
      lengthInOnParent += (gapWidth / 2);

      const childrenChainComponent: HTMLDivElement = childrenRefs[i].current;
      const partnership = data.partnerships[i];

      const {
        percentageToCenterOfChildChain
      } = ChildPathSpacingCalculator.getChildChainSpacing(childrenChainComponent, partnership, nodeWidth, gapWidth);

      const ratio = childrenChainComponent.offsetWidth / childrenChainRef.current.offsetWidth
      const percentageToCenterOfChildChainRelativeToChildren = percentageToCenterOfChildChain * ratio;

      percentagesInForLineUpFromChildren.push(lengthInOnChildren + percentageToCenterOfChildChainRelativeToChildren);

      lengthInOnChildren += gapWidth / childrenChainRef.current.offsetWidth * 100;
      lengthInOnChildren += ratio * 100;
    }

    setPercentagesInForLineDownFromPartnership(percentagesInForLineDownFromPartnership);
    setPercentagesInForLineUpFromChildren(percentagesInForLineUpFromChildren);

  }, [JSON.stringify(data), nodeWidth, gapWidth, childrenRefs]);

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
    }}>
      <div className="partnership-chain">
        <div ref={parentChainRef} className="person-nodes" style={{ gap: gapWidth }}>
          {children}
        </div>
        <div ref={pathsRef} className="partnership-paths">
          { data.partnerships.map((partnership, i) => (
            <PartnershipPath
              key={partnership.valueId}
              data={partnership}
              partnerships={partnerships}
              nodeWidth={nodeWidth}
              gapWidth={gapWidth}
              index={i}
              isFocused={treePathIds.includes(partnership.valueId)}
              onLeftClick={onPartnershipLeftClick}
              onRightClick={onPartnershipRightClick} />
          ))}
        </div>
      </div>
      <div ref={childrenChainRef} className="children-chain"
           style={{ alignItems: 'flex-start', justifyContent: 'center', position: 'relative', margin: 'auto', gap: gapWidth }}>
        {data.partnerships.map((partnership, i) => {
            if (partnership.children.length === 0) return;
            return <div  key={`${partnership.valueId}-parent`}>
              <div  key={`${partnership.valueId}-child-line-parent`}>
                <div key={`${partnership.valueId}-child-line`} style={{
                  position: 'absolute',
                  left: 50 - percentagesInForLineUpFromChildren[i] > 0 ? `${percentagesInForLineUpFromChildren[i]}%` : `${percentagesInForLineDownFromPartnership[i]}%`,
                  width:  `${Math.abs(percentagesInForLineDownFromPartnership[i] - percentagesInForLineUpFromChildren[i])}%`,
                  height: gapWidth * 2,
                }}>
                  <div key={`${partnership.valueId}-child-line-1`}  style={{
                    position: 'absolute',
                    left: 50 - percentagesInForLineUpFromChildren[i] > 0 ? '50%' : '0',
                    width: '50%',
                    height: '50%',
                    borderBottom: treePathIds.includes(partnership.valueId) ? '2px solid green' : '2px solid black',
                    borderRight: 50 - percentagesInForLineUpFromChildren[i] > 0 ? (treePathIds.includes(partnership.valueId) ? '2px solid green' : '2px solid black') : 'none',
                    borderLeft: 50 - percentagesInForLineUpFromChildren[i] > 0 ? 'none' : (treePathIds.includes(partnership.valueId) ? '2px solid green' : '2px solid black'),
                    borderBottomRightRadius: 50 - percentagesInForLineUpFromChildren[i] > 0 ? '10px' : '0',
                    borderBottomLeftRadius: 50 - percentagesInForLineUpFromChildren[i] > 0 ? '0' : '10px',
                  }}/>
                  <div key={`${partnership.valueId}-child-line-2`}  style={{
                    position: 'absolute',
                    top: '50%',
                    left: 50 - percentagesInForLineUpFromChildren[i] > 0 ? '0' : '50%',
                    width: '50%',
                    height: '50%',
                    borderTop: treePathIds.includes(partnership.valueId) ? '2px solid green' : '2px solid black',
                    borderLeft: 50 - percentagesInForLineUpFromChildren[i] > 0 ? (treePathIds.includes(partnership.valueId) ? '2px solid green' : '2px solid black') : 'none',
                    borderRight: 50 - percentagesInForLineUpFromChildren[i] > 0 ? 'none' : (treePathIds.includes(partnership.valueId) ? '2px solid green' : '2px solid black'),
                    borderTopRightRadius: 50 - percentagesInForLineUpFromChildren[i] > 0 ? '0' : '10px',
                    borderTopLeftRadius: 50 - percentagesInForLineUpFromChildren[i] > 0 ? '10px' : '0',
                  }}/>
                </div>
              </div>
              <div style={{ marginTop: `${gapWidth * 2}px` }}>
                <PartnershipChildren
                  ref={childrenRefs[i]}
                  key={`${partnership.valueId}-children`}
                  data={partnership}
                  people={people}
                  partnerships={partnerships}
                  treePathIds={treePathIds}
                  nodeWidth={nodeWidth}
                  gapWidth={gapWidth}
                  onPersonLeftClick={onPersonLeftClick}
                  onPersonRightClick={onPersonRightClick}
                  onPartnershipLeftClick={onPartnershipLeftClick}
                  onPartnershipRightClick={onPartnershipRightClick} />
              </div>
            </div>})}
      </div>
    </div>
  );
}

export default TreeSegment;
