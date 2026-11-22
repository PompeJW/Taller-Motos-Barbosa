package org.biker.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.biker.model.SparePart;
import org.biker.repository.SparePartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryExcelReportService {
    private final SparePartRepository repository;

    @Transactional(readOnly = true)
    public byte[] generate() throws IOException {
        List<SparePart> parts = repository.findAll().stream()
                .sorted(Comparator.comparing(SparePart::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(SparePart::getId))
                .toList();
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Inventario");
            String[] headers = {"ID", "SKU", "Repuesto", "Descripción", "Marca", "Categoría", "Subcategoría",
                    "Estado", "Stock sistema", "Stock mínimo", "Costo unitario", "Precio venta", "Valor inventario",
                    "Conteo físico", "Diferencia", "Observaciones"};
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont(); font.setBold(true); headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
            Row heading = sheet.createRow(0);
            for (int c = 0; c < headers.length; c++) {
                Cell cell = heading.createCell(c); cell.setCellValue(headers[c]); cell.setCellStyle(headerStyle);
            }
            int rowNum = 1;
            for (SparePart p : parts) {
                Row r = sheet.createRow(rowNum++);
                num(r, 0, p.getId());
                str(r, 1, p.getSkuCode()); str(r, 2, p.getName()); str(r, 3, p.getDescription());
                str(r, 4, p.getBrand() == null ? "" : p.getBrand().getName());
                str(r, 5, p.getSubCategory() == null || p.getSubCategory().getCategory() == null ? "" : p.getSubCategory().getCategory().getName());
                str(r, 6, p.getSubCategory() == null ? "" : p.getSubCategory().getName());
                str(r, 7, p.getStatus() == null ? "" : p.getStatus().getName());
                num(r, 8, p.getStock()); num(r, 9, p.getMinStock());
                money(r, 10, p.getPriceBuy(), moneyStyle); money(r, 11, p.getPriceSell(), moneyStyle);
                money(r, 12, (p.getPriceBuy() == null ? BigDecimal.ZERO : p.getPriceBuy())
                        .multiply(BigDecimal.valueOf(p.getStock() == null ? 0 : p.getStock())), moneyStyle);
                // Columnas 13-15 quedan vacías para el inventario físico.
                r.createCell(14).setCellFormula("IF(N" + rowNum + "=\"\",\"\",N" + rowNum + "-I" + rowNum + ")");
            }
            sheet.createFreezePane(0, 1);
            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, Math.max(1, parts.size()), 0, 15));
            int[] widths = {12, 23, 38, 55, 24, 24, 24, 20, 17, 17, 19, 19, 22, 18, 18, 40};
            for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i] * 256);
            Sheet info = workbook.createSheet("Información");
            info.createRow(0).createCell(0).setCellValue("Reporte generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            info.createRow(1).createCell(0).setCellValue("Referencias: " + parts.size());
            info.createRow(2).createCell(0).setCellValue("Stock sistema: existencias registradas al generar el reporte; no se actualizan al editar Excel.");
            info.createRow(3).createCell(0).setCellValue("Conteo físico: anotar unidades contadas; Diferencia = conteo físico - stock sistema.");
            info.setColumnWidth(0, 110 * 256);
            workbook.write(out);
            return out.toByteArray();
        }
    }
    private void str(Row r, int col, String value) { r.createCell(col).setCellValue(value == null ? "" : value); }
    private void num(Row r, int col, Number value) { if (value != null) r.createCell(col).setCellValue(value.doubleValue()); }
    private void money(Row r, int col, BigDecimal value, CellStyle style) {
        Cell c = r.createCell(col); c.setCellValue(value == null ? 0 : value.doubleValue()); c.setCellStyle(style);
    }
}
