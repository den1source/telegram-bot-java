package org.example;

import java.sql.*;
import java.util.ArrayList;

public class read_shops {

    private Connection c=null;

    read_shops() throws SQLException, ClassNotFoundException {
    }


    public ArrayList <String> get_all_shops() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager
                .getConnection("jdbc:postgresql://localhost:5432/testDB",
                        "denis", "123");
        ArrayList <String> adres_S=new ArrayList<>();
        try {

            Statement stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery( "SELECT * FROM table_of_shop" );
            while ( rs.next() ) {
                adres_S.add(rs.getString("adres"));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.out.println("Ошибка во чтении данных из БД");
            e.printStackTrace();
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return adres_S;
    }


}
