import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Map {
    File mapFile;
    int [][] mapMatrix;
    static List<Coordinate> emptyPos;
    List<Coordinate> stackPos;
    private int height, width;
    private Window window;

    public int[][] getMapMatrix() {
        return mapMatrix;
    }

    public List<Coordinate> getEmptyPos() {
        return emptyPos;
    }

    public Map(String mapFilename, Window window) {
        this.window = window;
        mapFile = new File(mapFilename);
        stackPos = new ArrayList<Coordinate>();
    }

    public boolean loadMap() throws IOException {
        height = MapUtils.countFileRows(mapFile);
        width = MapUtils.countFileColumns(mapFile);
        mapMatrix = MapUtils.fileToMatrix(mapFile);
        emptyPos = MapUtils.getEmptyPos(mapMatrix);

        assert (mapMatrix != null && emptyPos != null && height>0 && width>0 && !emptyPos.isEmpty());

        return true;
    }

    public void printAnsiMap(){
        assert (mapMatrix != null && height>0 && width>0);

        StringBuilder sb = new StringBuilder();

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                if(mapMatrix[y][x] == 1)
                    sb.append(" ");
                else if(mapMatrix[y][x] == 0)
                    sb.append("\u2588");
                else if(mapMatrix[y][x] == 2)
                    sb.append("C");
                else if(mapMatrix[y][x] == 3)
                    sb.append("T");
            }
            sb.append("\n");
        }

        window.updateGUI(sb.toString());
        //System.out.print(sb.toString() + "\n");
    }

    public void moveInMap(TaxiUser o, Coordinate to){
        assert (o.getPos() != null && to != null && !o.getPos().equals(to) && mapMatrix != null && stackPos != null && emptyPos != null);

        if(!emptyPos.contains(to))
            stackPos.add(to);
        else
            emptyPos.remove(to);

        Coordinate from = o.getPos();
        o.setPrevPos(from);
        o.setPos(to);

        if (o instanceof Client)
            mapMatrix[to.getY()][to.getX()] = 2;
        else if (o instanceof Driver)
            mapMatrix[to.getY()][to.getX()] = 3;

        if(!stackPos.contains(from)){
            mapMatrix[from.getY()][from.getX()] = 1;
            emptyPos.add(from);
        } else {
            stackPos.remove(from);
        }
    }

    public void addTaxiUser(TaxiUser o){
        assert (!emptyPos.isEmpty());

        int randomEmptyPosIdx = ThreadLocalRandom.current().nextInt(0, emptyPos.size());

        o.setPos(emptyPos.remove(randomEmptyPosIdx));

        if(o instanceof Client)
            mapMatrix[o.getPos().getY()][o.getPos().getX()] = 2;
        else if(o instanceof Driver)
            mapMatrix[o.getPos().getY()][o.getPos().getX()] = 3;
    }
}
