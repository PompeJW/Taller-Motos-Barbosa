package org.biker.repository;

import org.biker.model.AccountingEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingEntryRepository extends JpaRepository<AccountingEntry, Long> {
}
