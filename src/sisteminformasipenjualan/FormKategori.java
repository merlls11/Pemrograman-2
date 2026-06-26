package sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FormKategori extends JPanel {
    private Connection conn = Koneksi.getKoneksi();
    private DefaultTableModel modelTabel;
    private JTable tblKategori;
    private JTextField txtNamaKategori;
    private JButton btnTambah;

    public FormKategori() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setBackground(new Color(248, 250, 252));

        JLabel lblTitle = new JLabel("🏷️ Data Kategori Barang");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 26));
        lblTitle.setForeground(new Color(15, 23, 42));
        add(lblTitle, BorderLayout.NORTH);

        // Tabel dengan dekorasi elegan
        modelTabel = new DefaultTableModel(new Object[]{"ID Kategori", "Nama Kategori"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblKategori = new JTable(modelTabel);
        KomponenDesainHelper.dekorasiTabelElegan(tblKategori);
        add(new JScrollPane(tblKategori), BorderLayout.CENTER);

        // Panel Input (Rounded Card)
        JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        panelInput.setOpaque(false);
        panelInput.setBorder(new EmptyBorder(20, 20, 20, 20));

        txtNamaKategori = buatTextFieldOval(15);
        btnTambah = buatTombolOval("Tambah", new Color(59, 130, 246), Color.WHITE);

        panelInput.add(buatLabel("Nama Kategori:"));
        panelInput.add(txtNamaKategori);
        panelInput.add(btnTambah);
        
        add(panelInput, BorderLayout.SOUTH);

        btnTambah.addActionListener(e -> aksiSimpanKategori());
        loadKategoriDariDatabase();
    }

    // --- HELPER METODE UI CUSTOM ---
    private JLabel buatLabel(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Inter", Font.BOLD, 12));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }

    private JTextField buatTextFieldOval(int columns) {
        JTextField tf = new JTextField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        tf.setOpaque(false); tf.setBorder(new EmptyBorder(8, 15, 8, 15));
        tf.setFont(new Font("Inter", Font.PLAIN, 13)); tf.setBackground(Color.WHITE);
        return tf;
    }

    private JButton buatTombolOval(String teks, Color bg, Color fg) {
        JButton btn = new JButton(teks) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        btn.setBackground(bg); btn.setForeground(fg); btn.setFont(new Font("Inter", Font.BOLD, 12));
        btn.setContentAreaFilled(false); btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 35));
        return btn;
    }

    // --- FUNGSI ASLI TIDAK DIUBAH ---
    private void loadKategoriDariDatabase() {
        modelTabel.setRowCount(0);
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM tb_kategori");
            while (rs.next()) {
                modelTabel.addRow(new Object[]{rs.getInt("id_kategori"), rs.getString("nama_kategori")});
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void aksiSimpanKategori() {
        if (txtNamaKategori.getText().trim().isEmpty()) return;
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO tb_kategori(nama_kategori) VALUES(?)");
            ps.setString(1, txtNamaKategori.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kategori Sukses Ditambahkan!");
            txtNamaKategori.setText("");
            loadKategoriDariDatabase();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}