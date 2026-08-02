package org.biker.controller;

import org.biker.dto.AccountingEntryRequest;
import org.biker.dto.AccountingEntryResponse;
import org.biker.model.AccountingEntry;
import org.biker.repository.AccountingEntryRepository;
import org.biker.service.AccountingEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounting-entries")
public class AccountingEntryController {

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


    // Consultar saldo de caja
    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance() {
        return ResponseEntity.ok(service.getBalance());
    }
}
