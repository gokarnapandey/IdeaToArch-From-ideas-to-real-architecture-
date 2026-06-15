import type { DesignSession } from '../../api/types';
import { BulletList, Card, EmptyState, SectionTitle } from '../ui';

export function ModulesPanel({ session }: { session: DesignSession }) {
  const r = session.modules;
  if (!r) {
    return <EmptyState label="Modules not generated yet." />;
  }
  return (
    <div className="space-y-4">
      <div className="grid gap-3 md:grid-cols-2">
        {(r.modules ?? []).map((m, i) => (
          <Card key={i}>
            <h4 className="font-medium text-zinc-100">{m.name}</h4>
            <p className="mt-1 text-sm text-zinc-400">{m.responsibility}</p>
            <div className="mt-2">
              <SectionTitle>Capabilities</SectionTitle>
              <BulletList items={m.capabilities} />
            </div>
          </Card>
        ))}
      </div>
      <Card>
        <SectionTitle>Dependencies</SectionTitle>
        {(r.dependencies ?? []).length === 0 ? (
          <p className="text-sm italic text-zinc-500">None</p>
        ) : (
          <ul className="space-y-1 text-sm text-zinc-300">
            {r.dependencies.map((d, i) => (
              <li key={i}>
                <code className="text-sky-300">{d.from}</code> →{' '}
                <code className="text-sky-300">{d.to}</code>{' '}
                <span className="text-zinc-500">({d.type})</span> — {d.reason}
              </li>
            ))}
          </ul>
        )}
      </Card>
    </div>
  );
}
