package student_player;

import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.ArrayList;
import java.util.List;

public class State {
    private PentagoBoardState currentStateBoard;
    //int currentPlayer;
    private int visitCount;
    private double winScore;
    private Move currentMove;
    //List<State> allPossibleStates;

    State(PentagoBoardState pBoard, Move pMove){
        currentStateBoard = pBoard;
        //currentPlayer = pBoard.getTurnPlayer();
        visitCount = 0;
        winScore = 0;
        currentMove = pMove;
    }

    int getVisitCount(){
        return visitCount;
    }

    void incrementVisit() {
        visitCount++;
    }

    double getWinScore(){
        return winScore;
    }

    void addScore(int WIN_SCORE){
        winScore += WIN_SCORE;
    }

    PentagoBoardState getCurrentStateBoard(){
        return currentStateBoard;
    }

    Move getCurrentMove(){
        return currentMove;
    }

    public ArrayList<State> getAllPossibleState(){
        ArrayList<State> allStates = new ArrayList<State>();
        for(Move m: MyTools.pruneSymmetricMove(currentStateBoard)){
            //for(Move m: currentStateBoard.getAllLegalMoves()){
            PentagoBoardState p = (PentagoBoardState) currentStateBoard.clone();
            p.processMove((PentagoMove) m);
            allStates.add(new State(p, m));
        }
        return allStates;
    }

    /*
    return a randomState from a list of given states
     */
    public State randomState(List<State> allStates){
        return allStates.get((int)(Math.random()%allStates.size()));
    }
}
