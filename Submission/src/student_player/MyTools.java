package student_player;

import boardgame.Board;
import boardgame.Move;
import pentago_twist.PentagoBoard;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.*;


public class MyTools {


    /*
        |0|1|
        |2|3|
     */
    private static ArrayList<PentagoBoardState.Piece[][]> getQuads(PentagoBoardState.Piece[][] board){
        ArrayList<PentagoBoardState.Piece[][]> quads = new ArrayList<>();
        PentagoBoardState.Piece[][] quad0 = new PentagoBoardState.Piece[3][3];
        PentagoBoardState.Piece[][] quad1 = new PentagoBoardState.Piece[3][3];
        PentagoBoardState.Piece[][] quad2 = new PentagoBoardState.Piece[3][3];
        PentagoBoardState.Piece[][] quad3 = new PentagoBoardState.Piece[3][3];
        for(int i = 0; i < 6; i++){
            for(int j = 0; j <6; j++){
                if(i<3 && j<3){
                    quad0[i][j] = board[i][j];
                }else if(i<3 && j>=3){
                    quad1[i][j-3] = board[i][j];
                }
                else if(i>=3 && j<3){
                    quad2[i-3][j] = board[i][j];
                }
                else{
                    quad3[i-3][j-3] = board[i][j];
                }
            }
        }

        quads.add(quad0);
        quads.add(quad1);
        quads.add(quad2);
        quads.add(quad3);
        return quads;
    }

    private static boolean checkRotation(PentagoBoardState.Piece[][] quad){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(quad[i][j] != quad[j][Math.abs(2-i)]){
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkFlip(PentagoBoardState.Piece[][] quad){
        for(int i = 0 ; i < 3; i++){
            if(quad[i][0]!=quad[i][2] || quad[0][i]!=quad[2][i]){
                return false;
            }
        }
        return true;
    }

    public static ArrayList<Boolean> isSymmetric(PentagoBoardState.Piece[][] board){
        ArrayList<Boolean> sym = new ArrayList<Boolean>();
        ArrayList<PentagoBoardState.Piece[][]> quads = MyTools.getQuads(board);

        sym.add(MyTools.checkFlip(quads.get(0)));
        sym.add(MyTools.checkFlip(quads.get(1)));
        sym.add(MyTools.checkFlip(quads.get(2)));
        sym.add(MyTools.checkFlip(quads.get(3)));
        sym.add(MyTools.checkRotation(quads.get(0)));
        sym.add(MyTools.checkRotation(quads.get(1)));
        sym.add(MyTools.checkRotation(quads.get(2)));
        sym.add(MyTools.checkRotation(quads.get(3)));

        return sym;
    }
    public static ArrayList<PentagoMove> pruneSymmetricMove(PentagoBoardState currentState){
        PentagoBoardState.Piece[][] board = currentState.getBoard();
        int turnPlayer = currentState.getTurnPlayer();
        PentagoBoardState.Piece color;
        if(turnPlayer == 0){
            color = PentagoBoardState.Piece.WHITE;
        }
        else {
            color = PentagoBoardState.Piece.BLACK;
        }
        ArrayList<PentagoMove> legalMoves = new ArrayList<>();

        for (int i = 0; i < 6; i++) { //Iterate through positions on board
            for (int j = 0; j < 6; j++) {
                if (board[i][j] == PentagoBoardState.Piece.EMPTY){
                    PentagoBoardState clone = (PentagoBoardState) currentState.clone();
                    PentagoBoardState.Piece[][] tempBoard = clone.getBoard();
                    tempBoard[i][j] = color;
                    ArrayList<Boolean> b = MyTools.isSymmetric(tempBoard);
                    List<Boolean> flip = b.subList(0, b.size()/2);
                    List<Boolean> rotate = b.subList(b.size()/2, b.size()/2);
                    if(flip.contains(false)){
                        for (int k = 0; k < 4; k++) { // Iterate through valid moves for rotate/flip
                            if(!flip.get(k)){
                                legalMoves.add(new PentagoMove(i, j, k, 1, turnPlayer));
                            }
                        }
                    }
                    else{
                        legalMoves.add(new PentagoMove(i, j, 0, 1, turnPlayer));
                    }
                    if(rotate.contains(false)){
                        for (int k = 0; k < 4; k++) { // Iterate through valid moves for rotate/flip
                            if(!rotate.get(k)){
                                legalMoves.add(new PentagoMove(i, j, k, 0, turnPlayer));
                            }
                        }
                    }
                    else{
                        legalMoves.add(new PentagoMove(i, j, 0, 0, turnPlayer));
                    }
                }
            }
        }
        return legalMoves;
    }




    public static Optional checkCenters(PentagoBoardState currentBoard){
        if(currentBoard.getPieceAt(1, 1) == PentagoBoardState.Piece.EMPTY){
            Move m = new PentagoMove(1, 1, 0, 0,currentBoard.getTurnPlayer());
            return Optional.of(m);
        }
        else if(currentBoard.getPieceAt(4, 1) == PentagoBoardState.Piece.EMPTY){
            Move m = new PentagoMove(4, 1, 2, 0,currentBoard.getTurnPlayer());
            return Optional.of(m);
        }
        else if(currentBoard.getPieceAt(1, 4) == PentagoBoardState.Piece.EMPTY){
            Move m = new PentagoMove(1, 4, 1, 0,currentBoard.getTurnPlayer());
            return Optional.of(m);
        }
        else if(currentBoard.getPieceAt(4, 4) == PentagoBoardState.Piece.EMPTY){
            Move m = new PentagoMove(4, 4, 3, 0,currentBoard.getTurnPlayer());
            return Optional.of(m);
        }
        return Optional.empty();
    }

}