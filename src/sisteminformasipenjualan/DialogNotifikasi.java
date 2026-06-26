package sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Dialog notifikasi kustom (pengganti JOptionPane) yang selaras dengan tema
 * FormLogin: kartu putih bersudut melengkung, ikon bulat berwarna sesuai
 * tipe pesan, font Inter, dan tombol pill oranye.
 */
public class DialogNotifikasi extends JDialog {

    public enum Tipe { SUKSES, ERROR, PERINGATAN }

    private DialogNotifikasi(Window owner, Tipe tipe, String judul, String pesan) {
        super(owner, judul, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        Color warnaAksen;
        String simbol;
        switch (tipe) {
            case SUKSES:
                warnaAksen = new Color(22, 163, 74);
                simbol = "✓";
                break;
            case ERROR:
                warnaAksen = new Color(239, 68, 68);
                simbol = "✕";
                break;
            default:
                warnaAksen = new Color(249, 115, 22);
                simbol = "!";
                break;
        }

        // --- WRAPPER TRANSPARAN (supaya sudut di luar rounded-rect tidak terisi kotak) ---
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBackground(new Color(0, 0, 0, 0));

        // --- CARD PUTIH ROUNDED ---
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 35, 35);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ikon bulat berwarna
        final Color warnaIkon = warnaAksen;
        final String simbolIkon = simbol;
        JPanel lingkaranIkon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(warnaIkon);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Inter", Font.BOLD, 26));
                FontMetrics fm = g2d.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(simbolIkon)) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(simbolIkon, tx, ty);
                g2d.dispose();
            }
        };
        lingkaranIkon.setOpaque(false);
        lingkaranIkon.setPreferredSize(new Dimension(56, 56));
        lingkaranIkon.setMinimumSize(new Dimension(56, 56));
        lingkaranIkon.setMaximumSize(new Dimension(56, 56));

        JPanel panelIkonWadah = new JPanel();
        panelIkonWadah.setOpaque(false);
        panelIkonWadah.add(lingkaranIkon);

        JLabel lblJudul = new JLabel(judul, SwingConstants.CENTER);
        lblJudul.setFont(new Font("Inter", Font.BOLD, 18));
        lblJudul.setForeground(new Color(15, 23, 42));

        JLabel lblPesan = new JLabel("<html><div style='text-align:center; width:260px;'>" + pesan + "</div></html>", SwingConstants.CENTER);
        lblPesan.setFont(new Font("Inter", Font.PLAIN, 13));
        lblPesan.setForeground(new Color(100, 116, 139));
        lblPesan.setBorder(new EmptyBorder(4, 0, 16, 0));
        lblPesan.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnOk = new JButton("OK") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        btnOk.setPreferredSize(new Dimension(160, 42));
        btnOk.setBackground(new Color(249, 115, 22));
        btnOk.setForeground(Color.WHITE);
        btnOk.setFont(new Font("Inter", Font.BOLD, 14));
        btnOk.setContentAreaFilled(false);
        btnOk.setFocusPainted(false);
        btnOk.setBorderPainted(false);
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btnOk.setBackground(new Color(234, 88, 12)); }
            public void mouseExited(MouseEvent evt) { btnOk.setBackground(new Color(249, 115, 22)); }
            public void mousePressed(MouseEvent evt) { btnOk.setBackground(new Color(194, 65, 12)); }
            public void mouseReleased(MouseEvent evt) { btnOk.setBackground(new Color(234, 88, 12)); }
        });
        btnOk.addActionListener(e -> dispose());

        JPanel panelTombolWadah = new JPanel();
        panelTombolWadah.setOpaque(false);
        panelTombolWadah.add(btnOk);

        gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE; card.add(panelIkonWadah, gbc);
        gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; card.add(lblJudul, gbc);
        gbc.gridy = 2; card.add(lblPesan, gbc);
        gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; card.add(panelTombolWadah, gbc);

        wrapper.add(card);
        setContentPane(wrapper);
        getRootPane().setOpaque(false);
        wrapper.setOpaque(false);

        getContentPane().setBackground(new Color(0, 0, 0, 0));

        pack();
        setLocationRelativeTo(owner);
    }

    /** Menampilkan dialog notifikasi modal. Method ini blocking sampai tombol OK ditekan. */
    public static void tampilkan(Window owner, Tipe tipe, String judul, String pesan) {
        DialogNotifikasi dialog = new DialogNotifikasi(owner, tipe, judul, pesan);
        dialog.setVisible(true);
    }

    // =====================================================================
    // VARIAN KEDUA: DIALOG DETAIL (header oranye + isi panjang yang bisa
    // di-scroll, misalnya untuk Detail Transaksi). Tetap satu class yang
    // sama supaya gaya visualnya konsisten dengan dialog notifikasi di atas.
    // =====================================================================

    private DialogNotifikasi(Window owner, String judulHeader, String isiDetail) {
        super(owner, judulHeader, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBackground(new Color(0, 0, 0, 0));

        // --- CARD PUTIH ROUNDED ---
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 35, 35);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(400, 480));

        // --- HEADER ORANYE (hanya sudut atas melengkung, mengikuti bentuk card) ---
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(249, 115, 22));
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() * 2, 35, 35);
                g2d.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));
        header.setPreferredSize(new Dimension(400, 70));

        JLabel lblHeader = new JLabel(judulHeader);
        lblHeader.setFont(new Font("Inter", Font.BOLD, 17));
        lblHeader.setForeground(Color.WHITE);
        header.add(lblHeader, BorderLayout.WEST);

        JButton btnTutupX = new JButton("✕");
        btnTutupX.setFont(new Font("Inter", Font.BOLD, 14));
        btnTutupX.setForeground(Color.WHITE);
        btnTutupX.setContentAreaFilled(false);
        btnTutupX.setBorderPainted(false);
        btnTutupX.setFocusPainted(false);
        btnTutupX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTutupX.addActionListener(e -> dispose());
        header.add(btnTutupX, BorderLayout.EAST);

        // --- ISI DETAIL (scrollable, monospace agar angka rapi) ---
        JTextArea area = new JTextArea(isiDetail);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setForeground(new Color(30, 41, 59));
        area.setBackground(Color.WHITE);
        area.setBorder(new EmptyBorder(20, 25, 20, 25));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        // --- FOOTER: TOMBOL TUTUP (pill oranye, gaya sama dengan btnOk) ---
        JButton btnTutup = new JButton("Tutup") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        btnTutup.setPreferredSize(new Dimension(160, 42));
        btnTutup.setBackground(new Color(249, 115, 22));
        btnTutup.setForeground(Color.WHITE);
        btnTutup.setFont(new Font("Inter", Font.BOLD, 14));
        btnTutup.setContentAreaFilled(false);
        btnTutup.setFocusPainted(false);
        btnTutup.setBorderPainted(false);
        btnTutup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTutup.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btnTutup.setBackground(new Color(234, 88, 12)); }
            public void mouseExited(MouseEvent evt) { btnTutup.setBackground(new Color(249, 115, 22)); }
            public void mousePressed(MouseEvent evt) { btnTutup.setBackground(new Color(194, 65, 12)); }
            public void mouseReleased(MouseEvent evt) { btnTutup.setBackground(new Color(234, 88, 12)); }
        });
        btnTutup.addActionListener(e -> dispose());

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(15, 0, 25, 0));
        footer.add(btnTutup);

        card.add(header, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        wrapper.add(card);
        setContentPane(wrapper);
        getRootPane().setOpaque(false);
        getContentPane().setBackground(new Color(0, 0, 0, 0));

        // Overlay gelap di belakang, muncul/hilang selaras dengan dialog
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) { OverlayBlur.tampilkan(owner); }
            @Override
            public void windowClosed(WindowEvent e) { OverlayBlur.tutup(); }
        });

        pack();
        setLocationRelativeTo(owner);
    }

    /** Menampilkan dialog detail (header oranye + isi panjang yang bisa di-scroll), dengan overlay gelap di belakang. */
    public static void tampilkanDetail(Window owner, String judulHeader, String isiDetail) {
        DialogNotifikasi dialog = new DialogNotifikasi(owner, judulHeader, isiDetail);
        dialog.setVisible(true);
    }
}