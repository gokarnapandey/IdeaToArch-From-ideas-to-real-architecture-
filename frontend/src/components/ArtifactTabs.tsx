import type { ReactNode } from 'react';
import type { DesignSession } from '../api/types';
import { STAGES, type ArtifactKey, type StageState } from '../lib/pipeline';
import { cn } from '../lib/utils';
import { ArchitecturePanel } from './panels/ArchitecturePanel';
import { DatabasePanel } from './panels/DatabasePanel';
import { DiagramsPanel } from './panels/DiagramsPanel';
import { EntitiesPanel } from './panels/EntitiesPanel';
import { LldPanel } from './panels/LldPanel';
import { ModulesPanel } from './panels/ModulesPanel';
import { PromptsPanel } from './panels/PromptsPanel';
import { RequirementsPanel } from './panels/RequirementsPanel';

const PANELS: Record<ArtifactKey, (session: DesignSession) => ReactNode> = {
  requirements: (s) => <RequirementsPanel session={s} />,
  modules: (s) => <ModulesPanel session={s} />,
  entities: (s) => <EntitiesPanel session={s} />,
  architecture: (s) => <ArchitecturePanel session={s} />,
  database: (s) => <DatabasePanel session={s} />,
  lld: (s) => <LldPanel session={s} />,
  diagrams: (s) => <DiagramsPanel session={s} />,
  prompts: (s) => <PromptsPanel session={s} />,
};

export function ArtifactTabs({
  session,
  active,
  states,
  onChange,
  footer,
}: {
  session: DesignSession;
  active: ArtifactKey;
  states: Record<ArtifactKey, StageState>;
  onChange: (key: ArtifactKey) => void;
  footer?: ReactNode;
}) {
  return (
    <div className="flex h-full flex-col">
      <div className="flex flex-wrap gap-1 border-b border-zinc-800">
        {STAGES.map((s) => {
          const locked = states[s.key] === 'LOCKED';
          return (
            <button
              key={s.key}
              onClick={() => !locked && onChange(s.key)}
              disabled={locked}
              className={cn(
                'px-3 py-2 text-sm transition-colors',
                active === s.key
                  ? 'border-b-2 border-sky-500 text-zinc-100'
                  : 'text-zinc-400 hover:text-zinc-200',
                locked && 'cursor-not-allowed opacity-40',
              )}
            >
              {s.label}
            </button>
          );
        })}
      </div>
      <div className="flex-1 overflow-auto py-4">
        {PANELS[active](session)}
        {footer}
      </div>
    </div>
  );
}
