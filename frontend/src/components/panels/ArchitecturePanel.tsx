import type { DesignSession } from '../../api/types';
import { BulletList, Card, EmptyState, Pill, SectionTitle } from '../ui';

export function ArchitecturePanel({ session }: { session: DesignSession }) {
  const a = session.architecture;
  if (!a) {
    return <EmptyState label="Architecture not generated yet." />;
  }
  return (
    <div className="space-y-4">
      <Card>
        <Pill>{a.style}</Pill>
        <p className="mt-2 text-sm text-zinc-300">{a.rationale}</p>
      </Card>
      <div className="grid gap-3 md:grid-cols-2">
        {(a.layers ?? []).map((l, i) => (
          <Card key={i}>
            <h4 className="font-medium text-zinc-100">{l.name}</h4>
            <p className="mt-1 text-sm text-zinc-400">{l.responsibility}</p>
            <div className="mt-2">
              <BulletList items={l.components} />
            </div>
          </Card>
        ))}
      </div>
      <Card>
        <SectionTitle>Request flow</SectionTitle>
        <ol className="list-decimal space-y-1 pl-5 text-sm text-zinc-300">
          {(a.requestFlow ?? []).map((s, i) => (
            <li key={i}>{s}</li>
          ))}
        </ol>
      </Card>
      <Card>
        <SectionTitle>Cross-cutting concerns</SectionTitle>
        <BulletList items={a.crossCuttingConcerns} />
      </Card>
    </div>
  );
}
