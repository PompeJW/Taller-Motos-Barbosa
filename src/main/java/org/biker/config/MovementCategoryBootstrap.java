package org.biker.config;

import lombok.RequiredArgsConstructor;
import org.biker.model.MovementCategory;
import org.biker.repository.MovementCategoryRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MovementCategoryBootstrap implements ApplicationRunner {
    private final MovementCategoryRepository categories;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        ensure("VENTA_REPUESTOS", "Venta de repuestos");
        ensure("SERVICIOS_TALLER", "Servicios de taller");
        ensure("NOMINA", "Nómina");
        ensure("COMPRA_INVENTARIO", "Compra de inventario");
        ensure("GASTOS_OPERATIVOS", "Gastos operativos");
        ensure("PAGO_PROVEEDORES", "Pago a proveedores");
        ensure("SERVICIOS_PUBLICOS", "Servicios públicos");
        ensure("ARRIENDO", "Arriendo");
    }

    private void ensure(String code, String name) {
        if (categories.findByCode(code).isPresent()) return;
        // Reutilizar una categoría preexistente con el mismo nombre, si no tiene código.
        // No renombrar ni reclasificar categorías ya utilizadas.
        var existing = categories.findByNameIgnoreCase(name);
        if (existing.isPresent()) {
            MovementCategory category = existing.get();
            if (category.getCode() == null || category.getCode().isBlank()) {
                category.setCode(code);
                categories.save(category);
                return;
            }
        }
        MovementCategory category = new MovementCategory();
        category.setCode(code);
        category.setName(name);
        categories.save(category);
    }
}
