import React, { useEffect, useRef, useState } from 'react';
import TreeSegmentData from '../models/TreeSegmentData';
import '../styles/PartnershipChain.css';
import Path from './Path';
import PartnershipData from '../models/PartnershipData';

interface PartnershipChainProps {
  children: React.ReactNode;
  data: TreeSegmentData;
  partnerships: Map<string, PartnershipData>;
  gap: number;
}

function PartnershipChain({ children, data, partnerships, gap }: PartnershipChainProps) {
  const nodesRef = useRef(null);
  const pathsRef = useRef(null);
  const [nodeWidth, setNodeWidth] = useState(0);
  const [gapWidth, setGapWidth] = useState(0);

  useEffect(() => {
    if (nodesRef) {
      const firstNode = nodesRef.current.firstChild;
      const firstNodeWidth = firstNode.offsetWidth;
      const nodesWidth = nodesRef.current.offsetWidth;
      const firstNodeWidthPercentage = (firstNodeWidth / nodesWidth) * 100;
      const gapWidthPercentage = (gap / nodesWidth) * 100;

      pathsRef.current.style.paddingLeft = `${firstNodeWidthPercentage / 2}%`;
      pathsRef.current.style.paddingRight = `${firstNodeWidthPercentage / 2}%`;
      pathsRef.current.style.height = `${firstNodeWidth / 2}px`;
      setNodeWidth(firstNodeWidthPercentage);
      setGapWidth(gapWidthPercentage);
    }
  }, [children]);

  return (
    <div className="partnership-chain">
      <div ref={nodesRef} className="person-nodes" style={{ gap: gap }}>
        {children}
      </div>
      <div ref={pathsRef} className="partnership-paths">
        { data.partnerships.map((partnership, i) => (
          <Path key={partnership.valueId} data={partnership} partnerships={partnerships} width={`${(nodeWidth + gapWidth) * (i + 1)}%`} />
        ))}
      </div>
    </div>
  );
}

export default PartnershipChain;
