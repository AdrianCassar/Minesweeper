package minesweeperfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public enum BoardSize {
    beginnerEasy(new int[]{8, 8}), beginnerMedium(new int[]{9, 9}), beginnerHard(new int[]{10, 10}),
    intermediateEasy(new int[]{13, 15}), intermediateMedium(new int[]{16, 16}),
    expertEasy(new int[]{16, 30});

//, expertMedium(new int[]{30, 16}), expertHard(new int[]{18, 50}

    private final int[] size;

//    ,custom(0, 0)
//    private BoardSize(int rows, int columns) {
//        this.size = new int[]{rows, columns};
//    }
//    
//    public void setCustom(int rows, int columns) {
//        this(rows, columns);
//    }
//    
    private BoardSize(int[] size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return size[0] + " x " + size[1];
    }
    
    public int[] getValue() {
        return size;
    }
    
    public static BoardSize getEnum(String value) throws IllegalArgumentException {
        for(BoardSize v : values()) {
            if(v.toString().equalsIgnoreCase(value)) {
                return v;
            }
        }
        
        throw new IllegalArgumentException();
    }
    
    public static ObservableList<String> getCollection(Difficulty difficultyType) {
        ObservableList<String> items = FXCollections.observableArrayList();
        
        switch (difficultyType) {
            case Beginner:
                items = FXCollections.observableArrayList(beginnerEasy.toString(), beginnerMedium.toString(), beginnerHard.toString());
                break;
            case Intermediate:
                items = FXCollections.observableArrayList(intermediateEasy.toString(), intermediateMedium.toString());
                break;
            case Expert:
//                items = FXCollections.observableArrayList(expertEasy.toString(), expertMedium.toString(), expertHard.toString());
                items = FXCollections.observableArrayList(expertEasy.toString());
                break;
        }
        
        return items;
    }
}