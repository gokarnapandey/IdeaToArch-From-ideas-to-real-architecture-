import type { SessionStatus } from '../api/types';
import { cn } from '../lib/utils';

const MAP: Record<SessionStatus, string> = {
  PENDING: 'bg-zinc-700/40 text-zinc-300',
  RUNNING: 'bg-sky-500/15 text-sky-300',
  COMPLETED: 'bg-emerald-500/15 text-emerald-300',
  FAILED: 'bg-red-500/15 text-red-300',
};

export function StatusBadge({ status }: { status: SessionStatus }) {
  return (
    <span
      className={cn(
        'inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-medium',
        MAP[status],
      )}
    >
      {status === 'RUNNING' && <span className="h-1.5 w-1.5 animate-pulse rounded-full bg-sky-400" />}
      {status}
    </span>
  );
}
