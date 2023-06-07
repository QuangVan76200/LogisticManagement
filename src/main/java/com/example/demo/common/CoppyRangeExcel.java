package com.example.demo.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

public class CoppyRangeExcel {

	/**
	 * @param sourceFilePath      : đường dẫn tới file Excel nguồn (file A)
	 * @param destinationFilePath : là đường dẫn tới file Excel đích (file B)
	 * @param sheetName           : là tên của sheet trong cả hai file,
	 * @param sourceRange         : là đoạn Excel bạn muốn sao chép (ví dụ: "A1:C5")
	 * @param destinationCell     : à ô đầu tiên mà bạn muốn dán đoạn Excel đã sao
	 *                            chép đến (ví dụ: "D1")
	 * @throws IOException
	 */
	public static void copyRangeExcel(String sourceFilePath, String destinationFilePath, String sheetName,
			String sourceRange, String destinationCell) throws IOException {
		try {
			FileInputStream sourceFile = new FileInputStream(sourceFilePath);
			Workbook sourceWorkbook = WorkbookFactory.create(sourceFile);
            

			FileInputStream destinationFile = new FileInputStream(destinationFilePath);
			Workbook destinationWorkbook = WorkbookFactory.create(destinationFile);

			Sheet sourceSheet = sourceWorkbook.getSheet(sheetName);
			Sheet destinationSheet = destinationWorkbook.getSheet(sheetName);

			CellRangeAddress sourceRangeAddress = CellRangeAddress.valueOf(sourceRange);

			int rowCount = sourceRangeAddress.getLastRow() - sourceRangeAddress.getFirstRow() + 1;
			int columnCount = sourceRangeAddress.getLastColumn() - sourceRangeAddress.getFirstColumn() + 1;

			for (int i = 0; i < rowCount; i++) {
				Row sourceRow = sourceSheet.getRow(sourceRangeAddress.getFirstRow() + i);
				Row destinationRow = destinationSheet.getRow(sourceRangeAddress.getFirstRow() + i);

				if (destinationRow == null) {
					destinationRow = destinationSheet.createRow(sourceRangeAddress.getFirstRow() + i);
				}

				for (int j = 0; j < columnCount; j++) {
					Cell sourceCell = sourceRow.getCell(sourceRangeAddress.getFirstColumn() + j);
					Cell destination_Cell = destinationRow.getCell(sourceRangeAddress.getFirstColumn() + j);

					if (destination_Cell == null) {
						destination_Cell = destinationRow.createCell(sourceRangeAddress.getFirstColumn() + j);
					}

					copyCellValue(sourceCell, destination_Cell);
				}
			}

			FileOutputStream outputStream = new FileOutputStream(destinationFilePath);
			destinationWorkbook.write(outputStream);

			sourceFile.close();
			destinationFile.close();
			outputStream.close();

			System.out.println(
					"Range copied successfully from '" + sourceFilePath + "' to '" + destinationFilePath + "'.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void copyCellValue(Cell sourceCell, Cell destinationCell) {
		CellType cellType = sourceCell.getCellType();

		if (cellType == CellType.NUMERIC) {
			destinationCell.setCellValue(sourceCell.getNumericCellValue());
		} else if (cellType == CellType.STRING) {
			destinationCell.setCellValue(sourceCell.getStringCellValue());
		} else if (cellType == CellType.BOOLEAN) {
			destinationCell.setCellValue(sourceCell.getBooleanCellValue());
		} else if (cellType == CellType.FORMULA) {
			destinationCell.setCellFormula(sourceCell.getCellFormula());
		} else if (cellType == CellType.BLANK) {
			// Do nothing for blank cells
		}
	}
	
	 public static void main(String[] args) throws IOException {
	        String sourceFilePath = "E:\\TraiiningHTML\\ProjectE-commerce\\MicroService\\A1.xlsx";
	        String destinationFilePath = "E:\\TraiiningHTML\\ProjectE-commerce\\MicroService\\A2.xlsx";
	        String sheetName = "Sheet1";
	        String sourceRange = "A1:C5";
	        String destinationCell = "D1";
	        
	        CoppyRangeExcel.copyRangeExcel(sourceFilePath, destinationFilePath, sheetName, sourceRange, destinationCell);
	    }

}
