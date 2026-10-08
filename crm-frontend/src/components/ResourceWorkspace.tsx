"use client";

import { useEffect, useMemo, useState } from "react";
import { CheckCircle2, Eye, Inbox, Pencil, Plus, Search, Trash2, X } from "lucide-react";
import { apiDelete, apiPost, apiPut, fetchAll } from "@/lib/api";

export type Column = { key: string; label: string; format?: (value: unknown, row: Record<string, unknown>) => React.ReactNode };
export type Field = { key: string; label: string; type?: "text" | "email" | "number" | "date" | "datetime-local" | "select" | "textarea"; required?: boolean; options?: { label: string; value: string | number }[]; placeholder?: string };
type Props = { title: string; description: string; endpoint: string; idKey: string; singular: string; columns: Column[]; fields: Field[]; filters?: { key: string; label: string; options: string[] }[]; canCreate?: boolean; canDelete?: boolean; emptyMessage?: string; rowActions?: (row: Record<string, unknown>) => React.ReactNode };

const badgeTone = (value: unknown) => {
  const key = String(value ?? "").toLowerCase();
  if (["active", "customer", "qualified", "won", "completed", "hot"].includes(key)) return "border-emerald-200 bg-emerald-50 text-emerald-700";
  if (["inactive", "unqualified", "lost", "failed", "cold"].includes(key)) return "border-rose-200 bg-rose-50 text-rose-700";
  if (["new", "open", "prospect", "delivered"].includes(key)) return "border-blue-200 bg-blue-50 text-blue-700";
  if (["warm", "pending", "contacted", "unread"].includes(key)) return "border-amber-200 bg-amber-50 text-amber-700";
  return "border-slate-200 bg-slate-50 text-slate-700";
};
const badge = (value: unknown) => <span className={`inline-flex rounded-full border px-2.5 py-1 text-xs font-medium capitalize ${badgeTone(value)}`}>{String(value ?? "-").replaceAll("_", " ")}</span>;
export const statusColumn = (key: string, label: string): Column => ({ key, label, format: badge });
export const money = (value: unknown) => value === null || value === undefined ? "-" : new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 0 }).format(Number(value));
export const avatarColumn = (key: string, label: string): Column => ({ key, label, format: (value) => {
  const name = String(value || "Unknown");
  const initials = name.split(/\s+/).slice(0, 2).map((part) => part[0]).join("").toUpperCase();
  return <span className="flex items-center gap-3"><span className="grid size-8 shrink-0 place-items-center rounded-full bg-blue-50 text-xs font-semibold text-blue-700">{initials}</span><span className="font-medium text-slate-900">{name}</span></span>;
} });

export default function ResourceWorkspace({ title, description, endpoint, idKey, singular, columns, fields, filters = [], canCreate = true, canDelete = true, emptyMessage, rowActions }: Props) {
  const [rows, setRows] = useState<Record<string, unknown>[]>([]);
  const [query, setQuery] = useState("");
  const [filterValues, setFilterValues] = useState<Record<string, string>>({});
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [editing, setEditing] = useState<Record<string, unknown> | null | undefined>();
  const [viewing, setViewing] = useState<Record<string, unknown> | null>(null);
  const [deleting, setDeleting] = useState<Record<string, unknown> | null>(null);
  const [notice, setNotice] = useState("");
  const [saving, setSaving] = useState(false);
  const pageSize = 10;

  async function load() {
    setLoading(true); setError("");
    try { setRows(await fetchAll<Record<string, unknown>>(endpoint)); }
    catch (err) { setError(err instanceof Error ? err.message : `Unable to load ${title.toLowerCase()}.`); }
    finally { setLoading(false); }
  }
  useEffect(() => {
    let active = true;
    fetchAll<Record<string, unknown>>(endpoint)
      .then((items) => { if (active) setRows(items); })
      .catch((err) => { if (active) setError(err instanceof Error ? err.message : `Unable to load ${title.toLowerCase()}.`); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [endpoint, title]);

  const filtered = useMemo(() => rows.filter((row) => {
    const matchesQuery = !query || Object.values(row).some((value) => String(value ?? "").toLowerCase().includes(query.toLowerCase()));
    const matchesFilters = filters.every((filter) => !filterValues[filter.key] || String(row[filter.key]) === filterValues[filter.key]);
    return matchesQuery && matchesFilters;
  }), [rows, query, filters, filterValues]);
  const pages = Math.max(1, Math.ceil(filtered.length / pageSize));
  const visible = filtered.slice((page - 1) * pageSize, page * pageSize);

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault(); setSaving(true); setError("");
    const form = new FormData(event.currentTarget);
    const payload = Object.fromEntries(fields.map((field) => {
      const raw = form.get(field.key)?.toString() ?? "";
      if (field.type === "number") return [field.key, raw === "" ? null : Number(raw)];
      if (field.type === "datetime-local") return [field.key, raw ? `${raw}:00` : null];
      return [field.key, raw || null];
    }));
    try {
      const id = editing?.[idKey];
      if (id) await apiPut(`${endpoint}/${id}`, { ...editing, ...payload }); else await apiPost(endpoint, payload);
      setEditing(undefined); setNotice(`${singular} ${id ? "updated" : "created"} successfully.`); await load();
    } catch (err) { setError(err instanceof Error ? err.message : `Unable to save ${singular.toLowerCase()}.`); }
    finally { setSaving(false); }
  }

  async function remove(row: Record<string, unknown>) {
    try { await apiDelete(`${endpoint}/${row[idKey]}`); setDeleting(null); setNotice(`${singular} deleted successfully.`); await load(); }
    catch (err) { setError(err instanceof Error ? err.message : `Unable to delete ${singular.toLowerCase()}.`); }
  }

  return <div className="space-y-5">
    <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center"><div><div className="flex items-center gap-3"><h1 className="text-2xl font-semibold tracking-tight text-slate-950">{title}</h1>{!loading && <span className="rounded-full border border-slate-200 bg-white px-2.5 py-1 text-xs font-medium text-slate-500">{rows.length}</span>}</div><p className="mt-1.5 text-sm text-slate-500">{description}</p></div>{canCreate && <button onClick={() => setEditing(null)} className="inline-flex items-center justify-center gap-2 rounded-md bg-blue-600 px-3.5 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-blue-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500"><Plus size={16}/> Add {singular}</button>}</div>
    {error && <div role="alert" className="rounded-xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-700">{error}</div>}
    <section className="overflow-hidden rounded-lg border border-slate-200 bg-white">
      <div className="flex flex-col gap-3 border-b border-slate-200 bg-slate-50/40 p-3 sm:flex-row">
        <label className="relative min-w-0 flex-1"><Search className="absolute left-3 top-2.5 text-slate-400" size={17}/><input value={query} onChange={(e) => { setQuery(e.target.value); setPage(1); }} placeholder={`Search ${title.toLowerCase()}...`} className="w-full rounded-md border border-slate-300 bg-white py-2 pl-9 pr-3 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"/></label>
        {filters.map((filter) => <select key={filter.key} aria-label={`Filter by ${filter.label}`} value={filterValues[filter.key] || ""} onChange={(e) => { setFilterValues({ ...filterValues, [filter.key]: e.target.value }); setPage(1); }} className="rounded-md border border-slate-300 bg-white px-3 py-2 text-sm text-slate-700 outline-none focus:border-blue-500"><option value="">All {filter.label}</option>{filter.options.map((option) => <option key={option} value={option}>{option.replaceAll("_", " ")}</option>)}</select>)}
      </div>
      <div className="overflow-x-auto"><table className="w-full min-w-[760px] text-left text-sm"><thead className="border-b border-slate-200 bg-slate-50/80 text-xs text-slate-500"><tr>{columns.map((column) => <th key={column.key} className="px-4 py-3 font-semibold">{column.label}</th>)}<th className="px-4 py-3 text-right font-semibold">Actions</th></tr></thead><tbody className="divide-y divide-slate-100">{visible.map((row) => <tr key={String(row[idKey])} className="group transition-colors hover:bg-blue-50/30">{columns.map((column) => <td key={column.key} className="whitespace-nowrap px-4 py-3.5 text-slate-600">{column.format ? column.format(row[column.key], row) : String(row[column.key] ?? "-")}</td>)}<td className="px-4 py-3 text-right"><span className="inline-flex rounded-md border border-slate-200 bg-white shadow-sm">{rowActions?.(row)}<button onClick={() => setViewing(row)} className="border-r border-slate-200 p-1.5 text-slate-500 hover:bg-slate-50 hover:text-blue-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500" aria-label={`View ${singular}`} title="View"><Eye size={15}/></button><button onClick={() => setEditing(row)} className={`${canDelete ? "border-r" : ""} border-slate-200 p-1.5 text-slate-500 hover:bg-slate-50 hover:text-blue-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500`} aria-label={`Edit ${singular}`} title="Edit"><Pencil size={15}/></button>{canDelete && <button onClick={() => setDeleting(row)} className="p-1.5 text-slate-500 hover:bg-rose-50 hover:text-rose-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-rose-500" aria-label={`Delete ${singular}`} title="Delete"><Trash2 size={15}/></button>}</span></td></tr>)}</tbody></table></div>
      {loading ? <div className="space-y-3 p-5">{[1,2,3,4].map((item) => <div key={item} className="h-11 animate-pulse rounded-md bg-slate-100"/>)}</div> : filtered.length === 0 ? <div className="flex flex-col items-center px-5 py-14 text-center"><span className="grid size-11 place-items-center rounded-full bg-slate-100 text-slate-400"><Inbox size={21}/></span><h2 className="mt-4 text-sm font-semibold text-slate-800">{query || Object.values(filterValues).some(Boolean) ? `No matching ${title.toLowerCase()}` : `No ${title.toLowerCase()} yet`}</h2><p className="mt-1 max-w-sm text-sm text-slate-500">{emptyMessage || (query ? "Try changing your search or clearing a filter." : canCreate ? `Use the Add ${singular} button above to create the first record.` : `No ${title.toLowerCase()} are currently available.`)}</p></div> : <div className="flex flex-col gap-3 border-t border-slate-200 px-4 py-3 text-sm text-slate-500 sm:flex-row sm:items-center sm:justify-between"><span>Showing {(page - 1) * pageSize + 1}-{Math.min(page * pageSize, filtered.length)} of {filtered.length} records</span><div className="flex items-center gap-2"><button disabled={page === 1} onClick={() => setPage(page - 1)} className="rounded-md border border-slate-300 bg-white px-3 py-1.5 text-slate-700 hover:bg-slate-50 disabled:opacity-40">Previous</button><span className="min-w-14 text-center">{page} of {pages}</span><button disabled={page === pages} onClick={() => setPage(page + 1)} className="rounded-md border border-slate-300 bg-white px-3 py-1.5 text-slate-700 hover:bg-slate-50 disabled:opacity-40">Next</button></div></div>}
    </section>
    {editing !== undefined && <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/35 p-4" onMouseDown={(e) => e.target === e.currentTarget && setEditing(undefined)}><form onSubmit={submit} className="max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-xl border border-slate-200 bg-white shadow-xl"><div className="flex items-start justify-between border-b border-slate-200 px-6 py-5"><div><h2 className="text-lg font-semibold text-slate-950">{editing ? `Edit ${singular}` : `Add ${singular}`}</h2><p className="mt-1 text-sm text-slate-500">Fields marked with * are required.</p></div><button type="button" onClick={() => setEditing(undefined)} className="rounded-md p-2 text-slate-500 hover:bg-slate-100"><X size={18}/></button></div><div className="grid gap-x-5 gap-y-4 p-6 sm:grid-cols-2">{fields.map((field) => <label key={field.key} className={field.type === "textarea" ? "sm:col-span-2" : ""}><span className="mb-1.5 block text-sm font-medium text-slate-700">{field.label}{field.required ? " *" : ""}</span>{field.type === "select" ? <select name={field.key} required={field.required} defaultValue={String(editing?.[field.key] ?? "")} className="w-full rounded-md border border-slate-300 bg-white px-3 py-2.5 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"><option value="">Select {field.label.toLowerCase()}</option>{field.options?.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}</select> : field.type === "textarea" ? <textarea name={field.key} defaultValue={String(editing?.[field.key] ?? "")} rows={4} className="w-full rounded-md border border-slate-300 px-3 py-2.5 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"/> : <input name={field.key} type={field.type || "text"} required={field.required} min={field.type === "number" ? 0 : undefined} defaultValue={field.type === "datetime-local" ? String(editing?.[field.key] ?? "").slice(0,16) : String(editing?.[field.key] ?? "")} placeholder={field.placeholder} className="w-full rounded-md border border-slate-300 px-3 py-2.5 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"/>}</label>)}</div><div className="flex justify-end gap-3 border-t border-slate-200 bg-slate-50/60 px-6 py-4"><button type="button" onClick={() => setEditing(undefined)} className="rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700">Cancel</button><button disabled={saving} className="rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700 disabled:opacity-60">{saving ? "Saving..." : `Save ${singular}`}</button></div></form></div>}
    {viewing && <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/35 p-4" onMouseDown={(e) => e.target === e.currentTarget && setViewing(null)}><div className="w-full max-w-xl rounded-xl border border-slate-200 bg-white shadow-xl"><div className="flex items-start justify-between border-b border-slate-200 px-6 py-5"><div><p className="text-xs font-semibold uppercase tracking-wide text-blue-600">{singular} details</p><h2 className="mt-1 text-lg font-semibold text-slate-950">{String(viewing[fields.find((field) => field.required)?.key || idKey] || `${singular} #${viewing[idKey]}`)}</h2></div><button onClick={() => setViewing(null)} className="rounded-md p-2 text-slate-500 hover:bg-slate-100"><X size={18}/></button></div><dl className="grid gap-x-6 gap-y-5 p-6 sm:grid-cols-2">{fields.map((field) => <div key={field.key} className={field.type === "textarea" ? "sm:col-span-2" : ""}><dt className="text-xs font-medium text-slate-500">{field.label}</dt><dd className="mt-1 text-sm font-medium text-slate-800">{String(viewing[field.key] ?? "-").replaceAll("_", " ")}</dd></div>)}</dl><div className="flex justify-end border-t border-slate-200 bg-slate-50/60 px-6 py-4"><button onClick={() => { setEditing(viewing); setViewing(null); }} className="inline-flex items-center gap-2 rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white"><Pencil size={15}/> Edit {singular}</button></div></div></div>}
    {deleting && <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/35 p-4" onMouseDown={(e) => e.target === e.currentTarget && setDeleting(null)}><div role="alertdialog" aria-modal="true" className="w-full max-w-md rounded-xl border border-slate-200 bg-white p-6 shadow-xl"><span className="grid size-10 place-items-center rounded-full bg-rose-50 text-rose-600"><Trash2 size={18}/></span><h2 className="mt-4 text-lg font-semibold text-slate-950">Delete {singular.toLowerCase()}?</h2><p className="mt-2 text-sm leading-6 text-slate-500">This record will be permanently removed. This action cannot be undone.</p><div className="mt-6 flex justify-end gap-3"><button onClick={() => setDeleting(null)} className="rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700">Cancel</button><button onClick={() => remove(deleting)} className="rounded-md bg-rose-600 px-4 py-2 text-sm font-semibold text-white hover:bg-rose-700">Delete</button></div></div></div>}
    {notice && <div role="status" className="fixed bottom-5 right-5 z-[60] flex items-center gap-2 rounded-lg border border-emerald-200 bg-white px-4 py-3 text-sm font-medium text-slate-700 shadow-lg"><CheckCircle2 size={18} className="text-emerald-600"/>{notice}<button onClick={() => setNotice("")} className="ml-2 text-slate-400 hover:text-slate-700" aria-label="Dismiss notification"><X size={15}/></button></div>}
  </div>;
}
