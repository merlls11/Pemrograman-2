package sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class FormDashboard extends JPanel {
    private JLabel lblTotalBarang, lblTotalCustomer, lblTotalTransaksi;
    private Connection conn = Koneksi.getKoneksi();
    private FormMenuUtama parentFrame;
    
    // Tambahan komponen untuk widget tabel baru
    private DefaultTableModel modelTerlaris, modelKritis;
    private JTable tblTerlaris, tblKritis;

    public FormDashboard(FormMenuUtama parent) {
        this.parentFrame = parent;
        setLayout(new BorderLayout(0, 25));
        setBackground(new Color(248, 250, 252)); // Background abu-abu sangat muda biar bersih
        setBorder(new EmptyBorder(40, 40, 40, 40));

        // --- Header ---
        JPanel panelHeader = new JPanel(new GridLayout(2, 1, 0, 5));
        panelHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("🚀 Dashboard Analitik Toko");
        lblTitle.setFont(new Font("Inter", Font.BOLD, 28));
        lblTitle.setForeground(new Color(15, 23, 42));
        JLabel lblSubtitle = new JLabel("Pantau performa inventori barang dan transaksi Berkah Jaya secara real-time.");
        lblSubtitle.setFont(new Font("Inter", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        panelHeader.add(lblTitle);
        panelHeader.add(lblSubtitle);

        // --- Cards Panel ---
        JPanel panelCards = new JPanel(new GridLayout(1, 3, 30, 0));
        panelCards.setOpaque(false);

        lblTotalBarang = new JLabel("0 Item");
        lblTotalCustomer = new JLabel("0 Member");
        lblTotalTransaksi = new JLabel("0 Nota");

        JPanel cardBarang = createCard("TOTAL INVENTORI", lblTotalBarang, "📦", new Color(59, 130, 246));
        JPanel cardCustomer = createCard("PELANGGAN AKTIF", lblTotalCustomer, "👥", new Color(16, 185, 129));
        JPanel cardTransaksi = createCard("TRANSAKSI SUKSES", lblTotalTransaksi, "🛒", new Color(249, 115, 22));

        // Listener untuk navigasi klik (Logika navigasi tetap aman)
        cardBarang.addMouseListener(new MouseAdapter() { 
            public void mouseClicked(MouseEvent e) { parentFrame.tampilkanHalaman("pageBarang"); } 
        });
        cardCustomer.addMouseListener(new MouseAdapter() { 
            public void mouseClicked(MouseEvent e) { parentFrame.tampilkanHalaman("pageCustomer"); } 
        });
        cardTransaksi.addMouseListener(new MouseAdapter() { 
            public void mouseClicked(MouseEvent e) { parentFrame.tampilkanHalaman("pageLaporan"); } 
        });

        panelCards.add(cardBarang);
        panelCards.add(cardCustomer);
        panelCards.add(cardTransaksi);

        JPanel wrapperAtas = new JPanel(new BorderLayout(0, 35));
        wrapperAtas.setOpaque(false);
        wrapperAtas.add(panelHeader, BorderLayout.NORTH);
        wrapperAtas.add(panelCards, BorderLayout.CENTER);

        add(wrapperAtas, BorderLayout.NORTH);
        
        // --- PANEL WIDGET BARU (GRID BAWAH KIRI & KANAN) ---
        JPanel panelWidgetBawah = new JPanel(new GridLayout(1, 2, 25, 0));
        panelWidgetBawah.setOpaque(false);

        // Sektor Tabel 1: Produk Terlaris
        modelTerlaris = new DefaultTableModel(new Object[]{"Nama Barang", "Unit Terjual"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTerlaris = new JTable(modelTerlaris);
        KomponenDesainHelper.dekorasiTabelElegan(tblTerlaris);
        JPanel cardTerlaris = buatCardWidgetBawah("🔥 5 Produk Terlaris (Top Selling)", new JScrollPane(tblTerlaris));

        // Sektor Tabel 2: Notifikasi Stok Kritis
        modelKritis = new DefaultTableModel(new Object[]{"Nama Barang", "Sisa Stok"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKritis = new JTable(modelKritis);
        KomponenDesainHelper.dekorasiTabelElegan(tblKritis);
        JPanel cardKritis = buatCardWidgetBawah("⚠️ Notifikasi Stok Kritis (< 5)", new JScrollPane(tblKritis));

        panelWidgetBawah.add(cardTerlaris);
        panelWidgetBawah.add(cardKritis);
        
        add(panelWidgetBawah, BorderLayout.CENTER);
        
        // Memuat seluruh hitungan data statistik dan database widget
        loadDataDashboard();
    }

    // Method pembuat Card yang sudah dimodernisasi dengan sudut melengkung
    private JPanel createCard(String title, JLabel lblValue, String iconText, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(20, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40); 
                super.paintComponent(g);
                g2d.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(226, 232, 240)); 
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
                g2d.dispose();
            }
        };
        
        card.setOpaque(false); 
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(25, 25, 25, 25)); 

        JLabel lblIcon = new JLabel(iconText);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40)); 

        JPanel panelText = new JPanel(new GridLayout(2, 1, 0, 5));
        panelText.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 12));
        lblTitle.setForeground(accentColor); 

        lblValue.setFont(new Font("Inter", Font.BOLD, 32));
        lblValue.setForeground(new Color(15, 23, 42));

        panelText.add(lblTitle);
        panelText.add(lblValue);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(panelText, BorderLayout.CENTER);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(244, 244, 245)); 
                card.repaint();
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE); 
                card.repaint();
            }
        });
        
        return card;
    }

    // FUNGSI BARU: Kontainer Card Khusus Widget Tabel Bawah bertema Mustard Oranye Anda
    private JPanel buatCardWidgetBawah(String judul, JScrollPane scrollComponent) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                super.paintComponent(g);
                g2d.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(217, 119, 6)); // Garis Border Mustard Oranye konsisten
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font("Inter", Font.BOLD, 14));
        lblJudul.setForeground(new Color(15, 23, 42));
        lblJudul.setBorder(new EmptyBorder(0, 0, 10, 0));

        scrollComponent.setBorder(BorderFactory.createEmptyBorder());
        scrollComponent.getViewport().setBackground(Color.WHITE);

        card.add(lblJudul, BorderLayout.NORTH);
        card.add(scrollComponent, BorderLayout.CENTER);
        return card;
    }

    // FUNGSI BARU: Memuat Kombinasi Seluruh Hitungan Data Atas & Tabel Bawah Sekaligus
    public void loadDataDashboard() {
        // 1. Eksekusi hitung data statistik atas (Kode asli dipertahankan)
        kalkulasiDataStatistik();

        // 2. Kosongkan baris tabel sebelum dimuat ulang
        modelTerlaris.setRowCount(0);
        modelKritis.setRowCount(0);

        if (conn == null) return;
        try (Statement st = conn.createStatement()) {
            // Tarik data 5 Produk Terlaris berdasarkan kuantitas transaksi sukses
            String sqlTerlaris = "SELECT b.nama_barang, SUM(d.jumlah_beli) AS total_terjual " +
                                 "FROM tb_detail_penjualan d " +
                                 "JOIN tb_barang b ON d.id_barang = b.id_barang " +
                                 "GROUP BY b.id_barang " +
                                 "ORDER BY total_terjual DESC LIMIT 5";
            ResultSet rsT = st.executeQuery(sqlTerlaris);
            while (rsT.next()) {
                modelTerlaris.addRow(new Object[]{
                    rsT.getString("nama_barang"), 
                    rsT.getInt("total_terjual") + " Unit"
                });
            }

            // Tarik data barang yang kritis di bawah kualifikasi 5 pcs
            String sqlKritis = "SELECT nama_barang, stok FROM tb_barang WHERE stok < 5 ORDER BY stok ASC";
            ResultSet rsK = st.executeQuery(sqlKritis);
            while (rsK.next()) {
                modelKritis.addRow(new Object[]{
                    rsK.getString("nama_barang"), 
                    rsK.getInt("stok") + " Pcs"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method penghitungan statistik asli (Tetap jalan normal bawaan)
    public void kalkulasiDataStatistik() {
        if (conn == null) return;
        try (Statement st = conn.createStatement()) {
            ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM tb_barang");
            if(rs1.next()) lblTotalBarang.setText(rs1.getInt(1) + " Item");
            
            ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM tb_customer");
            if(rs2.next()) lblTotalCustomer.setText(rs2.getInt(1) + " Member");
            
            ResultSet rs3 = st.executeQuery("SELECT COUNT(*) FROM tb_penjualan");
            if(rs3.next()) lblTotalTransaksi.setText(rs3.getInt(1) + " Nota");
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
}