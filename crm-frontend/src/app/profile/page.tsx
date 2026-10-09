"use client";

import { useEffect, useState } from "react";
import { Eye, EyeOff } from "lucide-react";
import { apiGet, apiPut } from "@/lib/api";

type Profile = { uid: number; name: string; email: string; active: boolean };

type PasswordFieldProps = {
    label: string;
    name: string;
    autoComplete: "current-password" | "new-password";
    minLength?: number;
};

function PasswordField({ label, name, autoComplete, minLength }: PasswordFieldProps) {
    const [visible, setVisible] = useState(false);

    return (
        <label className="mt-4 block text-sm font-medium">
            {label}
            <div className="relative mt-1.5">
                <input
                    name={name}
                    type={visible ? "text" : "password"}
                    autoComplete={autoComplete}
                    minLength={minLength}
                    required
                    className="w-full rounded-lg border border-slate-300 py-2.5 pl-3 pr-11 font-normal outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                />
                <button
                    type="button"
                    onClick={() => setVisible((value) => !value)}
                    aria-label={visible ? `Hide ${label.toLowerCase()}` : `Show ${label.toLowerCase()}`}
                    aria-pressed={visible}
                    className="absolute inset-y-0 right-0 flex w-11 items-center justify-center rounded-r-lg text-slate-500 hover:text-slate-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-blue-500"
                >
                    {visible ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
            </div>
        </label>
    );
}

export default function ProfilePage() {
    const [profile, setProfile] = useState<Profile | null>(null);
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        apiGet<Profile>("/profile").then(setProfile).catch((e) => setError(e.message));
    }, []);

    async function update(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();
        setSaving(true);
        setError("");
        const form = new FormData(e.currentTarget);
        try {
            setProfile(await apiPut<Profile>("/profile", { name: form.get("name") }));
            setMessage("Profile updated.");
        } catch (err) {
            setError(err instanceof Error ? err.message : "Unable to update profile.");
        } finally {
            setSaving(false);
        }
    }

    async function password(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();
        setError("");
        setMessage("");

        const formElement = e.currentTarget;
        const form = new FormData(formElement);
        const currentPassword = String(form.get("currentPassword") ?? "");
        const newPassword = String(form.get("newPassword") ?? "");
        const confirmPassword = String(form.get("confirmPassword") ?? "");

        if (newPassword !== confirmPassword) {
            setError("New password and confirm password do not match.");
            return;
        }

        setSaving(true);
        try {
            await apiPut<string>("/profile/change-password", { currentPassword, newPassword });
            formElement.reset();
            setMessage("Password changed successfully.");
        } catch (err) {
            setError(err instanceof Error ? err.message : "Unable to change password.");
        } finally {
            setSaving(false);
        }
    }

    return (
        <div className="mx-auto max-w-4xl space-y-6">
            <div>
                <p className="text-sm font-semibold text-blue-600">Account</p>
                <h1 className="mt-1 text-2xl font-semibold">Profile &amp; security</h1>
                <p className="mt-1 text-sm text-slate-500">Manage your personal details and password.</p>
            </div>
            {error && <div className="rounded-xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-700">{error}</div>}
            {message && <div className="rounded-xl border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-700">{message}</div>}
            {!profile ? (
                <div className="rounded-xl border bg-white p-10 text-center text-sm text-slate-500">Loading profile...</div>
            ) : (
                <div className="grid gap-5 lg:grid-cols-2">
                    <form onSubmit={update} className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                        <h2 className="font-semibold">Personal information</h2>
                        <label className="mt-5 block text-sm font-medium">Name<input name="name" defaultValue={profile.name} required className="mt-1.5 w-full rounded-lg border border-slate-300 px-3 py-2.5 font-normal" /></label>
                        <label className="mt-4 block text-sm font-medium">Email<input type="email" value={profile.email} readOnly aria-readonly="true" className="mt-1.5 w-full cursor-not-allowed rounded-lg border border-slate-200 bg-slate-100 px-3 py-2.5 font-normal text-slate-500" /><span className="mt-1.5 block text-xs font-normal text-slate-500">Email cannot be changed from your profile.</span></label>
                        <button disabled={saving} className="mt-6 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white disabled:opacity-60">Save changes</button>
                    </form>
                    <form onSubmit={password} className="rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
                        <h2 className="font-semibold">Change password</h2>
                        <PasswordField label="Current password" name="currentPassword" autoComplete="current-password" />
                        <PasswordField label="New password" name="newPassword" autoComplete="new-password" minLength={8} />
                        <PasswordField label="Confirm password" name="confirmPassword" autoComplete="new-password" minLength={8} />
                        <p className="mt-2 text-xs text-slate-500">Use at least 8 characters.</p>
                        <button disabled={saving} className="mt-6 rounded-lg bg-slate-900 px-4 py-2.5 text-sm font-semibold text-white disabled:opacity-60">Change password</button>
                    </form>
                </div>
            )}
        </div>
    );
}
