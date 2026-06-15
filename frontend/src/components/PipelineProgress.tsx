import { STAGES, type ArtifactKey, type StageState } from '../lib/pipeline';
import { cn } from '../lib/utils';

const DOT: Record<StageState, string> = {
  DONE: 'bg-emerald-400',
  RUNNING: 'bg-sky-400 animate-pulse',
  CURRENT: 'bg-sky-400',
  PENDING: 'bg-zinc-600',
  ERROR: 'bg-red-400',
  LOCKED: 'bg-zinc-700',
};

export function PipelineProgress({
  states,
  active,
  onSelect,
}: {
  states: Record<ArtifactKey, StageState>;
  active: ArtifactKey;
  onSelect: (key: ArtifactKey) => void;
}) {
  return (
    <nav className="space-y-0.5">
      {STAGES.map((stage) => {
        const state = states[stage.key];
        const locked = state === 'LOCKED';
        return (
          <button
            key={stage.key}
            onClick={() => !locked && onSelect(stage.key)}
            disabled={locked}
            className={cn(
              'flex w-full items-center gap-3 rounded-md px-3 py-2 text-left text-sm transition-colors',
              active === stage.key ? 'bg-zinc-800 text-zinc-100' : 'text-zinc-400 hover:bg-zinc-800/50',
              locked && 'cursor-not-allowed opacity-40 hover:bg-transparent',
            )}
          >
            <span className={cn('h-2 w-2 shrink-0 rounded-full', DOT[state])} />
            <span className="flex-1 truncate">{stage.label}</span>
            {state === 'ERROR' && <span className="text-xs font-bold text-red-400">!</span>}
            {locked && <span className="text-[10px] text-zinc-600">🔒</span>}
          </button>
        );
      })}
    </nav>
  );
}
