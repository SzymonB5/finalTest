package com.databaseviewer.dbviewerapp;

import java.sql.*;
import java.util.HashSet;

public class DatabaseConnector {
    private static final String jdbcUrl = "jdbc:postgresql://ep-wild-bush-363371.eu-central-1.aws.neon.tech/neondb";
    private static final String username = "SzymonB5";
    private static final String password = "AiSRg15nCLpa";

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            return null;
        }
    }

    private static boolean closeConnection(Connection connection) {
        try {
            connection.close();
            return true;
        }
        catch (SQLException exception) {
            return false;
        }
    }

    public static String getTableContent(String tableName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<h3>Table ").append(tableName).append(" content:</h3>\n");
        Connection connection = getConnection();
        String query = "SELECT * FROM " + tableName;

        try {
            assert connection != null;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            stringBuilder.append("<table>");
            stringBuilder.append("<tr>");
            for (int i = 1; i <= columnCount; i++)
                stringBuilder.append("<th>").append(metaData.getColumnName(i)).append("</th>");

            stringBuilder.append("</tr>");

            while (resultSet.next()) {
                stringBuilder.append("<tr>");
                for (int i = 1; i <= columnCount; i++)
                    stringBuilder.append("<td>").append(resultSet.getString(i)).append("</td>");

                stringBuilder.append("</tr>");
            }

            stringBuilder.append("</table>");

        } catch (SQLException e) {
            return null;
        }

        return stringBuilder.toString();
    }

    public static HashSet<String> getDatabaseInfo() {
        Connection connection = getConnection();
        HashSet<String> ret = new HashSet<>();
        String query = "SELECT table_name FROM information_schema.tables" +
                " WHERE table_schema='public' AND table_type='BASE TABLE'";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next())
                ret.add(resultSet.getString("table_name"));

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        closeConnection(connection);

        return ret;
    }

    public static String getDatabaseName() {
        Connection connection = getConnection();
        String ret;
        try {
            assert connection != null;
            ret = connection.getCatalog();
        } catch (SQLException e) {
            return null;
        }
        closeConnection(connection);

        return ret;
    }

    public static void test() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            String query = "SELECT * FROM users";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String column1Value = resultSet.getString("name");
                int column2Value = resultSet.getInt("counter");

                System.out.println("column1: " + column1Value);
                System.out.println("column2: " + column2Value);
                System.out.println("--------------------");
            }

            resultSet.close();
            statement.close();
            connection.close();

            System.out.println("Connection to the database and query execution were successful!");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database or execute the query.");
            e.printStackTrace();
        }
    }

    public static void removeEntry(String tableName, int id) throws Exception {
        var conn = getConnection();
        String query = "DELETE FROM " + tableName + " WHERE id = " + id;
        System.out.println(query);
        Statement statement = conn.createStatement();
        System.out.println("1");
        int resultSet = statement.executeUpdate(query);
        System.out.println("2");
        statement.close();
        conn.close();
    }

    public static void alterEntry(String tableName, String columnName, String newValue, int id) throws Exception {
        var conn = getConnection();

        String query = "UPDATE " + tableName + " SET " + columnName + " = '" + newValue + "' WHERE ID = " + id;
        Statement statement = conn.createStatement();
        System.out.println(query);
        statement.executeUpdate(query);

        statement.close();
        conn.close();
    }

}
