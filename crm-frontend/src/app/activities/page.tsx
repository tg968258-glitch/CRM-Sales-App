"use client";
import ResourceWorkspace, { statusColumn } from "@/components/ResourceWorkspace";
import { activityTypes, options } from "@/lib/crm";
import { CalendarCheck, Mail, MessageSquare, Phone, SquareCheckBig, UsersRound } from "lucide-react";
const currentTime = Date.now();
const activityIcon = (value: unknown) => {
  const type = String(value || "Activity");
  const Icon = type === "Call" ? Phone : type === "Email" ? Mail : type === "Meeting" ? UsersRound : type === "Task" ? SquareCheckBig : type === "Comment" ? MessageSquare : CalendarCheck;
  return <span className="flex items-center gap-2.5"><span className="grid size-8 place-items-center rounded-md bg-blue-50 text-blue-700"><Icon size={15}/></span><span className="font-medium text-slate-800">{type}</span></span>;
};
export default function ActivitiesPage() {
  return <ResourceWorkspace title="Activities" singular="Activity" description="Plan calls, meetings, tasks and customer follow-ups." endpoint="/activities" idKey="id"
    columns={[{ key: "activityType", label: "Type", format: activityIcon }, { key: "subject", label: "Subject" }, statusColumn("status", "Status"), { key: "dueAt", label: "Due", format: (value) => { if (!value) return "-"; const date = new Date(String(value)); const overdue = date.getTime() < currentTime; return <span className={overdue ? "font-medium text-rose-600" : "text-slate-600"}>{date.toLocaleString("en-IN")}{overdue ? " · Overdue" : ""}</span>; } }, { key: "dealId", label: "Deal" }, { key: "leadId", label: "Lead" }]}
    filters={[{ key: "activityType", label: "types", options: activityTypes }]}
    fields={[{ key: "activityType", label: "Type", type: "select", options: options(activityTypes) }, { key: "subject", label: "Subject", required: true }, { key: "status", label: "Status" }, { key: "dueAt", label: "Due date", type: "datetime-local" }, { key: "completedAt", label: "Completed date", type: "datetime-local" }, { key: "dealId", label: "Deal ID", type: "number" }, { key: "leadId", label: "Lead ID", type: "number" }]}/>;
}
