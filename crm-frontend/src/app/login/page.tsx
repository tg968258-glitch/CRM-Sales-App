
"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { ArrowRight, LoaderCircle } from "lucide-react";

export default function LoginPage() {
    const router = useRouter();
    const [expired, setExpired] = useState(false);

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const timer = window.setTimeout(() => setExpired(new URLSearchParams(window.location.search).has("expired")), 0);
        return () => window.clearTimeout(timer);
    }, []);

    async function handleLogin(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            const response = await fetch(
                "/api/session/login",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ email, password }),
                }
            );

            if (!response.ok) {
                throw new Error("Invalid email or password");
            }

            router.replace("/dashboard");
        } catch (err) {
            setError(
                err instanceof Error ? err.message : "Login failed"
            );
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="flex min-h-screen bg-[#f7f6f3] text-slate-900">
            <main className="flex w-full items-center justify-center px-5 pb-[10vh] pt-8 sm:px-8">
                <div className="w-full max-w-[380px]">
                    <div className="mb-8">
                        <h1 className="text-[1.75rem] font-semibold tracking-[-0.025em] text-slate-950">
                            Sign in to CRM Sales
                        </h1>
                        <p className="mt-2 text-sm leading-6 text-slate-600">
                            Enter your credentials to access your workspace.
                        </p>
                    </div>

                    {expired && (
                        <p className="mb-5 rounded-md border border-amber-200 bg-amber-50 px-3.5 py-3 text-sm text-amber-800">
                            Your session ended. Please sign in again.
                        </p>
                    )}

                    <form onSubmit={handleLogin} autoComplete="off" className="space-y-5">
                        <div>
                            <label htmlFor="email" className="mb-2 block text-sm font-medium text-slate-700">
                                Email address
                            </label>
                            <input
                                id="email"
                                name="email"
                                type="email"
                                autoComplete="off"
                                value={email}
                                onChange={(event) => setEmail(event.target.value)}
                                placeholder="name@company.com"
                                required
                                className="w-full rounded-md border border-slate-300 bg-white px-3.5 py-2.5 text-sm text-slate-900 outline-none transition-colors placeholder:text-slate-400 focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                            />
                        </div>

                        <div>
                            <label htmlFor="password" className="mb-2 block text-sm font-medium text-slate-700">
                                Password
                            </label>
                            <input
                                id="password"
                                name="password"
                                type="password"
                                autoComplete="new-password"
                                value={password}
                                onChange={(event) => setPassword(event.target.value)}
                                placeholder="Enter your password"
                                required
                                className="w-full rounded-md border border-slate-300 bg-white px-3.5 py-2.5 text-sm text-slate-900 outline-none transition-colors placeholder:text-slate-400 focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                            />
                        </div>

                        {error && (
                            <p role="alert" className="rounded-md border border-red-200 bg-red-50 px-3.5 py-3 text-sm text-red-700">
                                {error}
                            </p>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="flex w-full items-center justify-center gap-2 rounded-md bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-blue-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:bg-blue-400"
                        >
                            {loading ? (
                                <><LoaderCircle size={16} className="animate-spin" /> Signing in...</>
                            ) : (
                                <>Sign In <ArrowRight size={16} /></>
                            )}
                        </button>
                    </form>

                </div>
            </main>
        </div>
    );
}
