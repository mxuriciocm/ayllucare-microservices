-- ==========================================
-- AYLLUCARE - DATABASE INITIALIZATION SCRIPT
-- ==========================================
-- Este script crea las bases de datos necesarias
-- para todos los microservicios
-- ==========================================

-- IAM Database
CREATE DATABASE IF NOT EXISTS iam_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Profiles Database
CREATE DATABASE IF NOT EXISTS profiles_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Anamnesis Database
CREATE DATABASE IF NOT EXISTS anamnesis_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Triage Database
CREATE DATABASE IF NOT EXISTS triage_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CaseDesk Database
CREATE DATABASE IF NOT EXISTS casedesk_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant permissions to root user
GRANT ALL PRIVILEGES ON iam_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON profiles_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON anamnesis_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON triage_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON casedesk_db.* TO 'root'@'%';

FLUSH PRIVILEGES;

-- Log successful initialization
SELECT 'AylluCare databases initialized successfully!' AS Status;

