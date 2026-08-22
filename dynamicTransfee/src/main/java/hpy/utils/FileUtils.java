package hpy.utils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private final String[] ALLOW_EXT = {"xlsx"};

    public static void isValidFile(File file) {
        if (file == null  || !file.exists()) {
            throw new IllegalArgumentException("File is null");
        }

        String name = file.getName();
        System.out.println(name);
        if (!name.endsWith(".xlsx")) {
            throw new IllegalArgumentException("File is not a XLSX file");
        }
    }


    public static List<String> getHeaders(String rowStr, String filePath) {
        if (filePath.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        File file = new File(filePath);
        isValidFile(file);

        List<String> headers = new ArrayList<>();
        try (
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
        ) {

            int numberOfSheets = workbook.getNumberOfSheets();
            if (numberOfSheets == 1) {
                XSSFSheet sheet = workbook.getSheetAt(0);
                XSSFRow row = sheet.getRow(Integer.parseInt(rowStr));
                if (row != null) {
                    throw new IllegalArgumentException("헤더 ROW가 비어있습니다.");
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return headers;
    }



}
