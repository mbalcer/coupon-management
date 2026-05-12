CREATE TABLE coupon_usages
(
    id        UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    coupon_id UUID         NOT NULL REFERENCES coupons (id),
    user_id   VARCHAR(255) NOT NULL,
    used_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_coupon_user UNIQUE (coupon_id, user_id)
);