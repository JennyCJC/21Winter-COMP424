package student_player;

import java.util.Hashtable;

public class TranspositionData {

    private Hashtable<Integer, TranspositionState> transposition;
    private static final TranspositionData INSTANCE = new TranspositionData();

    private TranspositionData() {
        transposition = new Hashtable<Integer, TranspositionState>();
    }
    public static TranspositionData instance() { return INSTANCE; }

    public void addData(int key, TranspositionState state){
        transposition.put(key, state);
    }

    public boolean checkIfContainsKey(int key){
        return transposition.containsKey(key);
    }

    public int getSize(){
        return transposition.size();
    }

    public TranspositionState getValue(int key){
        return transposition.get(key);
    }
}
