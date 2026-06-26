package sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FormLogin extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private Connection conn = Koneksi.getKoneksi();

    public FormLogin() {
        setTitle("Login - Berkah Jaya");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // --- BACKGROUND DENGAN GRADASI ORANYE, PUTIH, HITAM ---
        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                float[] dist = {0.0f, 0.5f, 1.0f};
                Color[] colors = {new Color(249, 115, 22), Color.WHITE, Color.BLACK};
                LinearGradientPaint gp = new LinearGradientPaint(0, 0, 0, getHeight(), dist, colors);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(bgPanel);

        // --- CARD PANEL MELENGKUNG (OVAL CORNERS) ---
        JPanel cardPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                // Bikin sudut melengkung 40 pixel
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 40, 40);
                g2d.dispose();
            }
        };
        cardPanel.setOpaque(false); // Transparan biar yang kelihatan cuma area ovalnya
        cardPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel lblIcon = new JLabel("👋", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        
        JLabel lblTitle = new JLabel("Selamat Datang", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 24));
        lblTitle.setForeground(new Color(15, 23, 42));
        
        JLabel lblSub = new JLabel("Masuk untuk mengelola toko", SwingConstants.CENTER);
        lblSub.setFont(new Font("Inter", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        lblSub.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Inter", Font.BOLD, 12));
        lblUser.setForeground(new Color(71, 85, 105));

        // --- CUSTOM TEXTFIELD MELENGKUNG (OVAL) ---
        txtUsername = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30); // Sudut 30 pixel
                super.paintComponent(g);
                g2d.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(226, 232, 240));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2d.dispose();
            }
        };
        dekorasiInput(txtUsername);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Inter", Font.BOLD, 12));
        lblPass.setForeground(new Color(71, 85, 105));
        lblPass.setBorder(new EmptyBorder(10, 0, 0, 0));

        // --- CUSTOM PASSWORD FIELD MELENGKUNG (OVAL) ---
        txtPassword = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                super.paintComponent(g);
                g2d.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(226, 232, 240));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2d.dispose();
            }
        };
        dekorasiInput(txtPassword);

        // --- CUSTOM BUTTON MELENGKUNG (OVAL) ---
        btnLogin = new JButton("Masuk Sistem") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40); // Sudut 40 pixel (bentuk pill)
                super.paintComponent(g);
                g2d.dispose();
            }
        };
        btnLogin.setPreferredSize(new Dimension(300, 45));
        btnLogin.setBackground(new Color(249, 115, 22)); 
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Inter", Font.BOLD, 14));
        btnLogin.setContentAreaFilled(false); // Wajib agar custom paint jalan
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btnLogin.setBackground(new Color(234, 88, 12)); }
            public void mouseExited(MouseEvent evt) { btnLogin.setBackground(new Color(249, 115, 22)); }
            public void mousePressed(MouseEvent evt) { btnLogin.setBackground(new Color(194, 65, 12)); }
            public void mouseReleased(MouseEvent evt) { btnLogin.setBackground(new Color(234, 88, 12)); }
        });

        btnLogin.addActionListener(e -> prosesLogin());

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) prosesLogin();
            }
        });

        // Menyusun komponen ke dalam panel
        gbc.gridy = 0; cardPanel.add(lblIcon, gbc);
        gbc.gridy = 1; cardPanel.add(lblTitle, gbc);
        gbc.gridy = 2; cardPanel.add(lblSub, gbc);
        gbc.gridy = 3; gbc.insets = new Insets(2, 0, 2, 0); cardPanel.add(lblUser, gbc);
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 10, 0); cardPanel.add(txtUsername, gbc);
        gbc.gridy = 5; gbc.insets = new Insets(2, 0, 2, 0); cardPanel.add(lblPass, gbc);
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 25, 0); cardPanel.add(txtPassword, gbc);
        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 0, 0); cardPanel.add(btnLogin, gbc);

        bgPanel.add(cardPanel);
    }

    private void dekorasiInput(JTextField textField) {
        textField.setPreferredSize(new Dimension(300, 45));
        textField.setFont(new Font("Inter", Font.PLAIN, 14));
        textField.setBackground(new Color(248, 250, 252));
        textField.setOpaque(false); // Wajib false agar custom background oval muncul
        textField.setBorder(new EmptyBorder(5, 20, 5, 20)); // Jarak teks biar agak ke tengah
    }

    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            DialogNotifikasi.tampilkan(this, DialogNotifikasi.Tipe.PERINGATAN,
                    "Peringatan", "Username dan Password wajib diisi!");
            return;
        }

        try {
            String sql = "SELECT * FROM tb_user WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String namaLengkap = rs.getString("nama_lengkap");
                String level = rs.getString("level");

                DialogNotifikasi.tampilkan(this, DialogNotifikasi.Tipe.SUKSES,
                        "Berhasil", "Login Sukses! Selamat Datang, " + namaLengkap);

                FormMenuUtama menuUtama = new FormMenuUtama(namaLengkap, level);
                menuUtama.setVisible(true);
                this.dispose();
            } else {
                DialogNotifikasi.tampilkan(this, DialogNotifikasi.Tipe.ERROR,
                        "Akses Ditolak", "Username atau Password Salah!");
                txtPassword.setText("");
                txtUsername.requestFocus();
            }
        } catch (Exception ex) {
            DialogNotifikasi.tampilkan(this, DialogNotifikasi.Tipe.ERROR,
                    "Error", "Gagal terhubung database: " + ex.getMessage());
        }
    }
}