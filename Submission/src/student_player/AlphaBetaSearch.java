package student_player;

import boardgame.Board;
import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.ArrayList;
import java.util.Optional;

import static student_player.MyTools.checkCenters;

public class AlphaBetaSearch {
    private TranspositionData transposition;
    public AlphaBetaSearch(TranspositionData transpositionTable){
        transposition = transpositionTable;
    }

    public Move AlphaBetaSearch(PentagoBoardState currentBoard, int myPlayer, int pMaxTurns){

        int maxTurns = pMaxTurns;
        int currentTurnNumber = 0;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int currentVal;
        Move bestMove = currentBoard.getRandomMove();
        int bestVal = Integer.MIN_VALUE;
        int test_counter = 0;
        boolean first_move;
        PentagoMove firstBestMove = null;
        long time = System.currentTimeMillis();

        if(currentBoard.getTurnNumber() == 0){
            first_move = true;
        }
        else{
            first_move = false;
        }

        Optional center = checkCenters(currentBoard);
        if(center != Optional.empty()){
            bestMove = (Move) center.get();
        }

        if(first_move){
            firstBestMove = new PentagoMove(((PentagoMove)bestMove).getMoveCoord(),  ((PentagoMove)bestMove).getASwap(),  ((PentagoMove)bestMove).getBSwap(), ((PentagoMove)bestMove).getPlayerID());
            currentBoard.processMove((PentagoMove) bestMove);
            currentBoard.processMove((PentagoMove) currentBoard.getRandomMove());
        }

        for(PentagoMove m: MyTools.pruneSymmetricMove(currentBoard)){
            if(first_move){
                if(System.currentTimeMillis()-time > 29990){
                    break;
                }
            }
            else{
                if(System.currentTimeMillis()-time > 1920){
                    break;
                }
            }
            //System.out.println("Test Counter" + test_counter++);
            PentagoBoardState cloned_board = (PentagoBoardState) currentBoard.clone();
            cloned_board.processMove(m);
            currentVal = min_value(cloned_board, currentTurnNumber, maxTurns, alpha, beta, myPlayer, first_move);;

            if(currentVal > bestVal){
                bestVal = currentVal;
                bestMove = m;
            }
        }

        if(first_move){
            return firstBestMove;
        }

        center = checkCenters(currentBoard);
        if(center != Optional.empty()){
            bestMove = (Move) center.get();
        }


        //System.out.println("TimeStamp" + (System.currentTimeMillis()-time));
        if(first_move){
            currentBoard.processMove((PentagoMove) bestMove);
            ArrayList<PentagoMove> moves = MyTools.pruneSymmetricMove(currentBoard);
            int i = 0;
            while(System.currentTimeMillis()-time < 29950 ){
                PentagoBoardState clone = (PentagoBoardState) currentBoard.clone();
                clone.processMove(moves.get((int) ((Math.random()+i)%moves.size())));

                int key = TranspositionState.KeysGenerator(clone);
                if(!transposition.checkIfContainsKey(key)){
                    transposition.addData(key, new TranspositionState(clone, Utility(clone, myPlayer)));
                }
                i++;
                //System.out.println(i);
            }

        }

        //System.out.println("Transposition" + transposition.getSize());

        return bestMove;
    }

    private int max_value(PentagoBoardState currentBoard, int currentTurnNumber, int maxTurnNumber, int alpha, int beta, int initial_player, boolean first_move){

        if(currentBoard.gameOver() || (first_move && currentTurnNumber > 2) || (!first_move && currentTurnNumber > 1)){
            int currentKey = TranspositionState.KeysGenerator(currentBoard);
            int currentUtil;
            if(transposition.checkIfContainsKey(currentKey)){
                currentUtil = transposition.getValue(currentKey).getUtility();
            }else{
                currentUtil = Utility(currentBoard, initial_player);
                transposition.addData(currentKey, new TranspositionState(currentBoard, currentUtil));

            }
            return currentUtil;
        }

        int val = Integer.MIN_VALUE;

        for(Move m : MyTools.pruneSymmetricMove(currentBoard)){

            PentagoBoardState clone = (PentagoBoardState) currentBoard.clone();
            clone.processMove((PentagoMove) m);
            int currentKey = TranspositionState.KeysGenerator(clone);
            int util;
            if(transposition.checkIfContainsKey(currentKey)){
                util = transposition.getValue(currentKey).getUtility();
            }else{
                util = min_value(clone, currentTurnNumber+1, maxTurnNumber, alpha, beta, initial_player, first_move);
            }
            val = Math.max(val, util);
            alpha = Math.max(alpha, val);
            if(beta <= alpha){
                return val;
            }
        }

        return val;
    }

    private int min_value(PentagoBoardState currentBoard, int currentTurnNumber, int maxTurnNumber, int alpha, int beta, int initial_player, boolean first_move){

        if(currentBoard.gameOver() || (first_move && currentTurnNumber > 2) || (!first_move && currentTurnNumber > 1)){
            int currentKey = TranspositionState.KeysGenerator(currentBoard);
            int currentUtil;
            if(transposition.checkIfContainsKey(currentKey)){
                currentUtil = transposition.getValue(currentKey).getUtility();
            }else{
                currentUtil = Utility(currentBoard, initial_player);
                transposition.addData(currentKey, new TranspositionState(currentBoard, currentUtil));
            }
            return currentUtil;
        }

        int val = Integer.MAX_VALUE;

        for(Move m: MyTools.pruneSymmetricMove(currentBoard)){

            PentagoBoardState clone = (PentagoBoardState) currentBoard.clone();
            clone.processMove((PentagoMove) m);
            int currentKey = TranspositionState.KeysGenerator(clone);
            int util;
            if(transposition.checkIfContainsKey(currentKey)){
                util = transposition.getValue(currentKey).getUtility();
            }else{
                util = max_value(clone, currentTurnNumber+1, maxTurnNumber, alpha, beta, initial_player, first_move);
            }
            val = Math.min(val, util);
            beta = Math.min(beta, val);
            if(beta <= alpha){
                return val;
            }

        }
        return val;
    }


    private static int Utility(PentagoBoardState currentBoard, int initial_player){

        if(currentBoard.gameOver()){
            int winner = currentBoard.getWinner();
            if(winner == Board.DRAW){
                return 0;
            }
            else if(winner == initial_player){
                return Integer.MAX_VALUE-10;
            }
            else if(winner == 1-initial_player){
                return Integer.MIN_VALUE+10;
            }
            else{
                return InLineScore(currentBoard, initial_player);
            }
        }

        return InLineScore(currentBoard, initial_player);

    }

    public static int InLineScore(PentagoBoardState currentBoard, int initial_player){
        PentagoBoardState.Piece[][] board = currentBoard.getBoard();
        int totalScore = 0;
        PentagoBoardState.Piece myColor;
        if(initial_player == 0){
            myColor = PentagoBoardState.Piece.WHITE;
        }
        else{
            myColor = PentagoBoardState.Piece.BLACK;
        }
        for(int i = 0; i < board.length; i++){
            int myScore = 0;
            int opponentScore = 0;
            int myAlign = 1;
            int myPiece = 0;
            int opponentAlign = 1;
            int opponentPiece = 0;

            for(int j = 0; j < board[0].length; j++){
                if(board[i][j] == myColor){
                    myAlign = myAlign*10;
                    myPiece++;
                    opponentAlign = 1;
                    myScore += myAlign;
                }else if(board[i][j] == PentagoBoardState.Piece.EMPTY){
                    opponentAlign = 1;
                    myAlign = 1;
                }else {
                    opponentAlign = opponentAlign * 100;
                    myAlign = 1;
                    opponentPiece++;
                    opponentScore += opponentAlign;
                }
            }
            totalScore = (int) (totalScore + myScore - opponentScore + Math.pow(10, myPiece) - Math.pow(100, opponentPiece));
        }

        for(int i = 0; i < board[0].length; i++){
            int myScore = 0;
            int opponentScore = 0;
            int myAlign = 1;
            int myPiece = 0;
            int opponentAlign = 1;
            int opponentPiece = 0;
            for(int j = 0; j < board.length; j++){
                if(board[j][i] == myColor){
                    myAlign = myAlign*10;
                    myPiece++;
                    opponentAlign = 1;
                    myScore += myAlign;
                }else if(board[j][i] == PentagoBoardState.Piece.EMPTY){
                    opponentAlign = 1;
                    myAlign = 1;
                }else {
                    opponentAlign = opponentAlign * 10;
                    opponentPiece++;
                    myAlign = 1;
                    opponentScore += opponentAlign;
                }
            }
            totalScore = (int) (totalScore + myScore - opponentScore+ Math.pow(10, myPiece) - Math.pow(100, opponentPiece));
        }

        int i = 0;
        int j = 0;
        int myScore = 0;
        int opponentScore = 0;
        int myAlign = 1;
        int myPiece = 0;
        int opponentAlign = 1;
        int opponentPiece = 0;
        while(i < board.length && j < board[0].length){
            if(board[j][i] == myColor){
                myAlign = myAlign*10;
                myPiece++;
                opponentAlign = 1;
                myScore += myAlign;
            }else if(board[j][i] == PentagoBoardState.Piece.EMPTY){
                opponentAlign = 1;
                myAlign = 1;
            }else {
                opponentAlign = opponentAlign * 100;
                opponentPiece++;
                myAlign = 1;
                opponentScore += opponentAlign;
            }
            i++;
            j++;
        }
        totalScore = (int) (totalScore + myScore - opponentScore + Math.pow(10, myPiece) - Math.pow(100, opponentPiece));

        i = 0;
        myScore = 0;
        opponentScore = 0;
        myAlign = 1;
        myPiece = 0;
        opponentAlign = 1;
        opponentPiece = 0;
        while(i < board.length){
            if(board[5-i][i] == myColor){
                myAlign = myAlign*10;
                myPiece++;
                opponentAlign = 1;
                myScore += myAlign;
            }else if(board[5-i][i] == PentagoBoardState.Piece.EMPTY){
                opponentAlign = 1;
                myAlign = 1;
            }else {
                opponentAlign = opponentAlign * 100;
                opponentPiece++;
                myAlign = 1;
                opponentScore += opponentAlign;
            }
            i++;
        }
        totalScore = (int) (totalScore + myScore - opponentScore + Math.pow(10, myPiece) - Math.pow(100, opponentPiece));
        return totalScore;
    }

}
