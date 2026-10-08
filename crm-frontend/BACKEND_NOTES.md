# Backend integration review

Reviewed against the current controllers and DTOs on 8 October 2026. No backend source files were changed.

## Current blockers

- **Lead creation is unavailable:** the current `LeadController` exposes list, detail, update, and delete operations, but no `POST /leads`. The frontend cannot create leads until that endpoint is restored.
- **Lead conversion is unavailable:** the earlier path-variable mismatch is no longer present because the conversion method itself has been removed. Restore a verified conversion endpoint accepting `LeadConversionDto` before enabling conversion in the frontend.
- **User administration is create/read only:** `/api/users` has no update, activation/deactivation, role-change, or delete endpoints. The frontend exposes only supported list and create actions.
- **Notifications are read-only for the signed-in user:** no mark-read endpoint exists, so the notification dropdown intentionally performs no status mutation.
- **Deal stage changes require full updates:** no focused stage-transition endpoint exists. Kanban drag-and-drop sends the complete `DealDto` to `PUT /deals/{id}` and rolls the UI back if it fails.
- **Assignee discovery is restricted:** `GET /api/users` is admin-only. Non-admin users cannot receive a verified owner dropdown. A role-authorized endpoint returning only assignable user IDs and names is recommended.
- **Deal responses contain relationship IDs:** the frontend currently resolves account, contact, and stage names by fetching those collections and joining them client-side. Returning compact relationship summaries would reduce requests and simplify rendering.

## Resolved since the previous review

- `UserResponse` now includes `roleName`. The frontend uses `GET /profile` to show admin-only navigation without probing `/api/users`.
- Lead detail, update, and delete path variables now bind explicitly with `@PathVariable("id")`.

## Security integration

The frontend now exchanges the backend JWT for an HttpOnly, same-site cookie in a Next.js route handler and proxies API requests server-side. Configure `BACKEND_API_URL` for the Spring Boot base URL. `NEXT_PUBLIC_API_URL` remains a compatibility fallback but is no longer required in the browser.
