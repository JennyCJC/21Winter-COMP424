package student_player;

import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static student_player.MyTools.checkCenters;

public class MCTS {
    static final int WIN_SCORE = 10;
    int level;
    int opponent;

    Move findNextMove(PentagoBoardState currentBoard){

        Optional center = checkCenters(currentBoard);
        if(center != Optional.empty()){
            return (Move) center.get();
        }

        int player = currentBoard.getTurnPlayer();
        opponent = 1-player;
        Node root = new Node(new State(currentBoard, null));

        long beginningTime = System.currentTimeMillis();

        while(System.currentTimeMillis() - beginningTime < 1940){
            //selection
            Node promisingNode = selectPromisingNode(root);
            //expansion
            if(!promisingNode.getState().getCurrentStateBoard().gameOver()){
                expandNode(promisingNode);
            }
            //exploration
            Node nodeToExplore = promisingNode;
            if(promisingNode.getChildArray().size()>0){
                nodeToExplore = promisingNode.getRandomChild();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
            //backPropogation
            backPropogation(nodeToExplore, playoutResult);
        }
        Node winnerNode = root.getChildWithMaxScore();
        //tree.setRoot(winnerNode);
        return winnerNode.getState().getCurrentMove();
    }

    private Node selectPromisingNode(Node currentRoot){
        Node node = currentRoot;
        while(node.getChildArray().size() > 0){
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    //private

    private void expandNode(Node node) {
        List<State> possibleStates = node.getState().getAllPossibleState();
        possibleStates.forEach(state -> {
            Node newNode = new Node(state);
            newNode.setParent(node);
            //newNode.getState().setPlayerNo(node.getState().getOpponent());
            node.getChildArray().add(newNode);
        });
    }

    private int simulateRandomPlayout(Node pNode){
        PentagoBoardState board = (PentagoBoardState) pNode.getState().getCurrentStateBoard().clone();
        int result = board.getWinner();
        while(!board.gameOver()){
            result = board.getWinner();
            ArrayList<PentagoMove> moves = MyTools.pruneSymmetricMove(board);
            board.processMove(moves.get((int) (Math.random()%moves.size())));
        }
        return result;
    }

    private void backPropogation(Node nodeToExplore, int playerNo){
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getCurrentStateBoard().getTurnPlayer() == playerNo) {
                tempNode.getState().addScore(WIN_SCORE);
            }
            tempNode = tempNode.getParent();
        }
    }
}
