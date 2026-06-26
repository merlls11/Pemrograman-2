package sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class FormUser extends JPanel {
    private Connection conn = Koneksi.getKoneksi();
    private DefaultTableModel modelTabel;
    private JTable tblUser;
    private JTextField txtUser, txtPass, txtNama;
    private JComboBox<String> cmbLevel;
    private JButton btnSimpan, btnHapus, btnReset;
    private int selectedIdUser = 0;
    private boolean isEditMode = false;
    
    // Tambahan label untuk pesan eror dibawah kotak teks
    private JLabel lblErrorUser, lblErrorNama;

    public FormUser() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setBackground(new Color(248, 250, 252));

        JLabel title = new JLabel("⚙️ Otoritas Hak Akses Akun Petugas");
        title.setFont(new Font("Inter", Font.BOLD, 26));
        title.setForeground(new Color(15, 23, 42));
        add(title, BorderLayout.NORTH);

        modelTabel = new DefaultTableModel(new Object[]{"ID Akun", "Username Sistem", "Nama Lengkap Petugas", "Otoritas Level"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblUser = new JTable(modelTabel);
        KomponenDesainHelper.dekorasiTabelElegan(tblUser);
        add(new JScrollPane(tblUser), BorderLayout.CENTER);

        // --- FORM PANEL (Custom Rounded Card, disamakan dengan FormBarang) ---
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
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtUser = buatTextFieldOval(12);
        txtPass = buatTextFieldOval(12);
        txtNama = buatTextFieldOval(15);
        cmbLevel = new JComboBox<>(new String[]{"Admin", "Kasir"});
        cmbLevel.setFont(new Font("Inter", Font.PLAIN, 13));
        cmbLevel.setPreferredSize(new Dimension(220, 40));

        // Inisialisasi Label Notifikasi Eror Warna Merah
        lblErrorUser = new JLabel("Hanya bisa diisi huruf!");
        lblErrorUser.setFont(new Font("Inter", Font.BOLD, 11));
        lblErrorUser.setForeground(new Color(239, 68, 68)); // Merah cerah
        lblErrorUser.setVisible(false);

        lblErrorNama = new JLabel("Hanya bisa diisi huruf!");
        lblErrorNama.setFont(new Font("Inter", Font.BOLD, 11));
        lblErrorNama.setForeground(new Color(239, 68, 68)); // Merah cerah
        lblErrorNama.setVisible(false);

        // Pasang fungsi pemfilter ketikan huruf otomatis
        pasangValidasiHanyaHuruf(txtUser, lblErrorUser);
        pasangValidasiHanyaHuruf(txtNama, lblErrorNama);

        btnSimpan = buatTombolOval("Simpan", new Color(249, 115, 22), Color.WHITE);
        btnHapus = buatTombolOval("Hapus", new Color(239, 68, 68), Color.WHITE);
        btnReset = buatTombolOval("Clear", new Color(226, 232, 240), Color.BLACK);

        // Judul Form di dalam Card
        JLabel lblFormTitle = new JLabel("📝 Form Input Data Petugas");
        lblFormTitle.setFont(new Font("Inter", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(15, 23, 42));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        gbc.insets = new Insets(0, 8, 20, 8);
        panelForm.add(lblFormTitle, gbc);

        // Label di atas, field di bawahnya
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 0, 8);
        gbc.gridy = 1;
        gbc.gridx = 0; panelForm.add(buatLabel("Username:"), gbc);
        gbc.gridx = 1; panelForm.add(buatLabel("Password:"), gbc);
        gbc.gridx = 2; panelForm.add(buatLabel("Nama:"), gbc);
        gbc.gridx = 3; panelForm.add(buatLabel("Level:"), gbc);

        gbc.insets = new Insets(0, 8, 4, 8);
        gbc.gridy = 2;
        gbc.gridx = 0; panelForm.add(txtUser, gbc);
        gbc.gridx = 1; panelForm.add(txtPass, gbc);
        gbc.gridx = 2; panelForm.add(txtNama, gbc);
        gbc.gridx = 3; panelForm.add(cmbLevel, gbc);

        // Menambahkan Notifikasi Teks Merah tepat di baris bawah kotak input terkait
        gbc.insets = new Insets(0, 12, 4, 8);
        gbc.gridy = 3;
        gbc.gridx = 0; panelForm.add(lblErrorUser, gbc);
        gbc.gridx = 2; panelForm.add(lblErrorNama, gbc);

        JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelAksi.setOpaque(false);
        panelAksi.add(btnSimpan); panelAksi.add(btnHapus); panelAksi.add(btnReset);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.setOpaque(false);
        panelBawah.add(panelForm, BorderLayout.CENTER);
        panelBawah.add(panelAksi, BorderLayout.SOUTH);
        add(panelBawah, BorderLayout.SOUTH);

        btnSimpan.addActionListener(e -> aksiSimpanAtauEdit());
        btnHapus.addActionListener(e -> aksiHapus());
        btnReset.addActionListener(e -> resetForm());
        tblUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { pilihBaris(); }
        });
        loadData();
    }

    /** Logika KeyListener untuk mencegat angka/simbol dan mengatur visibility pesan eror */
    private void pasangValidasiHanyaHuruf(JTextField textField, JLabel labelError) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                
                // Izinkan huruf, spasi, backspace, dan delete
                if (Character.isLetter(c) || Character.isSpaceChar(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                    labelError.setVisible(false);
                } else {
                    // Blokir angka/simbol agar tidak masuk ke kotak input
                    e.consume(); 
                    labelError.setVisible(true);
                }
                
                // Refresh susunan layout agar label rapi saat muncul/menghilang
                if (labelError.getParent() != null) {
                    labelError.getParent().revalidate();
                    labelError.getParent().repaint();
                }
            }
        });
    }

    // --- HELPER UI ---
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

                g2d.setColor(new Color(100, 116, 139)); 
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
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        btn.setBackground(bg); btn.setForeground(fg); btn.setFont(new Font("Inter", Font.BOLD, 12));
        btn.setContentAreaFilled(false); btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 35));
        return btn;
    }

    // --- FUNGSI ASLI ---
    public void loadData() {
        modelTabel.setRowCount(0);
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id_user, username, nama_lengkap, level FROM tb_user");
            while(rs.next()) modelTabel.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)});
        } catch(Exception e) {}
    }

    private void pilihBaris() {
        int b = tblUser.getSelectedRow();
        if(b != -1) {
            selectedIdUser = Integer.parseInt(modelTabel.getValueAt(b, 0).toString());
            txtUser.setText(modelTabel.getValueAt(b, 1).toString());
            txtNama.setText(modelTabel.getValueAt(b, 2).toString());
            cmbLevel.setSelectedItem(modelTabel.getValueAt(b, 3).toString());
            txtPass.setText("");
            isEditMode = true;
            btnSimpan.setText("Update");
            btnSimpan.setBackground(new Color(234, 88, 12));
            
            // Sembunyikan eror jika baris dipilih
            lblErrorUser.setVisible(false);
            lblErrorNama.setVisible(false);
            this.revalidate();
            this.repaint();
        }
    }

    private void aksiSimpanAtauEdit() {
        if(txtUser.getText().isEmpty() || txtNama.getText().isEmpty()) return;
        try {
            if(!isEditMode) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO tb_user VALUES(null,?,?,?,?)");
                ps.setString(1, txtUser.getText()); ps.setString(2, txtPass.getText()); ps.setString(3, txtNama.getText()); ps.setString(4, cmbLevel.getSelectedItem().toString());
                ps.executeUpdate();
            } else {
                PreparedStatement ps = conn.prepareStatement("UPDATE tb_user SET username=?, password=?, nama_lengkap=?, level=? WHERE id_user=?");
                ps.setString(1, txtUser.getText()); ps.setString(2, txtPass.getText()); ps.setString(3, txtNama.getText()); ps.setString(4, cmbLevel.getSelectedItem().toString()); ps.setInt(5, selectedIdUser);
                ps.executeUpdate();
            }
            loadData(); resetForm();
        } catch(Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }

    private void aksiHapus() {
        if(selectedIdUser == 0) return;
        if(JOptionPane.showConfirmDialog(this, "Hapus petugas ini?", "Hapus", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_user WHERE id_user=?");
                ps.setInt(1, selectedIdUser); ps.executeUpdate();
                loadData(); resetForm();
            } catch(Exception e) {}
        }
    }

    private void resetForm() {
        txtUser.setText(""); txtPass.setText(""); txtNama.setText("");
        selectedIdUser = 0; isEditMode = false;
        btnSimpan.setText("Simpan"); btnSimpan.setBackground(new Color(249, 115, 22));
        
        // Sembunyikan notifikasi eror saat form di-clear
        lblErrorUser.setVisible(false);
        lblErrorNama.setVisible(false);
        
        // Refresh panel FormUser saat ini
        this.revalidate();
        this.repaint();
    }
}