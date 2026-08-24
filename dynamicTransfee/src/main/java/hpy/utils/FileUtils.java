package hpy.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
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

    public static void openFolder(String path) {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("해당 경로가 존재하지 않습니다");
        }

        File file = new File(path);
        // Desktop 지원 여부 확인
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();

            try {
                if (file.exists() && file.isDirectory()) {
                    desktop.open(file);
                } else {
                    throw new IllegalArgumentException("해당 폴더가 존재하지 않습니다");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

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
                if (row == null) {
                    throw new IllegalArgumentException("헤더 ROW가 비어있습니다.");
                }

                short lastCellNum = row.getLastCellNum();
                for (int i = 0; i < lastCellNum; i++) {
                    XSSFCell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (cell != null) {
                        headers.add(cell.getStringCellValue());
                    }
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return headers;
    }

    public static BaseFont getKoreanBaseFont() throws Exception {
        return BaseFont.createFont(
                "HYGoThic-Medium",     // 또는 HYSMyeongJo-Medium
                "UniKS-UCS2-H",        // 한국어용 인코딩
                BaseFont.NOT_EMBEDDED
        );
    }

    public static Font getDefaultFont() throws Exception {
        return getDefaultFont(8);
    }

    public static Font getDefaultFont(int size) throws Exception {
        return new Font(getKoreanBaseFont(), size, Font.NORMAL, BaseColor.BLACK);
    }




}
