package com.example.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class HelloApplication extends Application {
    ArrayList<ProductSale> productSaleList = new ArrayList<>();
    HashMap<Integer, HashMap<Integer, Integer>> yearlySales = new HashMap<>();
    int n = 0;
    int lol = 0;
    File file;
    @Override
    public void start(Stage stage) throws IOException {
        Button button = new Button("Открыть йоу");


        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Месяц");
        yAxis.setLabel("Продажи");
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Магазин Артур агая");
        lineChart.getData().add(series);
        lineChart.setVisible(false);
        VBox root = new VBox();
        VBox root1 = new VBox();
        VBox box1 = new VBox();
        VBox box11 = new VBox(lineChart);
        VBox box2 = new VBox(button);
        root.getChildren().add(box1);
        root.getChildren().add(box2);
        root.getChildren().add(box11);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");
        Scene scene = new Scene(root, 1000, 600);
        Scene scene1 = new Scene(root1, 1000, 600);
        FileChooser fileChooser = new FileChooser();
        button.setOnAction(event -> {
            fileChooser.setTitle("Открыть йоу");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files", "*.xls", "*.xlsx"));
            file = fileChooser.showOpenDialog(stage);
            lol = countRow(file);
            try {
                productSaleList.clear();
                yearlySales.clear();
                ExcelRead(file, productSaleList);
                lineChart.setVisible(true);
                updaterLines(series,productSaleList,yearlySales);

            } catch (IOException e) {
                lineChart.setVisible(false);
                throw new RuntimeException(e);
            }
        });

        stage.setTitle("Али-Нур не сигма");
        stage.setScene(scene);
        stage.show();
    }

    public static void updaterLines(XYChart.Series<String, Number> series, ArrayList<ProductSale> productSaleList, HashMap<Integer, HashMap<Integer, Integer>> yearlySales) {
        for (ProductSale sale : productSaleList) {
            int year = getYearFromDate(sale.getDate());
            int month = getMonthFromDate(sale.getDate());
            yearlySales.putIfAbsent(year, new HashMap<>());
            HashMap<Integer, Integer> monthlySales = yearlySales.get(year);
            monthlySales.put(month, monthlySales.getOrDefault(month, 0) + sale.getFinalPrice());
        }
        series.getData().clear();
        for (int year : yearlySales.keySet()) {
            HashMap<Integer, Integer> monthlySales = yearlySales.get(year);
            for (int month = 1; month <= 12; month++) {
                int sales = monthlySales.getOrDefault(month, 0);
                if (sales > 0) {
                    series.getData().add(new XYChart.Data<>(getMonthName(month) + " " + year, sales));
                }
            }
        }

    }
    public static int getYearFromDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            // Парсинг строки в объект Date
            java.util.Date date = dateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.YEAR);  // Получаем год
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static int getMonthFromDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            // Парсинг строки в объект Date
            java.util.Date date = dateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.MONTH) + 1;  // Месяцы в Calendar начинаются с 0
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;  // Если ошибка парсинга, вернуть -1
        }
    }
    public static String getMonthName(int month) {
        String[] months = {
                "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        };
        return months[month - 1];
    }
    public static void changeScene(Button button,Scene scene, Stage stage) {
        button.setOnAction(event -> {
            stage.setScene(scene);
        });
    }
    public static int countRow(File file){
        int count = 0;
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = new XSSFWorkbook(fis)) {
                Sheet sheet = workbook.getSheetAt(0);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        count++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
    public static void ExcelRead(File file, ArrayList<ProductSale> productSaleList) throws IOException {
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = new XSSFWorkbook(fis)) {
                Sheet sheet = workbook.getSheetAt(0);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;
                    int id = (int) getNum(row, 0);
                    String name = getStr(row, 1);
                    double price = getNum(row, 2);
                    int quantity = (int) getNum(row, 3);
                    int finalPrice = (int) getNum(row, 4);
                    String date = getStr(row, 5);
                    productSaleList.add(new ProductSale(id, name, price, quantity, finalPrice, date));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static double getNum(Row row, int index){
        Cell cell = row.getCell(index);
        if (cell != null) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                case FORMULA:
                    try {
                        return cell.getNumericCellValue();
                    } catch (Exception e) {
                        return 0;
                    }
                default:
                    return 0;
            }
        }
        return 0;
    }
    public static String getStr(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell != null) {
            if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue().trim();
            } else if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return new SimpleDateFormat("dd.MM.yyyy").format(cell.getDateCellValue());
            }
        }
        return "";
    }
    public static void main(String[] args) {
        launch();
    }
}