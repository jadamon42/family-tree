import Partnership from '../models/Partnership';

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

  return await response.json();
}
