import type { Metadata } from "next";
import Topbar from "@/components/TopBar";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "CRM Sales",
  description: "Sales Application",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
  <html
    lang="en"
    className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
  >
    <body>
      <div className="min-h-screen bg-gray-50">
        <Topbar />

        <main className="p-6">
          {children}
        </main>
      </div>
    </body>
  </html>
);
}