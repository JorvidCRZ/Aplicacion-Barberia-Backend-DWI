package com.sistemabarberia.fadex_backend.modules.analisis.Service;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import com.sistemabarberia.fadex_backend.modules.analisis.dto.ResumenDiaDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.entity.MetodoPago;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.itextpdf.layout.element.Table;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {
    private final ReservaRepository reservaRepository;

    @Override
    public List<ResumenDiaDTO> getResumenSemanal(LocalDate desde, LocalDate hasta) {
        List<Object[]> rows = reservaRepository.resumenSemanal(desde, hasta);
        return rows.stream().map(r -> new ResumenDiaDTO(
                (String) r[0],
                ((Number) r[1]).longValue(),
                ((Number) r[2]).longValue(),
                ((Number) r[3]).longValue(),
                r[4] != null ? (BigDecimal) r[4] : BigDecimal.ZERO
        )).toList();
    }

    // ─── PDF RESERVAS
    @Override
    public byte[] generarReservasPdf(LocalDate desde, LocalDate hasta, Long barberoId, Long servicioId, EstadoReserva estado, MetodoPago metodoPago) {
        List<Reserva> reservas = reservaRepository.findReservasFiltradas(
                desde, hasta, barberoId, servicioId,
                estado != null ? estado.name() : null,
                metodoPago != null ? metodoPago.name() : null
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        DeviceRgb negro     = new DeviceRgb(18, 18, 18);
        DeviceRgb dorado    = new DeviceRgb(201, 168, 76);
        DeviceRgb blanco    = new DeviceRgb(255, 255, 255);
        DeviceRgb grisClaro = new DeviceRgb(245, 245, 245);

        Table header = new Table(UnitValue.createPercentArray(new float[]{1})).useAllAvailableWidth();
        Cell headerCell = new Cell()
                .add(new Paragraph("FadeX — Barbería").setBold().setFontSize(20).setFontColor(dorado))
                .add(new Paragraph("Reporte: Reservas").setFontSize(12).setFontColor(dorado))
                .add(new Paragraph("Período: " + desde + " al " + hasta).setFontSize(10).setFontColor(blanco))
                .setBackgroundColor(negro)
                .setBorder(Border.NO_BORDER)
                .setPadding(20);
        header.addCell(headerCell);
        doc.add(header);
        doc.add(new Paragraph("\n"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2, 2, 2}))
                .useAllAvailableWidth();

        String[] cols = {"ID", "Fecha", "Cliente", "Barbero", "Servicio", "Total"};
        for (String col : cols) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(col).setBold().setFontColor(negro))
                    .setBackgroundColor(dorado)
                    .setPadding(8));
        }

        boolean par = false;
        for (Reserva r : reservas) {
            DeviceRgb fondo = par ? grisClaro : blanco;
            table.addCell(new Cell().add(new Paragraph(String.valueOf(r.getId()))).setBackgroundColor(fondo).setPadding(6));
            table.addCell(new Cell().add(new Paragraph(r.getFecha().toString())).setBackgroundColor(fondo).setPadding(6));
            table.addCell(new Cell().add(new Paragraph(r.getCliente() != null ? r.getCliente().getPersona().getNombre() : "Sin cliente")).setBackgroundColor(fondo).setPadding(6));
            table.addCell(new Cell().add(new Paragraph(r.getBarbero() != null ? r.getBarbero().getPersona().getNombre() : "Sin barbero")).setBackgroundColor(fondo).setPadding(6));
            table.addCell(new Cell().add(new Paragraph(r.getServicio() != null ? r.getServicio().getNombre() : "Sin servicio")).setBackgroundColor(fondo).setPadding(6));
            table.addCell(new Cell().add(new Paragraph("S/ " + r.getTotal())).setBackgroundColor(fondo).setPadding(6));
            par = !par;
        }

        long total = reservas.size();
        table.addCell(new Cell(1, 5).add(new Paragraph("TOTAL").setBold()).setBackgroundColor(dorado).setPadding(8));
        table.addCell(new Cell().add(new Paragraph(total + " reservas").setBold()).setBackgroundColor(dorado).setPadding(8));

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    // ─── EXCEL RESERVAS
    @Override
    public byte[] generarReservasExcel(LocalDate desde, LocalDate hasta, Long barberoId, Long servicioId, EstadoReserva estado, MetodoPago metodoPago) {
        List<Reserva> reservas = reservaRepository.findReservasFiltradas(
                desde, hasta, barberoId, servicioId,
                estado != null ? estado.name() : null,
                metodoPago != null ? metodoPago.name() : null
        );
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reservas");

            XSSFCellStyle headerStyle    = crearEstiloHeader(workbook);
            XSSFCellStyle filaParStyle   = crearEstiloFilaPar(workbook);
            XSSFCellStyle filaImparStyle = crearEstiloFilaImpar(workbook);

            Row header = sheet.createRow(0);
            String[] cols = {"ID", "Fecha", "Cliente", "Barbero", "Servicio", "Estado", "Total"};
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 5000);
            }

            int rowNum = 1;
            for (Reserva r : reservas) {
                Row row = sheet.createRow(rowNum);
                XSSFCellStyle estilo = (rowNum % 2 == 0) ? filaParStyle : filaImparStyle;
                setCellStyled(row, 0, String.valueOf(r.getId()), estilo);
                setCellStyled(row, 1, r.getFecha().toString(), estilo);
                setCellStyled(row, 2, r.getCliente() != null ? r.getCliente().getPersona().getNombre() : "Sin cliente", estilo);
                setCellStyled(row, 3, r.getBarbero() != null ? r.getBarbero().getPersona().getNombre() : "Sin barbero", estilo);
                setCellStyled(row, 4, r.getServicio() != null ? r.getServicio().getNombre() : "Sin servicio", estilo);
                setCellStyled(row, 5, r.getEstadoReserva().toString(), estilo);
                setCellStyled(row, 6, "S/ " + r.getTotal(), estilo);
                rowNum++;
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de reservas", e);
        }
    }


    // ─── PDF VENTAS ─────────────────────────────────────────────
    @Override
    public byte[] generarVentasPdf(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        BigDecimal total = reservaRepository.calcularIngresosPorPeriodo(desde, hasta);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(out)));

        DeviceRgb negro     = new DeviceRgb(18, 18, 18);
        DeviceRgb dorado    = new DeviceRgb(201, 168, 76);
        DeviceRgb blanco    = new DeviceRgb(255, 255, 255);
        DeviceRgb grisClaro = new DeviceRgb(245, 245, 245);

        Table header = new Table(UnitValue.createPercentArray(new float[]{1})).useAllAvailableWidth();
        header.addCell(new Cell()
                .add(new Paragraph("FadeX — Barbería").setBold().setFontSize(20).setFontColor(dorado))
                .add(new Paragraph("Reporte: Ventas e Ingresos").setFontSize(12).setFontColor(dorado))
                .add(new Paragraph("Período: " + desde + " al " + hasta).setFontSize(10).setFontColor(blanco))
                .add(new Paragraph("Total ingresos: S/ " + (total != null ? total : BigDecimal.ZERO)).setBold().setFontSize(11).setFontColor(dorado))
                .setBackgroundColor(negro).setBorder(Border.NO_BORDER).setPadding(20));
        doc.add(header);
        doc.add(new Paragraph("\n"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 3, 2})).useAllAvailableWidth();
        for (String col : new String[]{"Fecha", "Servicio", "Estado", "Total"}) {
            table.addHeaderCell(new Cell().add(new Paragraph(col).setBold().setFontColor(negro))
                    .setBackgroundColor(dorado).setPadding(8));
        }

        boolean par = false;
        for (Reserva r : reservas) {
            if (r.getEstadoReserva().toString().equals("FINALIZADA")) {
                DeviceRgb fondo = par ? grisClaro : blanco;
                table.addCell(new Cell().add(new Paragraph(r.getFecha().toString())).setBackgroundColor(fondo).setPadding(6));
                table.addCell(new Cell().add(new Paragraph(r.getServicio().getNombre())).setBackgroundColor(fondo).setPadding(6));
                table.addCell(new Cell().add(new Paragraph(r.getEstadoReserva().toString())).setBackgroundColor(fondo).setPadding(6));
                table.addCell(new Cell().add(new Paragraph("S/ " + r.getTotal())).setBackgroundColor(fondo).setPadding(6));
                par = !par;
            }
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    // ─── EXCEL VENTAS ────────────────────────────────────────────
    @Override
    public byte[] generarVentasExcel(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Ventas");

            XSSFCellStyle headerStyle = crearEstiloHeader(workbook);
            XSSFCellStyle filaParStyle = crearEstiloFilaPar(workbook);
            XSSFCellStyle filaImparStyle = crearEstiloFilaImpar(workbook);

            Row header = sheet.createRow(0);
            String[] cols = {"Fecha", "Servicio", "Estado", "Total"};
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 6000);
            }

            int rowNum = 1;
            for (Reserva r : reservas) {
                if (r.getEstadoReserva().toString().equals("FINALIZADA")) {
                    Row row = sheet.createRow(rowNum);
                    XSSFCellStyle estilo = (rowNum % 2 == 0) ? filaParStyle : filaImparStyle;
                    setCellStyled(row, 0, r.getFecha().toString(), estilo);
                    setCellStyled(row, 1, r.getServicio().getNombre(), estilo);
                    setCellStyled(row, 2, r.getEstadoReserva().toString(), estilo);
                    setCellStyled(row, 3, "S/ " + r.getTotal(), estilo);
                    rowNum++;
                }
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de ventas", e);
        }
    }

    // ─── PDF CLIENTES ────────────────────────────────────────────
    @Override
    public byte[] generarClientesPdf(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(out)));

        DeviceRgb negro     = new DeviceRgb(18, 18, 18);
        DeviceRgb dorado    = new DeviceRgb(201, 168, 76);
        DeviceRgb blanco    = new DeviceRgb(255, 255, 255);
        DeviceRgb grisClaro = new DeviceRgb(245, 245, 245);

        Table header = new Table(UnitValue.createPercentArray(new float[]{1})).useAllAvailableWidth();
        header.addCell(new Cell()
                .add(new Paragraph("FadeX — Barbería").setBold().setFontSize(20).setFontColor(dorado))
                .add(new Paragraph("Reporte: Clientes").setFontSize(12).setFontColor(dorado))
                .add(new Paragraph("Período: " + desde + " al " + hasta).setFontSize(10).setFontColor(blanco))
                .setBackgroundColor(negro).setBorder(Border.NO_BORDER).setPadding(20));
        doc.add(header);
        doc.add(new Paragraph("\n"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2})).useAllAvailableWidth();
        for (String col : new String[]{"Cliente", "Fecha", "Servicio", "Total"}) {
            table.addHeaderCell(new Cell().add(new Paragraph(col).setBold().setFontColor(negro))
                    .setBackgroundColor(dorado).setPadding(8));
        }

        boolean par = false;
        for (Reserva r : reservas) {
            DeviceRgb fondo = par ? grisClaro : blanco;
            table.addCell(new Cell().add(new Paragraph(r.getCliente().getPersona().getNombre())).setBackgroundColor(fondo).setPadding(6));
            table.addCell(new Cell().add(new Paragraph(r.getFecha().toString())).setBackgroundColor(fondo).setPadding(6));
            table.addCell(new Cell().add(new Paragraph(r.getServicio().getNombre())).setBackgroundColor(fondo).setPadding(6));
            table.addCell(new Cell().add(new Paragraph("S/ " + r.getTotal())).setBackgroundColor(fondo).setPadding(6));
            par = !par;
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    // ─── EXCEL CLIENTES ──────────────────────────────────────────
    @Override
    public byte[] generarClientesExcel(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Clientes");

            XSSFCellStyle headerStyle = crearEstiloHeader(workbook);
            XSSFCellStyle filaParStyle = crearEstiloFilaPar(workbook);
            XSSFCellStyle filaImparStyle = crearEstiloFilaImpar(workbook);

            Row header = sheet.createRow(0);
            String[] cols = {"Cliente", "Fecha", "Servicio", "Estado", "Total"};
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 6000);
            }

            int rowNum = 1;
            for (Reserva r : reservas) {
                Row row = sheet.createRow(rowNum);
                XSSFCellStyle estilo = (rowNum % 2 == 0) ? filaParStyle : filaImparStyle;
                setCellStyled(row, 0, r.getCliente().getPersona().getNombre(), estilo);
                setCellStyled(row, 1, r.getFecha().toString(), estilo);
                setCellStyled(row, 2, r.getServicio().getNombre(), estilo);
                setCellStyled(row, 3, r.getEstadoReserva().toString(), estilo);
                setCellStyled(row, 4, "S/ " + r.getTotal(), estilo);
                rowNum++;
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de clientes", e);
        }
    }

    // ─── PDF BARBEROS ────────────────────────────────────────────
    @Override
    public byte[] generarBarberosPdf(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(new PdfDocument(new PdfWriter(out)));

        DeviceRgb negro     = new DeviceRgb(18, 18, 18);
        DeviceRgb dorado    = new DeviceRgb(201, 168, 76);
        DeviceRgb blanco    = new DeviceRgb(255, 255, 255);
        DeviceRgb grisClaro = new DeviceRgb(245, 245, 245);

        Table header = new Table(UnitValue.createPercentArray(new float[]{1})).useAllAvailableWidth();
        header.addCell(new Cell()
                .add(new Paragraph("FadeX — Barbería").setBold().setFontSize(20).setFontColor(dorado))
                .add(new Paragraph("Reporte: Actividad de Barberos").setFontSize(12).setFontColor(dorado))
                .add(new Paragraph("Período: " + desde + " al " + hasta).setFontSize(10).setFontColor(blanco))
                .setBackgroundColor(negro).setBorder(Border.NO_BORDER).setPadding(20));
        doc.add(header);
        doc.add(new Paragraph("\n"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2})).useAllAvailableWidth();
        for (String col : new String[]{"Barbero", "Fecha", "Servicio", "Total"}) {
            table.addHeaderCell(new Cell().add(new Paragraph(col).setBold().setFontColor(negro))
                    .setBackgroundColor(dorado).setPadding(8));
        }

        boolean par = false;
        for (Reserva r : reservas) {
            DeviceRgb fondo = par ? grisClaro : blanco;
            table.addCell(new Cell().add(new Paragraph(r.getBarbero().getPersona().getNombre())).setBackgroundColor(fondo).setPadding(6));
            table.addCell(new Cell().add(new Paragraph(r.getFecha().toString())).setBackgroundColor(fondo).setPadding(6));
            table.addCell(new Cell().add(new Paragraph(r.getServicio().getNombre())).setBackgroundColor(fondo).setPadding(6));
            table.addCell(new Cell().add(new Paragraph("S/ " + r.getTotal())).setBackgroundColor(fondo).setPadding(6));
            par = !par;
        }

        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    // ─── EXCEL BARBEROS ──────────────────────────────────────────
    @Override
    public byte[] generarBarberosExcel(LocalDate desde, LocalDate hasta) {
        List<Reserva> reservas = reservaRepository.findReservasPorPeriodo(desde, hasta);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Barberos");

            XSSFCellStyle headerStyle = crearEstiloHeader(workbook);
            XSSFCellStyle filaParStyle = crearEstiloFilaPar(workbook);
            XSSFCellStyle filaImparStyle = crearEstiloFilaImpar(workbook);

            Row header = sheet.createRow(0);
            String[] cols = {"Barbero", "Fecha", "Servicio", "Estado", "Total"};
            for (int i = 0; i < cols.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(cols[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 6000);
            }

            int rowNum = 1;
            for (Reserva r : reservas) {
                Row row = sheet.createRow(rowNum);
                XSSFCellStyle estilo = (rowNum % 2 == 0) ? filaParStyle : filaImparStyle;
                setCellStyled(row, 0, r.getBarbero().getPersona().getNombre(), estilo);
                setCellStyled(row, 1, r.getFecha().toString(), estilo);
                setCellStyled(row, 2, r.getServicio().getNombre(), estilo);
                setCellStyled(row, 3, r.getEstadoReserva().toString(), estilo);
                setCellStyled(row, 4, "S/ " + r.getTotal(), estilo);
                rowNum++;
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de barberos", e);
        }
    }


    private XSSFCellStyle crearEstiloHeader(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)201, (byte)168, (byte)76}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setColor(new XSSFColor(new byte[]{0, 0, 0}, null));
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private XSSFCellStyle crearEstiloFilaPar(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)245, (byte)245, (byte)245}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private XSSFCellStyle crearEstiloFilaImpar(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void setCellStyled(Row row, int col, String value, XSSFCellStyle style) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

}
