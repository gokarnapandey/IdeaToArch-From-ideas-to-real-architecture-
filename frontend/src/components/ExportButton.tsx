import { useState } from 'react';
import { exportZip } from '../api/client';

export function ExportButton({ id, disabled }: { id: string; disabled?: boolean }) {
  const [busy, setBusy] = useState(false);
  return (
    <button
      disabled={disabled || busy}
      onClick={async () => {
        setBusy(true);
        try {
          await exportZip(id);
        } catch (e) {
          alert((e as Error).message);
        } finally {
          setBusy(false);
        }
      }}
      className="rounded-md bg-emerald-600 px-3 py-1.5 text-sm font-medium text-white transition-colors hover:bg-emerald-500 disabled:cursor-not-allowed disabled:opacity-40"
    >
      {busy ? 'Exporting…' : 'Export docs'}
    </button>
  );
}
