CREATE TABLE points_ledger
(
    id          BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    amount      BIGINT       NOT NULL,
    type        VARCHAR(30)  NOT NULL,
    refer_key   VARCHAR(255) NULL,
    idem_key    VARCHAR(255) NOT NULL,
    acc_amount  BIGINT       NOT NULL DEFAULT 0,
    used_amount BIGINT       NOT NULL DEFAULT 0,
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP (6),

    PRIMARY KEY (id),
    UNIQUE KEY uq_idem_key (idem_key),
    INDEX       idx_user_created (user_id, created_at DESC),
    INDEX       idx_refer_key (refer_key)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci;