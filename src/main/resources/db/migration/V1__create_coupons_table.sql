CREATE TABLE coupons
(
    id           UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    code         VARCHAR(100) UNIQUE NOT NULL,
    created_at   TIMESTAMPTZ         NOT NULL DEFAULT now(),
    max_uses     INT                 NOT NULL CHECK (max_uses > 0),
    current_uses INT                 NOT NULL DEFAULT 0 CHECK (current_uses >= 0),
    country_code CHAR(2)             NOT NULL
);

CREATE INDEX idx_coupons_code ON coupons (code);