-- This file contains initial data for the database. It will be executed after the schema is created.
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Full access'),
('ANALYST', 'Read and analytics'),
('VIEWER', 'Read only');