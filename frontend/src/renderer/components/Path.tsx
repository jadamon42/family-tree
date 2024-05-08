import TreeSegmentPartnershipData from '../models/TreeSegmentPartnershipData';
import React from 'react';
import PartnershipData from '../models/PartnershipData';
import '../styles/Path.css';

interface PathProps {
  data: TreeSegmentPartnershipData;
  partnerships: Map<string, PartnershipData>;
  width: string | number;
}

function Path({ data, width }: PathProps) {
  const [focused, setFocused] = React.useState(false);

  return (
    <div key={data.valueId} className="path" style={{
      width: width,
      height: '100%',
      border: focused ? '2px solid green' : '2px solid black',
      borderTop: 'none'
    }} />
  );
}

export default Path;
