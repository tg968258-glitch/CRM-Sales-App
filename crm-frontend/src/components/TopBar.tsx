
"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import { BellRing, Inbox, LogOut, Menu, X } from "lucide-react";
import {
    ChartNoAxesCombined,
    Bell,
    CircleUserRound,
} from "lucide-react";

const menuItems = [
    { name: "Dashboard", path: "/dashboard" },
    { name: "Leads", path: "/leads" },
    { name: "Accounts", path: "/accounts" },
    { name: "Contacts", path: "/contacts" },
    { name: "Deals", path: "/deals" },
    { name: "Activities", path: "/activities" },
    { name: "Reports", path: "/reports" },
];

type Notification = {
    notificationId: number;
    message: string;
    status: string;
    type?: string;
    created_at?: string;
};

function notificationDate(value?: string) {
    if (!value) return "";
    return new Intl.DateTimeFormat("en-IN", {
        day: "numeric",
        month: "short",
        hour: "numeric",
        minute: "2-digit",
    }).format(new Date(value));
}

export default function Topbar() {
    const pathname = usePathname();
    const router = useRouter();
    const [open, setOpen] = useState(false);
    const [profile, setProfile] = useState<{ name: string; email: string; roleName?: string } | null>(null);
    const [notifications, setNotifications] = useState<Notification[]>([]);
    const [showNotifications, setShowNotifications] = useState(false);
    const [showAllNotifications, setShowAllNotifications] = useState(false);
    const [notificationError, setNotificationError] = useState("");
    const [isAdmin, setIsAdmin] = useState(false);

    useEffect(() => {
        Promise.all([
            fetch("/api/backend/profile", { cache: "no-store" }).then((r) => r.ok ? r.json() : null),
            fetch("/api/backend/notifications/my", { cache: "no-store" }).then((r) => r.ok ? r.json() : []),
        ]).then(([user, alerts]) => {
            setProfile(user);
            setNotifications([...alerts].sort((a: Notification, b: Notification) => new Date(b.created_at || 0).getTime() - new Date(a.created_at || 0).getTime()));
            setIsAdmin(user?.roleName === "ADMIN");
        });
    }, [pathname]);

    async function logout() {
        await fetch("/api/session/logout", { method: "POST" });
        router.replace("/login");
    }

    async function markAsRead(notification: Notification) {
        if (notification.status?.toLowerCase() !== "unread") return;
        setNotificationError("");
        const response = await fetch(`/api/backend/notifications/${notification.notificationId}/read`, { method: "PUT" });
        if (!response.ok) {
            setNotificationError("Unable to mark this notification as read.");
            return;
        }
        setNotifications((current) => current.map((item) => item.notificationId === notification.notificationId ? { ...item, status: "Read" } : item));
    }

    const unread = notifications.filter((item) => item.status?.toLowerCase() === "unread").length;
    const recentNotifications = notifications.slice(0, 5);

    const notificationList = (items: Notification[]) => items.map((item) => {
        const isUnread = item.status?.toLowerCase() === "unread";
        return <button key={item.notificationId} onClick={() => markAsRead(item)} className={`flex w-full gap-3 border-t border-slate-100 px-3 py-3 text-left transition-colors ${isUnread ? "bg-blue-50/40 hover:bg-blue-50" : "hover:bg-slate-50"}`}>
            <span className="mt-1.5 flex size-2 shrink-0">{isUnread && <span className="size-2 rounded-full bg-blue-600"/>}</span>
            <span className="min-w-0 flex-1"><span className={`block text-sm leading-5 ${isUnread ? "font-medium text-slate-900" : "text-slate-600"}`}>{item.message}</span><span className="mt-1 block text-xs text-slate-400">{notificationDate(item.created_at)}</span></span>
        </button>;
    });

    return (
        <header className="sticky top-0 z-40 border-b border-slate-200 bg-white/95 backdrop-blur">
            <nav className="mx-auto flex max-w-[1440px] flex-wrap items-center gap-x-7 px-4 py-3 sm:px-6 lg:px-8">

                <Link
                    href="/dashboard"
                    className="flex shrink-0 items-center gap-2"
                >
                    <ChartNoAxesCombined
                        size={26}
                        className="text-blue-600"
                    />
                    <span className="text-xl font-bold text-gray-900">
            CRM Sales
          </span>
                </Link>

                <div className={`${open ? "flex" : "hidden"} order-3 mt-3 w-full flex-col gap-1 border-t border-slate-100 pt-3 lg:order-none lg:mt-0 lg:flex lg:w-auto lg:flex-1 lg:flex-row lg:border-0 lg:pt-0`}>
                    {[...menuItems, ...(isAdmin ? [{ name: "Users", path: "/users" }] : [])].map((item) => {
                        const active =
                            pathname === item.path ||
                            pathname.startsWith(item.path + "/");

                        return (
                            <Link
                                key={item.path}
                                href={item.path}
                                onClick={() => setOpen(false)}
                                className={`whitespace-nowrap rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
                                    active
                                        ? "bg-blue-50 text-blue-700"
                                        : "text-gray-600 hover:bg-gray-100 hover:text-gray-900"
                                }`}
                            >
                                {item.name}
                            </Link>
                        );
                    })}
                </div>

                <div className="relative ml-auto flex shrink-0 items-center gap-2 sm:gap-3">
                    <button onClick={() => setOpen(!open)} className="rounded-lg p-2 text-slate-600 hover:bg-slate-100 lg:hidden" aria-label="Toggle navigation">{open ? <X size={20}/> : <Menu size={20}/>}</button>
                    <button onClick={() => setShowNotifications(!showNotifications)} className="relative rounded-lg p-2 text-slate-600 hover:bg-slate-100" aria-label="Notifications">
                        <Bell size={20} />
                        {unread > 0 && <span className="absolute right-0.5 top-0.5 grid h-4 min-w-4 place-items-center rounded-full bg-blue-600 px-1 text-[10px] font-bold text-white">{unread}</span>}
                    </button>
                    {showNotifications && <div className="absolute right-0 top-12 w-[min(23rem,calc(100vw-2rem))] overflow-hidden rounded-xl border border-slate-200 bg-white shadow-xl">
                        <div className="flex items-center justify-between px-4 py-3"><div><p className="text-sm font-semibold text-slate-900">Notifications</p>{unread > 0 && <p className="mt-0.5 text-xs text-slate-500">{unread} unread</p>}</div><BellRing size={17} className="text-slate-400"/></div>
                        {notificationError && <p className="border-t border-rose-100 bg-rose-50 px-4 py-2 text-xs text-rose-700">{notificationError}</p>}
                        {recentNotifications.length === 0 ? <div className="flex flex-col items-center border-t border-slate-100 px-4 py-8 text-center"><span className="grid size-9 place-items-center rounded-full bg-slate-100 text-slate-400"><Inbox size={17}/></span><p className="mt-3 text-sm font-medium text-slate-700">No notifications</p><p className="mt-1 text-xs text-slate-400">You are all caught up.</p></div> : notificationList(recentNotifications)}
                        {notifications.length > 5 && <button onClick={() => { setShowAllNotifications(true); setShowNotifications(false); }} className="w-full border-t border-slate-200 px-4 py-3 text-center text-sm font-semibold text-blue-700 hover:bg-blue-50">View all notifications</button>}
                    </div>}

                    <Link href="/profile" className="hidden items-center gap-2 border-l border-gray-200 pl-3 sm:flex">
                        <CircleUserRound
                            size={27}
                            className="text-gray-500"
                        />
                        <span className="text-sm font-medium text-gray-700">
                            {profile?.name || "Profile"}
            </span>
                    </Link>
                    <button onClick={logout} title="Sign out" className="rounded-lg p-2 text-slate-500 hover:bg-rose-50 hover:text-rose-600"><LogOut size={19}/></button>
                </div>

            </nav>
            {showAllNotifications && <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/35 p-4" onMouseDown={(event) => event.target === event.currentTarget && setShowAllNotifications(false)}><section className="max-h-[85vh] w-full max-w-xl overflow-hidden rounded-xl border border-slate-200 bg-white shadow-xl"><div className="flex items-start justify-between border-b border-slate-200 px-5 py-4"><div><h2 className="text-lg font-semibold text-slate-950">Notification history</h2><p className="mt-1 text-sm text-slate-500">{notifications.length} notifications · {unread} unread</p></div><button onClick={() => setShowAllNotifications(false)} className="rounded-md p-2 text-slate-500 hover:bg-slate-100" aria-label="Close notification history"><X size={18}/></button></div><div className="max-h-[calc(85vh-5rem)] overflow-y-auto">{notificationError && <p className="bg-rose-50 px-5 py-2 text-xs text-rose-700">{notificationError}</p>}{notifications.length === 0 ? <div className="flex flex-col items-center px-5 py-14 text-center"><Inbox size={22} className="text-slate-400"/><p className="mt-3 text-sm font-medium">No notifications</p></div> : notificationList(notifications)}</div></section></div>}
        </header>
    );
}
