"use client";

import { useEffect, useState } from "react";
import { Plus, ShieldCheck, UserRound, X } from "lucide-react";
import { ApiError, apiGet, apiPost } from "@/lib/api";

type User = { uid: number; name: string; email: string; roleName?: string; active?: boolean; isActive?: boolean };
const roles = ["ADMIN", "SALES_MANAGER", "SALES_EXECUTIVE"];

export default function UsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [creating, setCreating] = useState(false);
  const [saving, setSaving] = useState(false);

  async function load() {
    setError("");
    try { setUsers(await apiGet<User[]>("/api/users")); }
    catch (err) { setError(err instanceof ApiError && err.status === 403 ? "Only administrators can manage users." : err instanceof Error ? err.message : "Unable to load users."); }
    finally { setLoading(false); }
  }
  useEffect(() => { apiGet<User[]>("/api/users").then(setUsers).catch((err) => setError(err instanceof ApiError && err.status === 403 ? "Only administrators can manage users." : err.message)).finally(() => setLoading(false)); }, []);

  async function submit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault(); setSaving(true); setError("");
    const form = new FormData(event.currentTarget);
    try {
      await apiPost("/api/users", { uid: Number(form.get("uid")), name: form.get("name"), email: form.get("email"), password: form.get("password"), role: form.get("role") });
      setCreating(false); await load();
    } catch (err) { setError(err instanceof Error ? err.message : "Unable to create user."); }
    finally { setSaving(false); }
  }

  return <div className="space-y-5">
    <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center"><div><div className="flex items-center gap-3"><h1 className="text-2xl font-semibold tracking-tight text-slate-950">Users</h1>{!loading && <span className="rounded-full border border-slate-200 bg-white px-2.5 py-1 text-xs font-medium text-slate-500">{users.length}</span>}</div><p className="mt-1.5 text-sm text-slate-500">Create accounts and review access to the CRM.</p></div><button onClick={() => setCreating(true)} className="inline-flex items-center justify-center gap-2 rounded-md bg-blue-600 px-3.5 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-blue-700"><Plus size={16}/> Add user</button></div>
    {error && <div className="rounded-lg border border-rose-200 bg-rose-50 p-4 text-sm text-rose-700">{error}</div>}
    <section className="overflow-hidden rounded-lg border border-slate-200 bg-white"><div className="border-b border-slate-200 bg-slate-50/60 px-4 py-3"><p className="text-sm font-medium text-slate-700">Team access</p></div><div className="overflow-x-auto"><table className="w-full min-w-[700px] text-left text-sm"><thead className="border-b border-slate-200 bg-slate-50/80 text-xs text-slate-500"><tr><th className="px-4 py-3 font-semibold">User</th><th className="px-4 py-3 font-semibold">Email</th><th className="px-4 py-3 font-semibold">Role</th><th className="px-4 py-3 font-semibold">User ID</th><th className="px-4 py-3 font-semibold">Status</th></tr></thead><tbody className="divide-y divide-slate-100">{users.map((user) => { const initials=user.name.split(/\s+/).slice(0,2).map((part)=>part[0]).join("").toUpperCase(); const active=user.active ?? user.isActive; return <tr key={user.uid} className="hover:bg-blue-50/30"><td className="px-4 py-3.5"><span className="flex items-center gap-3"><span className="grid size-8 place-items-center rounded-full bg-blue-50 text-xs font-semibold text-blue-700">{initials}</span><span className="font-medium text-slate-900">{user.name}</span></span></td><td className="px-4 py-3.5 text-slate-600">{user.email}</td><td className="px-4 py-3.5"><span className="rounded-full border border-slate-200 bg-slate-50 px-2.5 py-1 text-xs font-medium text-slate-700">{user.roleName?.replaceAll("_", " ") || "-"}</span></td><td className="px-4 py-3.5 text-slate-600">#{user.uid}</td><td className="px-4 py-3.5"><span className={`inline-flex rounded-full border px-2.5 py-1 text-xs font-medium ${active ? "border-emerald-200 bg-emerald-50 text-emerald-700" : "border-slate-200 bg-slate-50 text-slate-600"}`}>{active ? "Active" : "Inactive"}</span></td></tr>; })}</tbody></table></div>{loading && <p className="p-10 text-center text-sm text-slate-500">Loading users...</p>}{!loading && users.length === 0 && !error && <div className="flex flex-col items-center py-14 text-center"><UserRound className="text-slate-400"/><p className="mt-3 text-sm font-semibold text-slate-800">No users found</p></div>}</section>
    {creating && <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/35 p-4" onMouseDown={(e)=>e.target===e.currentTarget&&setCreating(false)}><form onSubmit={submit} autoComplete="off" className="w-full max-w-xl rounded-xl border border-slate-200 bg-white shadow-xl"><div className="flex items-start justify-between border-b border-slate-200 px-6 py-5"><div><h2 className="text-lg font-semibold text-slate-950">Create user</h2><p className="mt-1 text-sm text-slate-500">Assign an initial role and temporary password.</p></div><button type="button" onClick={()=>setCreating(false)} className="rounded-md p-2 text-slate-500 hover:bg-slate-100"><X size={18}/></button></div><div className="grid gap-4 p-6 sm:grid-cols-2"><label className="text-sm font-medium text-slate-700">User ID *<input name="uid" type="number" min="1" required className="mt-1.5 w-full rounded-md border border-slate-300 px-3 py-2.5 outline-none focus:border-blue-500"/></label><label className="text-sm font-medium text-slate-700">Role *<select name="role" required className="mt-1.5 w-full rounded-md border border-slate-300 bg-white px-3 py-2.5">{roles.map((role)=><option key={role} value={role}>{role.replaceAll("_"," ")}</option>)}</select></label><label className="text-sm font-medium text-slate-700">Name *<input name="name" required className="mt-1.5 w-full rounded-md border border-slate-300 px-3 py-2.5 outline-none focus:border-blue-500"/></label><label className="text-sm font-medium text-slate-700">Email *<input name="email" type="email" required autoComplete="off" className="mt-1.5 w-full rounded-md border border-slate-300 px-3 py-2.5 outline-none focus:border-blue-500"/></label><label className="text-sm font-medium text-slate-700 sm:col-span-2">Temporary password *<input name="password" type="password" minLength={8} required autoComplete="new-password" className="mt-1.5 w-full rounded-md border border-slate-300 px-3 py-2.5 outline-none focus:border-blue-500"/><span className="mt-1.5 flex items-center gap-1.5 text-xs font-normal text-slate-500"><ShieldCheck size={13}/> At least 8 characters recommended.</span></label></div><div className="flex justify-end gap-3 border-t border-slate-200 bg-slate-50/60 px-6 py-4"><button type="button" onClick={()=>setCreating(false)} className="rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700">Cancel</button><button disabled={saving} className="rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white disabled:opacity-60">{saving?"Creating...":"Create user"}</button></div></form></div>}
  </div>;
}
