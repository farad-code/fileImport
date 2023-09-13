package com.example.fileimports.utils;


import com.example.fileimports.constant.Constant;
import com.example.fileimports.exception.NotSupportedFileFormat;
import com.example.fileimports.model.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

import static com.example.fileimports.constant.Constant.FILENAME;
import static com.example.fileimports.constant.Constant.FILESIZE;

@Service
public class FileReaderUtils {

   private static final String[] mimeTypes = {
           "csv","xlsx","docs","xls","docx"
   };


   private boolean isAcceptableMimeType(String mimeType){

      return !Arrays.asList(mimeTypes).contains(mimeType);
   }

   private static final String NOT_SUPPORTED = "Not Acceptable File Format";

   public List<Product> readWordFile(MultipartFile file) throws IOException {
      String mimeType = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
      if(isAcceptableMimeType(mimeType)){
         throw new NotSupportedFileFormat(NOT_SUPPORTED);
      }
      List<Product> products = new ArrayList<>();
      XWPFDocument document = new XWPFDocument(file.getInputStream());
      List<XWPFTable> tables = document.getTables();
      for(XWPFTable table: tables){
         XWPFTableRow headerRow = table.getRow(0);
         int nameColumnIndex = -1;
         int brandColumnIndex = -1;
         int colorColumnIndex = -1;
         int priceColumnIndex = -1;
         for(int cellIndex =0; cellIndex < headerRow.getTableCells().size(); cellIndex++){
            XWPFTableCell headerCell = headerRow.getCell(cellIndex);
            String headerText = headerCell.getText();
            switch (headerText.toLowerCase()) {
               case Constant.HEADER_PRICE -> priceColumnIndex = cellIndex;
               case Constant.HEADER_NAME -> nameColumnIndex = cellIndex;
               case Constant.HEADER_BRAND -> brandColumnIndex = cellIndex;
               case Constant.HEADER_COLOR -> colorColumnIndex = cellIndex;
               default -> {
                  break;
               }
            }
            if(priceColumnIndex != -1 && nameColumnIndex !=-1 && brandColumnIndex != -1 && colorColumnIndex != -1){
               for (int rowIndex = 1; rowIndex < table.getRows().size(); rowIndex++){
                 XWPFTableRow row = table.getRow(rowIndex);
                 String name = row.getCell(nameColumnIndex).getText();
                 String brand = row.getCell(brandColumnIndex).getText();
                 String color = row.getCell(colorColumnIndex).getText();
                 double price = Double.parseDouble(row.getCell(priceColumnIndex).getText());
                 Product product = Product.builder()
                         .price(price)
                         .brand(brand)
                         .color(color)
                         .name(name)
                         .build();
                  products.add(product);
               }
            }
         }
      }

      return products;
   }

   public List<Product> readCsvFile(MultipartFile file) throws IOException {
      String mimeType = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
      Product product = null;
      List<Product> products = new ArrayList<>();
      if(isAcceptableMimeType(mimeType)){
         throw new NotSupportedFileFormat(NOT_SUPPORTED);
      }
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
      String line;
      List<String> headers = Arrays.asList(bufferedReader.readLine().split(","));
      int nameColumnIndex = -1;
      int brandColumnIndex = -1;
      int colorColumnIndex = -1;
      int priceColumnIndex = -1;
      for (int i = 0; i < headers.size(); i++) {
         switch (headers.get(i).toLowerCase()) {
            case Constant.HEADER_PRICE -> priceColumnIndex = i;
            case Constant.HEADER_NAME -> nameColumnIndex = i;
            case Constant.HEADER_BRAND -> brandColumnIndex = i;
            case Constant.HEADER_COLOR -> colorColumnIndex = i;
            default -> {
               break;
            }
         }
      }

      while(( line = bufferedReader.readLine()) != null){
         List<String> columns = Arrays.stream(line.split(",")).toList();
         for (int i = 0; i < columns.size(); i++) {
            String name =columns.get(nameColumnIndex);
            String brand =columns.get(brandColumnIndex);
            String color =columns.get(colorColumnIndex);
            double price =Double.parseDouble(String.valueOf(columns.get(priceColumnIndex)));
            product = Product.builder()
                    .price(price)
                    .brand(brand)
                    .color(color)
                    .name(name)
                    .build();
         }
      products.add(product);
      }
      bufferedReader.close();
      return products;
   }

   public List<Product> readExcelFile(MultipartFile file) throws IOException {
      String mimeType = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[1];
      List<Product> products = new ArrayList<>();
      if(isAcceptableMimeType(mimeType)){
         throw new NotSupportedFileFormat(NOT_SUPPORTED);
      }
      Workbook workbook = WorkbookFactory.create(file.getInputStream());
      Sheet sheet = workbook.getSheetAt(0);
      Row headerRow = sheet.getRow(0);
      int nameColumnIndex = -1;
      int brandColumnIndex = -1;
      int colorColumnIndex = -1;
      int priceColumnIndex = -1;
      Product product = null;
      for(Cell cell: headerRow) {
         String headerValue = cell.getStringCellValue().toLowerCase();
            switch (headerValue) {
               case Constant.HEADER_PRICE -> priceColumnIndex = cell.getColumnIndex();
               case Constant.HEADER_NAME -> nameColumnIndex = cell.getColumnIndex();
               case Constant.HEADER_COLOR -> colorColumnIndex = cell.getColumnIndex();
               case Constant.HEADER_BRAND -> brandColumnIndex = cell.getColumnIndex();
               default -> {
                  break;
               }
         }
      }
         if(priceColumnIndex != -1 && nameColumnIndex !=-1 && brandColumnIndex != -1 && colorColumnIndex != -1){
            for(int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++){
               Row row = sheet.getRow(rowIndex);
               for(int cellIndex = 0; cellIndex < row.getPhysicalNumberOfCells(); cellIndex++){
                  String name = getCellValues(row.getCell(nameColumnIndex));
                  String brand = getCellValues(row.getCell(brandColumnIndex));
                  String color = getCellValues(row.getCell(colorColumnIndex));
                  double price = Double.parseDouble(Objects.requireNonNull(getCellValues(row.getCell(priceColumnIndex))));
                   product = Product.builder()
                          .name(name)
                          .color(color)
                          .brand(brand)
                          .price(price)
                          .build();

               }
               products.add(product);
            }
            }
      workbook.close();
      return products;
   }

   public void writeToCsvFile(){

   }

   public void writeToExcelFile(){

   }

   public void writeToWordFile(List<Product> products) throws IOException {
      XWPFDocument xwpfDocument = new XWPFDocument();
      int row = Product.getColumnCount();
      int column = products.size();
      final int  headerPriceIndex = 3;
      final int headerBrandIndex = 1;
      final int headerColorIndex = 2;
      final int headerNameIndex = 0;
      XWPFTable table = xwpfDocument.createTable(column,row);
      for (int cellIndex =0; cellIndex < row; cellIndex++ ){
         XWPFTableCell cell = table.getRow(0).getCell(cellIndex);
         switch (cellIndex) {
            case headerPriceIndex -> cell.setText(Constant.HEADER_PRICE.toUpperCase());
            case headerNameIndex -> cell.setText(Constant.HEADER_NAME.toUpperCase());
            case headerBrandIndex -> cell.setText(Constant.HEADER_BRAND.toUpperCase());
            case headerColorIndex -> cell.setText(Constant.HEADER_COLOR.toUpperCase());
            default -> {
               break;
            }
         }
      }
      for (int rowIndex = 1; rowIndex < table.getRows().size() ; rowIndex++) {
         for (int cellIndex = 0; cellIndex < row; cellIndex++) {

            XWPFTableCell cell = table.getRow(rowIndex).getCell(cellIndex);
            cell.setWidth(Integer.toString(8000));

            switch (cellIndex) {
               case headerPriceIndex -> cell.setText(String.valueOf(products.get(rowIndex).getPrice()));
               case headerNameIndex ->  cell.setText(products.get(rowIndex).getName().toUpperCase());
               case headerBrandIndex ->  cell.setText(products.get(rowIndex).getBrand().toUpperCase());
               case headerColorIndex -> cell.setText(products.get(rowIndex).getColor().toUpperCase());
               default -> {
                  break;
               }
            }
         }
      }
      try(FileOutputStream file = new FileOutputStream(FILENAME +".docx")) {
        xwpfDocument.write(file);

      }catch(IOException e){
         System.err.println(e.getMessage());
      }
      xwpfDocument.close();
   }
   private String getCellValues(Cell cell){
      if(Objects.equals(cell.getCellType(),CellType.STRING)){
         return cell.getStringCellValue();
      }
      if(Objects.equals(cell.getCellType(),CellType.BOOLEAN)){
         return String.valueOf(cell.getBooleanCellValue());
      }
      if(Objects.equals(cell.getCellType(),CellType.NUMERIC)){
         return String.valueOf(cell.getNumericCellValue());
      }
      return null;
   }

   public Map<String,String> extractFileDetails(MultipartFile file){
      String filename = file.getOriginalFilename();
      String fileType ="";
      if(filename != null){
         fileType = filename.split("\\.")[1];
      }
      String fileSize = "";
      long uploadSize = file.getSize();
      final double KILOBYTES = ((double) file.getSize() /FILESIZE);
      Map<String,String> fileDetails = new HashMap<>();
      if (uploadSize >= 1024 && uploadSize < 1048576) {
         fileSize= String.format("%.2f",KILOBYTES) + " KB";
      }
      if (uploadSize >= 1048576) {
         double megabyte = (KILOBYTES / 1024);
         fileSize = String.format("%.2f",megabyte) + " MB";
      }
      if (uploadSize <= 1024) {
         fileSize =  uploadSize + " bytes";
      }
       fileDetails.put("filename",filename);
      fileDetails.put("size",fileSize);
      fileDetails.put("type",fileType);
      return fileDetails;

   }

}
