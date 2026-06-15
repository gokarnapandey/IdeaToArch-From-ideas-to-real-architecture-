import type { DesignSession, JavaType } from '../../api/types';
import { Card, EmptyState, SectionTitle } from '../ui';

function TypeList({ types }: { types?: JavaType[] }) {
  if (!types || types.length === 0) {
    return <p className="text-sm italic text-zinc-500">None</p>;
  }
  return (
    <div className="space-y-2">
      {types.map((t, i) => (
        <div key={i}>
          <div className="font-mono text-sm text-zinc-200">{t.name}</div>
          <ul className="pl-4 text-xs text-zinc-400">
            {(t.fields ?? []).map((f, j) => (
              <li key={j}>
                <code>
                  {f.name}: {f.type}
                </code>
                {f.note ? ` — ${f.note}` : ''}
              </li>
            ))}
          </ul>
        </div>
      ))}
    </div>
  );
}

export function LldPanel({ session }: { session: DesignSession }) {
  const l = session.lld;
  if (!l) {
    return <EmptyState label="Low-level design not generated yet." />;
  }
  return (
    <div className="space-y-4">
      <Card>
        <SectionTitle>Packages</SectionTitle>
        <ul className="space-y-1 text-sm text-zinc-300">
          {(l.packages ?? []).map((p, i) => (
            <li key={i}>
              <code className="text-sky-300">{p.path}</code> —{' '}
              <span className="text-zinc-400">{p.purpose}</span>
            </li>
          ))}
        </ul>
      </Card>

      <Card>
        <SectionTitle>Services</SectionTitle>
        <div className="space-y-3">
          {(l.services ?? []).map((s, i) => (
            <div key={i}>
              <div className="font-mono text-sm text-zinc-200">
                {s.iface} <span className="text-zinc-500">→</span> {s.impl}
              </div>
              <ul className="mt-1 list-disc space-y-0.5 pl-5 text-xs text-zinc-400">
                {(s.methods ?? []).map((m, j) => (
                  <li key={j}>
                    <code>{m.signature}</code> — {m.behavior}
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      </Card>

      <Card>
        <SectionTitle>Controllers</SectionTitle>
        <div className="space-y-3">
          {(l.controllers ?? []).map((c, i) => (
            <div key={i}>
              <div className="text-sm font-medium text-zinc-200">
                {c.name} <code className="text-xs text-sky-300">{c.basePath}</code>
              </div>
              <ul className="mt-1 space-y-0.5 text-xs text-zinc-400">
                {(c.endpoints ?? []).map((e, j) => (
                  <li key={j}>
                    <span className="font-mono text-emerald-300">{e.httpMethod}</span>{' '}
                    <code>{e.path}</code> — {e.summary}
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      </Card>

      {l.exceptions && (
        <Card>
          <SectionTitle>Exception hierarchy</SectionTitle>
          <p className="text-sm text-zinc-300">
            Base: <code className="text-sky-300">{l.exceptions.base}</code>
          </p>
          <ul className="mt-1 space-y-0.5 text-xs text-zinc-400">
            {(l.exceptions.types ?? []).map((t, i) => (
              <li key={i}>
                <code>{t.name}</code> extends <code>{t.parent}</code> → HTTP {t.httpStatus} ({t.when})
              </li>
            ))}
          </ul>
        </Card>
      )}

      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <SectionTitle>Entities</SectionTitle>
          <TypeList types={l.entities} />
        </Card>
        <Card>
          <SectionTitle>DTOs</SectionTitle>
          <TypeList types={l.dtos} />
        </Card>
      </div>
    </div>
  );
}
