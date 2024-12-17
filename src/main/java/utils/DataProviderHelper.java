package utils;

import datasources.XLSXTableReader;

public class DataProviderHelper {

    public static Object[][] getTableData(String xlsFilePath, String sheetTitle) {
        // open xls file
        XLSXTableReader tableReader = new XLSXTableReader(xlsFilePath, sheetTitle);
        // get number of rows and columns in a file
        int totalRows = tableReader.getRowCount();
        int totalCols = tableReader.getColumnCount(1);
        // create object that will contain data from xls file
        String[][] tableData = new String[totalRows][totalCols];

        // which row and column should we start reading from
        int startRow = 1; // 0 - row with column names
        int startCol = 0;

        // Populate our tableData object with data from a table
        int ci, cj;
        ci = 0;
        for (int i = startRow; i <= totalRows; i++, ci++) {
            cj = 0;
            for (int j = startCol; j < totalCols; j++, cj++) {
                tableData[ci][cj] = tableReader.getCellValueAsString(i, j);
            }
        }
        return tableData;
    }
}
