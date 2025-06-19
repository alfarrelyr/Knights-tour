import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
/**
 * Representasi papan catur 8×8 untuk permainan Knight's Tour.
 * Menggunakan Warnsdorff's algorithm untuk meningkatkan kehandalan menemukan solusi.
 */
public class Board {
    // Konstanta ukuran papan
    private static final int BOARD_SIZE = 8;
    private static final int POSSIBLE_MOVES = 8;

    private final int[][] chessBoard;      // Matriks untuk menyimpan urutan langkah
    private int currentMoveNumber;        // Nomor langkah terakhir yang sudah di-set

    // Vektor gerak kuda (8 kemungkinan arah dalam bentuk L)
    private static final int[] ROW_DIRECTIONS = {-2, -2, -1, -1, 1, 1, 2, 2};
    private static final int[] COL_DIRECTIONS = {-1, 1, -2, 2, -2, 2, -1, 1};

    private final Random randomGenerator = new Random();
    private boolean isAnimated = false;   // Flag untuk mode animasi
    private int animationDelayMs = 150;   // Durasi jeda animasi (milidetik)
    private boolean useWarnsdorff = true; // Flag untuk menggunakan algoritma Warnsdorff

    // Konstruktor dengan parameter default
    public Board() {
        this(false, 150, true);
    }

    // Konstruktor dengan parameter animasi dan delay
    public Board(boolean isAnimated, int animationDelayMs) {
        this(isAnimated, animationDelayMs, true);
    }

    // Konstruktor utama
    public Board(boolean isAnimated, int animationDelayMs, boolean useWarnsdorff) {
        this.isAnimated = isAnimated;
        this.animationDelayMs = animationDelayMs;
        this.useWarnsdorff = useWarnsdorff;
        chessBoard = new int[BOARD_SIZE][BOARD_SIZE];  // Inisialisasi papan
        resetBoard();
    }

    /** 
     * Mengosongkan papan dan mengatur seluruh kotak ke nilai 0 
     */
    private void resetBoard() {
        for (int row = 0; row < BOARD_SIZE; row++)
            for (int col = 0; col < BOARD_SIZE; col++)
                chessBoard[row][col] = 0;
        currentMoveNumber = 0;
    }

    /** 
     * Memulai Knight's Tour dari posisi (startRow,startCol)
     * @param startRow Baris awal (0-based)
     * @param startCol Kolom awal (0-based)
     * @return true jika solusi ditemukan, false jika tidak
     */
    public boolean solveTour(int startRow, int startCol) {
        if (!isValidPosition(startRow, startCol)) return false;
        resetBoard();
        chessBoard[startRow][startCol] = 1;  // Langkah pertama
        currentMoveNumber = 1;
        if (isAnimated) showBoardWithDelay();
        
        // Coba beberapa kali dengan seed acak berbeda jika menggunakan backtracking biasa
        int maxAttempts = useWarnsdorff ? 1 : 10;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            if (solveRecursively(startRow, startCol, 2)) {
                return true;
            }
            resetBoard();
            chessBoard[startRow][startCol] = 1;
            currentMoveNumber = 1;
        }
        return false;
    }

    /** 
     * Fungsi rekursif untuk menyelesaikan Knight's Tour dengan backtracking
     * @param currentRow Posisi baris saat ini (0-based)
     * @param currentCol Posisi kolom saat ini (0-based)
     * @param nextMoveNumber Nomor langkah berikutnya
     * @return true jika solusi ditemukan, false jika tidak
     */
    private boolean solveRecursively(int currentRow, int currentCol, int nextMoveNumber) {
        // Base case: seluruh papan terisi
        if (nextMoveNumber > BOARD_SIZE * BOARD_SIZE) return true;

        // Kumpulkan semua gerakan yang mungkin
        ArrayList<Integer> possibleMoveIndices = new ArrayList<>();
        for (int moveIndex = 0; moveIndex < POSSIBLE_MOVES; moveIndex++) {
            int newRow = currentRow + ROW_DIRECTIONS[moveIndex];
            int newCol = currentCol + COL_DIRECTIONS[moveIndex];
            if (isValidMove(newRow, newCol)) {
                possibleMoveIndices.add(moveIndex);
            }
        }
        // Urutkan gerakan berdasarkan heuristic Warnsdorff
        if (useWarnsdorff) {
            possibleMoveIndices.sort(Comparator.comparingInt(moveIndex -> {
                int newRow = currentRow + ROW_DIRECTIONS[moveIndex];
                int newCol = currentCol + COL_DIRECTIONS[moveIndex];
                return countPossibleMoves(newRow, newCol);
            }));
        } else {
            // Acak urutan langkah untuk backtracking biasa
            shuffleMoves(possibleMoveIndices);
        }

        // Coba semua gerakan yang mungkin
        for (int moveIndex : possibleMoveIndices) {
            int newRow = currentRow + ROW_DIRECTIONS[moveIndex];
            int newCol = currentCol + COL_DIRECTIONS[moveIndex];
            chessBoard[newRow][newCol] = nextMoveNumber;
            if (isAnimated) showBoardWithDelay();
            if (solveRecursively(newRow, newCol, nextMoveNumber + 1)) return true;
            // Backtrack jika tidak menemukan solusi
            chessBoard[newRow][newCol] = 0;
            if (isAnimated) showBoardWithDelay();
        }
        return false;
    }

    /**
     * Mengacak urutan gerakan yang mungkin
     * @param moves Daftar indeks gerakan yang akan diacak
     */
    private void shuffleMoves(ArrayList<Integer> moves) {
        for (int i = moves.size() - 1; i > 0; i--) {
            int j = randomGenerator.nextInt(i + 1);
            int temp = moves.get(i);
            moves.set(i, moves.get(j));
            moves.set(j, temp);
        }
    }

    /** 
     * Menghitung jumlah gerakan yang mungkin dari posisi (row,col)
     * @param row Baris (0-based)
     * @param col Kolom (0-based)
     * @return Jumlah gerakan valid dari posisi tersebut
     */
    private int countPossibleMoves(int row, int col) {
        int validMovesCount = 0;
        for (int moveIndex = 0; moveIndex < POSSIBLE_MOVES; moveIndex++) {
            int newRow = row + ROW_DIRECTIONS[moveIndex];
            int newCol = col + COL_DIRECTIONS[moveIndex];
            if (isValidMove(newRow, newCol)) validMovesCount++;
        }
        return validMovesCount;
    }

    /** 
     * Memeriksa apakah posisi (row,col) valid dalam papan
     * @param row Baris (0-based)
     * @param col Kolom (0-based)
     * @return true jika posisi valid, false jika tidak
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    /** 
     * Memeriksa apakah gerakan ke (row,col) valid
     * @param row Baris tujuan (0-based)
     * @param col Kolom tujuan (0-based)
     * @return true jika gerakan valid, false jika tidak
     */
    private boolean isValidMove(int row, int col) {
        return isValidPosition(row, col) && chessBoard[row][col] == 0;
    }

    /** 
     * Menampilkan papan ke konsol (1-based untuk tampilan user)
     */
    public void displayBoard() {
        printChessBoard();
    }

    /** 
     * Mencetak representasi papan dengan grid 8×8 ke konsol (1-based untuk user)
     */
    private void printChessBoard() {
        System.out.println("\n   A   B   C   D   E   F   G   H");
        System.out.println(" +---+---+---+---+---+---+---+---+");
        for (int row = 0; row < BOARD_SIZE; row++) {
            System.out.print((row+1) + "|");  // Baris 1-based
            for (int col = 0; col < BOARD_SIZE; col++) {
                int moveNumber = chessBoard[row][col];
                if (moveNumber == 0) System.out.print("   |");
                else System.out.printf("%2d |", moveNumber);
            }
            System.out.println("\n +---+---+---+---+---+---+---+---+");
        }
    }

    /** 
     * Menampilkan papan lalu jeda sejenak (untuk animasi)
     */
    private void showBoardWithDelay() {
        printChessBoard();
        try { 
            Thread.sleep(animationDelayMs); 
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
