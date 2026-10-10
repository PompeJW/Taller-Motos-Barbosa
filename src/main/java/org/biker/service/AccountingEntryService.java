package org.biker.service;

import org.biker.dto.AccountingEntryRequest;
import org.biker.dto.AccountingEntryResponse;
import org.biker.model.AccountingEntry;
import org.biker.model.MovementCategory;
import org.biker.model.Payment;
import org.biker.model.Sale;
import org.biker.repository.AccountingEntryRepository;
import org.biker.repository.MovementCategoryRepository;
import org.biker.repository.PaymentRepository;
import org.biker.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountingEntryService {

    private final AccountingEntryRepository repository;
    private final PaymentRepository paymentRepository;
    private final SaleRepository saleRepository;
    private final MovementCategoryRepository categoryRepository;

    public AccountingEntryService(AccountingEntryRepository repository,
                                  PaymentRepository paymentRepository,
                                  SaleRepository saleRepository,
                                  MovementCategoryRepository categoryRepository) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
        this.saleRepository = saleRepository;
        this.categoryRepository = categoryRepository;
    }

    // Crear movimiento contable desde DTO
    public AccountingEntry createEntry(AccountingEntryRequest dto) {
        AccountingEntry entry = new AccountingEntry();
        entry.setType(dto.type());
        entry.setAmount(dto.amount());
        entry.setEntryDate(dto.entryDate() != null ? dto.entryDate() : java.time.LocalDateTime.now());
        entry.setDescription(dto.description());

        if (dto.payment() != null) {
            Payment payment = paymentRepository.findById(dto.payment()).orElse(null);
            entry.setPayment(payment);
        }

        if (dto.sale() != null) {
            Sale sale = saleRepository.findById(dto.sale()).orElse(null);
            entry.setSale(sale);
        }

        if (dto.expenseCategory() != null) {
            MovementCategory category = categoryRepository.findById(dto.expenseCategory()).orElse(null);
            entry.setExpenseCategory(category);
        }

        return repository.save(entry);
    }

    public List<AccountingEntry> getAllEntries() {
        return repository.findAll();
    }

    public Double getBalance() {
        return repository.findAll().stream()
                .mapToDouble(entry -> entry.getType().equalsIgnoreCase("INGRESO")
                        ? entry.getAmount().doubleValue()
                        : -entry.getAmount().doubleValue())
                .sum();
    }

    public List<AccountingEntryResponse> getAllEntriesResponse() {
        return repository.findAll()
                .stream()
                .map(entry -> new AccountingEntryResponse(
                        entry.getId(),
                        entry.getType(),
                        entry.getAmount(),
                        entry.getEntryDate(),
                        entry.getDescription(),
                        entry.getPayment() != null ? entry.getPayment().getId() : null,
                        entry.getSale() != null ? entry.getSale().getId() : null,
                        entry.getExpenseCategory() != null ? entry.getExpenseCategory().getId() : null,
                        entry.getExpenseCategory() != null ? entry.getExpenseCategory().getName() : null,
                        entry.getPayment() != null && entry.getPayment().getWorkOrder() != null
                                ? entry.getPayment().getWorkOrder().getId()
                                : null   // 👈 ahora sí pasamos el workOrderId
                ))
                .toList();
    }


}
