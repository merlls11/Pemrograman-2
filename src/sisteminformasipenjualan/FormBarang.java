package sisteminformasipenjualan;

import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormBarang extends JPanel {
    private Connection conn = Koneksi.getKoneksi();
    private DefaultTableModel modelTabel;
    private JTable tblBarang;
    private JTextField txtId, txtNama, txtSatuan, txtHarga, txtStok;
    private JComboBox<String> cmbKategori;
    private JButton btnSimpan, btnHapus, btnReset, btnTambahKategori, btnTambahStok;
    private boolean isEditMode = false;
    
    private FormKasir formKasir;
    private String roleUser = ""; 

    // Menyimpan seluruh baris hasil query agar bisa difilter ulang sesuai tab aktif
    private final List<Object[]> seluruhDataBarang = new ArrayList<>();
    private String filterStatusAktif = "Semua";

    public FormBarang() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setBackground(new Color(248, 250, 252)); 

        // --- HEADER ---
        JLabel title = new JLabel("📦 Master Data Inventori Barang");
        title.setFont(new Font("Inter", Font.BOLD, 26));
        title.setForeground(new Color(15, 23, 42));
        add(title, BorderLayout.NORTH);

        // --- TABEL ---
        modelTabel = new DefaultTableModel(new Object[]{"ID Barang", "ID Kategori", "Kategori Produk", "Nama Barang", "Satuan", "Harga Jual", "Stok"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tblBarang = new JTable(modelTabel);
        KomponenDesainHelper.dekorasiTabelElegan(tblBarang);
        tblBarang.getColumnModel().getColumn(6).setCellRenderer(KomponenDesainHelper.buatRendererBadgeStok());

        // --- TAB FILTER STATUS STOK ---
        JPanel panelTabFilter = KomponenDesainHelper.buatTabFilter(
                new String[]{"Semua", "Aman", "Menipis", "Habis"},
                status -> { filterStatusAktif = status; terapkanFilter(); }
        );
        panelTabFilter.setBorder(new EmptyBorder(0, 5, 10, 0));

        JPanel panelTabelWadah = new JPanel(new BorderLayout());
        panelTabelWadah.setOpaque(false);
        panelTabelWadah.add(panelTabFilter, BorderLayout.NORTH);
        panelTabelWadah.add(new JScrollPane(tblBarang), BorderLayout.CENTER);
        add(panelTabelWadah, BorderLayout.CENTER);

        // --- FORM PANEL (Custom Rounded Card) ---
        JPanel panelForm = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 35, 35); 
                super.paintComponent(g);
                g2d.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(226, 232, 240));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 35, 35);
                g2d.dispose();
            }
        };
        panelForm.setOpaque(false);
        panelForm.setBorder(new EmptyBorder(25, 25, 25, 25)); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = buatTextFieldOval(12); 
        txtId.setEditable(false); 
        txtId.setBackground(new Color(241, 245, 249));
        
        txtNama = buatTextFieldOval(15); 
        txtSatuan = buatTextFieldOval(8);
        txtHarga = buatTextFieldOval(12); 
        txtStok = buatTextFieldOval(8);
        
        cmbKategori = new JComboBox<>();
        cmbKategori.setFont(new Font("Inter", Font.PLAIN, 13));
        cmbKategori.setBackground(Color.WHITE);
        
        btnTambahKategori = buatTombolOval("+", new Color(30, 41, 59), Color.WHITE);

        btnSimpan = buatTombolOval("Simpan Produk", new Color(249, 115, 22), Color.WHITE);
        btnHapus = buatTombolOval("Hapus", new Color(239, 68, 68), Color.WHITE);
        btnReset = buatTombolOval("Reset", new Color(226, 232, 240), new Color(15, 23, 42));
        btnTambahStok = buatTombolOval("Tambah Stok (+)", new Color(16, 185, 129), Color.WHITE);
        btnTambahStok.setEnabled(false); 

        JPanel panelKatCombo = new JPanel(new BorderLayout(8, 0));
        panelKatCombo.setOpaque(false);
        panelKatCombo.add(cmbKategori, BorderLayout.CENTER);
        panelKatCombo.add(btnTambahKategori, BorderLayout.EAST);

        JLabel lblFormTitle = new JLabel("📝 Form Input Data Barang");
        lblFormTitle.setFont(new Font("Inter", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(15, 23, 42));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 6;
        gbc.insets = new Insets(0, 10, 20, 10);
        panelForm.add(lblFormTitle, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Baris 1
        gbc.gridx = 0; gbc.gridy = 1; panelForm.add(buatLabel("ID Barang:"), gbc);
        gbc.gridx = 1; panelForm.add(txtId, gbc);
        gbc.gridx = 2; panelForm.add(buatLabel("Kategori:"), gbc);
        gbc.gridx = 3; panelForm.add(panelKatCombo, gbc);
        gbc.gridx = 4; panelForm.add(buatLabel("Nama Barang:"), gbc);
        gbc.gridx = 5; panelForm.add(txtNama, gbc);

        // Baris 2
        gbc.gridx = 0; gbc.gridy = 2; panelForm.add(buatLabel("Satuan:"), gbc);
        gbc.gridx = 1; panelForm.add(txtSatuan, gbc);
        gbc.gridx = 2; panelForm.add(buatLabel("Harga Jual (Rp):"), gbc);
        gbc.gridx = 3; panelForm.add(txtHarga, gbc);
        gbc.gridx = 4; panelForm.add(buatLabel("Stok:"), gbc);
        gbc.gridx = 5; panelForm.add(txtStok, gbc);

        // --- PANEL AKSI ---
        JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelAksi.setOpaque(false);
        panelAksi.add(btnTambahStok); 
        panelAksi.add(btnSimpan); 
        panelAksi.add(btnHapus); 
        panelAksi.add(btnReset);

        JPanel panelBawahWadah = new JPanel(new BorderLayout(5, 5));
        panelBawahWadah.setOpaque(false);
        panelBawahWadah.add(panelForm, BorderLayout.CENTER);
        panelBawahWadah.add(panelAksi, BorderLayout.SOUTH);
        add(panelBawahWadah, BorderLayout.SOUTH);

        // ACTION LISTENERS
        btnTambahKategori.addActionListener(e -> panggilPopUpKategori());
        btnSimpan.addActionListener(e -> aksiSimpanAtauEdit());
        btnHapus.addActionListener(e -> aksiHapus());
        btnReset.addActionListener(e -> resetForm());
        btnTambahStok.addActionListener(e -> aksiTambahStok()); 
        
        tblBarang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { pilihBarisTabel(); }
        });

        loadData();
    }

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
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                g2d.setColor(new Color(148, 163, 184)); 
                g2d.setStroke(new BasicStroke(1.5f));  
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false); 
        tf.setBorder(new EmptyBorder(5, 15, 5, 15));
        tf.setFont(new Font("Inter", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(220, 40)); 
        return tf;
    }

    private JButton buatTombolOval(String teks, Color bg, Color fg) {
        JButton btn = new JButton(teks) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(isEnabled() ? getBackground() : new Color(203, 213, 225));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Inter", Font.BOLD, 12));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 35));
        return btn;
    }

    public void setFormKasir(FormKasir fk) { 
        this.formKasir = fk; 
    }

    // --- DIKUNCI TOTAL: Proteksi Hak Akses untuk Kasir ---
    public void setRole(String role) {
        this.roleUser = role;
        if (role.equalsIgnoreCase("Kasir")) {
            btnSimpan.setEnabled(false);
            btnHapus.setEnabled(false);
            btnReset.setEnabled(false);
            btnTambahKategori.setEnabled(false);
            btnTambahStok.setEnabled(false); 
            
            txtNama.setEditable(false);
            txtSatuan.setEditable(false);
            txtHarga.setEditable(false); // Blokir input harga
            txtStok.setEditable(false);
            cmbKategori.setEnabled(false);
            
            // Tooltip info edukatif
            btnSimpan.setToolTipText("Kasir tidak memiliki hak akses untuk memanipulasi data inventori.");
            txtHarga.setToolTipText("Hanya Admin/Owner yang diizinkan untuk mengubah harga barang.");
        }
    }

    private void buatIdOtomatis() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id_barang FROM tb_barang ORDER BY id_barang DESC LIMIT 1");
            if (rs.next()) {
                String idTerakhir = rs.getString("id_barang");
                int angka = Integer.parseInt(idTerakhir.substring(3)) + 1;
                txtId.setText(String.format("BRG%03d", angka));
            } else { 
                txtId.setText("BRG001"); 
            }
        } catch (Exception e) { 
            txtId.setText("BRG001"); 
        }
    }

    public void loadData() {
        cmbKategori.removeAllItems();
        seluruhDataBarang.clear();
        NumberFormat formatRp = NumberFormat.getCurrencyInstance(new Locale("id", "ID")); 
        
        try {
            Statement st = conn.createStatement();
            ResultSet rsK = st.executeQuery("SELECT * FROM tb_kategori");
            while(rsK.next()) cmbKategori.addItem(rsK.getInt(1) + " - " + rsK.getString(2));

            ResultSet rsB = st.executeQuery("SELECT b.id_barang, b.id_kategori, k.nama_kategori, b.nama_barang, b.satuan, b.harga_jual, b.stok FROM tb_barang b JOIN tb_kategori k ON b.id_kategori=k.id_kategori");
            while(rsB.next()) {
                seluruhDataBarang.add(new Object[]{
                    rsB.getString(1), 
                    rsB.getInt(2), 
                    rsB.getString(3), 
                    rsB.getString(4), 
                    rsB.getString(5), 
                    formatRp.format(rsB.getDouble(6)), 
                    rsB.getInt(7)
                });
            }
        } catch (Exception e) {}
        
        if(!isEditMode) buatIdOtomatis();
        terapkanFilter();
    }

    private void terapkanFilter() {
        modelTabel.setRowCount(0);
        for (Object[] baris : seluruhDataBarang) {
            int stok = (Integer) baris[6];
            String status = KomponenDesainHelper.statusStok(stok);
            if (filterStatusAktif.equals("Semua") || filterStatusAktif.equals(status)) {
                modelTabel.addRow(baris);
            }
        }
    }

    private void panggilPopUpKategori() {
        String namaBaru = JOptionPane.showInputDialog(this, "Ketik Nama Kategori Baru:", "Pop-Up Kategori Baru", JOptionPane.QUESTION_MESSAGE);
        if (namaBaru != null && !namaBaru.trim().isEmpty()) {
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO tb_kategori (nama_kategori) VALUES (?)");
                ps.setString(1, namaBaru.trim()); 
                ps.executeUpdate();
                loadData();
            } catch (Exception e) { 
                JOptionPane.showMessageDialog(this, e.getMessage()); 
            }
        }
    }

    private void pilihBarisTabel() {
        int baris = tblBarang.getSelectedRow();
        if(baris != -1) {
            // Data tetap ditampilkan ke field agar kasir bisa melihat detail spesifik barang
            txtId.setText(modelTabel.getValueAt(baris, 0).toString());
            txtNama.setText(modelTabel.getValueAt(baris, 3).toString());
            txtSatuan.setText(modelTabel.getValueAt(baris, 4).toString());
            
            String hargaRaw = modelTabel.getValueAt(baris, 5).toString().replaceAll("[^0-9]", ""); 
            txtHarga.setText(hargaRaw.substring(0, hargaRaw.length() - 2)); 
            
            txtStok.setText(modelTabel.getValueAt(baris, 6).toString());
            cmbKategori.setSelectedItem(modelTabel.getValueAt(baris, 1).toString() + " - " + modelTabel.getValueAt(baris, 2).toString());
            
            // PROTEKSI TAMBAHAN: Jika terdeteksi Kasir, kunci sistem edit mode
            if (!"Kasir".equalsIgnoreCase(roleUser)) {
                isEditMode = true;
                btnSimpan.setText("Update Data");
                btnSimpan.setBackground(new Color(234, 88, 12)); 
                btnTambahStok.setEnabled(true);
            } else {
                isEditMode = false;
                btnTambahStok.setEnabled(false);
            }
        }
    }

    private void aksiTambahStok() {
        if(txtId.getText().isEmpty()) return;
        
        String inputStok = JOptionPane.showInputDialog(this, 
            "Berapa jumlah stok yang ingin ditambahkan untuk " + txtNama.getText() + "?", 
            "Tambah Stok Barang Masuk", JOptionPane.QUESTION_MESSAGE);
            
        if (inputStok != null && !inputStok.trim().isEmpty()) {
            try {
                int stokMasuk = Integer.parseInt(inputStok.trim());
                if (stokMasuk <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                PreparedStatement ps = conn.prepareStatement("UPDATE tb_barang SET stok = stok + ? WHERE id_barang = ?");
                ps.setInt(1, stokMasuk);
                ps.setString(2, txtId.getText());
                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Berhasil! Stok " + txtNama.getText() + " berhasil ditambahkan sebanyak " + stokMasuk + ".");
                loadData(); 
                resetForm();
                
                if (formKasir != null) {
                    formKasir.loadDropdowns();
                    formKasir.loadKeranjang();
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Input tidak valid! Harap masukkan angka bulat.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            }
        }
    }

    private void aksiSimpanAtauEdit() {
        if(txtId.getText().isEmpty() || txtNama.getText().isEmpty()) return;
        
        try {
            Double.parseDouble(txtHarga.getText().trim());
            Integer.parseInt(txtStok.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga and Stok harus berupa angka!", "Error Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String idK = cmbKategori.getSelectedItem().toString().split(" - ")[0];
        try {
            if (!isEditMode) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO tb_barang VALUES(?,?,?,?,?,?)");
                ps.setString(1, txtId.getText().trim()); 
                ps.setInt(2, Integer.parseInt(idK));
                ps.setString(3, txtNama.getText().trim()); 
                ps.setString(4, txtSatuan.getText().trim());
                ps.setDouble(5, Double.parseDouble(txtHarga.getText().trim())); 
                ps.setInt(6, Integer.parseInt(txtStok.getText().trim()));
                ps.executeUpdate();
            } else {
                PreparedStatement ps = conn.prepareStatement("UPDATE tb_barang SET id_kategori=?, nama_barang=?, satuan=?, harga_jual=?, stok=? WHERE id_barang=?");
                ps.setInt(1, Integer.parseInt(idK)); 
                ps.setString(2, txtNama.getText().trim());
                ps.setString(3, txtSatuan.getText().trim()); 
                ps.setDouble(4, Double.parseDouble(txtHarga.getText().trim()));
                ps.setInt(5, Integer.parseInt(txtStok.getText().trim())); 
                ps.setString(6, txtId.getText().trim());
                ps.executeUpdate();
            }
            loadData(); 
            resetForm();
            
            if (formKasir != null) {
                formKasir.loadDropdowns();
                formKasir.loadKeranjang();
            }
            
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
        } catch(Exception e) { 
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage()); 
        }
    }

    private void aksiHapus() {
        if(txtId.getText().isEmpty()) return;
        if(JOptionPane.showConfirmDialog(this, "Hapus data produk ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_barang WHERE id_barang=?");
                ps.setString(1, txtId.getText()); 
                ps.executeUpdate();
                
                loadData(); 
                resetForm();
                
                if (formKasir != null) {
                    formKasir.loadDropdowns();
                    formKasir.loadKeranjang();
                }
                
            } catch(Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            }
        }
    }

    private void resetForm() {
        txtNama.setText(""); txtSatuan.setText(""); txtHarga.setText(""); txtStok.setText("");
        
        if (!"Kasir".equalsIgnoreCase(roleUser)) {
            isEditMode = false; 
            btnSimpan.setText("Simpan Produk"); 
            btnSimpan.setBackground(new Color(249, 115, 22));
            btnTambahStok.setEnabled(false); 
        }
        buatIdOtomatis();
    }
}