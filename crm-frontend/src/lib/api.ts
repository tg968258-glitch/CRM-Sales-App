
export type PageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

type RequestOptions = Omit<RequestInit, "body"> & { body?: unknown };

export class ApiError extends Error {
  constructor(message: string, public status: number) {
    super(message);
  }
}

export async function apiRequest<T>(endpoint: string, options: RequestOptions = {}): Promise<T> {
  const response = await fetch(`/api/backend${endpoint}`, {
    ...options,
    cache: "no-store",
    headers: {
      ...(options.body !== undefined ? { "Content-Type": "application/json" } : {}),
      ...options.headers,
    },
    body: options.body === undefined ? undefined : JSON.stringify(options.body),
  });

  if (response.status === 401) window.dispatchEvent(new Event("crm:unauthorized"));
  if (!response.ok) {
    const payload = await response.text();
    let message = payload || `Request failed (${response.status})`;
    try {
      const parsed = JSON.parse(payload) as { message?: string; error?: string };
      message = parsed.message || parsed.error || message;
    } catch {}
    throw new ApiError(message, response.status);
  }
  if (response.status === 204) return undefined as T;
  const text = await response.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

export const apiGet = <T,>(endpoint: string) => apiRequest<T>(endpoint);
export const apiPost = <T,>(endpoint: string, body?: unknown) => apiRequest<T>(endpoint, { method: "POST", body });
export const apiPut = <T,>(endpoint: string, body: unknown) => apiRequest<T>(endpoint, { method: "PUT", body });
export const apiDelete = (endpoint: string) => apiRequest<void>(endpoint, { method: "DELETE" });

export async function fetchAll<T>(endpoint: string): Promise<T[]> {
  const first = await apiGet<PageResponse<T>>(`${endpoint}?page=0&size=100`);
  const items = [...first.content];
  for (let page = 1; page < first.totalPages; page += 1) {
    items.push(...(await apiGet<PageResponse<T>>(`${endpoint}?page=${page}&size=100`)).content);
  }
  return items;
}
