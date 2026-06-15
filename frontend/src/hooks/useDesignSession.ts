import { useQuery } from '@tanstack/react-query';
import { getSession } from '../api/client';
import type { DesignSession } from '../api/types';

/**
 * Loads the session once. Steps are synchronous (each run returns the updated session,
 * which the page writes back into this query's cache), so no polling is needed.
 */
export function useDesignSession(id?: string) {
  return useQuery<DesignSession>({
    queryKey: ['session', id],
    queryFn: () => getSession(id as string),
    enabled: !!id,
    refetchInterval: false,
    refetchOnWindowFocus: false,
  });
}
