import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author User
 */
public class PengelolaanKontakFrame extends javax.swing.JFrame {
     private DefaultTableModel model; // Model untuk tabel kontak
     
      private void kosongkanInputKolom() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText(""); // Kosongkan field pencarian
        jComboBox1.setSelectedIndex(0); // Pilih item pertama di ComboBox
      }
    /**
     * Creates new form PengelolaanKontakFrame
     */
    public PengelolaanKontakFrame() {
        initComponents();
        buatDatabase(); // Membuat database dan tabel kontak jika belum ada

        // Nonaktifkan tombol Edit dan Hapus di awal
        jButton2.setEnabled(false);
        jButton3.setEnabled(false);
        
        // Cek koneksi database saat aplikasi dimulai
        if (connect() == null) {
            JOptionPane.showMessageDialog(this, "Gagal terhubung ke database. Aplikasi akan ditutup.");
            System.exit(0); // Keluar dari aplikasi jika koneksi gagal
        }
        
         // Tambahkan item ke ComboBox kategori
         jComboBox1.addItem("Keluarga");
         jComboBox1.addItem("Teman");
         jComboBox1.addItem("Rekan Kerja");
         
         // Inisialisasi model tabel
        model = new DefaultTableModel(new String[]{"ID", "Nama", "Nomor Telepon", "Kategori"}, 0);
        jTable1.setModel(model);
        
        // Sembunyikan kolom ID
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);
        
        // Kosongkan teks awal pada kolom input
         jTextField1.setText("Masukkan Nama");
         jTextField2.setText("Masukkan Nomor Telepon");
        
         
         // Menambahkan FocusListener untuk txtNama dan txtNomorTelpon
       jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
       
       jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField2FocusGained(evt);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField2FocusLost(evt);
            }
        });
    
       // Tambahkan KeyListener ke txtNomorTelpon untuk menerima angka saja
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                // Periksa jika karakter bukan angka atau bukan backspace
                if (!Character.isDigit(c) && c != '\b') {
                    evt.consume(); // Abaikan karakter yang tidak valid
                    JOptionPane.showMessageDialog(null, "Hanya boleh memasukkan angka!", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        //Menambahkan Placeholder di txtCari
         jTextField3.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if ( jTextField3.getText().equals("Cari Kontak")) {
                     jTextField3.setText(""); // Hapus placeholder
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if ( jTextField3.getText().isEmpty()) {
                     jTextField3.setText("Cari Kontak"); // Kembalikan placeholder jika kosong
                }
            }
        });
         
          // Muat data kontak ke dalam tabel
        tampilkanKontak();
        
        // Tambahkan ActionListener untuk tombol CRUD
        setupActionListeners();

        // Tambahkan event listener untuk mendeteksi klik pada baris tabel
        jTable1.getSelectionModel().addListSelectionListener(event -> {
            int selectedRow = jTable1.getSelectedRow();
            // Aktifkan atau nonaktifkan tombol berdasarkan seleksi tabel
            jButton2.setEnabled(selectedRow >= 0);
            jButton3.setEnabled(selectedRow >= 0);
            if (selectedRow >= 0) {
                // Ambil data dari tabel dan isi ke kolom input
                jTextField1.setText(model.getValueAt(selectedRow, 1).toString());
                jTextField2.setText(model.getValueAt(selectedRow, 2).toString());
                jComboBox1.setSelectedItem(model.getValueAt(selectedRow, 3).toString());
            }
        });
    }
     private void setupActionListeners() {
        // Tombol Tambah
        jButton1.addActionListener(e -> {
            String nama = jTextField1.getText();
            String nomorTelepon = jTextField2.getText();
            String kategori = jComboBox1.getSelectedItem().toString();

            // Validasi input
            if (nama.isEmpty() || nomorTelepon.isEmpty() || nama.equals("Masukkan Nama") || nomorTelepon.equals("Masukkan Nomor Telepon")) {
                JOptionPane.showMessageDialog(this, "Nama dan Nomor Telepon tidak boleh kosong.");
                return;
            }
            
            // Validasi nomor telepon hanya angka
            if (!nomorTelepon.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Nomor telepon hanya boleh berisi angka.");
                return;
            }
            
            tambahKontak(nama, nomorTelepon, kategori);
            tampilkanKontak(); // Refresh tabel setelah menambah kontak
            kosongkanInputKolom();
        });
        
         // Tombol Edit
        jButton2.addActionListener(e -> {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) model.getValueAt(selectedRow, 0); // Ambil ID dari kolom pertama
                String nama = jTextField1.getText();
                String nomorTelepon = jTextField2.getText();
                String kategori = jComboBox1.getSelectedItem().toString();
                editKontak(id, nama, nomorTelepon, kategori);
                tampilkanKontak(); // Refresh tabel setelah mengedit kontak
                kosongkanInputKolom();
            } else {
                JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin diedit.");
            }
        });
        
          // Tombol Hapus
         jButton3.addActionListener(e -> {
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) model.getValueAt(selectedRow, 0); // Ambil ID dari kolom pertama

                int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus kontak ini?",
                        "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    hapusKontak(id);
                    tampilkanKontak(); // Refresh tabel setelah menghapus kontak
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin dihapus.");
            }
        });
        
         // Tombol Cari
        jButton5.addActionListener(e -> {
            String keyword = jTextField3.getText().trim(); // Ambil input pencarian dan hilangkan spasi
            if (keyword.isEmpty() || keyword.equals("Masukkan Kata Kunci")) {
                JOptionPane.showMessageDialog(this, "Masukkan kata kunci untuk pencarian.");
                return;
            }
            cariKontak(keyword); // Lakukan pencarian
            kosongkanInputKolom(); // Kosongkan field pencarian setelah selesai
            jTextField3.setText("Cari Kontak"); // Reset placeholder
        });
        
         
         // Tombol Import (Muat Data Kontak dari File .CSV)
        jButton6.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                // Validasi file harus berformat CSV
                if (!file.getName().endsWith(".csv")) {
                    JOptionPane.showMessageDialog(this, "Harap pilih file dengan format CSV.");
                    return;
                }

                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line = br.readLine(); // Baca header
                    if (line == null || !line.equals("Nama,Nomor Telepon,Kategori")) {
                        JOptionPane.showMessageDialog(this, "Format file CSV tidak valid. Harus berisi header: Nama,Nomor Telepon,Kategori.");
                        return;
                    }

                    // Proses membaca baris berikutnya
                    while ((line = br.readLine()) != null) {
                        String[] data = line.split(","); // Pisahkan data berdasarkan koma
                        if (data.length != 3) {
                            JOptionPane.showMessageDialog(this, "Baris tidak valid: " + line);
                            continue;
                        }

                        String nama = data[0].trim();
                        String nomorTelepon = data[1].trim();
                        String kategori = data[2].trim();

                        // Validasi dan tambahkan ke database
                        if (!nomorTelepon.matches("\\d+")) {
                            JOptionPane.showMessageDialog(this, "Nomor telepon tidak valid: " + nomorTelepon);
                            continue;
                        }
                        tambahKontak(nama, nomorTelepon, kategori);
                    }

                    tampilkanKontak(); // Refresh tabel setelah import
                    JOptionPane.showMessageDialog(this, "Data berhasil diimpor dari file CSV.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat membaca file: " + ex.getMessage());
                }
            }
        });
        
        // Tombol Export (Simpan Data Kontak ke File .CSV)
        jButton7.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                // Pastikan file memiliki ekstensi .csv
                if (!file.getName().endsWith(".csv")) {
                    file = new File(file.getAbsolutePath() + ".csv");
                }

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    // Tulis header
                    bw.write("Nama,Nomor Telepon,Kategori");
                    bw.newLine();

                    // Ambil data dari tabel dan tulis ke file
                    for (int i = 0; i < model.getRowCount(); i++) {
                        String nama = model.getValueAt(i, 1).toString();
                        String nomorTelepon = model.getValueAt(i, 2).toString();
                        String kategori = model.getValueAt(i, 3).toString();
                        bw.write(nama + "," + nomorTelepon + "," + kategori);
                        bw.newLine();
                    }

                    JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke file CSV.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menulis file: " + ex.getMessage());
                }
            }
        });
    }
       
    
     // Metode untuk membuat database dan tabel jika belum ada
    private void buatDatabase() {
        String url = "jdbc:sqlite:kontak.db"; // Nama file database

        // SQL untuk membuat tabel kontak
        String sqlCreateTable = "CREATE TABLE IF NOT EXISTS kontak ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nama TEXT NOT NULL,"
                + "nomor_telepon TEXT NOT NULL,"
                + "kategori TEXT NOT NULL"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            // Membuat database dan tabel
            if (conn != null) {
                stmt.execute(sqlCreateTable);
                System.out.println("Database dan tabel kontak berhasil dibuat atau sudah ada.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

     // Metode koneksi untuk operasi CRUD
    public Connection connect() {
        String url = "jdbc:sqlite:kontak.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Koneksi berhasil!");
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
        return conn;
    }
    
     private void cariKontak(String keyword) {
        model.setRowCount(0); // Hapus semua baris sebelum menampilkan data hasil pencarian
        String sql = "SELECT * FROM kontak WHERE nama LIKE ? OR nomor_telepon LIKE ? OR kategori LIKE ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("nomor_telepon"),
                    rs.getString("kategori")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Kontak tidak ditemukan.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencari kontak: " + e.getMessage());
        }
    }
    
     private void tambahKontak(String nama, String nomorTelepon, String kategori) {
        String sql = "INSERT INTO kontak(nama, nomor_telepon, kategori) VALUES(?,?,?)";
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            pstmt.setString(2, nomorTelepon);
            pstmt.setString(3, kategori);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kontak berhasil ditambahkan.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan kontak: " + e.getMessage());
        }
    }
     
      private void tampilkanKontak() {
        model.setRowCount(0); // Hapus semua baris sebelum menampilkan data
        String sql = "SELECT * FROM kontak";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("nomor_telepon"),
                    rs.getString("kategori")
                });
            }
            kosongkanInputKolom(); // Bersihkan input setelah menampilkan data
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan kontak: " + e.getMessage());
        }
    }
      
      private void editKontak(int id, String nama, String nomorTelepon, String kategori) {
        String sql = "UPDATE kontak SET nama = ?, nomor_telepon = ?, kategori = ? WHERE id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            pstmt.setString(2, nomorTelepon);
            pstmt.setString(3, kategori);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kontak berhasil diperbarui.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui kontak: " + e.getMessage());
        }
    }
      
       private void hapusKontak(int id) {
        String sql = "DELETE FROM kontak WHERE id = ?";
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Kontak berhasil dihapus.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus kontak: " + e.getMessage());
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jTextField3 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 204, 204));

        jLabel1.setText("Nama :");

        jLabel2.setText("Nomor Telpon :");

        jLabel3.setText("Kategori :");

        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });

        jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField2FocusLost(evt);
            }
        });

        jButton1.setText("TAMBAH");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("EDIT");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("HAPUS");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("KELUAR");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton5.setText("MENCARI KONTAK");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("MUAT KONTAK");

        jButton7.setText("SIMPAN KONTAK");

        jTextField3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField3FocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(70, 70, 70)
                                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.LEADING, 0, 171, Short.MAX_VALUE)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(48, 48, 48))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton6)
                                .addGap(120, 120, 120)
                                .addComponent(jButton7)
                                .addGap(71, 71, 71))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5))
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addGap(42, 42, 42)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(jButton6))
                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
      
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
       
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin keluar?", "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        System.exit(0);
    }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
         if (jTextField1.getText().equals("Masukkan Nama")) {
            jTextField1.setText(""); // Hapus placeholder
            jTextField1.setForeground(java.awt.Color.BLACK); // Warna teks input
        }
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        if (jTextField1.getText().isEmpty()) {
            jTextField1.setText("Masukkan Nama"); // Kembalikan placeholder jika kosong
            jTextField1.setForeground(java.awt.Color.GRAY); // Warna placeholder
        }
    }//GEN-LAST:event_jTextField1FocusLost

    private void jTextField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusGained
       if (jTextField2.getText().equals("Masukkan Nomor Telepon")) {
            jTextField2.setText(""); // Hapus placeholder
            jTextField2.setForeground(java.awt.Color.BLACK); // Warna teks input
        }
    }//GEN-LAST:event_jTextField2FocusGained

    private void jTextField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusLost
          if (jTextField2.getText().isEmpty()) {
            jTextField2.setText("Masukkan Nomor Telepon"); // Kembalikan placeholder jika kosong
            jTextField2.setForeground(java.awt.Color.GRAY); // Warna placeholder
        }
    }//GEN-LAST:event_jTextField2FocusLost

    private void jTextField3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusGained
       if (jTextField3.getText().equals("Cari Kontak")) {
            jTextField3.setText(""); // Hapus placeholder
            jTextField3.setForeground(java.awt.Color.BLACK); // Warna teks input
        }
    }//GEN-LAST:event_jTextField3FocusGained

    private void jTextField3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusLost
       if (jTextField3.getText().isEmpty()) {
            jTextField3.setText("Cari Kontak"); // Kembalikan placeholder jika kosong
            jTextField3.setForeground(java.awt.Color.GRAY); // Warna placeholder
        }
    }//GEN-LAST:event_jTextField3FocusLost

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PengelolaanKontakFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables

}
