package sisteminformasipenjualan;

import javax.swing.*;
import java.awt.*;

/**
 * Overlay gelap transparan yang ditampilkan menutupi window utama selama
 * sebuah dialog modal tampil, memberi efek "dim background" (pengganti blur
 * asli yang tidak tersedia secara native di Swing/AWT).
 */
public class OverlayBlur {

    private static JWindow overlay;

    /** Tampilkan overlay gelap di atas window pemilik (owner). */
    public static void tampilkan(Window owner) {
        if (owner == null) return;

        overlay = new JWindow(owner) {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(15, 23, 42, 110)); // hitam-kebiruan transparan, kesan "dim"
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        overlay.setBackground(new Color(0, 0, 0, 0));
        overlay.setBounds(owner.getBounds());
        overlay.setVisible(true);
    }

    /** Tutup dan hapus overlay yang sedang tampil. */
    public static void tutup() {
        if (overlay != null) {
            overlay.dispose();
            overlay = null;
        }
    }
}