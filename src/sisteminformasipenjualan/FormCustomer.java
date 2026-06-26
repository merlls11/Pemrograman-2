package sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormCustomer extends JPanel {
    private Connection conn = Koneksi.getKoneksi();
    private DefaultTableModel modelTabel;
    private JTable tblCustomer;
    private JTextField txtId, txtNama, txtAlamat, txtTelp;
    private JLabel lblErrorNama; 
    private JButton btnSimpan, btnHapus, btnReset;
    private boolean isEditMode = false;
    private FormKasir formKasir;
    
    // Properti baru untuk manajemen hak akses role
    private String roleUser = ""; 

    public FormCustomer() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setBackground(new Color(248, 250, 252));

        JLabel title = new JLabel("👥 Database Manajemen Member Pelanggan");
        title.setFont(new Font("Inter", Font.BOLD, 26));
        title.setForeground(new Color(15, 23, 42));
        add(title, BorderLayout.NORTH);

        modelTabel = new DefaultTableModel(new Object[]{"ID Pelanggan", "Nama Lengkap Member", "Alamat Domisili", "Nomor Telepon"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblCustomer = new JTable(modelTabel);
        KomponenDesainHelper.dekorasiTabelElegan(tblCustomer);
        add(new JScrollPane(tblCustomer), BorderLayout.CENTER);

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
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.weightx = 1.0; 
        gbc.anchor = GridBagConstraints.WEST;

        txtId = buatTextFieldOval(10); txtId.setEditable(false); txtId.setBackground(new Color(241, 245, 249));
        txtNama = buatTextFieldOval(15); 
        txtAlamat = buatTextFieldOval(15); 
        txtTelp = buatTextFieldOval(10);
        
        lblErrorNama = new JLabel("hanya bisa diisi huruf saja");
        lblErrorNama.setForeground(Color.RED);
        lblErrorNama.setFont(new Font("Inter", Font.ITALIC, 10));
        lblErrorNama.setVisible(false);

        txtNama.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isLetter(c) && !Character.isWhitespace(c) && c != '\b') {
                    evt.consume();
                    lblErrorNama.setVisible(true);
                } else { lblErrorNama.setVisible(false); }
            }
        });

        btnSimpan = buatTombolOval("Simpan", new Color(249, 115, 22), Color.WHITE);
        btnHapus = buatTombolOval("Hapus", new Color(239, 68, 68), Color.WHITE);
        btnReset = buatTombolOval("Clear", new Color(226, 232, 240), Color.BLACK);

        // Baris label
        gbc.gridy = 0;
        gbc.gridx = 0; panelForm.add(buatLabel("ID Cust:"), gbc);
        gbc.gridx = 1; panelForm.add(buatLabel("Nama:"), gbc);
        gbc.gridx = 2; panelForm.add(buatLabel("Alamat:"), gbc);
        gbc.gridx = 3; panelForm.add(buatLabel("No Telp:"), gbc);

        // Baris field
        gbc.gridy = 1;
        gbc.gridx = 0; panelForm.add(txtId, gbc);
        gbc.gridx = 1; panelForm.add(txtNama, gbc);
        gbc.gridx = 2; panelForm.add(txtAlamat, gbc);
        gbc.gridx = 3; panelForm.add(txtTelp, gbc);

        gbc.gridy = 2; gbc.gridx = 1;
        panelForm.add(lblErrorNama, gbc);

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
        tblCustomer.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { pilihBaris(); }
        });
        loadData();
    }

    // --- METODE BARU: Mengunci akses hapus data untuk Kasir ---
    public void setRole(String role) {
        this.roleUser = role;
        if (role.equalsIgnoreCase("Kasir")) {
            btnHapus.setEnabled(false); // Blokir tombol hapus total
            btnHapus.setToolTipText("Hak akses terbatas! Kasir tidak diizinkan menghapus data pelanggan.");
        }
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

    private JLabel buatLabel(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setFont(new Font("Inter", Font.BOLD, 12));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
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
        btn.setBackground(bg); btn.setForeground(fg); btn.setFont(new Font("Inter", Font.BOLD, 12));
        btn.setContentAreaFilled(false); btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 35));
        return btn;
    }

    public void setFormKasir(FormKasir fk) { this.formKasir = fk; }
    
    private void buatIdOtomatis() {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id_customer FROM tb_customer ORDER BY id_customer DESC LIMIT 1");
            if (rs.next()) {
                String idTerakhir = rs.getString("id_customer");
                int angka = Integer.parseInt(idTerakhir.substring(3)) + 1;
                txtId.setText(String.format("CST%03d", angka));
            } else { txtId.setText("CST001"); }
        } catch (Exception e) { txtId.setText("CST001"); }
    }
    
    public void loadData() {
        modelTabel.setRowCount(0);
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM tb_customer");
            while(rs.next()) modelTabel.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
        } catch(Exception e) {}
        if(!isEditMode) buatIdOtomatis();
    }
    
    private void pilihBaris() {
        int b = tblCustomer.getSelectedRow();
        if(b != -1) {
            txtId.setText(modelTabel.getValueAt(b, 0).toString());
            txtNama.setText(modelTabel.getValueAt(b, 1).toString());
            txtAlamat.setText(modelTabel.getValueAt(b, 2).toString());
            txtTelp.setText(modelTabel.getValueAt(b, 3).toString());
            isEditMode = true;
            btnSimpan.setText("Update");
            btnSimpan.setBackground(new Color(234, 88, 12));
            lblErrorNama.setVisible(false);
        }
    }
    
    private void aksiSimpanAtauEdit() {
        if(txtId.getText().trim().isEmpty() || txtNama.getText().trim().isEmpty() 
                || txtAlamat.getText().trim().isEmpty() || txtTelp.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua data (Nama, Alamat, No Telp) wajib diisi!", "Data Belum Lengkap", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if(!isEditMode) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO tb_customer VALUES(?,?,?,?)");
                ps.setString(1, txtId.getText()); ps.setString(2, txtNama.getText()); 
                ps.setString(3, txtAlamat.getText()); ps.setString(4, txtTelp.getText()); 
                ps.executeUpdate();
            } else {
                PreparedStatement ps = conn.prepareStatement("UPDATE tb_customer SET nama_customer=?, alamat=?, telepon=? WHERE id_customer=?");
                ps.setString(1, txtNama.getText()); ps.setString(2, txtAlamat.getText()); 
                ps.setString(3, txtTelp.getText()); ps.setString(4, txtId.getText()); 
                ps.executeUpdate();
            }
            loadData(); resetForm();
            if(formKasir != null) formKasir.loadDropdowns();
        } catch(Exception e) { JOptionPane.showMessageDialog(this, e.getMessage()); }
    }
    
    private void aksiHapus() {
        if(txtId.getText().isEmpty()) return;
        if(JOptionPane.showConfirmDialog(this, "Hapus data member ini?", "Hapus", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_customer WHERE id_customer=?");
                ps.setString(1, txtId.getText()); ps.executeUpdate();
                loadData(); resetForm();
                if(formKasir != null) formKasir.loadDropdowns();
            } catch(Exception e) {}
        }
    }
    
    private void resetForm() {
        txtNama.setText(""); txtAlamat.setText(""); txtTelp.setText("");
        isEditMode = false; btnSimpan.setText("Simpan"); btnSimpan.setBackground(new Color(249, 115, 22));
        lblErrorNama.setVisible(false);
        buatIdOtomatis();
    }
}