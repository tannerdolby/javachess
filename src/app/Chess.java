package app;

import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chess {
    public static String[][] board;
    public static Map<String, Integer> pieceInfo;
    public static int[] pieceValues;
    public static String[] pieces;
    public static boolean playingWhitePieces;
    public static Map<String, String[]> backRanks;
    public static String[] pieceCodes;
    public static Map<String, Map<String, String>> pieceTable;
    // OPTIONS: RED, GREEN, YELLOW, BLUE, PURPLE, CYAN
    public static Map<String, String> colors;
    public static String colorOfWhitePieces;
    public static String colorOfBlackPieces;
    public static Map<String, int[]> chessCodeToCartesian;
    public static List<String[]> matchLog;
    public static int numberOfMoves;
    public static Map<String, List<String>> capturedPieces;

    // pawns can move forward 1 square or forward/left diagonal or forward/right diagonal
    // for attacking a piece
    public static int[] pawnVector = new int[]{1};

    // ANSI escape codes (styling text in console output)
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public static final int dim = 8;

    public Chess() {
        init(true, ANSI_BLUE, ANSI_PURPLE);
    }

    public Chess(Boolean isPlayingWhitePieces) {
        init(isPlayingWhitePieces, "cyan", "purple");
    }

    public Chess(Boolean isPlayingWhitePieces, String whitePieceColor, String blackPieceColor) {
        init(isPlayingWhitePieces, whitePieceColor, blackPieceColor);
    }

    private static void init(Boolean isPlayingWhitePieces, String whitePieceColor, String blackPieceColor) {
        board = new String[dim][dim];
        pieceValues = new int[]{10, 9, 5, 3, 3, 1};
        pieceCodes = new String[]{"K", "Q", "R", "N", "B", "P"};
        pieces = new String[]{"King", "Queen", "Rook", "Knight", "Bishop", "Pawn"};
        pieceInfo = new HashMap<>();
        backRanks = new HashMap<>();
        pieceTable = new HashMap<>();
        playingWhitePieces = isPlayingWhitePieces;
        colors = new HashMap<>();
        chessCodeToCartesian = new HashMap<>();
        matchLog = new ArrayList<>();
        capturedPieces = new HashMap<>();
        colorOfWhitePieces = whitePieceColor;
        colorOfBlackPieces = blackPieceColor;
        capturedPieces.put("white", new ArrayList<>());
        capturedPieces.put("black", new ArrayList<>());
        initPieceInfo();
        initColors(whitePieceColor, blackPieceColor);
        initBoardMapping();
        setupBoard();
    }

    private static void initPieceInfo() {
        for (int i=0; i < pieces.length; i++) {
            Map<String, Map<String, String>> piece = new HashMap<>();
            Map<String, String> info = new HashMap<>();
            info.put("piece", pieces[i]);
            info.put("value", pieceValues[i] + "");
            piece.put(pieceCodes[i], info);
            pieceTable.put(pieceCodes[i], info);
        }
    }

    private static void initBoardMapping() {
        String boardCols[] = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        for (int row=0; row < dim; row++) {
            for (int col=0; col < dim; col++) {
                chessCodeToCartesian.put(boardCols[col] + (dim-row), new int[]{row, col});
            }
        }
    }

    private static void initColors(String whitePieceColor, String blackPieceColor) {
        colors.put("red", ANSI_RED);
        colors.put("green", ANSI_GREEN);
        colors.put("yellow", ANSI_YELLOW);
        colors.put("purple", ANSI_PURPLE);
        colors.put("cyan", ANSI_CYAN);
        colors.put("whitePieces", whitePieceColor);
        colors.put("blackPieces", blackPieceColor);
    }

    private static String getKeyByValue(Map<String, Map<String, String>> map, String val) {
        for (Entry<String, Map<String, String>> entry : map.entrySet()) {
            if (val == entry.getValue().get("piece")) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static void arrangePieces(String pieceColor) {
        String positions[] = backRanks.get(pieceColor);
        for (int i=0; i < positions.length; i++) {
            String piece = pieceTable.get(positions[i]).get("piece");
            if (pieceColor == "black") {
                placePieces(piece, i);
            } else {
                placePieces(piece, i);
            }
        }
    }

    private static void placePieces(String piece, int i) {
        board[0][i] = colorText(colorOfBlackPieces, getKeyByValue(pieceTable, piece));
        board[7][i] = colorText(colorOfWhitePieces, getKeyByValue(pieceTable, piece));
        board[1][i] = colorText(colorOfBlackPieces, "P");
        board[6][i] = colorText(colorOfWhitePieces, "P");
    }

    public static void setupBoard() {
        backRanks.put("white", new String[]{"R", "N", "B", "Q", "K", "B", "N", "R"});
        backRanks.put("black", new String[]{"R", "N", "B", "K", "Q", "B", "N", "R"});

        for (int row=0; row < dim; row++) {
            for (int col=0; col < dim; col++) {
                if (row > 1 && row < 6) {
                    // Empty squares
                    board[row][col] = "o";
                } else if (playingWhitePieces) {
                    arrangePieces("white");
                } else if (!playingWhitePieces) {
                    arrangePieces("black");
                }
            }
        }
    }

    private static String colorText(String pieceColor, String text) {
        if (!colors.containsValue(pieceColor.toLowerCase())) return null;
        return colors.get(pieceColor) + text + ANSI_RESET;
    }

    public void move(String startingPos, String chessCode, String pieceColor) {
        if (startingPos.length() < 2 || chessCode.length() < 2) {
            throw new Error("Invalid chess code. Valid codes are two or more characters.");
        }

        String lastPieceMoved = "";
        int r = 0, c = 0;
        int pos[] = new int[2];
        int origin[] = chessCodeToCartesian.get(startingPos);
        String piece = chessCode.substring(0,1);
        String playerMove = chessCode;
        boolean capturingPiece = false;

        if (chessCode.charAt(1) == 'x') {
            // Capturing another piece
            playerMove = chessCode.substring(2);
            capturingPiece = true;
        }

        if (pieceColor == "white") {
            lastPieceMoved = colorOfWhitePieces;
        } else if (pieceColor == "black") {
            lastPieceMoved = colorOfBlackPieces;
        }

        // e.g. Ne4 would result in moving the appropriate piece to the position specified
        // checking if any of the pieces of `pieceColor` which match 'N' can do a valid
        // move to e4

        // Determine what type of piece is moving and store coordinates
        if (!pieceTable.containsKey(piece)) {
            // its a pawn move
            piece = "P";
            pos = chessCodeToCartesian.get(playerMove);
            r = pos[0];
            c = pos[1];
        } else {
            // Not a pawn (King, Queen, Rook, Bishop, Knight)
            pos = chessCodeToCartesian.get(chessCode.substring(1,3));
            r = pos[0];
            c = pos[1];
        }

        // add piece to captured pieces
        if (capturingPiece) {
            capturedPieces.get(pieceColor).add(board[r][c]);
        }
        
        // TODO: handle when a move wins a piece or checkmate
        // TODO: Before making the move, check if the piece has valid movement to the destination

        if (piece == "P") {
            // 2-directional (forward or left/right forward diagonal for capture)
            System.out.println(pos + ", " + r + " " + c);
        }

        // jump to the `chessCode`
        board[origin[0]][origin[1]] = "o";
        board[r][c] = colors.get(lastPieceMoved) + piece + ANSI_RESET;
        
        // Add move to match log
        matchLog.add(new String[]{chessCode, pieceColor});
      
        // show move list
        showMoveList();

        // show last move
        System.out.println("\nLast Move: " + colors.get(lastPieceMoved) + chessCode + ANSI_RESET + "\n");

        // Show Blacks captured pieces
        System.out.println(colorOfBlackPieces + ": " + capturedPieces.get("black") + "\n");

        // Redraw board after every move and display relevant match info
        showBoard();

        // Show Whites captured pieces
        System.out.println("\n" + colorOfWhitePieces + ": " + capturedPieces.get("white") + "\n");

        numberOfMoves++;

        return;
    }

    public void showMoveList() {
        System.out.println("Java Chess\n" + "-------------------------------");
        System.out.println("White pieces: " + colorOfWhitePieces);
        System.out.println("Black pieces: " + colorOfBlackPieces + "\n");
        System.out.println("Moves:");
        for (int i=0; i < matchLog.size(); i++) {
            String color = matchLog.get(i)[1];
            String recolor = "";
            if (color == "white") {
                recolor = colors.get(colors.get("whitePieces"));
            } else if (color == "black") {
                recolor = colors.get(colors.get("blackPieces"));
            }
            System.out.println((i+1) + ": " + recolor + matchLog.get(i)[0] + ANSI_RESET);
        }
    }

    public void showBoard() {
        String hyphens = "   -------------------------------";
        for (int row=0; row < dim; row++) {
            if (row == 0) System.out.print(hyphens);
            System.out.println("");
            for (int col=0; col < dim; col++) {
                if (col == 0) {
                    System.out.print("" + (dim - row) + " | ");
                    System.out.print(board[row][col] + " | ");
                } else if (col == 7) {
                    System.out.print(board[row][col] + " | ");
                    System.out.println();
                } else {
                    System.out.print(board[row][col] + " | ");
                }
            }
        }
        System.out.println(hyphens);
        System.out.println("    a   b   c   d   e   f   g   h");
    }
}