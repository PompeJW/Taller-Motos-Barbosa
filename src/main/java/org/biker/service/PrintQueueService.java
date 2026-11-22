package org.biker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.biker.dto.SaleResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.format.DateTimeFormatter;

@Service
public class PrintQueueService {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    public PrintQueueService(JdbcTemplate jdbc, ObjectMapper mapper) {
        this.jdbc=jdbc; this.mapper=mapper;
    }
    @Transactional
    public void queueSale(SaleResponse sale) {
        List<String> lines = new ArrayList<>();
        lines.add("Venta: " + sale.saleNumber());
        lines.add("Fecha: " + sale.saleDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lines.add("Cliente: " + sale.clientName());
        lines.add("Documento: " + sale.Clientdocumento());
        lines.add("--------------------------------");
        sale.items().forEach(i -> { lines.add(i.sparePartName() + " x" + i.quantity()); lines.add("  " + i.unitPrice() + " = " + i.subtotal()); });
        lines.add("--------------------------------");
        sale.payments().forEach(p -> lines.add(p.methodName() + ": " + p.amount()));
        lines.add("TOTAL: " + sale.total());
        String payload;
        try { payload=mapper.writeValueAsString(Map.of("title", "FACTURA DE VENTA", "lines", lines)); }
        catch (JsonProcessingException e) { throw new IllegalStateException("No se pudo generar comprobante",e); }
        jdbc.update("INSERT INTO biker_print_job (sale_id, job_type, payload, status) VALUES (?, 'SALE', CAST(? AS jsonb), 'PENDING') ON CONFLICT (sale_id, job_type) DO NOTHING",sale.id(),payload);
    }
    /** Cada petición de etiqueta es un trabajo independiente. */
    @Transactional
    public long queueQr(org.biker.model.SparePart part, int copies) {
        if (copies < 1 || copies > 20) throw new IllegalArgumentException("Cantidad de etiquetas entre 1 y 20");
        if (part.getBarcode() == null || part.getBarcode().isBlank())
            throw new IllegalArgumentException("El repuesto no tiene imagen QR guardada");
        String payload;
        try {
            payload = mapper.writeValueAsString(Map.of(
                "type", "QR_LABEL", "name", part.getName(),
                "sku", part.getSkuCode(), "barcode", part.getBarcode(), "copies", copies));
        } catch (JsonProcessingException e) { throw new IllegalStateException("Error generando etiqueta", e); }
        return jdbc.queryForObject("INSERT INTO biker_print_job (sale_id, job_type, payload, status) VALUES (NULL, 'QR_LABEL', CAST(? AS jsonb), 'PENDING') RETURNING id", Long.class, payload);
    }
    @Transactional
    public Map<String,Object> claim() {
        List<Map<String,Object>> rows=jdbc.queryForList("""
            UPDATE biker_print_job SET status='CLAIMED', claimed_at=now()
            WHERE id=(SELECT id FROM biker_print_job WHERE status='PENDING' ORDER BY id FOR UPDATE SKIP LOCKED LIMIT 1)
            RETURNING id, payload::text AS payload
            """);
        if(rows.isEmpty()) return Map.of("available",false);
        return Map.of("available",true,"id",rows.get(0).get("id"),"payload",rows.get(0).get("payload"));
    }
    public void acknowledge(long id, boolean success, String message) {
        // Nunca reintentar automáticamente un trabajo ambiguo: puede haberse impreso.
        jdbc.update("UPDATE biker_print_job SET status=?, finished_at=now(), result_message=? WHERE id=? AND status='CLAIMED'",success?"SUBMITTED":"REVIEW",message,id);
    }
    public String status(long saleId) {
        return jdbc.query("SELECT status FROM biker_print_job WHERE sale_id=? AND job_type='SALE' ORDER BY id DESC LIMIT 1",rs -> rs.next()?rs.getString(1):"NOT_QUEUED",saleId);
    }
    @Transactional
    public void requeue(long saleId) {
        int updated=jdbc.update("UPDATE biker_print_job SET status='PENDING',claimed_at=NULL,finished_at=NULL,result_message=NULL WHERE sale_id=? AND job_type='SALE' AND status IN ('SUBMITTED','REVIEW')",saleId);
        if(updated==0) throw new IllegalStateException("No existe trabajo terminado para reimprimir");
    }
}
