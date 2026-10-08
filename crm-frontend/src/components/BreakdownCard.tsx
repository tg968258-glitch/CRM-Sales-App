type BreakdownItem = {
  label: string;
  value: number;
  secondary?: string;
};

type Props = {
  title: string;
  subtitle: string;
  items: BreakdownItem[];
  formatValue?: (value: number) => string;
  totalLabel?: string;
};

const colors = ["#2563eb", "#06b6d4", "#8b5cf6", "#f59e0b", "#10b981", "#64748b"];

export default function BreakdownCard({ title, subtitle, items, formatValue = String, totalLabel = "Total" }: Props) {
  const total = items.reduce((sum, item) => sum + Number(item.value || 0), 0);
  const populated = items.filter((item) => item.value > 0);

  return <section className="rounded-lg border border-slate-200 bg-white p-4 sm:p-5">
    <div className="flex items-start justify-between gap-4">
      <div><h2 className="text-sm font-semibold text-slate-900">{title}</h2><p className="mt-1 text-xs leading-5 text-slate-500">{subtitle}</p></div>
      <div className="shrink-0 text-right"><p className="text-[11px] font-medium uppercase tracking-wide text-slate-400">{totalLabel}</p><p className="mt-0.5 text-lg font-semibold tracking-tight text-slate-900">{formatValue(total)}</p></div>
    </div>
    {populated.length === 0 ? <div className="mt-5 rounded-md border border-dashed border-slate-200 bg-slate-50/60 px-4 py-7 text-center"><p className="text-sm font-medium text-slate-600">No data available</p><p className="mt-1 text-xs text-slate-400">Breakdowns will appear when records are available.</p></div> : <>
      <div className="mt-5 flex h-2.5 w-full overflow-hidden rounded-full bg-slate-100" aria-label={`${title} distribution`}>
        {populated.map((item, index) => <span key={item.label} title={`${item.label}: ${formatValue(item.value)}`} style={{ width: `${item.value / total * 100}%`, backgroundColor: colors[index % colors.length] }} />)}
      </div>
      <div className="mt-4 grid gap-x-5 gap-y-3 sm:grid-cols-2">
        {items.map((item, index) => <div key={item.label} className="flex min-w-0 items-center justify-between gap-3">
          <span className="flex min-w-0 items-center gap-2 text-xs text-slate-600"><span className="size-2 shrink-0 rounded-full" style={{ backgroundColor: colors[index % colors.length] }}/><span className="truncate capitalize">{item.label.replaceAll("_", " ")}</span></span>
          <span className="shrink-0 text-right"><strong className="text-xs font-semibold text-slate-800">{formatValue(item.value)}</strong>{item.secondary && <span className="ml-1.5 text-[11px] text-slate-400">{item.secondary}</span>}</span>
        </div>)}
      </div>
    </>}
  </section>;
}
