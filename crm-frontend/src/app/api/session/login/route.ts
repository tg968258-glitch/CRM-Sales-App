import { NextRequest, NextResponse } from "next/server";

const BACKEND_URL = process.env.BACKEND_API_URL || process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

export async function POST(request: NextRequest) {
  try {
    const response = await fetch(`${BACKEND_URL}/api/auth/login`, {
      method: "POST", headers: { "Content-Type": "application/json" }, body: await request.text(), cache: "no-store",
    });
    const payload = await response.text();
    if (!response.ok) return new NextResponse(payload, { status: response.status });
    const { token } = JSON.parse(payload) as { token?: string };
    if (!token) return NextResponse.json({ message: "Login response did not contain a token." }, { status: 502 });
    const result = NextResponse.json({ ok: true });
    result.cookies.set("crm_session", token, { httpOnly: true, sameSite: "lax", secure: process.env.NODE_ENV === "production", path: "/", maxAge: 60 * 60 * 8 });
    return result;
  } catch {
    return NextResponse.json({ message: "The CRM server is unavailable." }, { status: 502 });
  }
}
