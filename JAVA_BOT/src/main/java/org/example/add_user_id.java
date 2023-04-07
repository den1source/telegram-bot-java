package org.example;

import java.sql.*;
import java.util.ArrayList;

public class add_user_id {

    add_user_id() throws SQLException, ClassNotFoundException {
    }

    public void add_data_in_table_of_users(Long id_user_id) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        if (!all_user().contains(id_user_id) && id_user_id!=0) {
            if(all_user().size()==0){
                try {
                    PreparedStatement pr_stmt = null;
                    String sql = "INSERT INTO table_of_user (ID,ID_USER) VALUES (?,?)";
                    pr_stmt = c.prepareStatement(sql);
                    pr_stmt.setInt(1, 0);
                    pr_stmt.setLong(2, id_user_id);
                    pr_stmt.executeUpdate();

                    pr_stmt.close();
                    c.close();
                } catch (Exception e) {
                    System.out.println("Ошибка во внесении данных в БД");
                    e.printStackTrace();
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);
                }
            }
            else {
                ArrayList<Integer> arrayList=all_id();
                int last_index=arrayList.get(arrayList.size()-1);
                try {
                    PreparedStatement pr_stmt = null;
                    String sql = "INSERT INTO table_of_user (ID,ID_USER) VALUES (?,?)";
                    pr_stmt = c.prepareStatement(sql);
                    pr_stmt.setInt(1, last_index+1);
                    pr_stmt.setLong(2, id_user_id);
                    pr_stmt.executeUpdate();

                    pr_stmt.close();
                    c.close();
                } catch (Exception e) {
                    System.out.println("Ошибка во внесении данных в БД");
                    e.printStackTrace();
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);
                }

            }


        }
    }

    private  ArrayList<Long> all_user() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        ArrayList<Long> all_id_user = new ArrayList<>();
        try {

            Statement stmt = c.createStatement();
            String temp = "SELECT * FROM table_of_user";
            ResultSet rs = stmt.executeQuery(temp);
            while (rs.next()) {
                all_id_user.add( rs.getLong("ID_USER"));
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
        return all_id_user;
    }

    private  ArrayList<Integer> all_id() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        ArrayList<Integer> all_id = new ArrayList<>();
        try {

            Statement stmt = c.createStatement();
            String temp = "SELECT * FROM table_of_user";
            ResultSet rs = stmt.executeQuery(temp);
            while (rs.next()) {
                all_id.add(rs.getInt("ID"));
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
        return all_id;
    }

    public int ID_USER_TO_PR_KEY(long id_user_telga) throws SQLException, ClassNotFoundException {

        int number = all_user().indexOf(id_user_telga);
        return all_id().get(number);
    }
}
