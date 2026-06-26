package sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.NumberFormat; 
import java.util.Locale;       

public class FormLaporan extends JPanel {
    private JTable tblLaporan;
    private DefaultTableModel model;
    private Connection conn = Koneksi.getKoneksi();
    
    // Komponen filter periode waktu
    private JComboBox<String> cmbPeriode;
    private JTextField txtTglMulai, txtTglSelesai;
    private JButton btnCari, btnVoid; // Tambah btnVoid
    private JPanel panelKustomTanggal;

    // Komponen Rekap
    private JLabel lblTotalPendapatan, lblTotalTransaksi, lblBarangTerjual;

    private FormDashboard formDashboard; // Tambahan objek refresh dashboard

    public FormLaporan() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setBackground(new Color(248, 250, 252));

        // --- NORTH: HEADER, FILTER & REKAP CARDS ---
        JPanel panelAtasWadah = new JPanel();
        panelAtasWadah.setLayout(new BoxLayout(panelAtasWadah, BoxLayout.Y_AXIS));
        panelAtasWadah.setOpaque(false);

        JLabel title = new JLabel("📊 Riwayat Transaksi Penjualan");
        title.setFont(new Font("Inter", Font.BOLD, 26));
        title.setForeground(new Color(15, 23, 42));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panelAtasWadah.add(title);

        // Bar Filter Waktu
        JPanel panelFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                super.paintComponent(g);
                g2d.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(226, 232, 240));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                g2d.dispose();
            }
        };
        panelFilter.setOpaque(false);
        panelFilter.setBorder(new EmptyBorder(10, 15, 10, 15));
        panelFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JLabel lblFilter = new JLabel("Periode:");
        lblFilter.setFont(new Font("Inter", Font.BOLD, 13));
        lblFilter.setForeground(new Color(71, 85, 105));

        cmbPeriode = new JComboBox<>(new String[]{"Semua", "7 Hari Terakhir", "30 Hari Terakhir", "1 Tahun Terakhir", "Kustom Tanggal"});
        cmbPeriode.setFont(new Font("Inter", Font.PLAIN, 13));
        cmbPeriode.setPreferredSize(new Dimension(160, 35));

        panelKustomTanggal = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelKustomTanggal.setOpaque(false);
        panelKustomTanggal.setVisible(false);

        txtTglMulai = buatTextFieldOvalTgl("YYYY-MM-DD");
        txtTglSelesai = buatTextFieldOvalTgl("YYYY-MM-DD");

        panelKustomTanggal.add(new JLabel("Dari:"));
        panelKustomTanggal.add(txtTglMulai);
        panelKustomTanggal.add(new JLabel(" Sampai:"));
        panelKustomTanggal.add(txtTglSelesai);

        btnCari = buatTombolOvalMundur("Filter", new Color(249, 115, 22), Color.WHITE);
        btnVoid = buatTombolOvalMundur("Void Nota", new Color(220, 38, 38), Color.WHITE); // Inisialisasi tombol baru warna merah

        panelFilter.add(lblFilter);
        panelFilter.add(cmbPeriode);
        panelFilter.add(panelKustomTanggal);
        panelFilter.add(btnCari);
        panelFilter.add(btnVoid); // Tambah tombol void ke bar filter
        panelFilter.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelAtasWadah.add(panelFilter);
        panelAtasWadah.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel Rekap (3 Card)
        JPanel panelRekap = new JPanel(new GridLayout(1, 3, 20, 0));
        panelRekap.setOpaque(false);
        panelRekap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        lblTotalPendapatan = new JLabel("Rp 0");
        lblTotalTransaksi = new JLabel("0 Transaksi");
        lblBarangTerjual = new JLabel("0 Pcs");

        panelRekap.add(buatCardRekap("💰 Total Omset Pendapatan", lblTotalPendapatan));
        panelRekap.add(buatCardRekap("📦 Total Volume Transaksi", lblTotalTransaksi));
        panelRekap.add(buatCardRekap("🛒 Jumlah Barang Terjual", lblBarangTerjual));
        panelRekap.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panelAtasWadah.add(panelRekap);
        add(panelAtasWadah, BorderLayout.NORTH);

        // --- CENTER: TABEL DATA ---
        model = new DefaultTableModel(new Object[]{"ID Nota", "Tanggal", "Customer", "Total Bayar"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tblLaporan = new JTable(model);
        KomponenDesainHelper.dekorasiTabelElegan(tblLaporan);
        
        JScrollPane scrollPane = new JScrollPane(tblLaporan);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        
        // --- LISTENERS ---
        tblLaporan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblLaporan.getSelectedRow();
                    if (row != -1) {
                        String idNota = model.getValueAt(row, 0).toString();
                        String total = model.getValueAt(row, 3).toString();
                        tampilkanDetailRiwayat(idNota, total);
                    }
                }
            }
        });

        cmbPeriode.addActionListener(e -> {
            boolean isKustom = cmbPeriode.getSelectedItem().toString().equals("Kustom Tanggal");
            panelKustomTanggal.setVisible(isKustom);
            panelKustomTanggal.getParent().revalidate();
            panelKustomTanggal.getParent().repaint();
        });

        btnCari.addActionListener(e -> loadData());
        btnVoid.addActionListener(e -> aksiVoidTransaksi()); // Tambah trigger aksi pembatalan
        
        loadData();
    }

    public void setFormDashboard(FormDashboard fd) {
        this.formDashboard = fd;
    }

    // FUNGSI BARU: Logika Pembatalan Transaksi (VOID) & Pengembalian Stok Otomatis
    private void aksiVoidTransaksi() {
        int row = tblLaporan.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris nota transaksi yang ingin dibatalkan!", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String idNota = model.getValueAt(row, 0).toString();
        int konfirmasi = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin membatalkan (VOID) Transaksi #" + idNota + "?\nStok barang akan dikembalikan otomatis dan omset akan berkurang.", 
                "Konfirmasi Void Transaksi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi == JOptionPane.YES_OPTION) {
            try {
                conn.setAutoCommit(false); // Memulai sistem transaksi ACID safe database

                // 1. Ambil rincian id_barang dan jumlah dari tabel detail penjualan terlebih dahulu
                String sqlDetail = "SELECT id_barang, jumlah_beli FROM tb_detail_penjualan WHERE id_jual = ?";
                PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                psDetail.setInt(1, Integer.parseInt(idNota));
                ResultSet rs = psDetail.executeQuery();

                // 2. Kembalikan/tambahkan kembali sisa stok tersebut ke tabel utama tb_barang
                String sqlUpdateStok = "UPDATE tb_barang SET stok = stok + ? WHERE id_barang = ?";
                PreparedStatement psUpdateStok = conn.prepareStatement(sqlUpdateStok);
                while (rs.next()) {
                    psUpdateStok.setInt(1, rs.getInt("jumlah_beli"));
                    psUpdateStok.setString(2, rs.getString("id_barang"));
                    psUpdateStok.addBatch();
                }
                psUpdateStok.executeBatch();

                // 3. Hapus data rincian dari tb_detail_penjualan terlebih dahulu guna menghindari restriksi foreign key
                String sqlDelDetail = "DELETE FROM tb_detail_penjualan WHERE id_jual = ?";
                PreparedStatement psDelDetail = conn.prepareStatement(sqlDelDetail);
                psDelDetail.setInt(1, Integer.parseInt(idNota));
                psDelDetail.executeUpdate();

                // 4. Hapus entitas induk utama dari tb_penjualan
                String sqlDelJual = "DELETE FROM tb_penjualan WHERE id_jual = ?";
                PreparedStatement psDelJual = conn.prepareStatement(sqlDelJual);
                psDelJual.setInt(1, Integer.parseInt(idNota));
                psDelJual.executeUpdate();

                conn.commit(); // Eksekusi menyeluruh secara bersamaan
                conn.setAutoCommit(true);

                JOptionPane.showMessageDialog(this, "Transaksi #" + idNota + " berhasil dibatalkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                
                // Segarkan muatan data visual di seluruh halaman program terkait
                loadData();
                if (formDashboard != null) formDashboard.loadDataDashboard();

            } catch (Exception ex) {
                try { conn.rollback(); conn.setAutoCommit(true); } catch (Exception e) {}
                JOptionPane.showMessageDialog(this, "Gagal membatalkan transaksi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadData() {
        model.setRowCount(0);
        NumberFormat formatRp = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String pilihan = cmbPeriode.getSelectedItem() != null ? cmbPeriode.getSelectedItem().toString() : "Semua";
        
        String sqlTabel = "SELECT p.id_jual, p.tgl_transaksi, c.nama_customer, p.total_bayar " +
                          "FROM tb_penjualan p " +
                          "JOIN tb_customer c ON p.id_customer = c.id_customer ";

        String sqlRekapOmset = "SELECT SUM(total_bayar) AS omset, COUNT(id_jual) AS transaksi FROM tb_penjualan p ";
        String sqlRekapBarang = "SELECT SUM(d.jumlah_beli) AS total_qty FROM tb_detail_penjualan d " +
                                "JOIN tb_penjualan p ON d.id_jual = p.id_jual ";

        String kondisiFilter = "";
        switch (pilihan) {
            case "7 Hari Terakhir":
                kondisiFilter = "WHERE p.tgl_transaksi >= DATE_SUB(NOW(), INTERVAL 7 DAY) ";
                break;
            case "30 Hari Terakhir":
                kondisiFilter = "WHERE p.tgl_transaksi >= DATE_SUB(NOW(), INTERVAL 30 DAY) ";
                break;
            case "1 Tahun Terakhir":
                kondisiFilter = "WHERE p.tgl_transaksi >= DATE_SUB(NOW(), INTERVAL 1 YEAR) ";
                break;
            case "Kustom Tanggal":
                kondisiFilter = "WHERE DATE(p.tgl_transaksi) BETWEEN ? AND ? ";
                break;
        }
        
        try {
            PreparedStatement psTabel = conn.prepareStatement(sqlTabel + kondisiFilter + "ORDER BY p.id_jual DESC");
            if (pilihan.equals("Kustom Tanggal")) {
                if (txtTglMulai.getText().equals("YYYY-MM-DD") || txtTglSelesai.getText().equals("YYYY-MM-DD")) {
                    JOptionPane.showMessageDialog(this, "Harap isi rentang tanggal pencarian dengan format YYYY-MM-DD!", "Input Kosong", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                psTabel.setString(1, txtTglMulai.getText().trim());
                psTabel.setString(2, txtTglSelesai.getText().trim());
            }
            
            ResultSet rsTabel = psTabel.executeQuery();
            while(rsTabel.next()) {
                model.addRow(new Object[]{
                    rsTabel.getInt("id_jual"),
                    rsTabel.getString("tgl_transaksi"), 
                    rsTabel.getString("nama_customer"), 
                    formatRp.format(rsTabel.getDouble("total_bayar"))
                });
            }

            PreparedStatement psOmset = conn.prepareStatement(sqlRekapOmset + kondisiFilter);
            if (pilihan.equals("Kustom Tanggal")) {
                psOmset.setString(1, txtTglMulai.getText().trim());
                psOmset.setString(2, txtTglSelesai.getText().trim());
            }
            ResultSet rsOmset = psOmset.executeQuery();
            if (rsOmset.next()) {
                double omset = rsOmset.getDouble("omset");
                int totalTransaksi = rsOmset.getInt("transaksi");
                lblTotalPendapatan.setText(formatRp.format(omset));
                lblTotalTransaksi.setText(totalTransaksi + " Transaksi");
            }

            PreparedStatement psBarang = conn.prepareStatement(sqlRekapBarang + kondisiFilter);
            if (pilihan.equals("Kustom Tanggal")) {
                psBarang.setString(1, txtTglMulai.getText().trim());
                psBarang.setString(2, txtTglSelesai.getText().trim());
            }
            ResultSet rsBarang = psBarang.executeQuery();
            if (rsBarang.next()) {
                int totalQty = rsBarang.getInt("total_qty");
                lblBarangTerjual.setText(totalQty + " Pcs");
            }

        } catch (Exception e) { 
            JOptionPane.showMessageDialog(this, "Gagal memproses rekap laporan: " + e.getMessage());
        }
    }

    private void tampilkanDetailRiwayat(String idNota, String grandTotal) {
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
        
        JLabel lblSubTitle = new JLabel("DETAIL TRANSAKSI #" + idNota, SwingConstants.CENTER);
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

        try {
            String sql = "SELECT b.nama_barang, d.harga_satuan, d.jumlah_beli, d.subtotal " +
                         "FROM tb_detail_penjualan d " +
                         "JOIN tb_barang b ON d.id_barang = b.id_barang " +
                         "WHERE d.id_jual = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(idNota));
            ResultSet rs = ps.executeQuery();
            
            int indeks = 1;
            while(rs.next()) {
                JPanel rowItem = new JPanel(new BorderLayout());
                rowItem.setOpaque(false);
                rowItem.setBorder(new EmptyBorder(6, 0, 6, 0));

                JLabel lblNama = new JLabel(indeks + ".  " + rs.getString("nama_barang"));
                lblNama.setFont(new Font("Inter", Font.BOLD, 14));
                lblNama.setForeground(new Color(55, 65, 81));

                JLabel lblDetailKalkulasi = new JLabel("    " + rs.getInt("jumlah_beli") + " x " + formatRp.format(rs.getDouble("harga_satuan")));
                lblDetailKalkulasi.setFont(new Font("Inter", Font.PLAIN, 12));
                lblDetailKalkulasi.setForeground(new Color(156, 163, 175));

                JPanel panelTeksKiri = new JPanel();
                panelTeksKiri.setLayout(new BoxLayout(panelTeksKiri, BoxLayout.Y_AXIS));
                panelTeksKiri.setOpaque(false);
                panelTeksKiri.add(lblNama);
                panelTeksKiri.add(lblDetailKalkulasi);

                JLabel lblSubtotalPrice = new JLabel(formatRp.format(rs.getDouble("subtotal")));
                lblSubtotalPrice.setFont(new Font("Inter", Font.BOLD, 14));
                lblSubtotalPrice.setForeground(new Color(17, 24, 39));

                rowItem.add(panelTeksKiri, BorderLayout.WEST);
                rowItem.add(lblSubtotalPrice, BorderLayout.EAST);
                panelItemsWadah.add(rowItem);
                indeks++;
            }
        } catch (Exception e) {
            panelItemsWadah.add(new JLabel("Gagal memuat detail barang."));
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

    private JPanel buatCardRekap(String judul, JLabel lblNilai) {
        JPanel card = new JPanel(new BorderLayout(5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                super.paintComponent(g);
                g2d.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(217, 119, 6)); 
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font("Inter", Font.BOLD, 12));
        lblJudul.setForeground(new Color(100, 116, 139));

        lblNilai.setFont(new Font("Inter", Font.BOLD, 20));
        lblNilai.setForeground(new Color(15, 23, 42));

        card.add(lblJudul, BorderLayout.NORTH);
        card.add(lblNilai, BorderLayout.CENTER);
        return card;
    }

    private JTextField buatTextFieldOvalTgl(String placeholder) {
        JTextField tf = new JTextField(placeholder, 10) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2d.setColor(new Color(217, 119, 6)); 
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false);
        tf.setBorder(new EmptyBorder(4, 10, 4, 10));
        tf.setFont(new Font("Inter", Font.PLAIN, 12));
        tf.setPreferredSize(new Dimension(110, 35));
        tf.setForeground(Color.GRAY);
        
        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(Color.GRAY);
                }
            }
        });
        return tf;
    }

    private JButton buatTombolOvalMundur(String teks, Color bg, Color fg) {
        JButton btn = new JButton(teks) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        btn.setBackground(bg); btn.setForeground(fg); btn.setFont(new Font("Inter", Font.BOLD, 12));
        btn.setContentAreaFilled(false); btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 35));
        return btn;
    }
}