import TreeSegmentPartnershipData from '../models/TreeSegmentPartnershipData';
import React from 'react';
import PartnershipData from '../models/PartnershipData';
import '../styles/Path.css';

interface PathProps {
  data: TreeSegmentPartnershipData;
  partnerships: Map<string, PartnershipData>;
  nodeWidthPercentage: number;
  gapWidthPercentage: number;
  index: number;
  isFocused: boolean;
  onLeftClick: (event: React.MouseEvent, partnership: PartnershipData) => void;
  onRightClick: (event: React.MouseEvent, partnership: PartnershipData) => void;
}

function Path({ data, partnerships, nodeWidthPercentage, gapWidthPercentage, index, isFocused, onLeftClick, onRightClick }: PathProps) {
  const pathRef = React.useRef(null);
  const clickableRef = React.useRef(null);

  const handleMouseMove = (event: React.MouseEvent) => {
    if (isOnBorder(event)) {
      clickableRef.current.style.cursor = 'pointer';
    } else {
      clickableRef.current.style.cursor = 'default';
    }
  }

  const handleClick = (event: React.MouseEvent) => {
    if (isOnBorder(event)) {
      onLeftClick(event, partnerships.get(data.valueId));
    }
  }

  const isOnBorder = (event: React.MouseEvent) => {
    const clickableRect = (clickableRef.current as Element).getBoundingClientRect();
    const pathRect = (pathRef.current as Element).getBoundingClientRect();

    const bottomDifference = clickableRect.bottom - pathRect.bottom;
    const rightDifference = clickableRect.right - pathRect.right;

    return event.clientX >= pathRect.right - rightDifference
      || event.clientY >= pathRect.bottom - (bottomDifference * 2);
  }

  return (
      <div>
      <div
        ref={clickableRef} className="clickable-portion"
        style={{
          width: `${nodeWidthPercentage + gapWidthPercentage}%`,
          left: `${(nodeWidthPercentage + gapWidthPercentage) * (index + 0.5)}%`
        }}
       onClick={handleClick}
       onMouseMove={handleMouseMove}/>
      <div
        ref={pathRef} key={data.valueId} className="path"
        style={{
          width: `${(nodeWidthPercentage + gapWidthPercentage) * (index + 1)}%`,
          height: '100%',
          borderLeft: isFocused ? '2px solid green' : '2px solid black',
          borderRight: isFocused ? '2px solid green' : '2px solid black',
          borderBottom: isFocused ? '2px solid green' : '2px solid black',
          borderTop: 'none',
           pointerEvents: 'none',
          zIndex: isFocused ? 100 : 1,
        }} />
    </div>
  );
}

export default Path;
