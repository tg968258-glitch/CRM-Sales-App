
import type { Metadata } from "next";
import { Geist } from "next/font/google";
import AppLayout from "@/components/AppLayout";
import "./globals.css";

const geist = Geist({
    subsets: ["latin"],
});

export const metadata: Metadata = {
    title: "CRM Sales",
    description: "Sales Management Application",
    icons: {
        icon: "/icon.svg",
    },
};

export default function RootLayout({
                                       children,
                                   }: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html lang="en">
        <body className={geist.className}>
        <AppLayout>{children}</AppLayout>
        </body>
        </html>
    );
}
