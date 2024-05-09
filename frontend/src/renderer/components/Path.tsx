import TreeSegmentPartnershipData from '../models/TreeSegmentPartnershipData';
import React, { useEffect } from 'react';
import PartnershipData from '../models/PartnershipData';
import '../styles/Path.css';

interface PathProps {
  data: TreeSegmentPartnershipData;
  partnerships: Map<string, PartnershipData>;
  width: string | number;
  zIndex: number;
  isFocused: boolean;
  onLeftClick: (event: React.MouseEvent, partnership: PartnershipData) => void;
  onRightClick: (event: React.MouseEvent, partnership: PartnershipData) => void;
}

function Path({ data, partnerships, width, zIndex, isFocused, onLeftClick, onRightClick }: PathProps) {
  const [clickableBorderMeasurements, setClickableBorderMeasurements] = React.useState({} as { left: number, right: number, bottom: number } );
  const pathRef = React.useRef(null);

  useEffect(() => {
    if( pathRef.current) {
      const rect = pathRef.current.getBoundingClientRect();
      const overlayLeft = parseFloat(getComputedStyle(pathRef.current).getPropertyValue('--overlay-left'));
      const overlayRight = parseFloat(getComputedStyle(pathRef.current).getPropertyValue('--overlay-right'));
      const overlayBottom = parseFloat(getComputedStyle(pathRef.current).getPropertyValue('--overlay-bottom'));
      const overlayBorderWidth = parseFloat(getComputedStyle(pathRef.current).getPropertyValue('--overlay-border-width'));

      setClickableBorderMeasurements({
        left: (overlayBorderWidth + (overlayLeft / 2)) / rect.width,
        right: 1 - ((overlayBorderWidth + (overlayRight / 2)) / rect.width),
        bottom: 1 - ((overlayBorderWidth + (overlayBottom / 2)) / rect.height)
      });
    }
  }, [width]);

  const handleMouseMove = (event: React.MouseEvent) => {
    if (isOnBorder(event)) {
      pathRef.current.style.cursor = 'pointer';
    } else {
      pathRef.current.style.cursor = 'default';
    }
  }

  const isOnBorder = (event: React.MouseEvent) => {
    const rect = (event.target as Element).getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;

    return x <= rect.width * clickableBorderMeasurements.left
      || x >= rect.width * clickableBorderMeasurements.right
      || y >= rect.height * clickableBorderMeasurements.bottom;
  }

  return (
    <div ref={pathRef} key={data.valueId} className="path"
     style={{
      width: width,
      height: '100%',
      borderLeft: isFocused ? '2px solid green' : '2px solid black',
      borderRight: isFocused ? '2px solid green' : '2px solid black',
      borderBottom: isFocused ? '2px solid green' : '2px solid black',
      borderTop: 'none',
      zIndex: isFocused ? 100 : zIndex,
    }}
    onClick={(event) => onLeftClick(event, partnerships.get(data.valueId))}
    onMouseMove={handleMouseMove}
    >
      {/*<div  className='path-left' style={{*/}
      {/*  borderLeft: isFocused ? '2px solid green' : '2px solid black',*/}
      {/*  borderBottom: isFocused ? '2px solid green' : '2px solid black',*/}
      {/*  // zIndex: zIndex*/}
      {/*}}*/}
      {/*onClick={(event) => onLeftClick(event, partnerships.get(data.valueId))}*/}
      {/*/>*/}
      {/*<div  className='path-bottom' style={{*/}
      {/*  borderBottom: isFocused ? '2px solid green' : '2px solid black',*/}
      {/*}}*/}
      {/*     onClick={(event) => onLeftClick(event, partnerships.get(data.valueId))}*/}
      {/*     // onMouseMove={handleMouseMove}*/}
      {/*/>*/}
      {/*<div className='path-right' style={{*/}
      {/*  borderRight: isFocused ? '2px solid green' : '2px solid black',*/}
      {/*  borderBottom: isFocused ? '2px solid green' : '2px solid black',*/}
      {/*}}*/}
      {/*     onClick={(event) => onLeftClick(event, partnerships.get(data.valueId))}*/}
      {/*     // onMouseMove={handleMouseMove}*/}
      {/*/>*/}
    </div>
  );
}

export default Path;
