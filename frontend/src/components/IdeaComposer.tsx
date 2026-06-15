import { useState } from 'react';

const SAMPLES = [
  'A URL shortener with analytics and custom aliases',
  'An e-commerce order service with cart, payment and inventory',
  'A multi-tenant task management API with roles and audit logs',
];

export function IdeaComposer({
  onSubmit,
  busy,
}: {
  onSubmit: (idea: string) => void;
  busy?: boolean;
}) {
  const [idea, setIdea] = useState('');
  return (
    <div className="w-full max-w-2xl">
      <textarea
        value={idea}
        onChange={(e) => setIdea(e.target.value)}
        rows={4}
        placeholder="Describe the system you want to design…"
        className="w-full resize-none rounded-lg border border-zinc-700 bg-zinc-900 p-4 text-sm text-zinc-100 outline-none transition-colors focus:border-sky-500"
        onKeyDown={(e) => {
          if ((e.metaKey || e.ctrlKey) && e.key === 'Enter' && idea.trim() && !busy) {
            onSubmit(idea.trim());
          }
        }}
      />
      <div className="mt-3 flex flex-wrap gap-2">
        {SAMPLES.map((s) => (
          <button
            key={s}
            onClick={() => setIdea(s)}
            className="rounded-full border border-zinc-700 px-3 py-1 text-xs text-zinc-400 transition-colors hover:bg-zinc-800"
          >
            {s}
          </button>
        ))}
      </div>
      <button
        disabled={!idea.trim() || busy}
        onClick={() => onSubmit(idea.trim())}
        className="mt-4 rounded-md bg-sky-600 px-4 py-2 text-sm font-semibold text-white transition-colors hover:bg-sky-500 disabled:cursor-not-allowed disabled:opacity-40"
      >
        {busy ? 'Starting…' : 'Start design →'}
      </button>
    </div>
  );
}
