-- Esquema idempotente de la persistencia en PostgreSQL (perfil "postgres").
CREATE TABLE IF NOT EXISTS rooms (
  id BIGINT PRIMARY KEY,
  number VARCHAR(10) NOT NULL UNIQUE,
  type VARCHAR(20) NOT NULL,
  floor INT NOT NULL,
  description VARCHAR(255) NOT NULL,
  status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
  id VARCHAR(20) PRIMARY KEY,
  room_id BIGINT NOT NULL REFERENCES rooms(id),
  guest_full_name VARCHAR(150) NOT NULL,
  guest_document VARCHAR(50) NOT NULL,
  guest_email VARCHAR(150) NOT NULL,
  guest_phone VARCHAR(50) NOT NULL,
  check_in DATE NOT NULL,
  check_out DATE NOT NULL,
  room_total NUMERIC(12,2) NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  checked_in_at TIMESTAMP,
  checked_out_at TIMESTAMP,
  invoice_number VARCHAR(20),
  key_code VARCHAR(36),
  key_issued_at TIMESTAMP,
  key_active BOOLEAN
);

CREATE TABLE IF NOT EXISTS reservation_services (
  id VARCHAR(20) PRIMARY KEY,
  reservation_id VARCHAR(20) NOT NULL REFERENCES reservations(id),
  type VARCHAR(20) NOT NULL,
  quantity INT NOT NULL,
  added_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS invoices (
  invoice_number VARCHAR(20) PRIMARY KEY,
  reservation_id VARCHAR(20) NOT NULL UNIQUE REFERENCES reservations(id),
  issued_at TIMESTAMP NOT NULL,
  tax_rate NUMERIC(5,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS invoice_lines (
  invoice_number VARCHAR(20) NOT NULL REFERENCES invoices(invoice_number),
  line_no INT NOT NULL,
  description VARCHAR(255) NOT NULL,
  category VARCHAR(20) NOT NULL,
  quantity BIGINT NOT NULL,
  unit_price NUMERIC(12,2) NOT NULL,
  PRIMARY KEY (invoice_number, line_no)
);
