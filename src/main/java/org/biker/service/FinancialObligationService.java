package org.biker.service;

import org.biker.dto.*;
import org.biker.model.*;
import org.biker.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinancialObligationService {
    private final FinancialObligationRepository obligations;
    private final FinancialObligationPaymentRepository payments;
    private final AccountingEntryRepository accounting;
    private final MovementCategoryRepository categories;

    public FinancialObligationService(FinancialObligationRepository obligations,
            FinancialObligationPaymentRepository payments, AccountingEntryRepository accounting,
            MovementCategoryRepository categories) {
        this.obligations = obligations;
        this.payments = payments;
        this.accounting = accounting;
        this.categories = categories;
    }

    @Transactional
    public FinancialObligationResponse create(FinancialObligationCreateRequest request) {
        if (request == null || request.type() == null ||
                !(request.type().equals("PAYABLE") || request.type().equals("RECEIVABLE")))
            throw bad("El tipo debe ser PAYABLE o RECEIVABLE");
        if (blank(request.counterparty()) || blank(request.description()))
            throw bad("El tercero y la descripcion son obligatorios");
        if (request.totalAmount() == null || request.totalAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw bad("El valor debe ser mayor que cero");
        FinancialObligation entity = new FinancialObligation();
        entity.setType(request.type());
        entity.setCounterparty(request.counterparty().trim());
        entity.setDescription(request.description().trim());
        entity.setTotalAmount(request.totalAmount());
        entity.setPaidAmount(BigDecimal.ZERO);
        entity.setDueDate(request.dueDate());
        entity.setStatus("PENDING");
        if (request.categoryId() != null) {
            MovementCategory category = categories.findById(request.categoryId())
                .orElseThrow(() -> bad("La categoria seleccionada no existe"));
            validateCategoryType(request.type(), category);
            entity.setCategory(category);
        }
        return map(obligations.save(entity));
    }

    @Transactional(readOnly = true)
    public List<FinancialObligationResponse> all() {
        return obligations.findAll().stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public FinancialObligationResponse byId(Long id) {
        return map(obligations.findById(id).orElseThrow(() -> notFound()));
    }

    @Transactional
    public FinancialObligationPaymentResponse registerPayment(Long id, FinancialObligationPaymentRequest request) {
        FinancialObligation obligation = obligations.findWithLockById(id).orElseThrow(() -> notFound());
        if (request == null || request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw bad("El abono debe ser mayor que cero");
        BigDecimal remaining = obligation.getTotalAmount().subtract(obligation.getPaidAmount());
        if (request.amount().compareTo(remaining) > 0) throw bad("El abono supera el saldo pendiente");

        // La obligacion no mueve caja al crearse; el abono si.
        AccountingEntry entry = new AccountingEntry();
        entry.setType(obligation.getType().equals("PAYABLE") ? "EGRESO" : "INGRESO");
        entry.setAmount(request.amount());
        entry.setExpenseCategory(obligation.getCategory());
        entry.setEntryDate(LocalDateTime.now());
        entry.setDescription("Abono obligacion #" + id + " - " + obligation.getCounterparty()
                + (request.note() == null || request.note().isBlank() ? "" : " - " + request.note().trim()));
        entry = accounting.save(entry);

        FinancialObligationPayment payment = new FinancialObligationPayment();
        payment.setObligation(obligation);
        payment.setAmount(request.amount());
        payment.setPaidAt(entry.getEntryDate());
        payment.setNote(request.note());
        payment.setAccountingEntry(entry);
        payment = payments.save(payment);

        obligation.setPaidAmount(obligation.getPaidAmount().add(request.amount()));
        obligation.setStatus(obligation.getPaidAmount().compareTo(obligation.getTotalAmount()) == 0
                ? "PAID" : "PARTIAL");
        obligations.save(obligation);
        return new FinancialObligationPaymentResponse(payment.getId(), id, payment.getAmount(),
                payment.getPaidAt(), payment.getNote(), entry.getId());
    }

    @Transactional(readOnly = true)
    public List<FinancialObligationPaymentResponse> payments(Long id) {
        if (!obligations.existsById(id)) throw notFound();
        return payments.findByObligationIdOrderByPaidAtDesc(id).stream()
            .map(p -> new FinancialObligationPaymentResponse(p.getId(), id, p.getAmount(),
                    p.getPaidAt(), p.getNote(), p.getAccountingEntry().getId())).toList();
    }

    /**
     * Las categorias de ingreso no deben usarse para cuentas por pagar y viceversa.
     * Las categorias antiguas sin codigo se conservan compatibles hasta normalizarlas.
     */
    private void validateCategoryType(String obligationType, MovementCategory category) {
        String code = category.getCode();
        if (code == null || code.isBlank()) return;
        boolean income = code.equals("VENTA_REPUESTOS") || code.equals("SERVICIOS_TALLER");
        boolean expense = code.equals("NOMINA") || code.equals("COMPRA_INVENTARIO")
                || code.equals("GASTOS_OPERATIVOS") || code.equals("PAGO_PROVEEDORES")
                || code.equals("SERVICIOS_PUBLICOS") || code.equals("ARRIENDO");
        if (obligationType.equals("PAYABLE") && income)
            throw bad("Una cuenta por pagar no puede usar una categoria de ingreso: " + category.getName());
        if (obligationType.equals("RECEIVABLE") && expense)
            throw bad("Una cuenta por cobrar no puede usar una categoria de egreso: " + category.getName());
    }

    private FinancialObligationResponse map(FinancialObligation o) {
        return new FinancialObligationResponse(o.getId(), o.getType(), o.getCounterparty(),
                o.getDescription(), o.getTotalAmount(), o.getPaidAmount(),
                o.getTotalAmount().subtract(o.getPaidAmount()), o.getDueDate(), o.getCreatedAt(), o.getStatus(),
                o.getCategory() == null ? null : o.getCategory().getId(),
                o.getCategory() == null ? null : o.getCategory().getName());
    }
    private boolean blank(String s) { return s == null || s.isBlank(); }
    private ResponseStatusException bad(String msg) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg); }
    private ResponseStatusException notFound() { return new ResponseStatusException(HttpStatus.NOT_FOUND, "Obligacion no encontrada"); }
}
