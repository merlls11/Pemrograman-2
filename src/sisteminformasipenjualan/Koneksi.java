package sisteminformasipenjualan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
    private static Connection koneksi;

    public static Connection getKoneksi() {
        try {
            if (koneksi == null || koneksi.isClosed()) {
                // Diarahkan ke database lo: db_baru
                String url = "jdbc:mysql://localhost:3306/db_baru"; 
                String user = "root";
                String pass = "";
                
                Class.forName("com.mysql.cj.jdbc.Driver");
                koneksi = DriverManager.getConnection(url, user, pass);
                System.out.println("Koneksi Database Sukses Terbuka!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Koneksi Database Gagal: " + e.getMessage());
        }
        return koneksi;
    }
}