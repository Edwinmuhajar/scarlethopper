# ScarletHopper

Plugin untuk **Paper 1.21.11**. Hopper dapat memindahkan hingga 32 item per transfer secara default, dan jumlahnya bisa diatur di `config.yml` atau melalui command.

## Build otomatis menjadi JAR melalui GitHub

1. Buat repository baru di GitHub, misalnya `ScarletHopper`.
2. Upload **isi folder `ScarletHopper` ini** ke root repository. Pastikan `pom.xml` dan folder `.github` berada di root repository, bukan di dalam folder bersarang.
3. Buka tab **Actions** dan pilih workflow **Build ScarletHopper**.
4. Jika diminta, izinkan Actions berjalan. Workflow juga otomatis berjalan saat push ke branch `main`/`master`.
5. Buka hasil run workflow yang berhasil, lalu unduh artifact **ScarletHopper-JAR**. Di dalamnya ada `ScarletHopper.jar`.
6. Upload `ScarletHopper.jar` ke folder `plugins/` server Paper, lalu restart server.

## Membuat Release yang berisi JAR

Setelah source sudah ada di GitHub, buat dan push tag versi, misalnya:

```bash
git tag v1.1.0
git push origin v1.1.0
```

GitHub Actions akan membangun plugin dan melampirkan `ScarletHopper.jar` ke bagian **Releases**.

## Build di komputer sendiri (opsional)

Perlu JDK 21 atau lebih baru dan Maven:

```bash
mvn clean package
```

Hasilnya: `target/ScarletHopper.jar`.

## Command

Permission: `scarlethopper.admin` (default: operator).

- `/scarlethopper info`
- `/scarlethopper set <jumlah>`
- `/scarlethopper reload`

## Versi

- Target API dependency: Paper `1.21.11`
- `api-version` pada `plugin.yml` tetap `1.21`, karena format API version Bukkit/Paper menggunakan versi mayor/minor, bukan nomor patch `1.21.11`.
- Kode dikompilasi ke Java 21 agar tetap kompatibel dengan runtime Java yang lebih baru, termasuk Java 25.
