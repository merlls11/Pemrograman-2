package sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FormKasir extends JPanel {
    private Connection conn = Koneksi.getKoneksi();
    private DefaultTableModel modelKeranjang;
    private JTable tblKeranjang;
    private JComboBox<String> cmbIdBarang, cmbPelanggan;
    private JTextField txtJumlah, txtUangBayar;
    private JButton btnTambah, btnProses, btnHapusItem;
    private JLabel lblKembalian;

    private FormBarang formBarang;
    private FormLaporan formLaporan;
    private FormDashboard formDashboard;

    public FormKasir() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setBackground(new Color(248, 250, 252));

        // JUDUL
        JLabel title = new JLabel("🛒 Kasir Transaksi Penjualan");
        title.setFont(new Font("Inter", Font.BOLD, 26));
        title.setForeground(new Color(15, 23, 42));
        add(title, BorderLayout.NORTH);

        // PANEL INPUT (Card Top Minimalis)
        JPanel panelInput = new JPanel(new GridBagLayout()) {
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
                g2d.setColor(new Color(217, 119, 6)); 
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 35, 35);
                g2d.dispose();
            }
        };
        panelInput.setOpaque(false);
        panelInput.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.NONE; 

        cmbIdBarang = new JComboBox<>();
        cmbIdBarang.setPreferredSize(new Dimension(200, 35));
        
        cmbPelanggan = new JComboBox<>();
        cmbPelanggan.setPreferredSize(new Dimension(200, 35));

        txtJumlah = buatTextFieldOval(5);
        txtJumlah.setText("1"); 
        txtUangBayar = buatTextFieldOval(10);
        
        lblKembalian = new JLabel("Kembalian: Rp 0");
        lblKembalian.setFont(new Font("Inter", Font.BOLD, 14));
        lblKembalian.setForeground(new Color(22, 163, 74));
        
        btnTambah = buatTombolOval("Tambah", new Color(59, 130, 246), Color.WHITE);
        btnHapusItem = buatTombolOval("Hapus Item", new Color(239, 68, 68), Color.WHITE);
        btnProses = buatTombolOval("Bayar", new Color(79, 70, 229), Color.WHITE);

        KeyAdapter angkaHanya = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                if (!Character.isDigit(evt.getKeyChar())) evt.consume();
            }
        };
        txtJumlah.addKeyListener(angkaHanya);
        txtUangBayar.addKeyListener(angkaHanya);

        gbc.gridx = 0; gbc.gridy = 0; panelInput.add(buatLabel("Barang:"), gbc);
        gbc.gridx = 1; panelInput.add(cmbIdBarang, gbc);
        gbc.gridx = 2; panelInput.add(buatLabel("Jml:"), gbc);
        gbc.gridx = 3; panelInput.add(txtJumlah, gbc);
        gbc.gridx = 4; panelInput.add(btnTambah, gbc);
        gbc.gridx = 5; panelInput.add(btnHapusItem, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; panelInput.add(buatLabel("Pelanggan:"), gbc);
        gbc.gridx = 1; panelInput.add(cmbPelanggan, gbc);
        gbc.gridx = 2; panelInput.add(buatLabel("Bayar:"), gbc);
        gbc.gridx = 3; panelInput.add(txtUangBayar, gbc);
        gbc.gridx = 4; panelInput.add(btnProses, gbc);
        gbc.gridx = 5; panelInput.add(lblKembalian, gbc);

        // DATA MODEL KASIR
        modelKeranjang = new DefaultTableModel(new Object[]{"ID", "Nama Barang", "Harga Satuan", "Jml", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { 
                return col == 3; // Mengaktifkan interaksi klik di kolom Jml
            }
        };
        tblKeranjang = new JTable(modelKeranjang);
        
        tblKeranjang.setFont(new Font("Inter", Font.PLAIN, 14));
        tblKeranjang.setRowHeight(85); 
        tblKeranjang.setShowGrid(false);
        tblKeranjang.setIntercellSpacing(new Dimension(0, 0));
        tblKeranjang.setSelectionBackground(new Color(243, 244, 246));
        tblKeranjang.setSelectionForeground(Color.BLACK);
        
        tblKeranjang.getTableHeader().setFont(new Font("Inter", Font.BOLD, 12));
        tblKeranjang.getTableHeader().setBackground(new Color(248, 250, 252));
        tblKeranjang.getTableHeader().setForeground(new Color(156, 163, 175));
        tblKeranjang.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tblKeranjang.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(243, 244, 246)));

        // RENDERER UNTUK BARIS KARTU BARANG
        tblKeranjang.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final NumberFormat rp = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                if (col == 1) {
                    JPanel panelProduk = new JPanel();
                    panelProduk.setLayout(new BoxLayout(panelProduk, BoxLayout.Y_AXIS));
                    panelProduk.setOpaque(true);
                    panelProduk.setBackground(isSel ? table.getSelectionBackground() : Color.WHITE);
                    panelProduk.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(243, 244, 246)),
                        new EmptyBorder(15, 20, 15, 10)
                    ));

                    JLabel lblNama = new JLabel(val != null ? val.toString() : "");
                    lblNama.setFont(new Font("Inter", Font.BOLD, 15));
                    lblNama.setForeground(new Color(17, 24, 39));

                    Object hrgVal = table.getValueAt(row, 2);
                    double hargaAwal = hrgVal != null ? Double.parseDouble(hrgVal.toString()) : 0;
                    JLabel lblHarga = new JLabel(rp.format(hargaAwal));
                    lblHarga.setFont(new Font("Inter", Font.PLAIN, 12));
                    lblHarga.setForeground(new Color(156, 163, 175));

                    panelProduk.add(lblNama);
                    panelProduk.add(Box.createRigidArea(new Dimension(0, 4)));
                    panelProduk.add(lblHarga);
                    return panelProduk;
                }

                if (col == 4) {
                    JPanel panelSub = new JPanel(new BorderLayout());
                    panelSub.setOpaque(true);
                    panelSub.setBackground(isSel ? table.getSelectionBackground() : Color.WHITE);
                    panelSub.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(243, 244, 246)),
                        new EmptyBorder(0, 0, 0, 25)
                    ));

                    double subtotal = val != null ? Double.parseDouble(val.toString()) : 0;
                    JLabel lblSub = new JLabel(rp.format(subtotal), SwingConstants.RIGHT);
                    lblSub.setFont(new Font("Inter", Font.BOLD, 15));
                    lblSub.setForeground(new Color(79, 70, 229)); 

                    panelSub.add(lblSub, BorderLayout.CENTER);
                    return panelSub;
                }
                return new JLabel();
            }
        });

        // PASANG COMPONENT PLUS MINUS KE KOLOM QUANTITY
        tblKeranjang.getColumnModel().getColumn(3).setCellRenderer(new PanelPlusMinusRenderer());
        tblKeranjang.getColumnModel().getColumn(3).setCellEditor(new PanelPlusMinusEditor());

        tblKeranjang.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int totalLebar = tblKeranjang.getWidth();
                tblKeranjang.getColumnModel().getColumn(0).setPreferredWidth(0);   
                tblKeranjang.getColumnModel().getColumn(0).setMaxWidth(0);
                tblKeranjang.getColumnModel().getColumn(2).setPreferredWidth(0);   
                tblKeranjang.getColumnModel().getColumn(2).setMaxWidth(0);
                
                tblKeranjang.getColumnModel().getColumn(1).setPreferredWidth((int) (totalLebar * 0.50)); 
                tblKeranjang.getColumnModel().getColumn(3).setPreferredWidth((int) (totalLebar * 0.25)); 
                tblKeranjang.getColumnModel().getColumn(4).setPreferredWidth((int) (totalLebar * 0.25)); 
            }
        });
        
        JPanel panelTabel = new JPanel(new BorderLayout());
        panelTabel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(tblKeranjang);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        panelTabel.add(scrollPane, BorderLayout.CENTER);
        
        add(panelInput, BorderLayout.NORTH);
        add(panelTabel, BorderLayout.CENTER);

        btnTambah.addActionListener(e -> tambahKeKeranjang());
        btnHapusItem.addActionListener(e -> aksiHapusItemTerpilih());
        btnProses.addActionListener(e -> prosesBayar());
        
        txtUangBayar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { hitungKembalianOtomatis(); }
            @Override
            public void removeUpdate(DocumentEvent e) { hitungKembalianOtomatis(); }
            @Override
            public void changedUpdate(DocumentEvent e) { hitungKembalianOtomatis(); }
        });
        
        loadDropdowns();
        loadKeranjang();
    }

    // --- SUB KLAS DESAIN PANEL TOMBOL ---
    private class PanelPlusMinusOpsi extends JPanel {
        JButton btnMinus = new JButton("—");
        JButton btnPlus = new JButton("+");
        JLabel lblAngka = new JLabel("1", SwingConstants.CENTER);

        public PanelPlusMinusOpsi() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 25));
            setOpaque(false);

            desainTombolMini(btnMinus);
            desainTombolMini(btnPlus);
            
            lblAngka.setFont(new Font("Inter", Font.BOLD, 14));
            lblAngka.setPreferredSize(new Dimension(30, 25));
            lblAngka.setForeground(new Color(55, 65, 81));

            add(btnMinus);
            add(lblAngka);
            add(btnPlus);
        }

        private void desainTombolMini(JButton btn) {
            btn.setFont(new Font("Inter", Font.BOLD, 12));
            btn.setPreferredSize(new Dimension(28, 28));
            btn.setMargin(new Insets(0, 0, 0, 0));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(true);
            btn.setBackground(new Color(243, 244, 246));
            btn.setForeground(new Color(107, 114, 128));
            btn.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 1, true));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    // --- CELL RENDERER ---
    private class PanelPlusMinusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int row, int col) {
            PanelPlusMinusOpsi panel = new PanelPlusMinusOpsi();
            panel.lblAngka.setText(val != null ? val.toString() : "1");
            panel.setBackground(isSel ? table.getSelectionBackground() : Color.WHITE);
            panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(243, 244, 246)));
            return panel;
        }
    }

    // --- PERBAIKAN UTAMA: CELL EDITOR STABIL ---
    private class PanelPlusMinusEditor extends AbstractCellEditor implements TableCellEditor {
        private PanelPlusMinusOpsi panelEdit;
        private int barisAktif;
        private JTable tabelTarget;

        public PanelPlusMinusEditor() {
            panelEdit = new PanelPlusMinusOpsi();

            // Aksi Tombol Minus Klik Inline
            panelEdit.btnMinus.addActionListener(e -> {
                try {
                    String idBarang = tabelTarget.getValueAt(barisAktif, 0).toString();
                    int qtySekarang = Integer.parseInt(panelEdit.lblAngka.getText());
                    double hargaSatuan = Double.parseDouble(tabelTarget.getValueAt(barisAktif, 2).toString());

                    // Amankan & hentikan sesi edit sebelum memanipulasi database/tabel
                    stopCellEditing(); 

                    if (qtySekarang <= 1) {
                        conn.createStatement().executeUpdate("DELETE FROM tb_keranjang WHERE id_barang='" + idBarang + "'");
                    } else {
                        int qtyBaru = qtySekarang - 1;
                        double subtotalBaru = qtyBaru * hargaSatuan;
                        conn.createStatement().executeUpdate("UPDATE tb_keranjang SET jumlah=" + qtyBaru + ", subtotal=" + subtotalBaru + " WHERE id_barang='" + idBarang + "'");
                    }
                    
                    loadKeranjang();      
                    hitungKembalianOtomatis();
                } catch (Exception ex) { ex.printStackTrace(); }
            });

            // Aksi Tombol Plus Klik Inline
            panelEdit.btnPlus.addActionListener(e -> {
                try {
                    String idBarang = tabelTarget.getValueAt(barisAktif, 0).toString();
                    int qtySekarang = Integer.parseInt(panelEdit.lblAngka.getText());
                    double hargaSatuan = Double.parseDouble(tabelTarget.getValueAt(barisAktif, 2).toString());

                    ResultSet rs = conn.createStatement().executeQuery("SELECT stok FROM tb_barang WHERE id_barang='" + idBarang + "'");
                    if (rs.next()) {
                        int stokGudang = rs.getInt("stok");
                        if (qtySekarang >= stokGudang) {
                            JOptionPane.showMessageDialog(FormKasir.this, "Stok tidak mencukupi! Sisa stok gudang: " + stokGudang, "Stok Habis", JOptionPane.WARNING_MESSAGE);
                            cancelCellEditing();
                            return;
                        }
                    }

                    stopCellEditing(); // Amankan & tutup fokus edit sel komponen

                    int qtyBaru = qtySekarang + 1;
                    double subtotalBaru = qtyBaru * hargaSatuan;
                    conn.createStatement().executeUpdate("UPDATE tb_keranjang SET jumlah=" + qtyBaru + ", subtotal=" + subtotalBaru + " WHERE id_barang='" + idBarang + "'");
                    
                    loadKeranjang();
                    hitungKembalianOtomatis();
                } catch (Exception ex) { ex.printStackTrace(); }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object val, boolean isSel, int row, int col) {
            this.barisAktif = row;
            this.tabelTarget = table;
            panelEdit.lblAngka.setText(val != null ? val.toString() : "1");
            panelEdit.setBackground(table.getSelectionBackground());
            return panelEdit;
        }

        @Override
        public Object getCellEditorValue() {
            return panelEdit.lblAngka.getText();
        }
        
        @Override
        public boolean stopCellEditing() {
            fireEditingStopped();
            return super.stopCellEditing();
        }
    }

    private void hitungKembalianOtomatis() {
        try {
            double grandTotal = 0;
            for(int i = 0; i < modelKeranjang.getRowCount(); i++) {
                Object valSub = modelKeranjang.getValueAt(i, 4);
                if (valSub != null) grandTotal += Double.parseDouble(valSub.toString());
            }
            
            String inputBayar = txtUangBayar.getText().trim();
            if (inputBayar.isEmpty()) {
                lblKembalian.setText("Kembalian: Rp 0");
                lblKembalian.setForeground(new Color(107, 114, 128));
                return;
            }
            
            double bayar = Double.parseDouble(inputBayar);
            double kembalian = bayar - grandTotal;
            NumberFormat formatRp = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            
            if (kembalian < 0) {
                lblKembalian.setText("Uang Kurang: " + formatRp.format(Math.abs(kembalian)));
                lblKembalian.setForeground(new Color(220, 38, 38)); 
            } else {
                lblKembalian.setText("Kembalian: " + formatRp.format(kembalian));
                lblKembalian.setForeground(new Color(22, 163, 74)); 
            }
        } catch (Exception e) {
            lblKembalian.setText("Kembalian: Rp 0");
        }
    }

    private void aksiHapusItemTerpilih() {
        if (tblKeranjang.isEditing()) {
            tblKeranjang.getCellEditor().stopCellEditing();
        }
        int row = tblKeranjang.getSelectedRow();
        if (row != -1) {
            String idBarang = tblKeranjang.getValueAt(row, 0).toString();
            String namaBarang = tblKeranjang.getValueAt(row, 1).toString();
            
            int konfirmasi = JOptionPane.showConfirmDialog(this, 
                    "Batalkan pembelian item \"" + namaBarang + "\" dari keranjang?", 
                    "Konfirmasi Batal Beli", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            
            if (konfirmasi == JOptionPane.YES_OPTION) {
                try {
                    conn.createStatement().executeUpdate("DELETE FROM tb_keranjang WHERE id_barang='" + idBarang + "'");
                    loadKeranjang();
                    hitungKembalianOtomatis();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal membatalkan item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris barang pada tabel terlebih dahulu yang ingin dibatalkan!", 
                    "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JLabel buatLabel(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Inter", Font.BOLD, 12));
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
                g2d.setColor(new Color(217, 119, 6)); 
                g2d.setStroke(new BasicStroke(1.5f));  
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false); 
        tf.setBorder(new EmptyBorder(5, 15, 5, 15));
        tf.setFont(new Font("Inter", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(150, 40));
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
        btn.setPreferredSize(new Dimension(110, 35));
        return btn;
    }

    public void setFormBarang(FormBarang fb) { this.formBarang = fb; }
    public void setFormLaporan(FormLaporan fl) { this.formLaporan = fl; }
    public void setFormDashboard(FormDashboard fd) { this.formDashboard = fd; }

    public void loadDropdowns() {
        cmbIdBarang.removeAllItems(); cmbPelanggan.removeAllItems();
        try {
            Statement st = conn.createStatement();
            ResultSet rsB = st.executeQuery("SELECT id_barang, nama_barang FROM tb_barang");
            while(rsB.next()) cmbIdBarang.addItem(rsB.getString("id_barang") + " - " + rsB.getString("nama_barang"));
            ResultSet rsC = conn.createStatement().executeQuery("SELECT id_customer, nama_customer FROM tb_customer");
            while(rsC.next()) cmbPelanggan.addItem(rsC.getString("id_customer") + " - " + rsC.getString("nama_customer"));
        } catch (Exception e) {}
    }

    private void tambahKeKeranjang() {
        if(txtJumlah.getText().isEmpty() || cmbIdBarang.getSelectedItem() == null) return;
        try {
            String id = cmbIdBarang.getSelectedItem().toString().split(" - ")[0];
            int jml = Integer.parseInt(txtJumlah.getText());
            
            ResultSet rs = conn.createStatement().executeQuery("SELECT nama_barang, harga_jual, stok FROM tb_barang WHERE id_barang='"+id+"'");
            if(rs.next()) {
                String namaBarang = rs.getString("nama_barang");
                double harga = rs.getDouble("harga_jual");
                int stok = rs.getInt("stok");
                
                int jmlDiKeranjang = 0;
                ResultSet rsK = conn.createStatement().executeQuery("SELECT jumlah FROM tb_keranjang WHERE id_barang='"+id+"'");
                if(rsK.next()) {
                    jmlDiKeranjang = rsK.getInt("jumlah");
                }
                
                if ((jml + jmlDiKeranjang) > stok) {
                    JOptionPane.showMessageDialog(this, 
                            "Stok tidak mencukupi!\n" + namaBarang + " sisa stok gudang saat ini: " + stok + 
                            "\nJumlah di keranjang Anda: " + jmlDiKeranjang, 
                            "Stok Tidak Mencukupi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                double sub = jml * harga;
                if (jmlDiKeranjang > 0) {
                    conn.createStatement().executeUpdate("UPDATE tb_keranjang SET jumlah = jumlah + "+jml+", subtotal = subtotal + "+sub+" WHERE id_barang='"+id+"'");
                } else {
                    conn.createStatement().executeUpdate("INSERT INTO tb_keranjang (id_barang, jumlah, subtotal) VALUES ('"+id+"', "+jml+", "+sub+")");
                }
                
                loadKeranjang(); 
                txtJumlah.setText("1");
                hitungKembalianOtomatis();
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    public void loadKeranjang() {
        if (tblKeranjang.isEditing()) {
            tblKeranjang.getCellEditor().stopCellEditing();
        }
        modelKeranjang.setRowCount(0);
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT k.id_barang, b.nama_barang, b.harga_jual, k.jumlah, k.subtotal FROM tb_keranjang k JOIN tb_barang b ON k.id_barang = b.id_barang");
            while(rs.next()) modelKeranjang.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getInt(4), rs.getDouble(5)});
        } catch (Exception e) {}
    }

    private void prosesBayar() {
        if (tblKeranjang.isEditing()) {
            tblKeranjang.getCellEditor().stopCellEditing();
        }
        if (modelKeranjang.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cmbPelanggan.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double grandTotal = 0;
            for(int i=0; i<modelKeranjang.getRowCount(); i++) grandTotal += Double.parseDouble(modelKeranjang.getValueAt(i, 4).toString());

            if (txtUangBayar.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan jumlah uang bayar!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double bayar = Double.parseDouble(txtUangBayar.getText());
            if(bayar < grandTotal) { JOptionPane.showMessageDialog(this, "Uang Kurang!"); return; }

            Map<String, Integer> totalBeliPerBarang = new HashMap<>();
            for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
                String idBarang = modelKeranjang.getValueAt(i, 0).toString();
                int jml = Integer.parseInt(modelKeranjang.getValueAt(i, 3).toString());
                totalBeliPerBarang.merge(idBarang, jml, Integer::sum);
            }

            for (Map.Entry<String, Integer> entry : totalBeliPerBarang.entrySet()) {
                ResultSet rsCek = conn.createStatement().executeQuery(
                        "SELECT nama_barang, stok FROM tb_barang WHERE id_barang='" + entry.getKey() + "'");
                if (rsCek.next()) {
                    int stokTersedia = rsCek.getInt("stok");
                    if (stokTersedia < entry.getValue()) {
                        JOptionPane.showMessageDialog(this,
                                "Stok " + rsCek.getString("nama_barang") + " tidak cukup! Tersedia: " + stokTersedia + ", dibutuhkan: " + entry.getValue(),
                                "Transaksi Ditolak", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            PreparedStatement psJual = conn.prepareStatement(
                    "INSERT INTO tb_penjualan (id_customer, tgl_transaksi, total_bayar) VALUES (?, NOW(), ?)",
                    Statement.RETURN_GENERATED_KEYS);
            psJual.setString(1, cmbPelanggan.getSelectedItem().toString().split(" - ")[0]);
            psJual.setDouble(2, grandTotal);
            psJual.executeUpdate();

            ResultSet keyJual = psJual.getGeneratedKeys();
            int idJualBaru = 0;
            if (keyJual.next()) idJualBaru = keyJual.getInt(1);

            for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
                String idBarang = modelKeranjang.getValueAt(i, 0).toString();
                int jml = Integer.parseInt(modelKeranjang.getValueAt(i, 3).toString());
                double subtotal = Double.parseDouble(modelKeranjang.getValueAt(i, 4).toString());
                double hargaSatuan = jml == 0 ? 0 : subtotal / jml;

                PreparedStatement psDetail = conn.prepareStatement(
                        "INSERT INTO tb_detail_penjualan (id_jual, id_barang, harga_satuan, jumlah_beli, subtotal) VALUES (?,?,?,?,?)");
                psDetail.setInt(1, idJualBaru);
                psDetail.setString(2, idBarang);
                psDetail.setDouble(3, hargaSatuan);
                psDetail.setInt(4, jml);
                psDetail.setDouble(5, subtotal);
                psDetail.executeUpdate();
            }

            for (Map.Entry<String, Integer> entry : totalBeliPerBarang.entrySet()) {
                conn.createStatement().executeUpdate(
                        "UPDATE tb_barang SET stok = stok - " + entry.getValue() + " WHERE id_barang='" + entry.getKey() + "'");
            }

            NumberFormat formatRp = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            tampilkanPopupReceiptTransaksi(String.valueOf(idJualBaru), formatRp.format(grandTotal));

            conn.createStatement().executeUpdate("DELETE FROM tb_keranjang");
            txtUangBayar.setText(""); 
            lblKembalian.setText("Kembalian: Rp 0");
            lblKembalian.setForeground(new Color(22, 163, 74));
            
            loadKeranjang();
            loadDropdowns();
            
            if (formBarang != null) formBarang.loadData();
            if (formLaporan != null) formLaporan.loadData();
            if (formDashboard != null) formDashboard.loadDataDashboard();

        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void tampilkanPopupReceiptTransaksi(String idNota, String grandTotal) {
        NumberFormat formatRp = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        JPanel panelNota = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                LinearGradientPaint baseGrad = new LinearGradientPaint(
                    0, 0, w, h,
                    new float[]{0.0f, 1.0f},
                    new Color[]{new Color(241, 245, 249), Color.WHITE}
                );
                g2d.setPaint(baseGrad);
                g2d.fillRect(0, 0, w, h);

                java.awt.geom.Path2D.Double waveAtas = new java.awt.geom.Path2D.Double();
                waveAtas.moveTo(w * 0.2, 0);
                waveAtas.curveTo(w * 0.4, h * 0.06, w * 0.7, h * 0.02, w, h * 0.09);
                waveAtas.lineTo(w, 0);
                waveAtas.closePath();

                LinearGradientPaint orangeGradAtas = new LinearGradientPaint(
                    0, 0, w, (float)(h * 0.09),
                    new float[]{0.0f, 0.8f, 1.0f},
                    new Color[]{
                        new Color(249, 115, 22, 160), 
                        new Color(255, 200, 120, 100), 
                        new Color(255, 255, 255, 0)   
                    }
                );
                g2d.setPaint(orangeGradAtas);
                g2d.fill(waveAtas);

                java.awt.geom.Path2D.Double waveBawah = new java.awt.geom.Path2D.Double();
                waveBawah.moveTo(0, h * 0.91);
                waveBawah.curveTo(w * 0.3, h * 0.89, w * 0.6, h * 0.96, w, h * 0.94);
                waveBawah.lineTo(w, h);
                waveBawah.lineTo(0, h);
                waveBawah.closePath();

                LinearGradientPaint orangeGradBawah = new LinearGradientPaint(
                    0, (float)(h * 0.91), w, h,
                    new float[]{0.0f, 0.5f, 1.0f},
                    new Color[]{
                        new Color(248, 250, 252, 80), 
                        new Color(255, 247, 237, 120), 
                        new Color(249, 115, 22, 120)   
                    }
                );
                g2d.setPaint(orangeGradBawah);
                g2d.fill(waveBawah);

                g2d.dispose();
            }
        };

        panelNota.setLayout(new BoxLayout(panelNota, BoxLayout.Y_AXIS));
        panelNota.setOpaque(false);
        panelNota.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblHeaderTitle = new JLabel("Order Receipt", SwingConstants.CENTER);
        lblHeaderTitle.setFont(new Font("Inter", Font.BOLD, 22));
        lblHeaderTitle.setForeground(new Color(17, 24, 39));
        lblHeaderTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubTitle = new JLabel("TRANSAKSI SUKSES #" + idNota, SwingConstants.CENTER);
        lblSubTitle.setFont(new Font("Inter", Font.PLAIN, 12));
        lblSubTitle.setForeground(new Color(107, 114, 128));
        lblSubTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelNota.add(Box.createRigidArea(new Dimension(0, 10))); 
        panelNota.add(lblHeaderTitle);
        panelNota.add(Box.createRigidArea(new Dimension(0, 4)));
        panelNota.add(lblSubTitle);
        panelNota.add(Box.createRigidArea(new Dimension(0, 20)));
        panelNota.add(new JSeparator(JSeparator.HORIZONTAL));
        panelNota.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel panelItemsWadah = new JPanel();
        panelItemsWadah.setLayout(new BoxLayout(panelItemsWadah, BoxLayout.Y_AXIS));
        panelItemsWadah.setOpaque(false);

        int indeks = 1;
        for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
            JPanel rowItem = new JPanel(new BorderLayout());
            rowItem.setOpaque(false);
            rowItem.setBorder(new EmptyBorder(6, 0, 6, 0));

            JLabel lblNama = new JLabel(indeks + ".  " + modelKeranjang.getValueAt(i, 1).toString());
            lblNama.setFont(new Font("Inter", Font.BOLD, 14));
            lblNama.setForeground(new Color(55, 65, 81));

            int qty = Integer.parseInt(modelKeranjang.getValueAt(i, 3).toString());
            double sub = Double.parseDouble(modelKeranjang.getValueAt(i, 4).toString());
            double hgSatuan = qty == 0 ? 0 : sub / qty;

            JLabel lblDetailKalkulasi = new JLabel("    " + qty + " x " + formatRp.format(hgSatuan));
            lblDetailKalkulasi.setFont(new Font("Inter", Font.PLAIN, 12));
            lblDetailKalkulasi.setForeground(new Color(156, 163, 175));

            JPanel panelTeksKiri = new JPanel();
            panelTeksKiri.setLayout(new BoxLayout(panelTeksKiri, BoxLayout.Y_AXIS));
            panelTeksKiri.setOpaque(false);
            panelTeksKiri.add(lblNama);
            panelTeksKiri.add(lblDetailKalkulasi);

            JLabel lblSubtotalPrice = new JLabel(formatRp.format(sub));
            lblSubtotalPrice.setFont(new Font("Inter", Font.BOLD, 14));
            lblSubtotalPrice.setForeground(new Color(17, 24, 39));

            rowItem.add(panelTeksKiri, BorderLayout.WEST);
            rowItem.add(lblSubtotalPrice, BorderLayout.EAST);
            panelItemsWadah.add(rowItem);
            indeks++;
        }

        panelNota.add(panelItemsWadah);
        panelNota.add(Box.createRigidArea(new Dimension(0, 15)));
        panelNota.add(new JSeparator(JSeparator.HORIZONTAL));
        panelNota.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel rowTotal = new JPanel(new BorderLayout());
        rowTotal.setOpaque(false);
        
        JLabel lblLabelTotal = new JLabel("Total");
        lblLabelTotal.setFont(new Font("Inter", Font.BOLD, 16));
        lblLabelTotal.setForeground(new Color(17, 24, 39));

        JLabel lblValueTotal = new JLabel(grandTotal);
        lblValueTotal.setFont(new Font("Inter", Font.BOLD, 18));
        lblValueTotal.setForeground(new Color(217, 119, 6)); 

        rowTotal.add(lblLabelTotal, BorderLayout.WEST);
        rowTotal.add(lblValueTotal, BorderLayout.EAST);
        panelNota.add(rowTotal);
        panelNota.add(Box.createRigidArea(new Dimension(0, 15))); 

        JScrollPane scrollNota = new JScrollPane(panelNota);
        scrollNota.setBorder(BorderFactory.createEmptyBorder());
        scrollNota.setPreferredSize(new Dimension(430, 500));
        scrollNota.getViewport().setBackground(new Color(241, 245, 249));

        JOptionPane.showOptionDialog(this, scrollNota, "Receipt Card #" + idNota, 
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"CLOSE"}, "CLOSE");
    }
}