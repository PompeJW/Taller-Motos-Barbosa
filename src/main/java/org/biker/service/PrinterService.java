package org.biker.service;

import org.biker.dto.*;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Service
public class PrinterService {

    public void printInvoice(SaleResponse sale) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Seleccionar code page 850 (Latin-1 con tildes y ñ)
        baos.write(new byte[]{0x1B, 0x74, 0x02});

        // Encabezado

        writeText(baos, "J&M Taller\n", true);
        writeText(baos, "FACTURA DE VENTA\n", true);
        writeText(baos, "Número: " + sale.saleNumber() + "\n", true);
        writeText(baos, "Fecha: " + sale.saleDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n", true);
        writeText(baos, "Cliente: " + sale.clientName() + "\n", true);
        writeText(baos, "Documento: " + sale.Clientdocumento() + "\n\n", true);
        // Métodos de pago
        writeText(baos, "Métodos de pago\n", true);
        for (SalePaymentResponse pay : sale.payments()) {
            writeText(baos, "  " + pay.methodName() + ": " + pay.amount() + "\n", true);
        }
        baos.write("\n".getBytes(StandardCharsets.UTF_8));


        //baos.write(center("FACTURA DE VENTA\n").getBytes(StandardCharsets.UTF_8));
        //baos.write(center("Número: " + sale.saleNumber() + "\n").getBytes(StandardCharsets.ISO_8859_1));
        //baos.write(center("Fecha: " + sale.saleDate() + "\n").getBytes(StandardCharsets.UTF_8));
        //baos.write(center("Cliente: " + sale.clientName() + "\n").getBytes(StandardCharsets.ISO_8859_1));
        //baos.write(center("Método de pago: " + sale.paymentMethod() + "\n\n").getBytes(StandardCharsets.ISO_8859_1));

        // Detalles
        for (SaleItemResponse item : sale.items()) {
            baos.write(("  " + item.sparePartName() +  " x" + item.quantity() + " @ " + item.unitPrice() + "\n").getBytes(StandardCharsets.UTF_8));
            baos.write(("  " + "Subtotal: " + item.subtotal() + "\n\n").getBytes(StandardCharsets.UTF_8));

            // ✅ Aquí luego podemos imprimir QR/barcode del producto
             //baos.write(printQr(item.sparePartName()).getBytes(StandardCharsets.UTF_8));
        }

        // Total
        baos.write(center("\nTOTAL VENTA: " + sale.total() + "\n").getBytes(StandardCharsets.UTF_8));

        // ✅ Agregar espacio extra (2 cm aprox. = 5 a 8 saltos de línea)
        for (int i = 0; i < 8; i++) {
            baos.write("\n".getBytes(StandardCharsets.UTF_8));
        }
        // Corte de papel
        baos.write(new byte[]{0x1D, 0x56, 0x00}); // ESC/POS cut command

        // Enviar a la impresora
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService printer = services[0]; // Selecciona la PR-255
        DocPrintJob job = printer.createPrintJob();
        Doc doc = new SimpleDoc(baos.toByteArray(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
        job.print(doc, null);
    }

    public void printWorkOrder(WorkOrderResponse wo, List<PaymentResponse> payments) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Encabezado
        baos.write(center("ORDEN DE TRABAJO\n").getBytes("CP850"));
        baos.write(center("Número: " + wo.orderNumber() + "\n").getBytes("CP850"));
        baos.write(center("Fecha: " + wo.createdAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n").getBytes("CP850"));
        baos.write("\n".getBytes("CP850"));

        // Cliente y moto
        if (wo.client() != null) {
            baos.write(("Cliente: " + wo.client().clientName() + "\n").getBytes("CP850"));
            baos.write(("Documento: " + (wo.client().documento() != null ? wo.client().documento() : "N/A") + "\n").getBytes("CP850"));
        }
        baos.write(("Moto: " + wo.brandName() + " " + wo.model() + " - Placa: " + wo.motorcyclePlate() + "\n").getBytes("CP850"));
        baos.write("\n".getBytes("CP850"));

        // Observaciones
        if (wo.observations() != null && !wo.observations().isEmpty()) {
            baos.write("Observaciones:\n".getBytes("CP850"));
            for (ObservationResponse obs : wo.observations()) {
                baos.write(("- " + obs.description() + "\n").getBytes("CP850"));
            }
            baos.write("\n".getBytes("CP850"));
        }


        baos.write("Repuestos:\n".getBytes("CP850"));
        for (WorkOrderDetailResponse d : wo.details()) {
            baos.write((d.sparePartName() + " x" + d.quantity() + " @ " + d.unitPrice() + " = " + d.unitPrice().multiply(BigDecimal.valueOf(d.quantity())) + "\n").getBytes("CP850"));
        }

        // Mano de obra y repuestos
        baos.write(("Mano de obra: " + wo.labour() + "\n").getBytes("CP850"));
        // Total y estado
        baos.write(("\nTOTAL: " + wo.totalValue() + "\n").getBytes("CP850"));
        baos.write(("Estado OT: " + wo.statusName() + "\n\n\n").getBytes("CP850"));

// Pago(s)
        if (payments != null && !payments.isEmpty()) {
            for (PaymentResponse p : payments) {


                baos.write(center("Pagos Realizados: " + "\n\n").getBytes("CP850"));
                baos.write(("Método Pago: " + p.paymentMethod() + "\n").getBytes("CP850"));
                baos.write(("Monto: " + p.amount() + "\n").getBytes("CP850"));
                baos.write(("Fecha: " + p.paymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n").getBytes("CP850"));
                baos.write(("Estado de Pago OT: " + (p.paymentStatus() != null ? p.paymentStatus() : "N/A") + "\n").getBytes("CP850"));
                baos.write("\n".getBytes("CP850"));
            }
        } else {
            baos.write("Sin pagos registrados\n".getBytes("CP850"));
        }



        // Espacio extra y corte
        for (int i = 0; i < 8; i++) baos.write("\n".getBytes("CP850"));
        baos.write(new byte[]{0x1D, 0x56, 0x00});

        // Enviar a impresora
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        if (services.length == 0) throw new IllegalStateException("No hay impresoras disponibles");
        PrintService printer = services[0];
        DocPrintJob job = printer.createPrintJob();
        Doc doc = new SimpleDoc(baos.toByteArray(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
        job.print(doc, null);
    }

    private void writeText(ByteArrayOutputStream baos, String text, boolean center) throws Exception {
        String formatted = center ? center(text) : text;
        baos.write(formatted.getBytes("CP850")); // encoding correcto
    }

    private String center(String text) {
        return "\u001b\u0061\u0001" + text + "\u001b\u0061\u0000"; // ESC/POS center
    }


    public void printQr(String base64Qr) throws Exception {
        // Buscar impresoras disponibles
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        if (services.length == 0) {
            throw new RuntimeException("❌ No se encontró ninguna impresora instalada");
        }

        PrintService printer = services[0]; // usa la primera impresora encontrada
        DocPrintJob job = printer.createPrintJob();

        // Decodificar el QR base64 a imagen
        byte[] qrBytes = Base64.getDecoder().decode(base64Qr);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(qrBytes));

        // Escalar la imagen a 3 cm x 3 cm
        // ⚠️ Ajusta según la resolución de tu impresora:
        // - 96 dpi ≈ 118 px
        // - 200 dpi ≈ 236 px
        // - 300 dpi ≈ 354 px
        int targetSizePx = 354; // ejemplo para impresora de 200 dpi
        BufferedImage scaled = new BufferedImage(targetSizePx, targetSizePx, BufferedImage.TYPE_INT_RGB);
        scaled.getGraphics().drawImage(image, 0, 0, targetSizePx, targetSizePx, null);

        // Convertir la imagen escalada a PNG en memoria
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(scaled, "png", baos);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        // Crear documento con formato PNG
        Doc doc = new SimpleDoc(bais, DocFlavor.INPUT_STREAM.PNG, null);

        // Enviar a la impresora (una sola copia)
        job.print(doc, null);
    }
    public void printPayment(WorkOrderResponse wo, PaymentResponse payment) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x1B, 0x74, 0x02});

        baos.write(center("COMPROBANTE DE PAGO\n").getBytes("CP850"));
        baos.write(("Orden: " + wo.orderNumber() + "\n").getBytes("CP850"));
        baos.write(("Cliente: " + wo.client().clientName() + "\n").getBytes("CP850"));
        baos.write(("Documento: " + wo.client().documento() + "\n\n").getBytes("CP850"));
        baos.write(("Placa Vehiculo: " + wo.motorcyclePlate() + "\n").getBytes("CP850"));
        baos.write(("Marca: " + wo.brandName() + "\n").getBytes("CP850"));
        baos.write(("Modelo: " + wo.model() + "\n").getBytes("CP850"));
        baos.write(("Estado: " + wo.statusName() + "\n").getBytes("CP850"));
        baos.write(("Método: " + payment.paymentMethod() + "\n").getBytes("CP850"));
        baos.write(("Monto: " + payment.amount() + "\n").getBytes("CP850"));
        baos.write(("Fecha: " + payment.paymentDate() + "\n").getBytes("CP850"));

        for (int i = 0; i < 6; i++) baos.write("\n".getBytes("CP850"));
        baos.write(new byte[]{0x1D, 0x56, 0x00});

        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService printer = services[0];
        DocPrintJob job = printer.createPrintJob();
        Doc doc = new SimpleDoc(baos.toByteArray(), DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
        job.print(doc, null);
    }


}
