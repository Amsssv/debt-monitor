-- Rename table
ALTER TABLE lender RENAME TO contact;

-- Add type column; existing rows are CREDITOR (was tracking debts owed)
ALTER TABLE contact ADD COLUMN IF NOT EXISTS type varchar(20) NOT NULL DEFAULT 'CREDITOR';

-- Rename FK column on debt table
ALTER TABLE debt RENAME COLUMN lender_id TO contact_id;

-- Drop old FK constraint and recreate pointing to renamed table/column
ALTER TABLE debt DROP CONSTRAINT fkiromur1tymupejipngr2pjqm9;
ALTER TABLE debt ADD CONSTRAINT fk_debt_contact FOREIGN KEY (contact_id) REFERENCES contact(id);
