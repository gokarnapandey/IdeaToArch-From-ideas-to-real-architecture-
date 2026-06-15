import { CopyButton } from './CopyButton';

export function CodeBlock({ code, language }: { code: string; language?: string }) {
  return (
    <div className="relative rounded-lg border border-zinc-800 bg-zinc-950">
      <div className="flex items-center justify-between border-b border-zinc-800 px-3 py-1.5">
        <span className="font-mono text-xs text-zinc-500">{language ?? 'text'}</span>
        <CopyButton text={code} />
      </div>
      <pre className="overflow-auto p-3 font-mono text-xs leading-relaxed text-zinc-200">
        <code>{code}</code>
      </pre>
    </div>
  );
}
