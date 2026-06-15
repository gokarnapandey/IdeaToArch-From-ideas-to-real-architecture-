import type { DesignSession } from '../../api/types';
import { downloadText } from '../../lib/utils';
import { CopyButton } from '../CopyButton';
import { Markdown } from '../Markdown';
import { Card, EmptyState } from '../ui';

export function PromptsPanel({ session }: { session: DesignSession }) {
  const r = session.prompts;
  if (!r) {
    return <EmptyState label="Build prompts not generated yet." />;
  }
  const phases = [...(r.phases ?? [])].sort((a, b) => a.order - b.order);
  return (
    <div className="space-y-4">
      <p className="text-sm text-zinc-400">
        Paste these into Claude in order — each phase builds on the ones before it.
      </p>
      {phases.map((p, i) => (
        <Card key={i}>
          <div className="mb-2 flex items-start justify-between gap-2">
            <div className="min-w-0">
              <h4 className="font-medium text-zinc-100">
                <span className="mr-2 rounded bg-zinc-800 px-1.5 py-0.5 text-xs text-sky-300">
                  Phase {p.order}
                </span>
                {p.title}
              </h4>
              {p.goal && <p className="mt-1 text-sm text-zinc-400">{p.goal}</p>}
              {(p.dependsOn ?? []).length > 0 && (
                <p className="mt-1 text-xs text-zinc-500">Depends on: {p.dependsOn.join(', ')}</p>
              )}
            </div>
            <div className="flex shrink-0 gap-2">
              <CopyButton text={p.markdown} />
              <button
                onClick={() => downloadText(p.fileName || `phase-${p.order}.md`, p.markdown)}
                className="rounded border border-zinc-700 px-2 py-1 text-xs text-zinc-300 transition-colors hover:bg-zinc-800"
              >
                Download .md
              </button>
            </div>
          </div>
          <details className="text-sm">
            <summary className="cursor-pointer text-sky-400">View prompt</summary>
            <div className="mt-2">
              <Markdown>{p.markdown}</Markdown>
            </div>
          </details>
        </Card>
      ))}
    </div>
  );
}
