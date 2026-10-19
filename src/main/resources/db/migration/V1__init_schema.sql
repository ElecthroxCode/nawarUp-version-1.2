CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL CHECK (role IN ('CUSTOMER','BUSINESS')),
    created_at    TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE customers (
    id        BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    full_name VARCHAR(150) NOT NULL,
    phone     VARCHAR(30),
    address   VARCHAR(255)
);

CREATE TABLE businesses (
    id            BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    name_business VARCHAR(150) NOT NULL,
    manager       VARCHAR(150),
    phones        VARCHAR(100),
    address       VARCHAR(255)
);

CREATE TABLE business_schedules (
    id           BIGSERIAL PRIMARY KEY,
    business_id  BIGINT      NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    day_of_week  VARCHAR(10) NOT NULL,
    opening_time TIME        NOT NULL,
    closing_time TIME        NOT NULL,
    CHECK (opening_time < closing_time),
    UNIQUE (business_id, day_of_week)
);

CREATE TABLE services (
    id               BIGSERIAL PRIMARY KEY,
    business_id      BIGINT        NOT NULL REFERENCES businesses(id) ON DELETE CASCADE,
    name_service     VARCHAR(150)  NOT NULL,
    description      TEXT,
    price            NUMERIC(12,2) NOT NULL CHECK (price >= 0),
    duration_minutes INT           NOT NULL DEFAULT 30 CHECK (duration_minutes > 0),
    active           BOOLEAN       NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_services_business ON services(business_id);

CREATE TABLE photos (
    id         BIGSERIAL PRIMARY KEY,
    service_id BIGINT       NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    name       VARCHAR(255) NOT NULL
);
CREATE INDEX idx_photos_service ON photos(service_id);

CREATE TABLE appointments (
    id               BIGSERIAL PRIMARY KEY,
    customer_id      BIGINT        NOT NULL REFERENCES customers(id),
    business_id      BIGINT        NOT NULL REFERENCES businesses(id),
    start_time       TIMESTAMP     NOT NULL,
    end_time         TIMESTAMP     NOT NULL,
    total            NUMERIC(12,2) NOT NULL,
    note             VARCHAR(500),
    appointment_type VARCHAR(20)   NOT NULL CHECK (appointment_type IN ('IN_STORE','DELIVERY')),
    delivery_address VARCHAR(255),
    status           VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    created_at       TIMESTAMP     NOT NULL DEFAULT now(),
    CHECK (start_time < end_time)
);
CREATE INDEX idx_appointments_business_time ON appointments(business_id, start_time);
CREATE INDEX idx_appointments_customer ON appointments(customer_id);

CREATE TABLE appointment_details (
    id             BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT        NOT NULL REFERENCES appointments(id) ON DELETE CASCADE,
    service_id     BIGINT        NOT NULL REFERENCES services(id),
    count          INT           NOT NULL CHECK (count > 0),
    subtotal       NUMERIC(12,2) NOT NULL
);
CREATE INDEX idx_details_appointment ON appointment_details(appointment_id);
