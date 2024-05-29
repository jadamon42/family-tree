class Relationship {
  pathIds: string[];
  relationshipLabel: string;
  inverseRelationshipLabel: string;

  constructor(
    pathIds: string[],
    relationshipLabel: string,
    inverseRelationshipLabel: string
  ) {
    this.pathIds = pathIds;
    this.relationshipLabel = relationshipLabel;
    this.inverseRelationshipLabel = inverseRelationshipLabel;
  }
}

export default Relationship;
