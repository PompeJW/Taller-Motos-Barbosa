CREATE TABLE IF NOT EXISTS biker_print_job (
 id BIGSERIAL PRIMARY KEY,
 sale_id BIGINT NOT NULL REFERENCES sale(id),
 job_type VARCHAR(30) NOT NULL,
 payload JSONB NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
 created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
 claimed_at TIMESTAMPTZ,
 finished_at TIMESTAMPTZ,
 result_message TEXT,
 CONSTRAINT uq_biker_print_job UNIQUE(sale_id,job_type)
);
CREATE INDEX IF NOT EXISTS idx_biker_print_job_pending ON biker_print_job(status,id);
