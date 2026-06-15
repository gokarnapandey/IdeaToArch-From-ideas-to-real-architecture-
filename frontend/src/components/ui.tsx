import type { ReactNode } from 'react';
import { cn } from '../lib/utils';

export function Card({ children, className }: { children: ReactNode; className?: string }) {
  return (
    <div className={cn('rounded-lg border border-zinc-800 bg-zinc-900/60 p-4', className)}>
      {children}
    </div>
  );
}

export function SectionTitle({ children }: { children: ReactNode }) {
  return (
    <h3 className="mb-3 text-xs font-semibold uppercase tracking-wider text-zinc-400">{children}</h3>
  );
}

export function BulletList({ items, empty = 'None' }: { items?: string[]; empty?: string }) {
  if (!items || items.length === 0) {
    return <p className="text-sm italic text-zinc-500">{empty}</p>;
  }
  return (
    <ul className="list-disc space-y-1 pl-5 text-sm text-zinc-300">
      {items.map((it, i) => (
        <li key={i}>{it}</li>
      ))}
    </ul>
  );
}

export function EmptyState({ label }: { label: string }) {
  return (
    <div className="flex h-48 items-center justify-center text-sm italic text-zinc-500">{label}</div>
  );
}

const TONES: Record<string, string> = {
  high: 'bg-red-500/15 text-red-300 border-red-500/30',
  medium: 'bg-amber-500/15 text-amber-300 border-amber-500/30',
  low: 'bg-emerald-500/15 text-emerald-300 border-emerald-500/30',
  default: 'bg-zinc-700/40 text-zinc-300 border-zinc-600/40',
};

export function Pill({ children, tone }: { children: ReactNode; tone?: string }) {
  const key = (tone ?? 'default').toLowerCase();
  return (
    <span className={cn('rounded border px-2 py-0.5 text-xs font-medium', TONES[key] ?? TONES.default)}>
      {children}
    </span>
  );
}
