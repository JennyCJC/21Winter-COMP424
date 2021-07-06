package student_player;

import boardgame.Move;

import boardgame.Player;
import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;

import java.util.ArrayList;
import java.util.Hashtable;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260917694");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...

        PentagoBoardState currentBoard = (PentagoBoardState) boardState.clone();

        int myPlayer = boardState.getTurnPlayer();

        // Return your move to be processed by the server.

        // Get transposition table, which uses the singleton design pattern
        TranspositionData transpositionTable = TranspositionData.instance();

        // Load transposition table into Alpha-beta search, and calls it
        AlphaBetaSearch minimax = new AlphaBetaSearch(transpositionTable);
        Move myMove = minimax.AlphaBetaSearch(currentBoard, myPlayer, 15);

        // Experiment with a working MCTS algorithm
        //MCTS mcts = new MCTS();
        //Move myMove = mcts.findNextMove(boardState);

        return myMove;
    }
}