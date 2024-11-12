package com.mvishok;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class MySQLTableViewer {

    static final String DEFAULT_HOST = "localhost";
    static final String DB_NAME = "minter";
    static final String USER = "root";
    static final String PASS = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String host = JOptionPane.showInputDialog(null, "Enter Host (leave empty for localhost):", "Host Name", JOptionPane.QUESTION_MESSAGE);
            if (host == null || host.trim().isEmpty()) {
                host = DEFAULT_HOST;
            }

            String dbUrl = "jdbc:mysql://" + host + ":3306/" + DB_NAME;

            String tableName = JOptionPane.showInputDialog(null, "Enter Table Name:", "Table Name", JOptionPane.QUESTION_MESSAGE);

            if (tableName != null && !tableName.trim().isEmpty()) {
                JFrame frame = new JFrame("MySQL Table Viewer");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 400);

                JTable table = new JTable();
                DefaultTableModel tableModel = new DefaultTableModel();
                table.setModel(tableModel);

                String query = "SELECT * FROM " + tableName;

                try (Connection conn = DriverManager.getConnection(dbUrl, USER, PASS);
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    Vector<String> columnNames = new Vector<>();
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames.add(metaData.getColumnName(i));
                    }
                    tableModel.setColumnIdentifiers(columnNames);

                    while (rs.next()) {
                        Vector<Object> rowData = new Vector<>();
                        for (int i = 1; i <= columnCount; i++) {
                            rowData.add(rs.getObject(i));
                        }
                        tableModel.addRow(rowData);
                    }

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Error fetching data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }

                JScrollPane scrollPane = new JScrollPane(table);
                frame.add(scrollPane);
                frame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Table name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}