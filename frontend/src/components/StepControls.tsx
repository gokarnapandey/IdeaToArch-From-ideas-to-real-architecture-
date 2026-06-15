import { useEffect, useRef, useState } from 'react';
import { STAGE_SECTIONS, type Stage } from '../lib/pipeline';

interface StepControlsProps {
  stage: Stage;
  present: boolean; // this step's artifact exists
  isFrontier: boolean; // this step is the furthest reached (the one to act on)
  nextLabel: string | null; // label of the next step, if any
  running: boolean;
  error: string | null;
  onRun: (feedback: string) => void; // generate or re-run THIS step
  onContinue: () => void; // approve and run the NEXT step
  onSkip: () => void; // advance past this step without running it
}

export function StepControls({
  stage,
  present,
  isFrontier,
  nextLabel,
  running,
  error,
  onRun,
  onContinue,
  onSkip,
}: StepControlsProps) {
  const sections = STAGE_SECTIONS[stage.key] ?? [];
  const [sectionFb, setSectionFb] = useState<Record<string, string>>({});
  const [overall, setOverall] = useState('');
  const prevRunning = useRef(false);

  // Clear the inputs after a successful run of this step.
  useEffect(() => {
    if (prevRunning.current && !running && !error) {
      setSectionFb({});
      setOverall('');
    }
    prevRunning.current = running;
  }, [running, error]);

  function composeFeedback(): string {
    const lines: string[] = [];
    for (const s of sections) {
      const v = (sectionFb[s.key] ?? '').trim();
      if (v) {
        lines.push(`- ${s.label}: ${v}`);
      }
    }
    const ov = overall.trim();
    let msg = '';
    if (lines.length > 0) {
      msg +=
        'Apply these section-specific revisions to your previous output. Change ONLY what each ' +
        'instruction asks for and keep every other section unchanged:\n' +
        lines.join('\n');
    }
    if (ov) {
      msg += (msg ? '\n\n' : '') + 'Overall: ' + ov;
    }
    return msg;
  }

  const showSections = present && sections.length > 0;
  const inputCls =
    'w-full rounded-md border border-zinc-700 bg-zinc-900 p-2 text-sm text-zinc-100 outline-none transition-colors focus:border-sky-500 disabled:opacity-50';

  return (
    <div className="mt-4 rounded-lg border border-zinc-800 bg-zinc-900/40 p-4">
      {showSections ? (
        <>
          <div className="mb-2 text-xs font-semibold uppercase tracking-wider text-zinc-500">
            Refine {stage.label} — fill only the slots you want changed; leave the rest blank
          </div>
          <div className="grid gap-2 sm:grid-cols-2">
            {sections.map((s) => (
              <div key={s.key}>
                <label className="mb-1 block text-xs text-zinc-400">{s.label}</label>
                <input
                  value={sectionFb[s.key] ?? ''}
                  onChange={(e) => setSectionFb((p) => ({ ...p, [s.key]: e.target.value }))}
                  disabled={running}
                  placeholder="add / change / remove…"
                  className={inputCls}
                />
              </div>
            ))}
          </div>
          <label className="mb-1 mt-3 block text-xs text-zinc-400">Other / overall</label>
          <textarea
            value={overall}
            onChange={(e) => setOverall(e.target.value)}
            rows={2}
            disabled={running}
            placeholder="cross-cutting feedback for the whole step…"
            className={`${inputCls} resize-none`}
          />
        </>
      ) : (
        <>
          <label className="mb-2 block text-xs font-semibold uppercase tracking-wider text-zinc-500">
            {present
              ? `Feedback for ${stage.label}`
              : `Extra requirements for ${stage.label} (optional)`}
          </label>
          <textarea
            value={overall}
            onChange={(e) => setOverall(e.target.value)}
            rows={2}
            disabled={running}
            placeholder="e.g. add a notifications module; this step missed X…"
            className={`${inputCls} resize-none`}
          />
        </>
      )}

      <div className="mt-3 flex flex-wrap items-center gap-2">
        {present ? (
          <button
            onClick={() => onRun(composeFeedback())}
            disabled={running}
            className="rounded-md border border-zinc-700 px-3 py-1.5 text-sm text-zinc-200 transition-colors hover:bg-zinc-800 disabled:opacity-40"
          >
            {running ? 'Running…' : 'Re-run this step'}
          </button>
        ) : isFrontier ? (
          <button
            onClick={() => onRun(overall.trim())}
            disabled={running}
            className="rounded-md bg-sky-600 px-3 py-1.5 text-sm font-medium text-white transition-colors hover:bg-sky-500 disabled:opacity-40"
          >
            {running ? `Generating ${stage.label}…` : `Generate ${stage.label}`}
          </button>
        ) : null}

        {present && isFrontier && nextLabel && (
          <button
            onClick={onContinue}
            disabled={running}
            className="rounded-md bg-emerald-600 px-3 py-1.5 text-sm font-medium text-white transition-colors hover:bg-emerald-500 disabled:opacity-40"
          >
            Approve &amp; continue → {nextLabel}
          </button>
        )}
        {!present && isFrontier && nextLabel && (
          <button
            onClick={onSkip}
            disabled={running}
            className="rounded-md border border-zinc-700 px-3 py-1.5 text-sm text-zinc-400 transition-colors hover:bg-zinc-800 disabled:opacity-40"
          >
            Skip →
          </button>
        )}
        {present && isFrontier && !nextLabel && (
          <span className="text-sm text-emerald-400">✓ Design complete — use “Export docs”.</span>
        )}
        {error && <span className="text-sm text-red-400">{error}</span>}
      </div>
    </div>
  );
}
