ALTER TABLE debt ADD COLUMN type VARCHAR(20) NOT NULL DEFAULT 'CREDITOR';

UPDATE debt d
SET type = (SELECT c.type FROM contact c WHERE c.id = d.contact_id);

ALTER TABLE contact DROP COLUMN type;