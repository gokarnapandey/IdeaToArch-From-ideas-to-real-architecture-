import type { DesignSession, DesignSessionRef } from './types';

export async function startDesign(idea: string): Promise<DesignSessionRef> {
  const res = await fetch('/api/design', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ idea }),
  });
  if (!res.ok) {
    throw new Error(`Failed to start design (${res.status})`);
  }
  return res.json();
}

export async function getSession(id: string): Promise<DesignSession> {
  const res = await fetch(`/api/design/${id}`);
  if (!res.ok) {
    throw new Error(`Failed to load session (${res.status})`);
  }
  return res.json();
}

/** Run a single pipeline step (agent) synchronously, optionally with revision feedback. */
export async function runStep(id: string, agent: string, feedback?: string): Promise<DesignSession> {
  const res = await fetch(`/api/design/${id}/steps/${agent}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ feedback: feedback ?? '' }),
  });
  if (!res.ok) {
    throw new Error(`Step "${agent}" failed (${res.status})`);
  }
  return res.json();
}

/** Download the generated docs/ tree as a zip via the browser. */
export async function exportZip(id: string): Promise<void> {
  const res = await fetch(`/api/design/${id}/export`, { method: 'POST' });
  if (!res.ok) {
    throw new Error(`Export failed (${res.status})`);
  }
  const blob = await res.blob();
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `docs-${id}.zip`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}
