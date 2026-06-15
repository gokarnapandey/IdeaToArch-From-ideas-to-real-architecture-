export interface HistoryItem {
  id: string;
  idea: string;
  createdAt: number;
}

const KEY = 'arch-copilot-history';

export function loadHistory(): HistoryItem[] {
  try {
    const raw = localStorage.getItem(KEY);
    return raw ? (JSON.parse(raw) as HistoryItem[]) : [];
  } catch {
    return [];
  }
}

export function addHistory(item: HistoryItem): void {
  const items = loadHistory().filter((i) => i.id !== item.id);
  items.unshift(item);
  localStorage.setItem(KEY, JSON.stringify(items.slice(0, 20)));
}
