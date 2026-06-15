import type { DesignSession } from '../../api/types';
import { Card, EmptyState, SectionTitle } from '../ui';

export function EntitiesPanel({ session }: { session: DesignSession }) {
  const m = session.entities;
  if (!m) {
    return <EmptyState label="Entities not generated yet." />;
  }
  return (
    <div className="space-y-4">
      {(m.entities ?? []).map((e, i) => (
        <Card key={i}>
          <h4 className="font-medium text-zinc-100">
            {e.name}
            {e.module ? <span className="ml-2 text-xs text-zinc-500">({e.module})</span> : null}
          </h4>
          {e.description && <p className="mt-1 text-sm text-zinc-400">{e.description}</p>}
          <div className="mt-2 overflow-auto">
            <table className="w-full text-left text-xs">
              <thead className="text-zinc-500">
                <tr>
                  <th className="py-1 pr-4">Attribute</th>
                  <th className="pr-4">Type</th>
                  <th className="pr-4">Id</th>
                  <th className="pr-4">Required</th>
                  <th>Note</th>
                </tr>
              </thead>
              <tbody className="text-zinc-300">
                {(e.attributes ?? []).map((a, j) => (
                  <tr key={j} className="border-t border-zinc-800">
                    <td className="py-1 pr-4 font-mono">{a.name}</td>
                    <td className="pr-4 font-mono">{a.type}</td>
                    <td className="pr-4">{a.identifier ? '✓' : ''}</td>
                    <td className="pr-4">{a.required ? 'yes' : 'no'}</td>
                    <td>{a.note}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      ))}
      {(m.relationships ?? []).length > 0 && (
        <Card>
          <SectionTitle>Relationships</SectionTitle>
          <ul className="space-y-1 text-sm text-zinc-300">
            {m.relationships.map((r, i) => (
              <li key={i}>
                <code className="text-sky-300">{r.from}</code> →{' '}
                <code className="text-sky-300">{r.to}</code>{' '}
                <span className="text-zinc-500">({r.cardinality})</span> — {r.description}
              </li>
            ))}
          </ul>
        </Card>
      )}
      {m.rationale && (
        <Card>
          <SectionTitle>Rationale</SectionTitle>
          <p className="text-sm text-zinc-300">{m.rationale}</p>
        </Card>
      )}
    </div>
  );
}
