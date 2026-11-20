package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.biker.dto.BulkUploadResult;
import org.biker.dto.SparePartRequest;
import org.biker.dto.SparePartResponse;
import org.biker.model.*;
import org.biker.repository.*;
import org.biker.utils.Message;
import org.biker.utils.QrCodeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SparePartService {

    private final SparePartRepository repository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final StatusRepository statusRepository;

    public List<SparePartResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(sp -> new SparePartResponse(
                        sp.getId(),
                        sp.getName(),
                        sp.getDescription(),
                        sp.getSkuCode(),
                        sp.getPriceBuy(),
                        sp.getPriceSell(),
                        sp.getStock(),
                        sp.getMinStock(),
                        sp.getBrand().getName(),
                        sp.getStatus().getName(),
                        // ✅ Aquí usas Optional
                        Optional.ofNullable(sp.getSubCategory())
                                .map(SubCategory::getName)
                                .orElse("Sin subcategoría"),
                        sp.getBarcode()
                ))
                .toList();
    }

    public SparePart findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException(Message.SPARE_PART_NOT_FOUND));
    }
    public SparePart findByCode(String code) {

        if (code == null || code.isBlank()) {
            throw new RuntimeException(Message.SPARE_PART_NOT_FOUND);
        }

        // 🔹 Normalizar el código escaneado
        String normalizedCode = code
                .trim()                  // quitar espacios y saltos
                .replace("'", "-")       // reemplazar apóstrofe por guion
                .replace("’", "-")       // reemplazar comilla curva por guion
                .replace("`", "-")       // reemplazar backtick por guion
                .toUpperCase();          // forzar mayúsculas

        System.out.println("=================================");
        System.out.println("CÓDIGO RECIBIDO DEL ESCÁNER: [" + code + "]");
        System.out.println("CÓDIGO NORMALIZADO: [" + normalizedCode + "]");
        System.out.println("=================================");

        // 1. Buscar por SKU normalizado
        Optional<SparePart> sparePart = repository.findBySkuCode(normalizedCode);

        if (sparePart.isPresent()) {
            System.out.println("PRODUCTO ENCONTRADO POR SKU: "
                    + sparePart.get().getId()
                    + " - "
                    + sparePart.get().getName());
            return sparePart.get();
        }

        // 2. Buscar por barcode normalizado como respaldo
        sparePart = repository.findByBarcode(normalizedCode);

        if (sparePart.isPresent()) {
            System.out.println("PRODUCTO ENCONTRADO POR BARCODE: "
                    + sparePart.get().getId()
                    + " - "
                    + sparePart.get().getName());
            return sparePart.get();
        }

        System.out.println("❌ NO SE ENCONTRÓ PRODUCTO PARA: [" + normalizedCode + "]");
        throw new RuntimeException(Message.SPARE_PART_NOT_FOUND);
    }


    public List<SparePartResponse> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(sp -> new SparePartResponse(
                        sp.getId(),
                        sp.getName(),
                        sp.getDescription(),
                        sp.getSkuCode(),
                        sp.getPriceBuy(),
                        sp.getPriceSell(),
                        sp.getStock(),
                        sp.getMinStock(),
                        sp.getBrand().getName(),
                        sp.getStatus().getName(),
                        Optional.ofNullable(sp.getSubCategory())
                                .map(SubCategory::getName)
                                .orElse("Sin subcategoría"),
                        sp.getBarcode()
                ))
                .toList();
    }

    public List<SparePartResponse> searchBySkuCode(String skuCode) {
        return repository.findBySkuCodeContainingIgnoreCase(skuCode)
                .stream()
                .map(sp -> new SparePartResponse(
                        sp.getId(),
                        sp.getName(),
                        sp.getDescription(),
                        sp.getSkuCode(),
                        sp.getPriceBuy(),
                        sp.getPriceSell(),
                        sp.getStock(),
                        sp.getMinStock(),
                        sp.getBrand().getName(),
                        sp.getStatus().getName(),
                        Optional.ofNullable(sp.getSubCategory())
                                .map(SubCategory::getName)
                                .orElse("Sin subcategoría"),
                        sp.getBarcode()
                ))
                .toList();
    }



    public List<SparePartResponse> search(String query) {
        return repository.findByNameContainingIgnoreCaseOrSkuCodeContainingIgnoreCase(query, query)
                .stream()
                .map(sp -> new SparePartResponse(
                        sp.getId(),
                        sp.getName(),
                        sp.getDescription(),
                        sp.getSkuCode(),
                        sp.getPriceBuy(),
                        sp.getPriceSell(),
                        sp.getStock(),
                        sp.getMinStock(),
                        sp.getBrand().getName(),
                        sp.getStatus().getName(),
                        // ✅ Aquí usas Optional
                        Optional.ofNullable(sp.getSubCategory())
                                .map(SubCategory::getName)
                                .orElse("Sin subcategoría"),
                        sp.getBarcode()
                ))
                .toList();
    }


    public List<SparePart> getLowStock() {
        return repository.findByStockLessThanEqual();
    }

    private String generateSkuCode(Category category) {
        // Prefijo: primeras 3 letras de la categoría
        String prefix = category.getName().substring(0, 3).toUpperCase();

        // Timestamp en milisegundos
        long timestamp = System.currentTimeMillis();

        // SKU final: CAT-<timestamp>
        return String.format("%s-%d", prefix, timestamp);
    }



    public SparePartResponse save(SparePartRequest request) {

        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() ->
                        new RuntimeException(Message.BRAND_NOT_FOUND));

        SubCategory subCategory = subCategoryRepository.findById(request.subCategoryId())
                .orElseThrow(() -> new RuntimeException(Message.SUBCATEGORY_NOT_FOUND));

        Category category = subCategory.getCategory();

        Status status = statusRepository.findById(request.statusId())
                .orElseThrow(() ->
                        new RuntimeException(Message.STATUS_NOT_FOUND));

        SparePart sparePart = new SparePart();
        sparePart.setName(request.name());
        sparePart.setDescription(request.description());
        sparePart.setPriceBuy(request.priceBuy());
        sparePart.setPriceSell(request.priceSell());
        sparePart.setStock(request.stock());
        sparePart.setMinStock(request.minStock());
        sparePart.setBrand(brand);
        sparePart.setStatus(status);
        sparePart.setSubCategory(subCategory);

        // 1️⃣ Guardar primero sin SKU
        SparePart saved = repository.save(sparePart);

        // 2️⃣ Generar SKU con categoría + ID
        String prefix = normalizePrefix(category.getName()).substring(0, 3);
        String nextCode = String.format("%s-%05d", prefix, saved.getId());
        saved.setSkuCode(nextCode);


        // 3️⃣ Generar QR
        String qrCode = QrCodeUtil.generateQrCode(nextCode);
        saved.setBarcode(qrCode);

        // 4️⃣ Guardar actualización
        SparePart updated = repository.save(saved);

        return new SparePartResponse(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getSkuCode(),
                saved.getPriceBuy(),
                saved.getPriceSell(),
                saved.getStock(),
                saved.getMinStock(),
                saved.getBrand().getName(),
                saved.getStatus().getName(),
                saved.getSubCategory().getName(),
                saved.getBarcode()
        );
    }

    private String normalizePrefix(String input) {
        if (input == null) return "";

        // Pasar a mayúsculas
        String normalized = input.toUpperCase();

        // Eliminar acentos y caracteres especiales
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        // Mantener solo letras A-Z y números
        normalized = normalized.replaceAll("[^A-Z0-9]", "");

        return normalized;
    }
    public SparePartResponse update(Long id, SparePartRequest request) {

        SparePart existing = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(Message.SPARE_PART_NOT_FOUND));

        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() ->
                        new RuntimeException(Message.BRAND_NOT_FOUND));

        SubCategory subCategory = subCategoryRepository.findById(request.subCategoryId())
                .orElseThrow(() -> new RuntimeException(Message.SUBCATEGORY_NOT_FOUND));

        Category category = subCategory.getCategory();


        Status status = statusRepository.findById(request.statusId())
                .orElseThrow(() ->
                        new RuntimeException(Message.STATUS_NOT_FOUND));

        existing.setName(request.name());
        existing.setDescription(request.description());
        existing.setSkuCode(request.skuCode());
        existing.setPriceBuy(request.priceBuy());
        existing.setPriceSell(request.priceSell());
        existing.setStock(request.stock());
        existing.setMinStock(request.minStock());
        existing.setBrand(brand);
        existing.setStatus(status);
        existing.setSubCategory(subCategory);

        SparePart updated = repository.save(existing);

        return new SparePartResponse(
                updated.getId(),
                updated.getName(),
                updated.getDescription(),
                updated.getSkuCode(),
                updated.getPriceBuy(),
                updated.getPriceSell(),
                updated.getStock(),
                updated.getMinStock(),
                updated.getBrand().getName(),
                updated.getStatus().getName(),
                updated.getSubCategory().getName(),
                updated.getBarcode()
        );
    }

    @Transactional
    public BulkUploadResult bulkUpload(MultipartFile file) {
        List<SparePartResponse> success = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo Excel está vacío.");
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet("CargaMasiva");

            if (sheet == null) {
                throw new IllegalArgumentException(
                        "No existe la hoja 'CargaMasiva' en el archivo.");
            }

            FormulaEvaluator evaluator = workbook.getCreationHelper()
                    .createFormulaEvaluator();
            DataFormatter formatter = new DataFormatter();

            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new IllegalArgumentException(
                        "El archivo no contiene encabezados.");
            }

            // Los encabezados determinan las posiciones de las columnas.
            Map<String, Integer> columns = new HashMap<>();

            for (Cell cell : headerRow) {
                String header = formatter
                        .formatCellValue(cell, evaluator)
                        .trim()
                        .toLowerCase(Locale.ROOT);

                if (!header.isEmpty()) {
                    columns.put(header, cell.getColumnIndex());
                }
            }

            List<String> requiredHeaders = List.of(
                    "name", "description", "pricebuy", "pricesell",
                    "stock", "minstock", "brandid", "statusid",
                    "categoryid", "subcategoryid"
            );

            List<String> missingHeaders = requiredHeaders.stream()
                    .filter(h -> !columns.containsKey(h))
                    .toList();

            if (!missingHeaders.isEmpty()) {
                throw new IllegalArgumentException(
                        "Faltan encabezados obligatorios: "
                                + String.join(", ", missingHeaders));
            }



            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (isEmptyRow(row, formatter, evaluator)) {
                    continue;
                }

                int excelRow = i + 1;

                try {
                    String name = requiredText(
                            row, columns, "name", evaluator);

                    String description = readText(
                            row, columns, "description", evaluator);

                    BigDecimal priceBuy = requiredDecimal(
                            row, columns, "pricebuy", evaluator);

                    BigDecimal priceSell = requiredDecimal(
                            row, columns, "pricesell", evaluator);

                    Integer stock = requiredInteger(
                            row, columns, "stock", evaluator);

                    Integer minStock = requiredInteger(
                            row, columns, "minstock", evaluator);

                    Long brandId = requiredLong(
                            row, columns, "brandid", evaluator);

                    Long statusId = requiredLong(
                            row, columns, "statusid", evaluator);

                    Long categoryId = requiredLong(
                            row, columns, "categoryid", evaluator);

                    Long subCategoryId = requiredLong(
                            row, columns, "subcategoryid", evaluator);

                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "No existe una categoría con ID " + categoryId
                            ));

                    SubCategory subCategory = subCategoryRepository
                            .findById(subCategoryId)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "No existe una subcategoría con ID " + subCategoryId
                            ));

                    if (subCategory.getCategory() == null ||
                            !Objects.equals(
                                    subCategory.getCategory().getId(),
                                    category.getId()
                            )) {

                        throw new IllegalArgumentException(
                                "La subcategoría con ID " + subCategoryId +
                                        " no pertenece a la categoría con ID " + categoryId
                        );
                    }

                    // Comprobar que los IDs de marca y estado existen.
                    if (!brandRepository.existsById(brandId)) {
                        throw new IllegalArgumentException(
                                "No existe una marca con ID " + brandId + ".");
                    }

                    if (!statusRepository.existsById(statusId)) {
                        throw new IllegalArgumentException(
                                "No existe un estado con ID " + statusId + ".");
                    }

                    if (priceBuy.signum() < 0 || priceSell.signum() < 0) {
                        throw new IllegalArgumentException(
                                "Los precios no pueden ser negativos.");
                    }

                    if (stock < 0 || minStock < 0) {
                        throw new IllegalArgumentException(
                                "El stock y el stock mínimo no pueden "
                                        + "ser negativos.");
                    }

                    SparePartRequest request = new SparePartRequest(
                            name,
                            description,
                            null, // SKU generado por save()
                            priceBuy,
                            priceSell,
                            stock,
                            minStock,
                            brandId,
                            statusId,
                            subCategory.getId()
                    );

                    success.add(save(request));

                } catch (Exception e) {
                    String message = e.getMessage() == null
                            ? e.getClass().getSimpleName()
                            : e.getMessage();

                    errors.add("Fila " + excelRow + ": " + message);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error leyendo Excel", e);
        }

        return new BulkUploadResult(success, errors);
    }


    private static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim()
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }

    private static boolean isEmptyRow(
            Row row,
            DataFormatter formatter,
            FormulaEvaluator evaluator) {

        if (row == null) {
            return true;
        }

        for (Cell cell : row) {
            if (!formatter.formatCellValue(cell, evaluator).isBlank()) {
                return false;
            }
        }

        return true;
    }

    private static CellValue getValue(
            Row row,
            Map<String, Integer> columns,
            String column,
            FormulaEvaluator evaluator) {

        Integer index = columns.get(column);

        if (row == null || index == null) {
            return null;
        }

        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        return cell == null ? null : evaluator.evaluate(cell);
    }

    private static String readText(
            Row row,
            Map<String, Integer> columns,
            String column,
            FormulaEvaluator evaluator) {

        CellValue value = getValue(row, columns, column, evaluator);

        if (value == null || value.getCellType() == CellType.BLANK) {
            return null;
        }

        return switch (value.getCellType()) {
            case STRING -> value.getStringValue().trim();
            case NUMERIC -> BigDecimal.valueOf(value.getNumberValue())
                    .stripTrailingZeros().toPlainString();
            default -> throw new IllegalArgumentException(
                    "La columna '" + column
                            + "' debe contener texto.");
        };
    }

    private static String requiredText(
            Row row,
            Map<String, Integer> columns,
            String column,
            FormulaEvaluator evaluator) {

        String value = readText(row, columns, column, evaluator);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "El campo '" + column + "' es obligatorio.");
        }

        return value;
    }

    private static BigDecimal requiredDecimal(
            Row row,
            Map<String, Integer> columns,
            String column,
            FormulaEvaluator evaluator) {

        CellValue value = getValue(row, columns, column, evaluator);

        if (value == null || value.getCellType() == CellType.BLANK) {
            throw new IllegalArgumentException(
                    "El campo '" + column + "' es obligatorio.");
        }

        try {
            if (value.getCellType() == CellType.NUMERIC) {
                return BigDecimal.valueOf(value.getNumberValue());
            }

            if (value.getCellType() == CellType.STRING) {
                String raw = value.getStringValue().trim()
                        .replaceAll("\\s+", "");

                // Admite decimales con coma, por ejemplo 12500,50.
                // Para importes, es preferible que Excel almacene
                // los valores como números reales.
                if (raw.contains(",") && raw.contains(".")) {
                    if (raw.lastIndexOf(',') > raw.lastIndexOf('.')) {
                        raw = raw.replace(".", "").replace(',', '.');
                    } else {
                        raw = raw.replace(",", "");
                    }
                } else if (raw.contains(",")) {
                    raw = raw.replace(',', '.');
                }

                return new BigDecimal(raw);
            }

            throw new IllegalArgumentException("Tipo de celda no válido.");

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El campo '" + column
                            + "' no contiene un decimal válido.");
        }
    }

    private static Integer requiredInteger(
            Row row,
            Map<String, Integer> columns,
            String column,
            FormulaEvaluator evaluator) {

        try {
            return requiredDecimal(row, columns, column, evaluator)
                    .intValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                    "El campo '" + column
                            + "' debe ser un número entero.");
        }
    }

    private static Long requiredLong(
            Row row,
            Map<String, Integer> columns,
            String column,
            FormulaEvaluator evaluator) {

        try {
            return requiredDecimal(row, columns, column, evaluator)
                    .longValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                    "El campo '" + column
                            + "' debe ser un ID entero válido.");
        }
    }

}