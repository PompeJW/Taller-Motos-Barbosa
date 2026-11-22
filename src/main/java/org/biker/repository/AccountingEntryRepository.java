package org.biker.repository;

import org.biker.model.AccountingEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountingEntryRepository extends JpaRepository<AccountingEntry, Long>,
        JpaSpecificationExecutor<AccountingEntry> {
}
