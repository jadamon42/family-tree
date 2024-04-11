Maybe children should be a part of the partnership, not the parent.
Parent doesn't know its children, it knows partnership with another person that resulted in children.

Focus on one tree at a time.
You can easily collapse partner's ancestral tree
If I'm looking at the Damon tree, I should be able to collapse Mike Steffes ancestral branches.
- Mike Steffes node can kind of hide behind Laurel Damon node
- nvm, bad idea, should always see both. If you want them gone, collapse a descendant's ancestral branches.
  If I also collapse Laurel Damon's ancestral branches, then Laurel's mother, father, and siblings will disappear.
- How do make the user know that they can expand the tree? Maybe a little plus sign next to the node?
  Also collapsable descendents!

1. Frontend sends a request to the backend to get all Persons without parents. These will be the root nodes of your family trees.
2. For each Person received, the frontend sends a request to the backend to get all Partnerships for that Person.
3. For each Partnership received, the frontend links the Persons involved in the Partnership and adds the Partnership's children to the family tree.
   1. get all partnerships for a person can return a list of objects that contain a list of Person IDs and the Partnership object
4. The frontend then sends a request to the backend to get all Partnerships for each child, excluding the Partnership they were just linked through. This ensures that you don't retrieve the same Partnership twice.
   1. get all partnerships for a person accepts a partnershipExclusions qsp
5. Repeat steps 3 and 4 until all Persons and Partnerships have been retrieved and added to the family tree.

```javascript
// Frontend code
async function buildFamilyTree() {
    const rootPersons = await getRootPersons();

    for (const person of rootPersons) {
        await addPersonAndPartnershipsToTree(person, []);
    }
}

async function getRootPersons() {
    // Send a request to the backend to get all Persons without parents
    const response = await fetch('/api/persons/roots');
    return response.json();
}

async function addPersonAndPartnershipsToTree(person, excludedPartnerships) {
    // Add the person to the family tree
    familyTree.addPerson(person);

    // Send a request to the backend to get all Partnerships for this person, excluding the specified partnerships
    const partnerships = await getPartnershipsForPerson(person.id, excludedPartnerships);

    for (const partnership of partnerships) {
        // Add the partnership to the family tree
        familyTree.addPartnership(partnership);

        for (const child of partnership.children) {
            // Recursively add each child and their partnerships to the tree, excluding the current partnership
            await addPersonAndPartnershipsToTree(child, [...excludedPartnerships, partnership.id]);
        }
    }
}

async function getPartnershipsForPerson(personId, excludedPartnerships) {
    // Send a request to the backend to get all Partnerships for this person, excluding the specified partnerships
    const response = await fetch(`/api/persons/${personId}/partnerships?exclusions=${excludedPartnerships.join(',')}`);
    return response.json();
}
```
