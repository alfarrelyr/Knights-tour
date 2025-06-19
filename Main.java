import java.util.Scanner;
/**
 * Entry-point aplikasi. Meminta posisi awal kuda dari user dan menampilkan
 * tur kuda. Pengguna dapat memilih mode animasi (menampilkan papan setiap
 * langkah) atau hanya hasil akhir.
 */
public class Main {
    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        System.out.println("=== TUR KUDA CATUR (KNIGHT'S TOUR) ===");

        // Pilih animasi atau tidak
        System.out.print("Tampilkan animasi per langkah? (y/t): ");
        boolean showAnimation = inputScanner.next().trim().equalsIgnoreCase("y");

        // Input posisi awal dengan validasi (1-8)
        int startRow, startCol;
        while (true) {
            System.out.print("Posisi baris awal (1-8): "); 
            startRow = inputScanner.nextInt() - 1; // Konversi ke 0-based
            System.out.print("Posisi kolom awal (1-8): "); 
            startCol = inputScanner.nextInt() - 1; // Konversi ke 0-based
            
            if (startRow >= 0 && startRow < 8 && startCol >= 0 && startCol < 8) break;
            System.out.println("Posisi diluar papan, silakan masukkan antara 1-8.\n");
        }

        // Inisialisasi papan dengan algoritma Warnsdorff
        Board chessBoard = new Board(showAnimation, 150, true);
        boolean isSolved = chessBoard.solveTour(startRow, startCol);

        // Tampilkan hasil akhir jika tidak dalam mode animasi
        if (!showAnimation) chessBoard.displayBoard();

        if (isSolved) {
            System.out.println("\nSolusi tur lengkap berhasil ditemukan!");
        } else {
            System.out.println("\nTidak ditemukan solusi tur lengkap dari posisi awal tersebut.");
            System.out.println("Mencoba solusi dengan algoritma backtracking standar...");
            
            // Coba lagi dengan backtracking biasa jika Warnsdorff gagal
            Board backtrackBoard = new Board(showAnimation, 150, false);
            isSolved = backtrackBoard.solveTour(startRow, startCol);
            if (!showAnimation) backtrackBoard.displayBoard();
            
            if (isSolved) {
                System.out.println("\nBerhasil menemukan solusi dengan backtracking!");
            } else {
                System.out.println("\nMaaf, tetap tidak ditemukan solusi tur lengkap.");
            }
        }
        inputScanner.close();
    }
}
