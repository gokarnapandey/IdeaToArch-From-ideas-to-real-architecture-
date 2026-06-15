import type { DesignSession } from '../../api/types';
import { BulletList, Card, EmptyState, SectionTitle } from '../ui';

export function RequirementsPanel({ session }: { session: DesignSession }) {
  const r = session.requirements;
  if (!r) {
    return <EmptyState label="Requirements not generated yet." />;
  }
  return (
    <div className="grid gap-4 md:grid-cols-2">
      <Card>
        <SectionTitle>Functional</SectionTitle>
        <BulletList items={r.functional} />
      </Card>
      <Card>
        <SectionTitle>Non-functional</SectionTitle>
        <BulletList items={r.nonFunctional} />
      </Card>
      <Card>
        <SectionTitle>Implicit</SectionTitle>
        <BulletList items={r.implicit} />
      </Card>
      <Card>
        <SectionTitle>Assumptions</SectionTitle>
        <BulletList items={r.assumptions} />
      </Card>
      <Card>
        <SectionTitle>Open questions</SectionTitle>
        <BulletList items={r.openQuestions} />
      </Card>
      <Card>
        <SectionTitle>Out of scope</SectionTitle>
        <BulletList items={r.outOfScope} />
      </Card>
    </div>
  );
}
