package student_player;

import java.util.ArrayList;

public class Node{
    static final int WIN_SCORE = 10;
    int level;
    int opponent;

    private final State state;
    private Node parent;
    private ArrayList<Node> childArray;

    Node(State pState){
        state = pState;
        parent = null;
        childArray = new ArrayList<>();
    }

    ArrayList<Node> getChildArray(){
        return childArray;
    }

    State getState(){
        return state;
    }

    public void setParent(Node pNode){
        parent = pNode;
    }

    public Node getParent() {
        return parent;
    }

    public Node getRandomChild(){
        return childArray.get((int) (Math.random()%childArray.size()));
    }

    public Node getChildWithMaxScore(){
        Node max = this.getRandomChild();
        for(Node n: childArray){
            if(max.getState().getWinScore() < n.getState().getWinScore()){
                max = n;
            }
        }
        return max;
    }
}
