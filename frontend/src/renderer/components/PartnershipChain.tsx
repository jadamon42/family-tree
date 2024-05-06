import React, { useEffect, useRef, useState } from 'react';
import '../styles/PartnershipChain.css';

interface PartnershipChainProps {
  children: React.ReactNode;
  nodeGap?: number;
}

function PartnershipChain({ children, nodeGap }: PartnershipChainProps) {
  const [nodeWidth, setNodeWidth] = useState(0);
  const nodeRef = useRef(null);

  useEffect(() => {
    if (nodeRef.current) {
      setNodeWidth(nodeRef.current.offsetWidth);
    }
  }, [nodeRef.current]);

  return (
    <div className='comb'>
      <div className='partners' style={{ gap: `${nodeGap}px` }}>
        {React.Children.map(children, (child) => {
            const childElement = child as React.ReactElement;
            return (
              <div className='partner'>
                <div style={{ borderLeft: '1px solid black', height: '50px', width: '1px' }} />
                <div ref={nodeRef}>{child}</div>
                {childElement.props.partnership &&
                  childElement.props.partnership.startDate &&
                  <div className="partnershipLabel"
                       style={{ left: `calc(50% - ${(nodeWidth / 2) + (nodeGap / 2)}px)` }}>
                    m. {childElement.props.partnership.startDate.substring(0, 4)}
                  </div>}
              </div>
            )
          }
        )}
      </div>
      <div className="spine" style={{ width: `calc(100% - ${nodeWidth}px)` }} />
    </div>
  );
}

export default PartnershipChain;
