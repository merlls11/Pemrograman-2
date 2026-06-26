package sisteminformasipenjualan;

import java.awt.Font;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;

public class SistemInformasiPenjualan {
    public static void main(String[] args) {
        
        try {
            // 1. Setup tema FlatLaf
            FlatLightLaf.setup();
            
            // 2. Trik Global Bikin Semuanya Melengkung (Rounded)
            UIManager.put("Button.arc", 15);        // Melengkungkan semua Tombol
            UIManager.put("Component.arc", 15);     // Melengkungkan ComboBox, ScrollBar, dll
            UIManager.put("TextComponent.arc", 15); // Melengkungkan semua kolom teks (TextField)
            
            // 3. Trik Global Ubah Font Keseluruhan biar rapi dan modern
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
            
        } catch (Exception e) {
            System.err.println("Gagal memuat tema FlatLaf: " + e.getMessage());
        }

        // Meluncurkan pintu gerbang Login
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FormLogin().setVisible(true);
            }
        });
    }
}