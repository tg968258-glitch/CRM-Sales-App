import { cookies } from "next/headers";
import { NextResponse } from "next/server";
export async function GET() {
  return NextResponse.json({ authenticated: Boolean((await cookies()).get("crm_session")) });
}
