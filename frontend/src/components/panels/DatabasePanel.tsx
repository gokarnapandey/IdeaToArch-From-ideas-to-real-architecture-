import type { DesignSession } from '../../api/types';
import { MermaidDiagram } from '../MermaidDiagram';
import { Card, EmptyState, SectionTitle } from '../ui';

export function DatabasePanel({ session }: { session: DesignSession }) {
  const d = session.database;
  if (!d) {
    return <EmptyState label="Database design not generated yet." />;
  }
  return (
    <div className="space-y-4">
      {(d.tables ?? []).map((t, i) => (
        <Card key={i}>
          <h4 className="mb-2 font-mono text-sm font-semibold text-sky-300">{t.name}</h4>
          <div className="overflow-auto">
            <table className="w-full text-left text-xs">
              <thead className="text-zinc-500">
                <tr>
                  <th className="py-1 pr-4">Column</th>
                  <th className="pr-4">Type</th>
                  <th className="pr-4">PK</th>
                  <th className="pr-4">Null</th>
                  <th>Note</th>
                </tr>
              </thead>
              <tbody className="text-zinc-300">
                {(t.columns ?? []).map((c, j) => (
                  <tr key={j} className="border-t border-zinc-800">
                    <td className="py-1 pr-4 font-mono">{c.name}</td>
                    <td className="pr-4 font-mono">{c.type}</td>
                    <td className="pr-4">{c.primaryKey ? '✓' : ''}</td>
                    <td className="pr-4">{c.nullable ? 'yes' : 'no'}</td>
                    <td>{c.note}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {(t.indexes ?? []).length > 0 && (
            <p className="mt-2 text-xs text-zinc-400">
              <span className="text-zinc-500">Indexes: </span>
              {t.indexes.join(', ')}
            </p>
          )}
          {(t.constraints ?? []).length > 0 && (
            <p className="text-xs text-zinc-400">
              <span className="text-zinc-500">Constraints: </span>
              {t.constraints.join(', ')}
            </p>
          )}
        </Card>
      ))}
      {d.rationale && (
        <Card>
          <SectionTitle>Rationale</SectionTitle>
          <p className="text-sm text-zinc-300">{d.rationale}</p>
        </Card>
      )}
      {d.erMermaid && (
        <Card>
          <SectionTitle>ER diagram</SectionTitle>
          <MermaidDiagram chart={d.erMermaid} />
        </Card>
      )}
    </div>
  );
}
