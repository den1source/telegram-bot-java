package org.example;

import java.sql.*;
import java.util.ArrayList;

public class tovar_BD {

    tovar_BD() throws SQLException, ClassNotFoundException {
        connect();
    }

    public void connect() throws ClassNotFoundException, SQLException {

    }

    public ArrayList<String> get_all_type_of_products() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        ArrayList<String> type_xleba = new ArrayList<>();
        try {

            Statement stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM pekarny_read_xleba");
            while (rs.next()) {
                String type = rs.getString("VID");
                if (!type_xleba.contains(type)) {
                    type_xleba.add(type);
                }
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return type_xleba;
    }

    public String get_type_of_product(String type) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        String vid = "";
        try {

            Statement stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM pekarny_read_xleba WHERE TYPE='" + type + "'");
            while (rs.next()) {
                vid = rs.getString("VID");
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return vid;
    }

    public ArrayList<Integer> read_all_id() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        ArrayList<Integer> type_xleba = new ArrayList<>();
        try {

            Statement stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM pekarny_read_xleba");
            while (rs.next()) {
                type_xleba.add(rs.getInt("id"));
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return type_xleba;
    }

    public ArrayList<String> get_all_products() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        ArrayList<String> type_xleba = new ArrayList<>();
        try {

            Statement stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM pekarny_read_xleba");
            while (rs.next()) {
                type_xleba.add(rs.getString("type"));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return type_xleba;
    }

    public int read_time(String xleb) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        int tt=0;
        try {

            Statement stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM pekarny_read_xleba WHERE TYPE='"+xleb+"'");
            while (rs.next()) {
                tt=(rs.getInt("time"));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return tt;
    }


    public ArrayList<String> all_products_of_type(String vid) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        ArrayList<String> tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            //String sql = "DELETE FROM pekarny_write WHERE id_order = ?";
            ResultSet rs = stmt.executeQuery("SELECT * FROM pekarny_read_xleba WHERE vid='" + vid + "'");
            while (rs.next()) {
                String name = rs.getString("TYPE");
                tt.add(name);
            }
            rs.close();
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return tt;
    }

    public ArrayList<Double> all_price_of_type(String vid) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        ArrayList<Double> tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM pekarny_read_xleba WHERE vid='" + vid + "'");
            while (rs.next()) {
                Double name = rs.getDouble("price");
                tt.add(name);
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return tt;
    }

    public double price_of_product(String tovar) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        double tt = 0.0;
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM pekarny_read_xleba WHERE type='" + tovar + "'");
            while (rs.next()) {
                tt = rs.getDouble("price");

            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return tt;
    }

}
