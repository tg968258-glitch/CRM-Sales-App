
"use client";

import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import Topbar from "./TopBar";

export default function AppLayout({
                                      children,
                                  }: {
    children: React.ReactNode;
}) {
    const pathname = usePathname();
    const router = useRouter();
    const isLoginPage = pathname === "/login";
    const [checking, setChecking] = useState(!isLoginPage);

    useEffect(() => {
        if (isLoginPage) return;
        let active = true;
        fetch("/api/session", { cache: "no-store" })
            .then((response) => response.json())
            .then((session: { authenticated: boolean }) => {
                if (!session.authenticated) router.replace("/login");
            })
            .finally(() => active && setChecking(false));
        const expire = () => router.replace("/login?expired=1");
        window.addEventListener("crm:unauthorized", expire);
        return () => { active = false; window.removeEventListener("crm:unauthorized", expire); };
    }, [isLoginPage, router]);

    if (!isLoginPage && checking) {
        return <div className="grid min-h-screen place-items-center bg-slate-50 text-sm text-slate-500">Loading your workspace...</div>;
    }

    return (
        <div className="min-h-screen bg-slate-50">
            {!isLoginPage && <Topbar />}

            <main className={isLoginPage ? "w-full" : "mx-auto w-full max-w-[1440px] px-4 py-7 sm:px-6 lg:px-8"}>
                {children}
            </main>
        </div>
    );
}
