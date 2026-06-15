import { useState } from 'react';
import { copyToClipboard } from '../lib/utils';

export function CopyButton({ text, label = 'Copy' }: { text: string; label?: string }) {
  const [done, setDone] = useState(false);
  return (
    <button
      onClick={async () => {
        await copyToClipboard(text);
        setDone(true);
        setTimeout(() => setDone(false), 1500);
      }}
      className="rounded border border-zinc-700 px-2 py-1 text-xs text-zinc-300 transition-colors hover:bg-zinc-800"
    >
      {done ? 'Copied ✓' : label}
    </button>
  );
}
