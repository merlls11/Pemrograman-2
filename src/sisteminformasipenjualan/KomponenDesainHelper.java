package sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class KomponenDesainHelper {
    // Palet Warna Modern
    public static final Color WARNA_HITAM_ABU = new Color(15, 23, 42);
    public static final Color WARNA_ORANGE_TUA = new Color(249, 115, 22);
    public static final Color WARNA_ORANGE_HOVER = new Color(234, 88, 12);
    public static final Color WARNA_BG_SOFT = new Color(248, 250, 252);

    // Palet warna status (selaras tema oranye/mustard)
    public static final Color WARNA_STATUS_AMAN_BG = new Color(220, 252, 231);
    public static final Color WARNA_STATUS_AMAN_FG = new Color(22, 163, 74);
    public static final Color WARNA_STATUS_MENIPIS_BG = new Color(255, 237, 213);
    public static final Color WARNA_STATUS_MENIPIS_FG = new Color(217, 119, 6);
    public static final Color WARNA_STATUS_HABIS_BG = new Color(254, 226, 226);
    public static final Color WARNA_STATUS_HABIS_FG = new Color(220, 38, 38);

    // PERBAIKAN: Menggunakan warna Oranye/Mustard Muda yang LEBIH PEKAT dan JELAS (tidak samar putih)
    private static final Color WARNA_BARIS_ORANGE = new Color(255, 228, 185);

    /**
     * Tabel dengan gaya modern: Font Inter, Baris Tinggi, Rounded Selection.
     * Warna baris dibuat selang-seling antara Putih dan Oranye Muda/Mustard Pekat.
     */
    public static void dekorasiTabelElegan(JTable tabel) {
        tabel.setFont(new Font("Inter", Font.PLAIN, 13));
        tabel.setRowHeight(40);
        tabel.setIntercellSpacing(new Dimension(0, 0));
        tabel.setShowGrid(false); // Hilangkan garis kaku
        tabel.setSelectionBackground(new Color(254, 215, 170));
        tabel.setSelectionForeground(Color.BLACK);
        JTableHeader header = tabel.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(71, 85, 105));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(226, 232, 240)));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        
        tabel.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, val, isSel, hasFocus, row, col);
                if (!isSel) {
                    // Baris genap tetap Putih murni, baris ganjil menggunakan warna oranye pekat yang baru
                    c.setBackground(row % 2 == 0 ? Color.WHITE : WARNA_BARIS_ORANGE);
                }
                setBorder(new EmptyBorder(0, 15, 0, 15));
                return c;
            }
        });
    }

    /**
     * PERBAIKAN LOGIKA STATUS STOK:
     * 0 atau kurang = Habis, di bawah 20 = Menipis, 20 ke atas = Aman.
     */
    public static String statusStok(int stok) {
        if (stok <= 0) return "Habis";
        if (stok < 20) return "Menipis";
        return "Aman";
    }

    /**
     * Renderer kolom Stok dengan badge pill berwarna sesuai status.
     */
    public static TableCellRenderer buatRendererBadgeStok() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                int stok;
                try { 
                    String cleanVal = val.toString().replaceAll("[^0-9-]", "");
                    stok = Integer.parseInt(cleanVal); 
                } catch (Exception e) { 
                    stok = 0; 
                }
                String status = statusStok(stok);

                Color bg, fg;
                switch (status) {
                    case "Aman":
                        bg = WARNA_STATUS_AMAN_BG; fg = WARNA_STATUS_AMAN_FG; break;
                    case "Menipis":
                        bg = WARNA_STATUS_MENIPIS_BG; fg = WARNA_STATUS_MENIPIS_FG; break;
                    default:
                        bg = WARNA_STATUS_HABIS_BG; fg = WARNA_STATUS_HABIS_FG; break;
                }

                JPanel wadah = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                wadah.setOpaque(true);
                
                // Menyesuaikan background wadah badge agar pas dengan warna baris tabel yang baru
                wadah.setBackground(isSel ? table.getSelectionBackground() : (row % 2 == 0 ? Color.WHITE : WARNA_BARIS_ORANGE));
                wadah.setBorder(new EmptyBorder(0, 15, 0, 15));

                JLabel badge = new JLabel(stok + " · " + status) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(getBackground());
                        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                        g2d.dispose();
                        super.paintComponent(g);
                    }
                };
                badge.setOpaque(false);
                badge.setBackground(bg);
                badge.setForeground(fg);
                badge.setFont(new Font("Inter", Font.BOLD, 12));
                badge.setBorder(new EmptyBorder(4, 14, 4, 14));
                wadah.add(badge);
                return wadah;
            }
        };
    }

    /**
     * Tombol aksi dengan gaya Pill-Shaped (Oval).
     */
    public static JButton buatTombolAksi(String teks) {
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
        btn.setBackground(WARNA_ORANGE_TUA);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Inter", Font.BOLD, 12));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(160, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Efek Hover
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) { btn.setBackground(WARNA_ORANGE_HOVER); }
            @Override
            public void mouseExited(MouseEvent evt) { btn.setBackground(WARNA_ORANGE_TUA); }
        });

        return btn;
    }

    /**
     * Toolbar tab filter (mis. "Semua / Aman / Menipis / Habis")
     */
    public static JPanel buatTabFilter(String[] label, Consumer<String> onPilih) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        panel.setOpaque(false);

        JLabel[] tombolTab = new JLabel[label.length];
        for (int i = 0; i < label.length; i++) {
            final int idx = i;
            JLabel tab = new JLabel(label[i]) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                }
            };
            tab.setFont(new Font("Inter", idx == 0 ? Font.BOLD : Font.PLAIN, 14));
            tab.setForeground(idx == 0 ? WARNA_HITAM_ABU : new Color(148, 163, 184));
            tab.setBorder(new EmptyBorder(0, 0, 8, 0));
            if (idx == 0) {
                tab.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, WARNA_ORANGE_TUA),
                        new EmptyBorder(0, 0, 6, 0)));
            }
            tab.setCursor(new Cursor(Cursor.HAND_CURSOR));
            tombolTab[i] = tab;
            panel.add(tab);
        }

        for (int i = 0; i < tombolTab.length; i++) {
            final int idx = i;
            tombolTab[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for (int j = 0; j < tombolTab.length; j++) {
                        boolean aktif = (j == idx);
                        tombolTab[j].setFont(new Font("Inter", aktif ? Font.BOLD : Font.PLAIN, 14));
                        tombolTab[j].setForeground(aktif ? WARNA_HITAM_ABU : new Color(148, 163, 184));
                        if (aktif) {
                            tombolTab[j].setBorder(BorderFactory.createCompoundBorder(
                                    BorderFactory.createMatteBorder(0, 0, 2, 0, WARNA_ORANGE_TUA),
                                    new EmptyBorder(0, 0, 6, 0)));
                        } else {
                            tombolTab[j].setBorder(new EmptyBorder(0, 0, 8, 0));
                        }
                    }
                    onPilih.accept(label[idx]);
                }
            });
        }

        return panel;
    }

    /**
     * Header form minimalis.
     */
    public static JPanel buatHeaderForm(String judul) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(judul);
        lbl.setFont(new Font("Inter", Font.BOLD, 24));
        lbl.setForeground(WARNA_HITAM_ABU);
        panel.add(lbl);
        return panel;
    }
}