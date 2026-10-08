"use client";
import { useEffect, useState } from "react";
import ResourceWorkspace, { avatarColumn, statusColumn } from "@/components/ResourceWorkspace";
import { lifecycleStatuses, options } from "@/lib/crm";
import { fetchAll } from "@/lib/api";
export default function ContactsPage() {
  const [accounts, setAccounts] = useState<{ accId: number; accountName: string }[]>([]);
  useEffect(() => { fetchAll<{ accId: number; accountName: string }>("/accounts").then(setAccounts).catch(() => setAccounts([])); }, []);
  return <ResourceWorkspace title="Contacts" singular="Contact" description="Manage the people connected to your customer accounts." endpoint="/contacts" idKey="contactId"
    columns={[avatarColumn("name", "Contact"), { key: "jobTitle", label: "Job title" }, { key: "email", label: "Email" }, { key: "phoneNumber", label: "Phone" }, { key: "accountId", label: "Account", format: (value) => accounts.find((item) => item.accId === Number(value))?.accountName || `#${value}` }, statusColumn("lifecycleStatus", "Lifecycle")]}
    filters={[{ key: "lifecycleStatus", label: "lifecycle stages", options: lifecycleStatuses }]}
    fields={[{ key: "accountId", label: "Account", type: "select", required: true, options: accounts.map((item) => ({ value: item.accId, label: item.accountName })) }, { key: "contactOwnerId", label: "Owner ID", type: "number", required: true }, { key: "name", label: "Name", required: true }, { key: "email", label: "Email", type: "email" }, { key: "jobTitle", label: "Job title" }, { key: "phoneNumber", label: "Phone" }, { key: "lifecycleStatus", label: "Lifecycle status", type: "select", options: options(lifecycleStatuses) }]}/>;
}
