package sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class FormMenuUtama extends JFrame {
    private CardLayout cardLayout;
    private JPanel panelWadahKonten;
    private JButton btnDashboard, btnKasir, btnBarang, btnCustomer, btnUser, btnLaporan, btnLogOut;
    private String roleUser;
    private String namaUserAktif;

    // Warna Sidebar Modern (Tema Oranye/Mustard, gaya frosted-card mengambang)
    private final Color SIDEBAR_BG = new Color(252, 247, 240);      // krem lembut, dasar sidebar
    private final Color CARD_BG = new Color(255, 255, 255, 235);    // kartu putih semi-transparan
    private final Color HOVER_COLOR = new Color(255, 237, 213);     // mustard sangat muda, untuk hover
    private final Color ACTIVE_COLOR = new Color(217, 119, 6);      // mustard tua, untuk pill aktif
    private final Color TEKS_NONAKTIF = new Color(120, 113, 108);   // abu-coklat lembut

    private boolean sidebarMengecil = false;
    private final int LEBAR_SIDEBAR_FULL = 270;
    private final int LEBAR_SIDEBAR_KECIL = 90;

    private JPanel sidebar;
    private JPanel panelMenuWadah;
    private JLabel lblAvatar;
    private JLabel lblSapaan;
    private JLabel lblNamaUser;
    private JButton btnToggleCollapse;

    private final List<ItemMenu> daftarItemMenu = new ArrayList<>();

    private FormDashboard pageDashboard;
    private FormKasir pageKasir;
    private FormBarang pageBarang;
    private FormCustomer pageCustomer;
    private FormLaporan pageLaporan;
    private FormUser pageUser;

    private static class ItemMenu {
        JButton tombol;
        String ikon;
        String label;
        String namaHalaman;
        ItemMenu(JButton tombol, String ikon, String label, String namaHalaman) {
            this.tombol = tombol; this.ikon = ikon; this.label = label; this.namaHalaman = namaHalaman;
        }
    }

    public FormMenuUtama(String namaUser, String levelUser) {
        this.roleUser = levelUser;
        this.namaUserAktif = namaUser;
        setTitle("Sistem Penjualan - Berkah Jaya");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- SIDEBAR DENGAN LUKISAN GRADASI ABSTRAK (ORANYE, PUTIH, ABU-ABU) ---
        sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // 1. Base Background
                LinearGradientPaint baseGrad = new LinearGradientPaint(
                    0, 0, w, h,
                    new float[]{0.0f, 1.0f},
                    new Color[]{new Color(241, 245, 249), Color.WHITE}
                );
                g2d.setPaint(baseGrad);
                g2d.fillRect(0, 0, w, h);

                // 2. Lapisan Gelombang Atas
                java.awt.geom.Path2D.Double waveAtas = new java.awt.geom.Path2D.Double();
                waveAtas.moveTo(w * 0.3, 0);
                waveAtas.curveTo(w * 0.5, h * 0.08, w * 0.8, h * 0.02, w, h * 0.12);
                waveAtas.lineTo(w, 0);
                waveAtas.closePath();

                LinearGradientPaint orangeGradAtas = new LinearGradientPaint(
                    0, 0, w, (float)(h * 0.12),
                    new float[]{0.0f, 0.8f, 1.0f},
                    new Color[]{
                        new Color(249, 115, 22, 180), 
                        new Color(255, 200, 120, 120), 
                        new Color(255, 255, 255, 0)   
                    }
                );
                g2d.setPaint(orangeGradAtas);
                g2d.fill(waveAtas);

                // 3. Lapisan Gelombang Bawah
                java.awt.geom.Path2D.Double waveBawah = new java.awt.geom.Path2D.Double();
                waveBawah.moveTo(0, h * 0.8);
                waveBawah.curveTo(w * 0.3, h * 0.78, w * 0.6, h * 0.88, w, h * 0.85);
                waveBawah.lineTo(w, h);
                waveBawah.lineTo(0, h);
                waveBawah.closePath();

                LinearGradientPaint orangeGradBawah = new LinearGradientPaint(
                    0, (float)(h * 0.8), w, h,
                    new float[]{0.0f, 0.5f, 1.0f},
                    new Color[]{
                        new Color(248, 250, 252, 100), 
                        new Color(255, 247, 237, 150), 
                        new Color(249, 115, 22, 140)   
                    }
                );
                g2d.setPaint(orangeGradBawah);
                g2d.fill(waveBawah);

                g2d.dispose();
            }
        };

        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false); 
        sidebar.setPreferredSize(new Dimension(LEBAR_SIDEBAR_FULL, getHeight()));
        sidebar.setBorder(new EmptyBorder(25, 16, 25, 16));

        // --- HEADER ---
        JPanel panelHeader = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 28, 28);
                g2d.dispose();
            }
        };
        panelHeader.setOpaque(false);
        panelHeader.setBorder(new EmptyBorder(18, 16, 18, 12));
        panelHeader.setMaximumSize(new Dimension(LEBAR_SIDEBAR_FULL, 90));

        JPanel panelIdentitas = new JPanel();
        panelIdentitas.setLayout(new BoxLayout(panelIdentitas, BoxLayout.X_AXIS));
        panelIdentitas.setOpaque(false);

        lblAvatar = buatAvatarBulat(namaUser);
        panelIdentitas.add(lblAvatar);
        panelIdentitas.add(Box.createRigidArea(new Dimension(12, 0)));

        JPanel panelTeksIdentitas = new JPanel();
        panelTeksIdentitas.setLayout(new BoxLayout(panelTeksIdentitas, BoxLayout.Y_AXIS));
        panelTeksIdentitas.setOpaque(false);
        lblSapaan = new JLabel("Selamat Datang 👋");
        lblSapaan.setFont(new Font("Inter", Font.PLAIN, 11));
        lblSapaan.setForeground(TEKS_NONAKTIF);
        lblNamaUser = new JLabel(namaUser);
        lblNamaUser.setFont(new Font("Inter", Font.BOLD, 15));
        lblNamaUser.setForeground(new Color(41, 37, 36));
        panelTeksIdentitas.add(lblSapaan);
        panelTeksIdentitas.add(lblNamaUser);
        panelIdentitas.add(panelTeksIdentitas);

        btnToggleCollapse = new JButton("‹");
        btnToggleCollapse.setFont(new Font("Inter", Font.BOLD, 16));
        btnToggleCollapse.setForeground(TEKS_NONAKTIF);
        btnToggleCollapse.setContentAreaFilled(false);
        btnToggleCollapse.setBorderPainted(false);
        btnToggleCollapse.setFocusPainted(false);
        btnToggleCollapse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggleCollapse.addActionListener(e -> toggleCollapseSidebar());

        panelHeader.add(panelIdentitas, BorderLayout.CENTER);
        panelHeader.add(btnToggleCollapse, BorderLayout.EAST);

        sidebar.add(panelHeader);
        sidebar.add(Box.createRigidArea(new Dimension(0, 24)));

        JLabel lblLabelMenu = new JLabel("MENU");
        lblLabelMenu.setFont(new Font("Inter", Font.BOLD, 11));
        lblLabelMenu.setForeground(new Color(180, 170, 160));
        lblLabelMenu.setBorder(new EmptyBorder(0, 10, 10, 0));
        lblLabelMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblLabelMenu);

        // --- WADAH ITEM MENU ---
        panelMenuWadah = new JPanel();
        panelMenuWadah.setLayout(new BoxLayout(panelMenuWadah, BoxLayout.Y_AXIS));
        panelMenuWadah.setOpaque(false);
        panelMenuWadah.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnDashboard = new JButton();
        btnKasir     = new JButton();
        btnBarang    = new JButton();
        btnCustomer  = new JButton();
        btnLaporan   = new JButton();
        btnUser      = new JButton();

        daftarItemMenu.add(new ItemMenu(btnDashboard, "▦", "Dashboard", "pageDashboard"));
        daftarItemMenu.add(new ItemMenu(btnKasir, "🛒", "Transaksi", "pageKasir"));
        daftarItemMenu.add(new ItemMenu(btnBarang, "📦", "Stok Barang", "pageBarang"));
        daftarItemMenu.add(new ItemMenu(btnCustomer, "👥", "Pelanggan", "pageCustomer"));
        daftarItemMenu.add(new ItemMenu(btnLaporan, "📊", "Riwayat", "pageLaporan"));
        if ("Admin".equalsIgnoreCase(roleUser)) {
            daftarItemMenu.add(new ItemMenu(btnUser, "⚙️", "Manajemen User", "pageUser"));
        }

        bangunUlangTampilanMenu();
        sidebar.add(panelMenuWadah);

        sidebar.add(Box.createVerticalGlue());

        // Logout
        btnLogOut = new JButton("⏻  Log Out");
        btnLogOut.setMaximumSize(new Dimension(240, 48));
        btnLogOut.setBackground(new Color(254, 226, 226));
        btnLogOut.setForeground(new Color(190, 40, 40));
        btnLogOut.setFont(new Font("Inter", Font.BOLD, 13));
        btnLogOut.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogOut.setFocusPainted(false);
        btnLogOut.setBorderPainted(false);
        btnLogOut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogOut.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Keluar aplikasi?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == 0) {
                new FormLogin().setVisible(true); dispose();
            }
        });
        sidebar.add(btnLogOut);

        // --- INSTANSIASI HALAMAN ---
        pageDashboard = new FormDashboard(this);
        pageKasir = new FormKasir();
        pageBarang = new FormBarang();
        pageCustomer = new FormCustomer();
        pageLaporan = new FormLaporan();
        pageUser = new FormUser();

        // Menyambungkan Relasi Antar Form
        pageBarang.setFormKasir(pageKasir);
        pageCustomer.setFormKasir(pageKasir);
        pageKasir.setFormBarang(pageBarang);
        pageKasir.setFormLaporan(pageLaporan);
        pageKasir.setFormDashboard(pageDashboard);
        pageLaporan.setFormDashboard(pageDashboard);

        // SYNC SECTOR: Langsung set hak akses role di awal inisialisasi aplikasi
        pageBarang.setRole(roleUser);
        pageCustomer.setRole(roleUser);

        cardLayout = new CardLayout();
        panelWadahKonten = new JPanel(cardLayout);
        panelWadahKonten.add(pageDashboard, "pageDashboard");
        panelWadahKonten.add(pageKasir, "pageKasir");
        panelWadahKonten.add(pageBarang, "pageBarang");
        panelWadahKonten.add(pageCustomer, "pageCustomer");
        panelWadahKonten.add(pageLaporan, "pageLaporan");
        panelWadahKonten.add(pageUser, "pageUser");

        add(sidebar, BorderLayout.WEST);
        add(panelWadahKonten, BorderLayout.CENTER);

        setupNavigation();
    }

    private JLabel buatAvatarBulat(String nama) {
        String inisial = nama == null || nama.trim().isEmpty() ? "?" : nama.trim().substring(0, 1).toUpperCase();
        JLabel avatar = new JLabel(inisial, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(ACTIVE_COLOR);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setOpaque(false);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Inter", Font.BOLD, 18));
        avatar.setPreferredSize(new Dimension(44, 44));
        avatar.setMinimumSize(new Dimension(44, 44));
        avatar.setMaximumSize(new Dimension(44, 44));
        return avatar;
    }

    private void toggleCollapseSidebar() {
        sidebarMengecil = !sidebarMengecil;
        int lebarBaru = sidebarMengecil ? LEBAR_SIDEBAR_KECIL : LEBAR_SIDEBAR_FULL;
        sidebar.setPreferredSize(new Dimension(lebarBaru, getHeight()));

        lblSapaan.setVisible(!sidebarMengecil);
        lblNamaUser.setVisible(!sidebarMengecil);
        btnToggleCollapse.setText(sidebarMengecil ? "›" : "‹");

        bangunUlangTampilanMenu();

        sidebar.revalidate();
        sidebar.repaint();
    }

    private void bangunUlangTampilanMenu() {
        panelMenuWadah.removeAll();
        for (ItemMenu item : daftarItemMenu) {
            dekorasiTombolMenu(item.tombol, item.ikon, item.label);
            panelMenuWadah.add(item.tombol);
            panelMenuWadah.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        panelMenuWadah.revalidate();
        panelMenuWadah.repaint();
    }

    private void dekorasiTombolMenu(JButton btn, String ikon, String label) {
        btn.setText(sidebarMengecil ? ikon : ("  " + ikon + "    " + label));
        btn.setHorizontalAlignment(sidebarMengecil ? SwingConstants.CENTER : SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(sidebarMengecil ? 56 : 240, 48));
        btn.setPreferredSize(new Dimension(sidebarMengecil ? 56 : 240, 48));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Inter", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);

        boolean aktif = Boolean.TRUE.equals(btn.getClientProperty("aktif"));
        btn.setForeground(aktif ? Color.WHITE : TEKS_NONAKTIF);

        for (MouseListener ml : btn.getMouseListeners()) btn.removeMouseListener(ml);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!Boolean.TRUE.equals(btn.getClientProperty("aktif"))) {
                    btn.putClientProperty("hover", true);
                    btn.repaint();
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.putClientProperty("hover", false);
                btn.repaint();
            }
        });
    }

    private void setupNavigation() {
        for (ItemMenu item : daftarItemMenu) {
            final JButton btn = item.tombol;
            btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
                @Override
                public void paint(Graphics g, JComponent c) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean aktif = Boolean.TRUE.equals(btn.getClientProperty("aktif"));
                    boolean hover = Boolean.TRUE.equals(btn.getClientProperty("hover"));
                    if (aktif) {
                        g2d.setColor(ACTIVE_COLOR);
                        g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), c.getHeight(), c.getHeight());
                    } else if (hover) {
                        g2d.setColor(HOVER_COLOR);
                        g2d.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), c.getHeight(), c.getHeight());
                    }
                    g2d.dispose();
                    super.paint(g, c);
                }
            });
        }

        btnDashboard.addActionListener(e -> setActive(btnDashboard, "pageDashboard"));
        btnKasir.addActionListener(e -> setActive(btnKasir, "pageKasir"));
        btnBarang.addActionListener(e -> setActive(btnBarang, "pageBarang"));
        btnCustomer.addActionListener(e -> setActive(btnCustomer, "pageCustomer"));
        btnLaporan.addActionListener(e -> setActive(btnLaporan, "pageLaporan"));
        if ("Admin".equalsIgnoreCase(roleUser)) {
            btnUser.addActionListener(e -> setActive(btnUser, "pageUser"));
        }

        setActive(btnDashboard, "pageDashboard");
    }

    private void setActive(JButton btnTerpilih, String pageName) {
        for (ItemMenu item : daftarItemMenu) {
            boolean aktif = (item.tombol == btnTerpilih);
            item.tombol.putClientProperty("aktif", aktif);
            item.tombol.putClientProperty("hover", false);
            item.tombol.setForeground(aktif ? Color.WHITE : TEKS_NONAKTIF);
            item.tombol.repaint();
        }
        
        // SYNC SECTOR: Kirim ulang proteksi hak akses disaat navigasi tombol diklik manual
        if (pageName.equals("pageBarang")) pageBarang.setRole(roleUser);
        if (pageName.equals("pageCustomer")) pageCustomer.setRole(roleUser);
        
        cardLayout.show(panelWadahKonten, pageName);
    }

    // --- SYNC SECTOR: Perbaikan Metode Tampilkan Halaman agar Menerapkan Proteksi Role ---
    public void tampilkanHalaman(String namaPage) {
        // Cari tombol sidebar yang sesuai dengan namaPage untuk disinkronkan status keaktifannya
        JButton targetButton = null;
        for (ItemMenu item : daftarItemMenu) {
            boolean cocok = item.namaHalaman.equals(namaPage);
            item.tombol.putClientProperty("aktif", cocok);
            item.tombol.setForeground(cocok ? Color.WHITE : TEKS_NONAKTIF);
            item.tombol.repaint();
            if (cocok) targetButton = item.tombol;
        }

        // Terapkan paksa restriksi hak akses role user saat berpindah halaman via kode
        if (namaPage.equals("pageBarang")) pageBarang.setRole(roleUser);
        if (namaPage.equals("pageCustomer")) pageCustomer.setRole(roleUser);

        cardLayout.show(panelWadahKonten, namaPage);
    }
}