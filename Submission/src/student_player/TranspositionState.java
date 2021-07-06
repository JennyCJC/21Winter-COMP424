package student_player;

import boardgame.Move;
import pentago_twist.PentagoBoardState;

import java.util.Arrays;


public class TranspositionState{
    PentagoBoardState currentBoardState;
    int utility;
    int visitCount;
    double pWinScore;
    Move nextBestMove;
    public TranspositionState(PentagoBoardState pState, int pUtility){
        currentBoardState = (PentagoBoardState) pState.clone();
        utility = pUtility;
        //visitCount =
    }

    public int getUtility(){
        return utility;
    }

    public static int KeysGenerator(PentagoBoardState currentBoard){

        PentagoBoardState.Piece[][] board = currentBoard.getBoard();
        return Arrays.deepHashCode(board);
        /*int[] black_x = new int[6];
        int[] white_x = new int[6];
        int[] black_y = new int[6];
        int[] white_y = new int[6];
        for(int i = 0; i< 6; i++){
            black_x[i] = 00; //heng
            white_x[i] = 00;
            black_y[i] = 00; //shu
            white_y[i] = 00;
        }

        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 6; j++){
                if(board[i][j] == PentagoBoardState.Piece.BLACK){
                    black_x[i]++;

                }else if(board[i][j] == PentagoBoardState.Piece.WHITE){
                    white_x[i]++;
                }

                if(board[j][i] == PentagoBoardState.Piece.BLACK){
                    black_y[i]++;

                }else if(board[j][i] == PentagoBoardState.Piece.WHITE){
                    white_y[i]++;
                }
            }
        }
        int bx = 00;
        int wx = 00;
        int by = 00;
        int wy = 00;
        for(int i = 0; i < 6; i++){
            bx = bx*8 +black_x[i];
            wx = wx*8 +white_x[i];
            by = by*8 + black_y[i];
            wy = wy*8 + white_y[i];
        }

        return (long) (bx*Math.pow(8, 18) + wx*Math.pow(8, 12) + by*Math.pow(8, 6)+ wy)*/

        /*long white_key = 0b0;
        long black_key = 0b0;
        long key = 0;

        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 6; j++){
                if(board[i][j] == PentagoBoardState.Piece.BLACK){
                    key = key*10 +2;

                }else if(board[i][j] == PentagoBoardState.Piece.WHITE){
                    key = key*10 +1;
                }
                else{
                    key = key*10 + 0;
                }
            }
        }

        return key;*/

    }

}