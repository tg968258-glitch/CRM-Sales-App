

INSERT INTO deal_stage (stage, probability, display_order, is_active)
SELECT seed.stage, seed.probability, seed.display_order, true
FROM (VALUES
    ('Prospecting', 10, 1),
    ('Qualification', 30, 2),
    ('Proposal', 55, 3),
    ('Negotiation', 75, 4),
    ('Closed', 100, 5)
) AS seed(stage, probability, display_order)
WHERE NOT EXISTS (
    SELECT 1
    FROM deal_stage existing
    WHERE existing.stage = seed.stage
);

-- Leads in New, Contacted, Qualified, and Unqualified states remain leads only.
-- Isha Menon is the single pre-converted lead and is linked below to the
-- corresponding Harbor Foods account, contact, and deal.
WITH owner AS (
    SELECT uid
    FROM users
    WHERE is_active = true
    ORDER BY uid
    LIMIT 1
), sample(
    salutation, name, email, phone_number, company_name, source,
    status, lead_rating, expected_value, notes
) AS (
    VALUES
        ('Ms', 'Neha Kapoor', 'neha.kapoor@brightpath.example', '+91 98765 41021', 'BrightPath Learning', 'Website', 'New', 'Warm', 275000.00, 'Requested a product walkthrough for the regional sales team.'),
        ('Mr', 'Arjun Rao', 'arjun.rao@greenfleet.example', '+91 98765 41022', 'GreenFleet Logistics', 'Referral', 'Qualified', 'Hot', 680000.00, 'Budget approved; ready for conversion workflow testing.'),
        ('Dr', 'Meera Shah', 'meera.shah@novacare.example', '+91 98765 41023', 'NovaCare Clinics', 'Campaign', 'Contacted', 'Warm', 425000.00, 'Follow up after the clinical operations review.'),
        ('Mr', 'Kabir Malhotra', 'kabir.malhotra@urbanest.example', '+91 98765 41024', 'UrbanNest Properties', 'Social_Media', 'Unqualified', 'Cold', 150000.00, 'Requirement is outside the current product scope.'),
        ('Mrs', 'Isha Menon', 'isha.menon@harborfoods.example', '+91 98765 41025', 'Harbor Foods', 'Referral', 'Converted', 'Hot', 540000.00, 'Converted after a successful discovery workshop.'),
        ('Mr', 'Rohan Desai', 'rohan.desai@quantumworks.example', '+91 98765 41026', 'QuantumWorks Manufacturing', 'Website', 'Qualified', 'Warm', 360000.00, 'Interested in sales workflow automation; not yet converted.')
)
INSERT INTO leads (
    owner_id, salutation, name, email, phone_number, company_name, source,
    status, lead_rating, expected_value, notes
)
SELECT
    owner.uid, sample.salutation, sample.name, sample.email,
    sample.phone_number, sample.company_name, sample.source, sample.status,
    sample.lead_rating, sample.expected_value, sample.notes
FROM sample
CROSS JOIN owner
WHERE NOT EXISTS (
    SELECT 1
    FROM leads existing
    WHERE existing.email = sample.email
);

-- One converted-lead account plus two accounts created directly in the CRM.
WITH owner AS (
    SELECT uid
    FROM users
    WHERE is_active = true
    ORDER BY uid
    LIMIT 1
), sample(account_name, industry, phone_number, website) AS (
    VALUES
        ('Harbor Foods', 'Retail', '+91 98765 41025', NULL),
        ('Apex Digital Systems', 'Technology', '+91 80 4123 7800', 'https://apexdigital.example'),
        ('Meridian Cooperative Bank', 'Banking', '+91 11 4055 9200', 'https://meridianbank.example')
)
INSERT INTO accounts (
    acc_owner_id, account_name, industry, phone_number, website,
    created_at, updated_at
)
SELECT
    owner.uid, sample.account_name, sample.industry, sample.phone_number,
    sample.website, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM sample
CROSS JOIN owner
WHERE NOT EXISTS (
    SELECT 1
    FROM accounts existing
    WHERE existing.account_name = sample.account_name
);

-- Isha mirrors the converted lead. The remaining contacts were created directly.
WITH owner AS (
    SELECT uid
    FROM users
    WHERE is_active = true
    ORDER BY uid
    LIMIT 1
), sample(
    account_name, name, email, job_title, phone_number, lifecycle_status
) AS (
    VALUES
        ('Harbor Foods', 'Isha Menon', 'isha.menon@harborfoods.example', 'Commercial Director', '+91 98765 41025', 'Customer'),
        ('Apex Digital Systems', 'Aarav Mehta', 'aarav.mehta@apexdigital.example', 'VP of Technology', '+91 99870 11201', 'Customer'),
        ('Apex Digital Systems', 'Vikram Sethi', 'vikram.sethi@apexdigital.example', 'Procurement Manager', '+91 99870 11202', 'Active'),
        ('Meridian Cooperative Bank', 'Ananya Iyer', 'ananya.iyer@meridianbank.example', 'Digital Transformation Director', '+91 99870 11204', 'Prospect')
)
INSERT INTO contact (
    account_id, contact_owner_id, name, email, "Job Title", phone_number,
    lifecycle_status, created_at, updated_at
)
SELECT
    account.acc_id, owner.uid, sample.name, sample.email, sample.job_title,
    sample.phone_number, sample.lifecycle_status,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM sample
CROSS JOIN owner
JOIN accounts account
  ON account.account_name = sample.account_name
WHERE NOT EXISTS (
    SELECT 1
    FROM contact existing
    WHERE existing.email = sample.email
);

-- Deals always use a contact that belongs to the selected account.
WITH owner AS (
    SELECT uid
    FROM users
    WHERE is_active = true
    ORDER BY uid
    LIMIT 1
), sample(
    title, account_name, contact_email, stage_name, value,
    close_offset_days, deal_status, closing_note
) AS (
    VALUES
        ('Harbor Foods Sales Platform', 'Harbor Foods', 'isha.menon@harborfoods.example', 'Qualification', 540000.00, 45, 'open', NULL),
        ('Cloud Migration Expansion', 'Apex Digital Systems', 'aarav.mehta@apexdigital.example', 'Proposal', 850000.00, 35, 'open', NULL),
        ('Enterprise Support Renewal', 'Apex Digital Systems', 'vikram.sethi@apexdigital.example', 'Closed', 320000.00, -18, 'won', 'Renewal signed for a twelve-month term.'),
        ('Compliance Platform Modernization', 'Meridian Cooperative Bank', 'ananya.iyer@meridianbank.example', 'Negotiation', 1250000.00, 21, 'open', NULL),
        ('Branch Automation Pilot', 'Meridian Cooperative Bank', 'ananya.iyer@meridianbank.example', 'Closed', 610000.00, -9, 'lost', 'Project postponed during the annual budget review.')
)
INSERT INTO deal (
    account_id, contact_id, deal_stage_id, deal_owner_id, title, value,
    expected_close_date, deal_status, "Closing Note", created_at, updated_at
)
SELECT
    account.acc_id, contact.contact_id, stage.deal_stage_id, owner.uid,
    sample.title, sample.value,
    CURRENT_TIMESTAMP + sample.close_offset_days * INTERVAL '1 day',
    sample.deal_status, sample.closing_note,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM sample
CROSS JOIN owner
JOIN accounts account
  ON account.account_name = sample.account_name
JOIN contact
  ON contact.email = sample.contact_email
 AND contact.account_id = account.acc_id
JOIN deal_stage stage
  ON stage.stage = sample.stage_name
WHERE NOT EXISTS (
    SELECT 1
    FROM deal existing
    WHERE existing.title = sample.title
);

-- Lead activities are attached only to unconverted leads; deal activities are
-- attached to valid deals. Each activity has exactly one business target.
WITH owner AS (
    SELECT uid
    FROM users
    WHERE is_active = true
    ORDER BY uid
    LIMIT 1
), sample(
    activity_type, subject, status, due_offset_days,
    completed_offset_days, deal_title, lead_email
) AS (
    VALUES
        ('Email', 'Send GreenFleet discovery summary', 'Completed', -2, -2, NULL, 'arjun.rao@greenfleet.example'),
        ('Call', 'Follow up on NovaCare operations review', 'Overdue', -1, NULL, NULL, 'meera.shah@novacare.example'),
        ('Meeting', 'Review Harbor Foods implementation plan', 'Scheduled', 3, NULL, 'Harbor Foods Sales Platform', NULL),
        ('Task', 'Prepare cloud migration proposal', 'Open', 4, NULL, 'Cloud Migration Expansion', NULL),
        ('Comment', 'Record compliance committee feedback', 'Completed', -3, -3, 'Compliance Platform Modernization', NULL),
        ('Call', 'Discuss branch pilot budget decision', 'Completed', -8, -8, 'Branch Automation Pilot', NULL)
)
INSERT INTO activity (
    activity_type, subject, status, due_at, completed_at, created_at,
    created_by, deal_id, lead_id
)
SELECT
    sample.activity_type, sample.subject, sample.status,
    CURRENT_TIMESTAMP + sample.due_offset_days * INTERVAL '1 day',
    CASE
        WHEN sample.completed_offset_days IS NULL THEN NULL
        ELSE CURRENT_TIMESTAMP + sample.completed_offset_days * INTERVAL '1 day'
    END,
    CURRENT_TIMESTAMP, owner.uid, deal.deal_id, lead.lead_id
FROM sample
CROSS JOIN owner
LEFT JOIN deal
  ON deal.title = sample.deal_title
LEFT JOIN leads lead
  ON lead.email = sample.lead_email
WHERE (deal.deal_id IS NOT NULL OR lead.lead_id IS NOT NULL)
  AND NOT EXISTS (
      SELECT 1
      FROM activity existing
      WHERE existing.subject = sample.subject
  );

WITH owner AS (
    SELECT uid
    FROM users
    WHERE is_active = true
    ORDER BY uid
    LIMIT 1
), sample(
    notification_type, message, notification_status,
    created_offset_days, sent_offset_days
) AS (
    VALUES
        ('DEAL_UPDATE', 'Compliance Platform Modernization moved to negotiation.', 'Unread', -1, -1),
        ('ACTIVITY_REMINDER', 'A follow-up call with NovaCare Clinics is overdue.', 'Unread', -1, -1),
        ('DEAL_WON', 'Enterprise Support Renewal was marked as won.', 'Read', -5, -5)
)
INSERT INTO notification (
    user_id, notification_type, message, notification_status,
    "created at", "sent at"
)
SELECT
    owner.uid, sample.notification_type, sample.message,
    sample.notification_status,
    CURRENT_TIMESTAMP + sample.created_offset_days * INTERVAL '1 day',
    CURRENT_TIMESTAMP + sample.sent_offset_days * INTERVAL '1 day'
FROM sample
CROSS JOIN owner
WHERE NOT EXISTS (
    SELECT 1
    FROM notification existing
    WHERE existing.user_id = owner.uid
      AND existing.message = sample.message
);
