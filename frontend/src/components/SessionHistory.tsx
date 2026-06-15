import { Link } from 'react-router-dom';
import { loadHistory } from '../lib/history';

export function SessionHistory() {
  const items = loadHistory();
  if (items.length === 0) {
    return null;
  }
  return (
    <div className="mt-12 w-full max-w-2xl">
      <h2 className="mb-2 text-xs font-semibold uppercase tracking-wider text-zinc-500">Recent designs</h2>
      <ul className="divide-y divide-zinc-800 rounded-lg border border-zinc-800">
        {items.map((it) => (
          <li key={it.id}>
            <Link
              to={`/design/${it.id}`}
              className="block truncate px-4 py-2.5 text-sm text-zinc-300 transition-colors hover:bg-zinc-800/60"
            >
              {it.idea}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
