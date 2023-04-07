package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class data_DB {
    public ArrayList<Long> id_order_bd_1 = new ArrayList<>();
    public ArrayList<String> all_shops  = new ArrayList<>();
    public ArrayList<String> type_tovar_1 = new ArrayList<>();
    public ArrayList<Integer> quantity_1 = new ArrayList<>();
    public ArrayList<Double> sum_1 = new ArrayList<>();
    public ArrayList<String> time_1 = new ArrayList<>();

    data_DB() throws SQLException, ClassNotFoundException {
    }


    public void add_data(ArrayList<HashMap> list) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        try {
            PreparedStatement pr_stmt = null;
            for (HashMap hashMap : list) {
                String sql = "INSERT INTO pekarny_write (ID_ORDER,ID_USER,ADRES,TYPE,kol_vo,sum,time) VALUES (?,?,?,?,?,?,?)";
                pr_stmt = c.prepareStatement(sql);
                pr_stmt.setLong(1, Long.parseLong(((String) hashMap.get("id_order"))));
                pr_stmt.setInt(2, Integer.parseInt((String) hashMap.get("id_user")));
                pr_stmt.setInt(3, Integer.parseInt((String) hashMap.get("adres")));
                pr_stmt.setInt(4, Integer.parseInt((String) hashMap.get("type")));
                pr_stmt.setInt(5, Integer.parseInt((String) hashMap.get("kol_vo")));
                pr_stmt.setFloat(6, Float.parseFloat((String) hashMap.get("sum")));
                pr_stmt.setString(7, ((String) hashMap.get("time")));
                pr_stmt.executeUpdate();
            }
            pr_stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Ошибка во внесении данных в БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

    }

    public void delete_order(Long id_or) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        PreparedStatement pr_stmt = null;
        try {
            String sql = "DELETE FROM pekarny_write WHERE id_order = ?";
            pr_stmt = c.prepareStatement(sql);
            pr_stmt.setLong(1, id_or);
            pr_stmt.executeUpdate();

            pr_stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println("Ошибка в удалении данных из БД");
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void get_data_user_id(int id_user_telega, ArrayList<String> vid_xleba_for_user,ArrayList<Integer> vid_xleba_id, ArrayList<String> adres_shop) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        try {

            Statement stmt = c.createStatement();
            String temp = "SELECT * FROM pekarny_write WHERE ID_USER= '" + id_user_telega + "'";
            ResultSet rs = stmt.executeQuery(temp);
            while (rs.next()) {
                id_order_bd_1.add(rs.getLong("id_order"));
                String tt=adres_shop.get(rs.getInt("adres"));
                if(!all_shops.contains(tt)) all_shops.add(tt);
                type_tovar_1.add(vid_xleba_for_user.get(vid_xleba_id.indexOf(rs.getInt("type"))));
                quantity_1.add(rs.getInt("kol_vo"));
                sum_1.add(rs.getDouble("sum"));
                String te=rs.getString("time");
                if(!time_1.contains(te)) time_1.add(te);
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
    }

    public ArrayList<Long> All_id_order_bd() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        ArrayList<Long> id_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM pekarny_write");
            while (rs.next()) {
                Long id = rs.getLong("id_order");
                id_tt.add(id);
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
        return id_tt;
    }





    public ArrayList<Long> all_id_order_for_user(long id_user_telega) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        ArrayList<Long> id_tt = new ArrayList<>();
        try {
            Statement stmt = c.createStatement();
            String temp = "SELECT * FROM pekarny_write WHERE ID_USER= '" + id_user_telega + "'";
            ResultSet rs = stmt.executeQuery(temp);
            while (rs.next()) {
                Long id = rs.getLong("id_order");
                id_tt.add(id);
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
        return id_tt;
    }



}
