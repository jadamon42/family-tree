import Partnership from '../models/Partnership';
import { mapToPerson } from './PersonActions';

function mapToPartnership(partnership: any): Partnership {
  return new Partnership(
    partnership.id,
    partnership.type,
    partnership.startDate ? new Date(partnership.startDate) : undefined,
    partnership.endDate ? new Date(partnership.endDate) : undefined,
    partnership.partners.map(mapToPerson),
    partnership.children.map(mapToPerson),
  );
}

export async function createPartnership(partnership: Partnership, partnerIds: string[]) {
  const response = await fetch('http://localhost:50000/api/partnership', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      ...partnership,
      partnerIds: partnerIds,
    }),
  });

  if (!response.ok) {
    console.error('Error:', response.statusText);
  }

  const createdPartnership = await response.json();
  return mapToPartnership(createdPartnership);
}

export async function getPartnerships() {
  let page = 0;
  const limit = 10;
  let hasMore = true;
  let allPartnerships: Partnership[] = [];

  while (hasMore) {
    const response = await fetch(`http://localhost:50000/api/partnership?page=${page}&limit=${limit}`);
    if (!response.ok) {
      console.error('Error:', response.statusText);
      break;
    }

    const data = await response.json();
    allPartnerships = [...allPartnerships, ...data.content.map(mapToPartnership)];

    hasMore = !data.last;
    page += 1;
  }

  return allPartnerships;
}

export  async function getPartnership(partnershipId: string) {
  const response = await fetch(`http://localhost:50000/api/partnership/${partnershipId}`);
  if (!response.ok) {
    console.error('Error:', response.statusText);
  }

  const partnership = await response.json();
  return mapToPartnership(partnership);
}

export async function deletePartnership(partnershipId: string) {
  const response = await fetch(`http://localhost:50000/api/partnership/${partnershipId}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    console.error('Error:', response.statusText);
  }
}
