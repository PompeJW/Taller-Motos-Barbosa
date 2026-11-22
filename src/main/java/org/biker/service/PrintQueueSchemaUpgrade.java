package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

/** Actualiza solamente la cola existente, sin tocar las ventas ni el inventario. */
@Component
@Order(1)
@RequiredArgsConstructor
public class PrintQueueSchemaUpgrade implements ApplicationRunner {
    private final JdbcTemplate jdbc;
    @Override public void run(ApplicationArguments args) {
        jdbc.execute("""
            CREATE TABLE IF NOT EXISTS biker_print_job (
              id BIGSERIAL PRIMARY KEY,
              sale_id BIGINT REFERENCES sale(id),
              job_type VARCHAR(30) NOT NULL,
              payload JSONB NOT NULL,
              status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
              created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
              claimed_at TIMESTAMPTZ,
              finished_at TIMESTAMPTZ,
              result_message TEXT,
              CONSTRAINT uq_biker_print_job UNIQUE(sale_id, job_type)
            )
            """);
        jdbc.execute("ALTER TABLE biker_print_job ALTER COLUMN sale_id DROP NOT NULL");
        jdbc.execute("CREATE INDEX IF NOT EXISTS idx_biker_print_job_pending ON biker_print_job(status, id)");
    }
}
