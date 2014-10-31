package com.github.onsdigital.generator.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Convenience class for handling CSV and Excel data.
 * <p>
 * The xslx handling uses Apache POI and is adapted from <a href=
 * "http://stackoverflow.com/questions/18282378/read-from-excel-file-xlsx-using-java-apache-poi-3-9-eclipse"
 * >http://stackoverflow.com/questions/18282378/read-from-excel-file-xlsx-using-
 * java-apache-poi-3-9-eclipse</a>
 * 
 * @author david
 *
 */
public class Csv implements Iterable<Map<String, String>> {

	private Path path;
	private String[] headings;
	private List<String[]> rows;
	private XSSFWorkbook xssfWorkbook;
	private int sheetIndex;
	private static Set<String> formatStrings = new TreeSet<>();

	public Csv(Path path) {
		this.path = path;
	}

	public Csv(String resourceName) {
		URL resource = Csv.class.getResource(resourceName);
		try {
			this.path = Paths.get(resource.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void read(int... sheetIndex) throws IOException {

		if (rows == null || (sheetIndex.length > 0 && sheetIndex[0] != this.sheetIndex)) {

			String extension = FilenameUtils.getExtension(path.getFileName().toString());
			if ("csv".equalsIgnoreCase(extension)) {
				try (CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(Files.newInputStream(path))))) {
					rows = csvReader.readAll();
				}
			} else if ("xlsx".equalsIgnoreCase(extension)) {

				// Read the workbook if it's not already cached:
				if (xssfWorkbook == null) {
					try (InputStream input = Files.newInputStream(path)) {
						xssfWorkbook = new XSSFWorkbook(input);
					}
				}

				// Now read the data:
				int index;
				if (sheetIndex.length > 0) {
					index = sheetIndex[0];
				} else {
					index = xssfWorkbook.getActiveSheetIndex();
				}
				XSSFSheet worksheet = xssfWorkbook.getSheetAt(index);
				rows = readSheet(worksheet);
			} else {
				throw new UnsupportedOperationException("I'm sorry, I don't know how to handle " + extension + " files at the moment.");
			}
		}
	}

	/**
	 * Reads a worksheet as a two-dimensional array of strings.
	 * 
	 * @param worksheet
	 * @return The contents of the sheet.
	 */
	public static List<String[]> readSheet(XSSFSheet worksheet) {
		List<String[]> result = new ArrayList<>();

		// Work out a "square" of cells:
		int rowTotal = worksheet.getLastRowNum() + 1;
		int columnTotal = 0;
		for (int r = 0; r < rowTotal; r++) {
			XSSFRow row = worksheet.getRow(r);
			if (row != null) {
				columnTotal = Math.max(columnTotal, row.getLastCellNum());
			}
		}

		for (int r = 0; r < rowTotal; r++) {
			String[] cells = new String[columnTotal];
			XSSFRow row = worksheet.getRow(r);
			if (row != null) {
				for (int c = 0; c < columnTotal; c++) {
					XSSFCell cell = row.getCell(c);
					if (cell != null) {

						String value;

						// Excel makes a dog's dinner of numerical values:

						// toString() before = 64.1
						// getRawValue() before = 64.099999999999994
						// [set cell type to string]
						// toString() after = 64.099999999999994
						// getRawValue() after = 1563 <- this is a reference
						// to the string table in the spreadsheet

						// toString() before = 2012.0
						// getRawValue() before = 2012
						// [set cell type to string]
						// toString() after = 2012
						// getRawValue() after = 1446 <- this is a reference
						// to the string table in the spreadsheet

						// So... we do some acrobatics here for Alpha purposes
						// only.
						// TODO: Burn this code in Beta. Then find a solution to
						// dealing with Microsoft's high regard for standards.

						// Format numbers as strings to try and get the value as
						// displayed in the spreadsheet:
						// String rawValue = cell.getRawValue();
						String dataFormat = cell.getCellStyle().getDataFormatString();
						formatStrings.add(dataFormat);
						if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {

							// System.out.println("toString() before     = " +
							// cell.toString());
							// System.out.println("getRawValue() before = " +
							// cell.getRawValue());
							cell.setCellType(Cell.CELL_TYPE_STRING);
							// System.out.println("toString()      after  = " +
							// cell.toString());
							// System.out.println("getRawValue() after  = " +
							// cell.getRawValue());

							if (dataFormat.contains("0") && !dataFormat.contains("[Red]")) {
								// Remove commas from numbers - these would
								// break the graphs.
								dataFormat = dataFormat.replaceAll(",", "");

								if (dataFormat.contains(";")) {
									// e.g. #,##0_);(#,##0)
									dataFormat = dataFormat.substring(0, dataFormat.indexOf(';'));
								}

								// e.g. ###0_)
								dataFormat = dataFormat.replaceAll("\\(", "");
								dataFormat = dataFormat.replaceAll("\\)", "");
								dataFormat = dataFormat.replaceAll("\\_", "");

								NumberFormat format = new DecimalFormat(dataFormat);
								Double d = Double.parseDouble(cell.toString());
								value = format.format(d);
							} else {
								// Probably a "General" format.
								value = cell.toString();
							}
							// System.out.println("Format: " + dataFormat);
						} else {
							value = cell.toString();
						}

						// Last-resort tweak.
						// This seems to be needed if the cell format is
						// "General"
						if (value.contains("00000")) {
							value = value.substring(0, value.indexOf("00000"));
						}
						if (value.contains("99999")) {
							// Not strictly correct (should round up), but good
							// enough for now:
							value = value.substring(0, value.indexOf("99999"));
						}

						if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							// System.out.println("Value: " + value);
							// System.out.println();
						}

						// if (rawValue != null && (rawValue.contains("00000")
						// || rawValue.contains("99999")) &&
						// !"0.0".equals(dataFormat)) {
						// System.out.println("mince");
						// } else if (rawValue == null) {
						// System.out.println("Canhasnull?");
						// }
						// if (value.endsWith("_)")) {
						// System.out.println("WAT?!");
						// }

						cells[c] = value;
					}
				}
				result.add(cells);
			}
		}

		System.out.println(formatStrings);
		return result;
	}

	public String[] getHeadings() {
		if (headings == null) {
			headings = rows.remove(0);
		}
		for (int i = 0; i < headings.length; i++) {
			headings[i] = StringUtils.trim(headings[i]);
		}
		return headings;
	}

	public void setHeadings(String[] headings) {
		this.headings = headings;
	}

	@Override
	public Iterator<Map<String, String>> iterator() {
		return new Iterator<Map<String, String>>() {
			int index = 0;

			@Override
			public boolean hasNext() {
				return rows != null && index < rows.size();
			}

			@Override
			public Map<String, String> next() {
				String[] headings = getHeadings();
				String[] row = rows.get(index++);
				Map<String, String> result = new HashMap<>();
				for (int i = 0; i < Math.min(headings.length, row.length); i++) {
					result.put(headings[i], row[i]);
				}
				return result;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public int sheetCount() {
		if (rows == null) {
			return 0;
		} else if (xssfWorkbook != null) {
			return xssfWorkbook.getNumberOfSheets();
		} else {
			// CSV only has one "Worksheet"
			return 1;
		}
	}

	public int size() {
		int result = 0;
		if (rows != null) {
			result = rows.size();
		}
		return result;
	}

	public String[] row(int index) {
		String[] result = null;
		if (rows != null && rows.size() > index) {
			result = rows.get(index);
		}
		return result;
	}
}
