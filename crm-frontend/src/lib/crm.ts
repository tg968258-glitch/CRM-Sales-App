export const leadStatuses = ["New", "Qualified", "Contacted", "Unqualified", "Converted"];
export const leadRatings = ["Hot", "Warm", "Cold"];
export const leadSources = ["Website", "Referral", "Social_Media", "Campaign"];
export const salutations = ["Mr", "Ms", "Mrs", "Dr", "Prof"];
export const industries = ["Technology", "Banking", "Healthcare", "Education", "Retail", "Manufacturing", "Real_Estate", "Telecommunication", "Others"];
export const lifecycleStatuses = ["Customer", "Active", "Inactive", "Prospect"];
export const activityTypes = ["Call", "Email", "Meeting", "Comment", "Task"];
export const dealStatuses = ["open", "won", "lost"];
export const options = (values: string[]) => values.map((value) => ({ value, label: value.replaceAll("_", " ") }));
