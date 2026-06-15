import { useEffect, useId, useState } from 'react';
import mermaid from 'mermaid';
import { CodeBlock } from './CodeBlock';

mermaid.initialize({ startOnLoad: false, theme: 'dark', securityLevel: 'loose' });

export function MermaidDiagram({ chart }: { chart: string }) {
  const [svg, setSvg] = useState<string | null>(null);
  const [failed, setFailed] = useState(false);
  const rawId = useId().replace(/[^a-zA-Z0-9]/g, '');

  useEffect(() => {
    let cancelled = false;
    if (!chart || !chart.trim()) {
      setFailed(true);
      return;
    }
    setFailed(false);
    setSvg(null);
    mermaid
      .render(`mmd-${rawId}`, chart.trim())
      .then((res) => {
        if (!cancelled) {
          setSvg(res.svg);
        }
      })
      .catch(() => {
        if (!cancelled) {
          setFailed(true);
        }
      });
    return () => {
      cancelled = true;
    };
  }, [chart, rawId]);

  if (failed) {
    return <CodeBlock code={chart} language="mermaid" />;
  }
  if (!svg) {
    return <div className="h-32 animate-pulse rounded-lg bg-zinc-800/40" />;
  }
  return (
    <div
      className="overflow-auto rounded-lg border border-zinc-800 bg-zinc-100 p-4"
      dangerouslySetInnerHTML={{ __html: svg }}
    />
  );
}
