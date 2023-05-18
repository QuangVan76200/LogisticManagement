package com.example.demo.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.example.demo.dto.response.WareTransactionDTO;
import com.example.demo.dto.response.WareTransactionDetailDTO;

@Component
public class ExcelExporter {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	public static void exportToExcel(List<WareTransactionDTO> transactions, String filePath) throws IOException {
		try {
			
			File directory = new File(filePath);
			boolean hasFullAccess = directory.exists() && directory.isDirectory() && directory.canRead() && directory.canWrite();

			System.out.println(hasFullAccess + " checked" );
			
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("Transactions");

			CellStyle headerStyle = workbook.createCellStyle();
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerStyle.setFont(headerFont);

			// Create header row
			Row headerRow = sheet.createRow(0);
			headerRow.createCell(0).setCellValue("fullName");
			headerRow.createCell(1).setCellValue("contactNumber");
			headerRow.createCell(2).setCellValue("email");
			headerRow.createCell(4).setCellValue("transactionId");
			headerRow.createCell(5).setCellValue("transactionDate");
			headerRow.createCell(6).setCellValue("transactionType");
			headerRow.createCell(7).setCellValue("note");

			// Apply header formatting
			for (Cell cell : headerRow) {
				cell.setCellStyle(headerStyle);
			}

			int rowNum = 1;

			for (WareTransactionDTO transactionDTO : transactions) {
				Row dataRow = sheet.createRow(rowNum++);
				dataRow.createCell(0).setCellValue(transactionDTO.getUser().getFullName());
				dataRow.createCell(1).setCellValue(transactionDTO.getUser().getContactNumber());
				dataRow.createCell(2).setCellValue(transactionDTO.getUser().getEmail());
				dataRow.createCell(4).setCellValue(transactionDTO.getTransactionId());
				dataRow.createCell(5).setCellValue(transactionDTO.getTransactionDate().format(DATE_TIME_FORMATTER));
				dataRow.createCell(6).setCellValue(transactionDTO.getTransactionType().toString());
				dataRow.createCell(7).setCellValue(transactionDTO.getNote());

				// Process transaction details

				int colNum = 7;
				for (WareTransactionDetailDTO detail : transactionDTO.getTransactionDetails()) {
					dataRow.createCell(colNum++).setCellValue(detail.getTransactionDetailId());
					dataRow.createCell(colNum++).setCellValue(detail.getStock().getStockId());
					dataRow.createCell(colNum++).setCellValue(detail.getStock().getProduct().getName());
					dataRow.createCell(colNum++).setCellValue(detail.getStock().getProduct().getProductCode());
					dataRow.createCell(colNum++).setCellValue(detail.getStock().getProduct().getPrice().doubleValue());
					dataRow.createCell(colNum++).setCellValue(detail.getStock().getProduct().getQuantity());
					dataRow.createCell(colNum++).setCellValue(detail.getStock().getProduct().getWeight().doubleValue());
					dataRow.createCell(colNum++)
							.setCellValue(detail.getStock().getProduct().getMeasurementUnit().toString());

				}
			}

			// Auto-size columns
			for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
				sheet.autoSizeColumn(i);
			}

			// Save the workbook to a file
			try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
				System.out.println("Print Test");
				workbook.write(outputStream);
			}

			workbook.close();
		} catch (Exception e) {
			throw e;
		}
	}
}
