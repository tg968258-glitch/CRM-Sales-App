import { cookies } from "next/headers";
import { NextRequest, NextResponse } from "next/server";

const BACKEND_URL = process.env.BACKEND_API_URL || process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

async function proxy(request: NextRequest, context: { params: Promise<{ path: string[] }> }) {
  const { path } = await context.params;
  const token = (await cookies()).get("crm_session")?.value;
  if (!token) return NextResponse.json({ message: "Your session has expired. Please sign in again." }, { status: 401 });
  const target = new URL(`/${path.join("/")}`, BACKEND_URL);
  target.search = request.nextUrl.search;
  const body = request.method === "GET" || request.method === "HEAD" ? undefined : await request.text();
  try {
    const response = await fetch(target, {
      method: request.method,
      headers: { Authorization: `Bearer ${token}`, ...(request.headers.get("content-type") ? { "Content-Type": request.headers.get("content-type")! } : {}) },
      body,
      cache: "no-store",
    });
    return new NextResponse(await response.arrayBuffer(), {
      status: response.status,
      headers: { "Content-Type": response.headers.get("content-type") || "application/json" },
    });
  } catch {
    return NextResponse.json({ message: "The CRM server is unavailable." }, { status: 502 });
  }
}

export const GET = proxy;
export const POST = proxy;
export const PUT = proxy;
export const DELETE = proxy;
