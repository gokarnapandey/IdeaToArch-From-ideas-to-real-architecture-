import type { DesignSession, Diagram } from '../../api/types';
import { CodeBlock } from '../CodeBlock';
import { MermaidDiagram } from '../MermaidDiagram';
import { EmptyState, SectionTitle } from '../ui';

export function DiagramsPanel({ session }: { session: DesignSession }) {
  const d = session.diagrams;
  if (!d) {
    return <EmptyState label="Diagrams not generated yet." />;
  }
  const entries: Array<[string, Diagram | null]> = [
    ['Sequence', d.sequence],
    ['Class', d.classDiagram],
    ['Component', d.component],
    ['Request flow', d.requestFlow],
  ];
  if (!entries.some(([, dia]) => dia)) {
    return <EmptyState label="No diagrams were produced." />;
  }
  return (
    <div className="space-y-6">
      {entries.map(([label, dia]) =>
        dia ? (
          <div key={label}>
            <SectionTitle>{label}</SectionTitle>
            {dia.mermaid && <MermaidDiagram chart={dia.mermaid} />}
            {dia.plantUml && (
              <div className="mt-2">
                <CodeBlock code={dia.plantUml} language="plantuml" />
              </div>
            )}
          </div>
        ) : null,
      )}
    </div>
  );
}
