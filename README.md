# AplikasiPengelolaanKontak
 Latihan3-ilfizahrah-2210010537

 ##Deskripsi
 ---
Aplikasi desktop Pengelolaan Kontak adalah sistem manajemen kontak yang dibangun menggunakan Java Swing dan SQLite untuk mempermudah pengelolaan kontak pribadi maupun profesional. Aplikasi ini dirancang untuk memenuhi kebutuhan pengguna dalam menyimpan, mencari, dan memodifikasi informasi kontak secara efisien.
________________________________________
Fitur Utama
---
1.	CRUD (Create, Read, Update, Delete):
- Menambahkan kontak baru.
- Menampilkan daftar kontak dalam tabel.
- Mengedit kontak yang dipilih.
- Menghapus kontak berdasarkan ID.
2.	Pencarian Dinamis:
- Mencari kontak berdasarkan nama, nomor telepon, atau kategori.
- Menggunakan input teks dengan placeholder.
3.	Import dan Export CSV:
-	Import: Memasukkan data dari file CSV dengan validasi format.
-	Export: Menyimpan data kontak ke file CSV dengan header Nama, Nomor Telepon, Kategori.
4.	Validasi Input:
-	Nomor telepon hanya dapat diisi angka.
- Nama hanya dapat diisi dengan huruf dan spasi.
- Menampilkan pesan kesalahan jika input tidak valid.
5.	Placeholder Input:
-	Input field memiliki placeholder seperti Masukkan Nama, Masukkan Nomor Telepon, dan Cari Kontak.
6.	Penyimpanan Data:
-	Data disimpan menggunakan SQLite dengan tabel kontak: 
  - id (INTEGER, PRIMARY KEY, AUTO_INCREMENT)
  - nama (TEXT)
  - nomor_telepon (TEXT)
  - kategori (TEXT)

----

Cara Menjalankan
----
•  Clone repository: 
git clone https://github.com/username/repository-name.git

•  Buka project di IDE (misalnya, NetBeans atau IntelliJ). 
•  Jalankan aplikasi.

----
Tampilan Aplikasi Pada saat Dijalankan
----
![Screenshot 2024-11-20 121210](https://github.com/user-attachments/assets/730db289-580e-498b-9eb2-b31d4545fa98)

---

Tampilan Data Dari Simpan (Eksport) .CSV
---
![Screenshot 2024-11-20 121238](https://github.com/user-attachments/assets/97a69956-dc87-433d-88d3-0fffe6df6ea1)

---

## Indikator Penilaian

| No  | Komponen           | Persentase |
|-----|---------------------|------------|
| 1   | Komponen GUI       | 10%        |
| 2   | Logika Program     | 10%        |
| 3   | Events             | 20%        |
| 4   | Kesesuaian UI      | 10%        |
| 5   | Memenuhi Variasi   | 50%        |
| *TOTAL* |               | *100%*   |

---
## Pembuat
---
Nama: Ilfi Zahrah

NPM: 2210010537

Kelas: 5B Reguler Pagi

Tugas : Latihan 3 Aplikasi Pengelolaan Kontak

Fakultas : Fakultas Teknologi Informasi (FTI)

Unversitas : Universitas Islam Kalimantan Muhammad Arsyad Al Banjari Banjarmasin





