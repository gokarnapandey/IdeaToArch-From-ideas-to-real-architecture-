import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { startDesign } from '../api/client';
import { IdeaComposer } from '../components/IdeaComposer';
import { SessionHistory } from '../components/SessionHistory';
import { addHistory } from '../lib/history';

export function HomePage() {
  const navigate = useNavigate();
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleStart(idea: string) {
    setBusy(true);
    setError(null);
    try {
      const ref = await startDesign(idea);
      addHistory({ id: ref.id, idea, createdAt: Date.now() });
      navigate(`/design/${ref.id}`);
    } catch (e) {
      setError((e as Error).message);
      setBusy(false);
    }
  }

  return (
    <div className="flex min-h-screen flex-col items-center px-6 py-20">
      <div className="mb-2 text-xs font-semibold uppercase tracking-[0.2em] text-sky-400">
        Architecture Copilot
      </div>
      <h1 className="mb-3 text-center text-3xl font-bold text-zinc-100">
        From idea to production architecture
      </h1>
      <p className="mb-10 max-w-xl text-center text-sm text-zinc-400">
        A multi-agent AI that researches, decides, and designs — requirements, engineering
        decisions, low-level design, diagrams, Claude implementation prompts, and one-click docs.
      </p>
      <IdeaComposer onSubmit={handleStart} busy={busy} />
      {error && <p className="mt-4 text-sm text-red-400">{error}</p>}
      <SessionHistory />
    </div>
  );
}
