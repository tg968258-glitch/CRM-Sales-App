"use client";
import ResourceWorkspace, { statusColumn } from "@/components/ResourceWorkspace";
import { industries, options } from "@/lib/crm";
export default function AccountsPage() {
  return <ResourceWorkspace title="Accounts" singular="Account" description="Keep company records and ownership details organized." endpoint="/accounts" idKey="accId"
    columns={[{ key: "accountName", label: "Account" }, statusColumn("industry", "Industry"), { key: "phoneNumber", label: "Phone" }, { key: "website", label: "Website" }, { key: "accOwnerId", label: "Owner ID" }]}
    filters={[{ key: "industry", label: "industries", options: industries }]}
    fields={[{ key: "accOwnerId", label: "Owner ID", type: "number", required: true }, { key: "accountName", label: "Account name", required: true }, { key: "industry", label: "Industry", type: "select", options: options(industries) }, { key: "phoneNumber", label: "Phone" }, { key: "website", label: "Website", placeholder: "https://example.com" }]}/>;
}
