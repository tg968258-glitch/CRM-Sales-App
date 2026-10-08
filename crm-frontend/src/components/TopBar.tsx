import Link from "next/link";
import { ChartNoAxesCombined, Bell } from "lucide-react";

export default function Topbar() {
  return (
    <header className="bg-white border-b border-gray-200">
      <nav className="flex items-center px-6 py-3 gap-8">

        <div className="flex items-center gap-2 shrink-0">
          <ChartNoAxesCombined size={25} className="text-blue-600" />
          <h2 className="text-xl font-bold whitespace-nowrap">
            CRM Sales
          </h2>
        </div>

       <div className="flex flex-1 items-center gap-8 pl-10">
          <Link href="/dashboard">Dashboard</Link>
          <Link href="/leads">Leads</Link>
          <Link href="/accounts">Accounts</Link>
          <Link href="/contacts">Contacts</Link>
          <Link href="/deals">Deals</Link>
          <Link href="/activities">Activities</Link>
        </div>

        <div className="flex items-center gap-5 ml-6 shrink-0">
          <button type="button" aria-label="Notifications">
            <Bell size={21} />
          </button>
          <p className="text-sm font-medium">Admin</p>
        </div>

      </nav>
    </header>
  );
}