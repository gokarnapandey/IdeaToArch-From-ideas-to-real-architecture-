import { useEffect, useRef, useState, type ReactNode } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { Link, useParams } from 'react-router-dom';
import { runStep } from '../api/client';
import type { DesignSession } from '../api/types';
import { ArtifactTabs } from '../components/ArtifactTabs';
import { ExportButton } from '../components/ExportButton';
import { PipelineProgress } from '../components/PipelineProgress';
import { StatusBadge } from '../components/StatusBadge';
import { StepControls } from '../components/StepControls';
import { useDesignSession } from '../hooks/useDesignSession';
import { STAGES, type ArtifactKey, type StageState } from '../lib/pipeline';

function present(session: DesignSession, key: ArtifactKey): boolean {
  return (session as unknown as Record<string, unknown>)[key] != null;
}

function leadingPresentCount(session: DesignSession): number {
  let n = 0;
  while (n < STAGES.length && present(session, STAGES[n].key)) {
    n++;
  }
  return n;
}

function erroredAgents(session: DesignSession): Set<string> {
  const set = new Set<string>();
  for (const e of session.agentErrors ?? []) {
    const name = e.split(':')[0]?.trim();
    if (name) {
      set.add(name);
    }
  }
  return set;
}

export function DesignPage() {
  const { id } = useParams<{ id: string }>();
  const qc = useQueryClient();
  const { data: session, isLoading, error } = useDesignSession(id);

  const [maxStep, setMaxStep] = useState<number | null>(null);
  const [activeTab, setActiveTab] = useState<ArtifactKey>('requirements');
  const [running, setRunning] = useState<ArtifactKey | null>(null);
  const [stepError, setStepError] = useState<string | null>(null);
  const [dismissed, setDismissed] = useState(false);
  const initRef = useRef(false);
  const autoRunRef = useRef(false);

  async function doRun(index: number, feedback?: string) {
    if (!id) {
      return;
    }
    const stage = STAGES[index];
    setRunning(stage.key);
    setStepError(null);
    try {
      const updated = await runStep(id, stage.agent, feedback);
      qc.setQueryData(['session', id], updated);
      setActiveTab(stage.key);
      setMaxStep((m) => Math.max(m ?? 0, index));
    } catch (e) {
      setStepError((e as Error).message);
    } finally {
      setRunning(null);
    }
  }

  // Initialize the gate + active tab once the session first loads (lands on the latest done step).
  useEffect(() => {
    if (!session || initRef.current) {
      return;
    }
    initRef.current = true;
    const init = Math.max(0, leadingPresentCount(session) - 1);
    setMaxStep(init);
    setActiveTab(STAGES[Math.min(init, STAGES.length - 1)].key);
  }, [session]);

  // Auto-run the first step (Requirements) for a brand-new session.
  useEffect(() => {
    if (!session || autoRunRef.current) {
      return;
    }
    if (leadingPresentCount(session) === 0) {
      autoRunRef.current = true;
      void doRun(0);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  if (isLoading || maxStep === null) {
    return <Centered>Loading session…</Centered>;
  }
  if (error || !session) {
    return <Centered>Failed to load this design session.</Centered>;
  }

  const errored = erroredAgents(session);
  const states = {} as Record<ArtifactKey, StageState>;
  STAGES.forEach((stage, i) => {
    if (present(session, stage.key)) {
      states[stage.key] = 'DONE';
    } else if (errored.has(stage.agent)) {
      states[stage.key] = 'ERROR';
    } else if (i === maxStep) {
      states[stage.key] = running === stage.key ? 'RUNNING' : 'CURRENT';
    } else if (i < maxStep) {
      states[stage.key] = 'PENDING';
    } else {
      states[stage.key] = 'LOCKED';
    }
  });

  const activeIndex = STAGES.findIndex((s) => s.key === activeTab);
  const activeStage = STAGES[activeIndex];
  const isFrontier = activeIndex === maxStep;
  const nextLabel = maxStep + 1 < STAGES.length ? STAGES[maxStep + 1].label : null;

  const controls = (
    <StepControls
      stage={activeStage}
      present={present(session, activeStage.key)}
      isFrontier={isFrontier}
      nextLabel={isFrontier ? nextLabel : null}
      running={running === activeStage.key}
      error={running === null ? stepError : null}
      onRun={(fb) => doRun(activeIndex, fb)}
      onContinue={() => {
        const next = maxStep + 1;
        if (next < STAGES.length) {
          setMaxStep(next);
          void doRun(next);
        }
      }}
      onSkip={() => {
        const next = Math.min(maxStep + 1, STAGES.length - 1);
        setMaxStep(next);
        setActiveTab(STAGES[next].key);
      }}
    />
  );

  const agentErrors = session.agentErrors ?? [];

  return (
    <div className="flex h-screen flex-col">
      <header className="flex items-center justify-between gap-4 border-b border-zinc-800 px-6 py-3">
        <div className="flex min-w-0 items-center gap-3">
          <Link to="/" className="text-zinc-500 transition-colors hover:text-zinc-300">
            ←
          </Link>
          <h1 className="truncate text-sm font-medium text-zinc-200" title={session.idea}>
            {session.idea}
          </h1>
        </div>
        <div className="flex shrink-0 items-center gap-3">
          <StatusBadge status={session.status} />
          <ExportButton id={session.id} />
        </div>
      </header>

      {session.status === 'FAILED' && session.error && (
        <Banner tone="error">Pipeline failed: {session.error}</Banner>
      )}
      {!dismissed && agentErrors.length > 0 && (
        <Banner tone="warn" onClose={() => setDismissed(true)}>
          {agentErrors.length} step issue{agentErrors.length > 1 ? 's' : ''}: {agentErrors.join(' · ')}
        </Banner>
      )}

      <div className="flex min-h-0 flex-1">
        <aside className="w-56 shrink-0 overflow-auto border-r border-zinc-800 p-3">
          <PipelineProgress states={states} active={activeTab} onSelect={setActiveTab} />
        </aside>
        <main className="min-w-0 flex-1 overflow-hidden px-6">
          <ArtifactTabs
            session={session}
            active={activeTab}
            states={states}
            onChange={setActiveTab}
            footer={controls}
          />
        </main>
      </div>
    </div>
  );
}

function Centered({ children }: { children: ReactNode }) {
  return (
    <div className="flex h-screen items-center justify-center text-sm text-zinc-400">{children}</div>
  );
}

function Banner({
  children,
  tone,
  onClose,
}: {
  children: ReactNode;
  tone: 'error' | 'warn';
  onClose?: () => void;
}) {
  const cls =
    tone === 'error'
      ? 'bg-red-500/10 text-red-300 border-red-500/30'
      : 'bg-amber-500/10 text-amber-300 border-amber-500/30';
  return (
    <div className={`flex items-center justify-between gap-4 border-b px-6 py-2 text-sm ${cls}`}>
      <span className="truncate">{children}</span>
      {onClose && (
        <button onClick={onClose} className="shrink-0 text-xs opacity-70 hover:opacity-100">
          dismiss
        </button>
      )}
    </div>
  );
}
