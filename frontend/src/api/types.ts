// TypeScript mirrors of the backend's structured-output DTOs (field names match Jackson output).
// All artifact fields are nullable — the pipeline fills them in one step at a time.

export type SessionStatus = 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED';

export interface Requirements {
  functional: string[];
  nonFunctional: string[];
  assumptions: string[];
  implicit: string[];
  openQuestions: string[];
  outOfScope: string[];
}

export interface ModuleDef {
  name: string;
  responsibility: string;
  capabilities: string[];
}
export interface ModuleDependency {
  from: string;
  to: string;
  type: string;
  reason: string;
}
export interface Modules {
  modules: ModuleDef[];
  dependencies: ModuleDependency[];
}

export interface EntityAttribute {
  name: string;
  type: string;
  identifier: boolean;
  required: boolean;
  note: string;
}
export interface DomainEntity {
  name: string;
  module: string;
  description: string;
  attributes: EntityAttribute[];
}
export interface EntityRelationship {
  from: string;
  to: string;
  cardinality: string;
  description: string;
}
export interface EntityModel {
  entities: DomainEntity[];
  relationships: EntityRelationship[];
  rationale: string;
}

export interface ArchLayer {
  name: string;
  responsibility: string;
  components: string[];
}
export interface Architecture {
  style: string;
  rationale: string;
  layers: ArchLayer[];
  requestFlow: string[];
  crossCuttingConcerns: string[];
}

export interface DbColumn {
  name: string;
  type: string;
  primaryKey: boolean;
  nullable: boolean;
  note: string;
}
export interface DbTable {
  name: string;
  columns: DbColumn[];
  indexes: string[];
  constraints: string[];
}
export interface DbRelationship {
  from: string;
  to: string;
  cardinality: string;
  onDelete: string;
}
export interface DatabaseDesign {
  tables: DbTable[];
  relationships: DbRelationship[];
  erMermaid: string;
  rationale: string;
}

export interface JavaField {
  name: string;
  type: string;
  note: string;
}
export interface JavaType {
  name: string;
  pkg: string;
  fields: JavaField[];
}
export interface PackageNode {
  path: string;
  purpose: string;
}
export interface RepoSpec {
  name: string;
  entity: string;
  methods: string[];
}
export interface MethodSpec {
  signature: string;
  behavior: string;
}
export interface ServiceSpec {
  iface: string;
  impl: string;
  methods: MethodSpec[];
}
export interface Endpoint {
  httpMethod: string;
  path: string;
  requestDto: string;
  responseDto: string;
  summary: string;
}
export interface ControllerSpec {
  name: string;
  basePath: string;
  endpoints: Endpoint[];
}
export interface ExceptionType {
  name: string;
  parent: string;
  httpStatus: number;
  when: string;
}
export interface ExceptionHierarchy {
  base: string;
  types: ExceptionType[];
}
export interface Lld {
  packages: PackageNode[];
  entities: JavaType[];
  dtos: JavaType[];
  repositories: RepoSpec[];
  services: ServiceSpec[];
  controllers: ControllerSpec[];
  configs: string[];
  utils: string[];
  exceptions: ExceptionHierarchy;
}

export interface Diagram {
  mermaid: string;
  plantUml: string;
}
export interface Diagrams {
  sequence: Diagram | null;
  classDiagram: Diagram | null;
  component: Diagram | null;
  requestFlow: Diagram | null;
}

export interface BuildPhase {
  order: number;
  title: string;
  goal: string;
  dependsOn: string[];
  fileName: string;
  markdown: string;
}
export interface ClaudePrompts {
  phases: BuildPhase[];
}

export interface DesignSession {
  id: string;
  idea: string;
  createdAt: string;
  status: SessionStatus;
  error: string | null;
  agentErrors: string[];

  requirements: Requirements | null;
  modules: Modules | null;
  entities: EntityModel | null;
  architecture: Architecture | null;
  database: DatabaseDesign | null;
  lld: Lld | null;
  diagrams: Diagrams | null;
  prompts: ClaudePrompts | null;
}

export interface DesignSessionRef {
  id: string;
  status: string;
}
