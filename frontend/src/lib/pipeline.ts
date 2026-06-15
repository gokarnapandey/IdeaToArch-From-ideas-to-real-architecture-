export type StageState = 'DONE' | 'RUNNING' | 'CURRENT' | 'PENDING' | 'ERROR' | 'LOCKED';

export type ArtifactKey =
  | 'requirements'
  | 'modules'
  | 'entities'
  | 'architecture'
  | 'database'
  | 'lld'
  | 'diagrams'
  | 'prompts';

export interface Stage {
  key: ArtifactKey;
  agent: string;
  label: string;
}

/** The lean, build-focused flow, in order. `agent` matches the backend AgentType name. */
export const STAGES: Stage[] = [
  { key: 'requirements', agent: 'REQUIREMENT', label: 'Requirements' },
  { key: 'modules', agent: 'MODULE_DISCOVERY', label: 'Modules' },
  { key: 'entities', agent: 'ENTITIES', label: 'Entities' },
  { key: 'architecture', agent: 'ARCHITECTURE', label: 'Architecture' },
  { key: 'database', agent: 'DATABASE', label: 'DB Schema' },
  { key: 'lld', agent: 'LLD', label: 'LLD' },
  { key: 'diagrams', agent: 'DIAGRAM', label: 'Diagrams' },
  { key: 'prompts', agent: 'PROMPT_GENERATION', label: 'Build Prompts' },
];

export interface Section {
  key: string;
  label: string;
}

/**
 * Editable slots per step. The UI shows a feedback input per slot so the user can target a
 * specific section; inputs are composed into the single feedback string the backend forwards to
 * the agent. Steps with no useful slots fall back to one overall feedback box.
 */
export const STAGE_SECTIONS: Record<ArtifactKey, Section[]> = {
  requirements: [
    { key: 'functional', label: 'Functional' },
    { key: 'nonFunctional', label: 'Non-functional' },
    { key: 'implicit', label: 'Implicit' },
    { key: 'assumptions', label: 'Assumptions' },
    { key: 'openQuestions', label: 'Open questions' },
    { key: 'outOfScope', label: 'Out of scope' },
  ],
  modules: [
    { key: 'modules', label: 'Modules' },
    { key: 'dependencies', label: 'Dependencies' },
  ],
  entities: [
    { key: 'entities', label: 'Entities' },
    { key: 'relationships', label: 'Relationships' },
    { key: 'rationale', label: 'Rationale' },
  ],
  architecture: [
    { key: 'style', label: 'Style & rationale' },
    { key: 'layers', label: 'Layers' },
    { key: 'requestFlow', label: 'Request flow' },
    { key: 'crossCutting', label: 'Cross-cutting concerns' },
  ],
  database: [
    { key: 'tables', label: 'Tables' },
    { key: 'relationships', label: 'Relationships' },
    { key: 'rationale', label: 'Rationale' },
    { key: 'er', label: 'ER diagram' },
  ],
  lld: [
    { key: 'packages', label: 'Packages' },
    { key: 'entities', label: 'Entities' },
    { key: 'dtos', label: 'DTOs' },
    { key: 'repositories', label: 'Repositories' },
    { key: 'services', label: 'Services' },
    { key: 'controllers', label: 'Controllers' },
    { key: 'exceptions', label: 'Exceptions' },
  ],
  diagrams: [
    { key: 'sequence', label: 'Sequence' },
    { key: 'classDiagram', label: 'Class' },
    { key: 'component', label: 'Component' },
    { key: 'requestFlow', label: 'Request flow' },
  ],
  prompts: [{ key: 'phases', label: 'Build phases' }],
};
