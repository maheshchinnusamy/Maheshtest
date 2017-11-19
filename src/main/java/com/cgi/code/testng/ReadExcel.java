package com.cgi.code.testng;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Used to read excel sheets
 * 
 */
public class ReadExcel {
	private static final Logger logger = Logger.getLogger(ActionKeywords.class.getName());

	private Collection<Object[]> data = null;

	/**
	 * Constructs a collection based on sheet in an excel workbook
	 * 
	 * @param sheetName Name of the excel sheet.
	 */
	public ReadExcel(String sheetName) {
		this.data = parse(sheetName);
	}

	/**
	 * 
	 * @return collection of data in an excel workbook sheet
	 */
	public Collection<Object[]> getData() {
		return data;
	}

	private Collection<Object[]> parse(String sheetName) {
		String excelFilePath = "Test Cases//";
		FileInputStream inputStream = null;
		List<Object[]> ret = new ArrayList<Object[]>();
		Workbook workbook = null;
		
			try {
				//Read in the Excel File
				inputStream = new FileInputStream(excelFilePath + "TestSuite.xlsx");
			} catch (FileNotFoundException e) {
				//Inform the user there is no Excel book
				logger.error("Excel file not found.");
				System.out.println("Excel file not found.");
			}
			try {
				//Start reading in the data of the excel workbook
				workbook = new XSSFWorkbook(inputStream);
			} catch (IOException e) {
				//Inform the user there is something wrong with their Excel file.
				System.out.println("Can not read excel file.");
				logger.error("Can not read excel file.");
			}
			//Sheet in Excel file
			Sheet sheet = workbook.getSheet(sheetName);
			
			//This holds the data from the excel rows.
			List<Object> rowData = new ArrayList<Object>();
			/*
			 * This finds the first empty cell in the sheet. So for example if there are 25 columns, then it 
			 * would know to look for column Z.
			 */
			int numberOfColumns = firstEmptyCellPosition(sheet);
			//How many columns were added.
			int columnsAdded = 0;
			
			Iterator<Row> iterator = sheet.iterator();
			// Loop throw the excel sheet row by row.
			while (iterator.hasNext()) {
				//This is a row in the excel sheet.
				Row nextRow = iterator.next();
				//Cycle over the data in each column.
				for (int cn = 0; cn < numberOfColumns; cn++) {
					//Grab the data in a cell.
			          Cell cell = nextRow.getCell(cn, Row.RETURN_BLANK_AS_NULL);
			          // if there is no cell then it's time to add a new column.
			          if (cell == null) {
			        	  rowData.add(" ");
						  columnsAdded++;
						  //If there is a cell with data grab the data in the format that's given.
			          } else {
			        	  switch (cell.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								rowData.add(cell.getStringCellValue());
								columnsAdded++;
								break;
							case Cell.CELL_TYPE_BOOLEAN:
								rowData.add(String.valueOf(cell.getBooleanCellValue()));
								columnsAdded++;
								break;
							case Cell.CELL_TYPE_NUMERIC:
								rowData.add(String.valueOf((cell.getNumericCellValue())));
								columnsAdded++;
								break;
							case Cell.CELL_TYPE_BLANK:
								rowData.add(" ");
								columnsAdded++;
								break;
							}
			          }
			          //If the column count equals the expected columns then go to the next row.
				if (columnsAdded == numberOfColumns) {
					ret.add(rowData.toArray());
					rowData.clear();
					columnsAdded = 0;
				}
			}
				//After we're done with the data then close the book.
			try {
				workbook.close();
			} catch (IOException e) {
				logger.error("Error closing workbook.");
			}
			try {
				inputStream.close();
			} catch (IOException e) {
				logger.error("Error closing workbook inputStream.");
			}
					}
		return ret;
	}

	
	/*
	 * This method finds the first empty cell and through this it determines how many columns it should expect.
	 */
	private int firstEmptyCellPosition(Sheet firstSheet) {
		int columnCount = 0;
		Row firstRow = firstSheet.getRow(0);
		for (Cell cell : firstRow) {
			if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
				break;
			}
			columnCount++;
		}
		return columnCount;
	}
}
