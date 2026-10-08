"use client";

import { useState } from "react";
import { ArrowRightCircle, CheckCircle2, X } from "lucide-react";
import ResourceWorkspace, { avatarColumn, money, statusColumn } from "@/components/ResourceWorkspace";
import { apiGet, apiPut } from "@/lib/api";
import { industries, leadRatings, leadSources, leadStatuses, lifecycleStatuses, options, salutations } from "@/lib/crm";

type Lead = { leadId: number; name: string; companyName: string | null; status: string };
type DealStage = { dealStageId: number; stage: string; displayOrder: number; active?: boolean; isActive?: boolean };

export default function LeadsPage() {
  const [converting, setConverting] = useState<Lead | null>(null);
  const [stages, setStages] = useState<DealStage[]>([]);
  const [conversionError, setConversionError] = useState("");
  const [conversionNotice, setConversionNotice] = useState("");
  const [loadingStages, setLoadingStages] = useState(false);
  const [saving, setSaving] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);

  async function openConversion(row: Record<string, unknown>) {
    setConversionError("");
    setLoadingStages(true);
    setConverting(row as Lead);
    try {
      const data = await apiGet<DealStage[]>("/deal-stages");
      setStages(data.filter((stage) => stage.active ?? stage.isActive ?? true).sort((a, b) => a.displayOrder - b.displayOrder));
    } catch (error) {
      setConversionError(error instanceof Error ? error.message : "Unable to load deal stages.");
    } finally {
      setLoadingStages(false);
    }
  }

  async function convertLead(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!converting) return;
    setSaving(true);
    setConversionError("");
    const form = new FormData(event.currentTarget);
    const expectedCloseDate = String(form.get("expectedCloseDate") || "");
    try {
      await apiPut(`/leads/${converting.leadId}/convert`, {
        industry: form.get("industry"),
        lifecycleStatus: form.get("lifecycleStatus"),
        dealTitle: form.get("dealTitle"),
        dealStageId: Number(form.get("dealStageId")),
        expectedCloseDate: expectedCloseDate ? `${expectedCloseDate}T00:00:00` : null,
      });
      setConverting(null);
      setConversionNotice(`${converting.name} was converted successfully.`);
      setRefreshKey((key) => key + 1);
    } catch (error) {
      setConversionError(error instanceof Error ? error.message : "Unable to convert this lead.");
    } finally {
      setSaving(false);
    }
  }

  return <>
    <ResourceWorkspace key={refreshKey} title="Leads" singular="Lead" description="Capture, qualify and manage potential customers." endpoint="/leads" idKey="leadId"
      columns={[avatarColumn("name", "Lead"), { key: "companyName", label: "Company" }, { key: "email", label: "Email" }, statusColumn("status", "Status"), statusColumn("leadRating", "Rating"), { key: "expectedValue", label: "Expected value", format: money }]}
      filters={[{ key: "status", label: "statuses", options: leadStatuses }, { key: "leadRating", label: "ratings", options: leadRatings }]}
      fields={[{ key: "ownerId", label: "Owner ID", type: "number", required: true }, { key: "salutation", label: "Salutation", type: "select", options: options(salutations) }, { key: "name", label: "Name", required: true }, { key: "email", label: "Email", type: "email", required: true }, { key: "phoneNumber", label: "Phone" }, { key: "companyName", label: "Company" }, { key: "source", label: "Source", type: "select", options: options(leadSources) }, { key: "status", label: "Status", type: "select", required: true, options: options(leadStatuses) }, { key: "leadRating", label: "Rating", type: "select", options: options(leadRatings) }, { key: "expectedValue", label: "Expected value", type: "number" }, { key: "notes", label: "Notes", type: "textarea" }]}
      rowActions={(row) => row.status === "Qualified" ? <button onClick={() => openConversion(row)} className="border-r border-slate-200 p-1.5 text-blue-600 hover:bg-blue-50 hover:text-blue-800 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500" aria-label={`Convert ${String(row.name)}`} title="Convert lead"><ArrowRightCircle size={15}/></button> : null}/>

    {converting && <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/35 p-4" onMouseDown={(event) => event.target === event.currentTarget && setConverting(null)}>
      <form onSubmit={convertLead} className="max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-xl border border-slate-200 bg-white shadow-xl">
        <div className="flex items-start justify-between border-b border-slate-200 px-6 py-5"><div><p className="text-xs font-semibold uppercase tracking-wide text-blue-600">Qualified lead</p><h2 className="mt-1 text-lg font-semibold text-slate-950">Convert {converting.name}</h2><p className="mt-1 text-sm text-slate-500">Create an account, contact, and initial deal for {converting.companyName || "this lead"}.</p></div><button type="button" onClick={() => setConverting(null)} className="rounded-md p-2 text-slate-500 hover:bg-slate-100" aria-label="Close"><X size={18}/></button></div>
        {conversionError && <div role="alert" className="mx-6 mt-5 rounded-lg border border-rose-200 bg-rose-50 p-3 text-sm text-rose-700">{conversionError}</div>}
        <div className="grid gap-x-5 gap-y-4 p-6 sm:grid-cols-2">
          <SelectField name="industry" label="Industry" options={options(industries)} required/>
          <SelectField name="lifecycleStatus" label="Contact lifecycle" options={options(lifecycleStatuses)} required/>
          <label className="sm:col-span-2"><span className="mb-1.5 block text-sm font-medium text-slate-700">Deal title *</span><input name="dealTitle" required defaultValue={`${converting.companyName || converting.name} Opportunity`} className="w-full rounded-md border border-slate-300 px-3 py-2.5 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"/></label>
          <SelectField name="dealStageId" label="Initial deal stage" options={stages.map((stage) => ({ value: stage.dealStageId, label: stage.stage }))} required disabled={loadingStages}/>
          <label><span className="mb-1.5 block text-sm font-medium text-slate-700">Expected close date *</span><input name="expectedCloseDate" type="date" required className="w-full rounded-md border border-slate-300 px-3 py-2.5 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"/></label>
        </div>
        <div className="flex justify-end gap-3 border-t border-slate-200 bg-slate-50/60 px-6 py-4"><button type="button" onClick={() => setConverting(null)} className="rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700">Cancel</button><button disabled={saving || loadingStages || stages.length === 0} className="rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700 disabled:opacity-60">{saving ? "Converting..." : loadingStages ? "Loading stages..." : "Convert lead"}</button></div>
      </form>
    </div>}

    {conversionNotice && <div role="status" className="fixed bottom-5 right-5 z-[60] flex items-center gap-2 rounded-lg border border-emerald-200 bg-white px-4 py-3 text-sm font-medium text-slate-700 shadow-lg"><CheckCircle2 size={18} className="text-emerald-600"/>{conversionNotice}<button onClick={() => setConversionNotice("")} className="ml-2 text-slate-400 hover:text-slate-700" aria-label="Dismiss notification"><X size={15}/></button></div>}
  </>;
}

function SelectField({ name, label, options: selectOptions, required, disabled }: { name: string; label: string; options: { label: string; value: string | number }[]; required?: boolean; disabled?: boolean }) {
  return <label><span className="mb-1.5 block text-sm font-medium text-slate-700">{label}{required ? " *" : ""}</span><select name={name} required={required} disabled={disabled} defaultValue="" className="w-full rounded-md border border-slate-300 bg-white px-3 py-2.5 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 disabled:bg-slate-100"><option value="">Select {label.toLowerCase()}</option>{selectOptions.map((option) => <option key={option.value} value={option.value}>{option.label}</option>)}</select></label>;
}
