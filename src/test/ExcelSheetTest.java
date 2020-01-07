package test;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ExcelSheetTest {
    private final static String EXCEL_SHEET = "/Users/mithileshparmar/Development/JAVA_PROJECTS/Firebase Demo/src/assets/Excel_Sheet.xlsx";

    public static void main(String[] args) throws IOException, InvalidFormatException {
        read(EXCEL_SHEET);
    }

    static void read(String path) throws IOException, InvalidFormatException {
        Workbook workbook = new XSSFWorkbook(new File(path));

        // Retrieving the number of sheets in the Workbook
        System.out.println("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");


        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            boolean firstColumn = true;
            Sheet sheet = workbook.getSheetAt(i);
            DataFormatter dataFormatter = new DataFormatter();
            System.out.println("\n\nIterating over Rows and Columns using for-each loop for " + sheet.getSheetName() + "\n");

            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (!firstColumn) {
                        String cellValue = dataFormatter.formatCellValue(cell);
                        System.out.print(cellValue + "\t");
                    }
                }
                System.out.println();
                firstColumn = false;
            }
        }
    }


    static void write() {
        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Employee Data");

        //This data needs to be written (Object[])
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[]{"ID", "NAME", "LASTNAME"});
        data.put("2", new Object[]{1, "Amit", "Shukla"});
        data.put("3", new Object[]{2, "Lokesh", "Gupta"});
        data.put("4", new Object[]{3, "John", "Adwards"});
        data.put("5", new Object[]{4, "Brian", "Schultz"});

        //Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset) {
            Row row = sheet.createRow(rownum++);
            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if (obj instanceof String)
                    cell.setCellValue((String) obj);
                else if (obj instanceof Integer)
                    cell.setCellValue((Integer) obj);
            }
        }
        try {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File("howtodoinjava_demo.xlsx"));
            workbook.write(out);
            out.close();
            System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
