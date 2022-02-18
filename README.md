# Travelling F1 Problem - Overdrive Entellect Chalenge 2020

## Spesifikasi minimum

- Maven
- Java (at least Java 8)

## Cara menjalankan

Jalankan makefile, atau eksekusi `mvn clean install -PoutputDir` dari CMD.

## Selayang pandang algoritma greedy

Dalam implementasi algoritma greedy pada permasalahan ini, kami membuat struktur data pohon sebagai struktur data utama
untuk membangkitkan command yang dimasukkan ke fungsi seleksi. Pohon dipilih karena karakteristiknya yang memiliki level
atau kedalaman. Selain itu, algoritma breadth-first-search (BFS) cocok dengan pohon.

Perlu diperhatikan bahwa pohon digunakan hanya untuk “mengunjungi” seluruh simpul dalam dan daun dari pohon (searching),
bukan untuk menyaring keputusan-keputusan yang ada seperti yang ada pada pohon keputusan. Maka dari itu, simpul dari
pohon hanya menyimpan GlobalState (state virtual untuk disimulasikan) dan action (command yang diambil oleh simpul
tersebut). Pohon akan diimplementasikan pada kelas Search (untuk menghitung langkah kita) dan OpponentMove (untuk
menghitung langkah lawan).

Intinya, algoritma greedy akan mempertimbangkan pilihan terbaik berdasarkan dua atau tiga langkah ke depan—bergantung
dari kedalaman pohon yang dispesifikasi dan bergantung pada situasi yang diketahui oleh player pada saat itu—sehingga
tidak hanya satu langkah seperti yang dilakukan bot referensi. Pertimbangan pilihan terbaik akan dilakukan oleh kelas
statis Scoring yang menghitung command-command yang disimulasikan berdasarkan bobot yang ada pada kelas statis Weights.
Kelas Scoring juga menjadi komponen utama dalam fungsi seleksi.

## Tim kami :)

Kelompok 8 - Travelling F1 Problem <br>
Institut Teknologi Bandung - Teknik Informatika 2020 <br>

- Firizky Ardiansyah 13520095 <br>
- Maria Khelli 13520115 <br>
- Bryan Amirul Husna 13520146 <br>