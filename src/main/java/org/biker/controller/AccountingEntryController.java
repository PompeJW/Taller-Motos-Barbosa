package org.biker.controller;

import org.biker.dto.AccountingEntryRequest;
import org.biker.dto.AccountingEntryResponse;
import org.biker.model.AccountingEntry;
import org.biker.repository.AccountingEntryRepository;
import org.biker.service.AccountingEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Map;
import java.util.ArrayList;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/api/accounting-entries")
public class AccountingEntryController {

    @PersistenceContext
    private EntityManager entityManager;

    private final AccountingEntryService service;
    private final AccountingEntryRepository repository;

    public AccountingEntryController(AccountingEntryService service, AccountingEntryRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    // Registrar movimiento (ingreso o egreso)
    @PostMapping
    public ResponseEntity<AccountingEntry> createEntry(@RequestBody AccountingEntryRequest dto) {
        AccountingEntry saved = service.createEntry(dto);
        return ResponseEntity.ok(saved);
    }

    // Registrar egreso directamente desde UI
    @PostMapping("/expense")
    public ResponseEntity<AccountingEntry> createExpense(@RequestBody AccountingEntryRequest dto) {
        AccountingEntryRequest expenseDto = new AccountingEntryRequest(
                "EGRESO",
                dto.amount(),
                dto.entryDate(),
                dto.description(),
                null,
                null,
                dto.expenseCategory()
        );
        AccountingEntry saved = service.createEntry(expenseDto);
        return ResponseEntity.ok(saved);
    }

    // Listar todos los movimientos
    @GetMapping
    public List<AccountingEntryResponse> getAllEntriesResponse() {
        return repository.findAll()
                .stream()
                .map(AccountingEntryResponse::fromEntity)
                .toList();
    }


    // Consulta paginada: rango [desde, hasta), para no cargar todo el historial.
    @GetMapping("/paged")
    public Page<AccountingEntryResponse> paged(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) Long category,
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.max(1, Math.min(size, 100));
        Specification<AccountingEntry> specification = (root, query, cb) -> {
            List<Predicate> conditions = new ArrayList<>();
            if (from != null) conditions.add(cb.greaterThanOrEqualTo(root.get("entryDate"), from.atStartOfDay()));
            if (to != null) conditions.add(cb.lessThan(root.get("entryDate"), to.plusDays(1).atStartOfDay()));
            if (type != null && !type.isBlank()) conditions.add(cb.equal(root.get("type"), type.trim()));
            if (category != null) conditions.add(cb.equal(root.get("expenseCategory").get("id"), category));
            if (search != null && !search.isBlank()) {
                conditions.add(cb.like(cb.lower(root.get("description")), "%" + search.trim().toLowerCase() + "%"));
            }
            return cb.and(conditions.toArray(new Predicate[0]));
        };
        return repository.findAll(specification,
            PageRequest.of(Math.max(0, page), safeSize,
                Sort.by(Sort.Direction.DESC, "entryDate").and(Sort.by(Sort.Direction.DESC, "id"))))
            .map(AccountingEntryResponse::fromEntity);
    }

    // Totales sobre TODOS los registros del período, independientes de la página actual.
    @GetMapping("/summary")
    public Map<String, Object> summary(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) Long category,
        @RequestParam(required = false) String search) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<AccountingEntry> root = query.from(AccountingEntry.class);
        List<Predicate> conditions = new ArrayList<>();
        if (from != null) conditions.add(cb.greaterThanOrEqualTo(root.get("entryDate"), from.atStartOfDay()));
        if (to != null) conditions.add(cb.lessThan(root.get("entryDate"), to.plusDays(1).atStartOfDay()));
        if (type != null && !type.isBlank()) conditions.add(cb.equal(root.get("type"), type.trim()));
        if (category != null) conditions.add(cb.equal(root.get("expenseCategory").get("id"), category));
        if (search != null && !search.isBlank()) {
            conditions.add(cb.like(cb.lower(root.get("description")), "%" + search.trim().toLowerCase() + "%"));
        }
        query.multiselect(
            cb.coalesce(cb.sum(cb.<Number>selectCase().when(cb.equal(root.get("type"), "INGRESO"), root.<Number>get("amount")).otherwise(0)), 0),
            cb.coalesce(cb.sum(cb.<Number>selectCase().when(cb.equal(root.get("type"), "EGRESO"), root.<Number>get("amount")).otherwise(0)), 0),
            cb.count(root)
        );
        query.where(conditions.toArray(new Predicate[0]));
        Object[] values = entityManager.createQuery(query).getSingleResult();
        double income = ((Number) values[0]).doubleValue();
        double expenses = ((Number) values[1]).doubleValue();
        return Map.of("income", income, "expenses", expenses, "net", income - expenses,
            "count", ((Number) values[2]).longValue());
    }

    // Consultar saldo de caja
    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance() {
        return ResponseEntity.ok(service.getBalance());
    }
}
