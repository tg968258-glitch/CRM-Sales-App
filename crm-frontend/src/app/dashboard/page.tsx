
"use client";

import { useEffect, useState } from "react";
import {
    Users,
    Handshake,
    IndianRupee,
    Trophy,
    CalendarClock,
} from "lucide-react";
import { apiGet, fetchAll } from "@/lib/api";

type Deal = {
    dealId: number;
    title: string;
    dealStatus: string;
    value: number | null;
    dealStageId: number | null;
};

type DealStage = {
    dealStageId: number;
    stage: string;
    displayOrder: number;
};

type Activity = {
    id: number;
    subject: string;
    activityType: string | null;
    status: string | null;
    dueAt: string | null;
};

type PageResponse<T> = {
    content: T[];
    totalElements: number;
    totalPages: number;
};

type DashboardStats = {
    totalLeads: number;
    openDeals: number;
    wonDeals: number;
    pipelineValue: number;
};

const initialStats: DashboardStats = {
    totalLeads: 0,
    openDeals: 0,
    wonDeals: 0,
    pipelineValue: 0,
};

async function getAllDeals(): Promise<Deal[]> {
    const firstPage = await apiGet<PageResponse<Deal>>(
        "/deals?page=0&size=100"
    );

    const deals = [...firstPage.content];

    for (let page = 1; page < firstPage.totalPages; page++) {
        const result = await apiGet<PageResponse<Deal>>(
            `/deals?page=${page}&size=100`
        );

        deals.push(...result.content);
    }

    return deals;
}

export default function DashboardPage() {
    const [stats, setStats] = useState(initialStats);
    const [deals, setDeals] = useState<Deal[]>([]);
    const [stages, setStages] = useState<DealStage[]>([]);
    const [activities, setActivities] = useState<Activity[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        async function loadDashboard() {
            try {
                const [leadsPage, dealData, stageData, activityData] = await Promise.all([
                    apiGet<PageResponse<unknown>>(
                        "/leads?page=0&size=1"
                    ),
                    getAllDeals(),
                    apiGet<DealStage[]>("/deal-stages"),
                    fetchAll<Activity>("/activities"),
                ]);

                const openDeals = dealData.filter(
                    (deal) => deal.dealStatus?.toLowerCase() === "open"
                );

                const wonDeals = dealData.filter(
                    (deal) => deal.dealStatus?.toLowerCase() === "won"
                );

                const pipelineValue = openDeals.reduce(
                    (total, deal) => total + Number(deal.value || 0),
                    0
                );

                setStats({
                    totalLeads: leadsPage.totalElements,
                    openDeals: openDeals.length,
                    wonDeals: wonDeals.length,
                    pipelineValue,
                });
                setDeals(dealData);
                setStages([...stageData].sort((a, b) => a.displayOrder - b.displayOrder));
                setActivities([...activityData].sort((a, b) => {
                    if (!a.dueAt) return 1;
                    if (!b.dueAt) return -1;
                    return new Date(a.dueAt).getTime() - new Date(b.dueAt).getTime();
                }).slice(0, 5));
            } catch (err) {
                setError(
                    err instanceof Error ? err.message : "Something went wrong"
                );
            } finally {
                setLoading(false);
            }
        }

        loadDashboard();
    }, []);

    const cards = [
        {
            title: "Total Leads",
            value: stats.totalLeads.toString(),
            icon: Users,
        },
        {
            title: "Open Deals",
            value: stats.openDeals.toString(),
            icon: Handshake,
        },
        {
            title: "Pipeline Value",
            value: new Intl.NumberFormat("en-IN", {
                style: "currency",
                currency: "INR",
                maximumFractionDigits: 0,
            }).format(stats.pipelineValue),
            icon: IndianRupee,
        },
        {
            title: "Won Deals",
            value: stats.wonDeals.toString(),
            icon: Trophy,
        },
    ];

    const dealStatuses = ["open", "won", "lost"].map((status) => ({
        status,
        count: deals.filter((deal) => deal.dealStatus?.toLowerCase() === status).length,
        value: deals
            .filter((deal) => deal.dealStatus?.toLowerCase() === status)
            .reduce((sum, deal) => sum + Number(deal.value || 0), 0),
    }));
    const maximumStageCount = Math.max(
        1,
        ...stages.map((stage) => deals.filter((deal) => deal.dealStageId === stage.dealStageId).length)
    );
    const maximumStatusCount = Math.max(1, ...dealStatuses.map((item) => item.count));

    return (
        <div className="space-y-7">
            <div>
                <h1 className="text-2xl font-semibold text-gray-900">
                    Overview
                </h1>
                <p className="mt-1 text-sm text-gray-500">
                    Track your sales pipeline and business performance
                </p>
            </div>

            {error && (
                <div className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">
                    {error}
                </div>
            )}

            <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
                {cards.map((card) => {
                    const Icon = card.icon;

                    return (
                        <div
                            key={card.title}
                            className="rounded-xl border border-pink-100 bg-pink-50 p-5"
                        >
                            <div className="flex items-center justify-between">
                                <p className="text-sm font-medium text-gray-600">
                                    {card.title}
                                </p>

                                <div className="rounded-lg bg-white p-2 text-blue-600">
                                    <Icon size={19} />
                                </div>
                            </div>

                            <h2 className="mt-4 text-3xl font-semibold text-gray-900">
                                {loading || error ? "—" : card.value}
                            </h2>

                            <p className="mt-2 text-xs text-gray-500">
                                {loading
                                    ? "Loading..."
                                    : error
                                        ? "Data unavailable"
                                        : "Current overview"}
                            </p>
                        </div>
                    );
                })}
            </div>

            <div className="grid grid-cols-1 gap-5 lg:grid-cols-2">
                <section className="flex min-h-[340px] flex-col rounded-xl border border-slate-200 bg-white p-6">
                    <div>
                        <h2 className="text-lg font-semibold text-slate-900">Deals</h2>
                        <p className="mt-1 text-sm text-slate-500">Pipeline distribution across deal stages</p>
                    </div>
                    <div className="mt-7 flex-1 space-y-5">
                        {stages.length === 0 && !loading ? (
                            <div className="flex h-full min-h-44 items-center justify-center rounded-lg border border-dashed border-slate-200 bg-slate-50/50 px-5 text-center">
                                <p className="text-sm text-slate-500">No pipeline stages are configured.</p>
                            </div>
                        ) : stages.map((stage, index) => {
                            const count = deals.filter((deal) => deal.dealStageId === stage.dealStageId).length;
                            const colors = ["bg-blue-600", "bg-cyan-500", "bg-violet-500", "bg-amber-500", "bg-emerald-500"];
                            return <div key={stage.dealStageId}>
                                <div className="mb-2 flex items-center justify-between text-sm">
                                    <span className="flex items-center gap-2 font-medium text-slate-700"><span className={`size-2 rounded-full ${colors[index % colors.length]}`}/>{stage.stage}</span>
                                    <span className="font-semibold tabular-nums text-slate-900">{count}</span>
                                </div>
                                <div className="h-2.5 overflow-hidden rounded-full bg-slate-100">
                                    <div className={`h-full rounded-full ${colors[index % colors.length]}`} style={{ width: `${count === 0 ? 0 : Math.max(8, count / maximumStageCount * 100)}%` }}/>
                                </div>
                            </div>;
                        })}
                    </div>
                </section>

                <section className="flex min-h-[340px] flex-col rounded-xl border border-slate-200 bg-white p-6">
                    <div>
                        <h2 className="text-lg font-semibold text-slate-900">Deal Status</h2>
                        <p className="mt-1 text-sm text-slate-500">Deal volume and value by current outcome</p>
                    </div>
                    <div className="mt-7 flex-1 space-y-5">
                        {dealStatuses.map((item) => {
                            const styles = item.status === "won"
                                ? { dot: "bg-emerald-500", bar: "bg-emerald-500", track: "bg-emerald-50" }
                                : item.status === "lost"
                                    ? { dot: "bg-rose-500", bar: "bg-rose-500", track: "bg-rose-50" }
                                    : { dot: "bg-blue-600", bar: "bg-blue-600", track: "bg-blue-50" };
                            return <div key={item.status}>
                                <div className="mb-2 flex items-end justify-between gap-4">
                                    <div className="flex items-center gap-2"><span className={`size-2.5 rounded-full ${styles.dot}`}/><span className="text-sm font-medium capitalize text-slate-700">{item.status}</span></div>
                                    <div className="text-right"><span className="text-base font-semibold tabular-nums text-slate-900">{item.count}</span><span className="ml-2 text-xs text-slate-500">{new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 0 }).format(item.value)}</span></div>
                                </div>
                                <div className={`h-2.5 overflow-hidden rounded-full ${styles.track}`}>
                                    <div className={`h-full rounded-full ${styles.bar}`} style={{ width: `${item.count === 0 ? 0 : Math.max(8, item.count / maximumStatusCount * 100)}%` }}/>
                                </div>
                            </div>;
                        })}
                    </div>
                </section>
            </div>

            <section className="rounded-xl border border-gray-200 bg-white p-6">
                <h2 className="text-lg font-semibold">
                    Recent Activities
                </h2>
                <p className="mt-1 text-sm text-gray-500">
                    Latest calls, meetings and follow-ups
                </p>

                <div className="mt-5 divide-y divide-slate-100">
                    {activities.length === 0 && !loading ? <p className="py-10 text-center text-sm text-gray-400">No activities available.</p> : activities.map((activity) => <div key={activity.id} className="flex items-center gap-3 py-3.5">
                        <div className="grid size-9 shrink-0 place-items-center rounded-lg bg-blue-50 text-blue-600"><CalendarClock size={17}/></div>
                        <div className="min-w-0 flex-1"><p className="truncate text-sm font-medium text-slate-800">{activity.subject}</p><p className="mt-0.5 text-xs text-slate-500">{activity.activityType || "Activity"}{activity.status ? ` · ${activity.status}` : ""}</p></div>
                        <time className="shrink-0 text-xs text-slate-500">{activity.dueAt ? new Date(activity.dueAt).toLocaleDateString("en-IN", { day: "numeric", month: "short" }) : "No due date"}</time>
                    </div>)}
                </div>
            </section>
        </div>
    );
}
