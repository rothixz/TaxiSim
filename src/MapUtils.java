import java.io.*;
import java.util.*;

public class MapUtils {
    private static int[][] DIRECTIONS
            = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };


    public static int countFileRows(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                return 0;
            }

            int count = 0;
            while (readChars == 1024) {
                for (int i=0; i<1024;) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            while (readChars != -1) {
                for (int i=0; i<readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            return count == 0 ? 1 : count;
        } finally {
            is.close();
        }
    }

    public static int countFileColumns(File file) throws IOException {
        Scanner sc;
        int columns = -1;

        try{
            sc = new Scanner(new BufferedReader(new FileReader(file)));
            columns = sc.nextLine().trim().split(" ").length;
            sc.close();
        } catch (FileNotFoundException e){

        }

       return columns;
    }

    public static int [][] fileToMatrix(File file) throws IOException {
        Scanner sc;

        int width = countFileColumns(file);
        int heigth = countFileRows(file);

        int [][] matrix = new int[heigth][width];

        try{
            sc = new Scanner(new BufferedReader(new FileReader(file)));

            while(sc.hasNextLine()) {
                for (int i=0; i<matrix.length; i++) {
                    String[] line = sc.nextLine().trim().split(" ");
                    for (int j=0; j<line.length; j++) {
                        matrix[i][j] = Integer.parseInt(line[j]);
                    }
                }
            }
        } catch (FileNotFoundException e){

        }

        return matrix;
    }

    public static ArrayList<Coordinate> getEmptyPos(int [][] mapMatrix){
        ArrayList<Coordinate> emptyPos = new ArrayList<>();

        for(int y=0; y<mapMatrix.length; y++) {
            for (int x = 0; x < mapMatrix[0].length; x++) {
                if(mapMatrix[y][x] == 1){
                    emptyPos.add(new Coordinate(x,y));
                }
            }
        }

        return emptyPos;
    }

    private static List<Coordinate> backtrackPath(Coordinate cur) {
        List<Coordinate> path = new ArrayList<>();
        Coordinate iter = cur;

        while (iter != null) {
            path.add(iter);
            iter = iter.parent;
        }

        return path;
    }

    public static List<Coordinate> getShortestItenerary(SharedWorld map, Coordinate start, Coordinate end) {
        int [][] mapMatrix = map.getMapMatrix();
        LinkedList<Coordinate> nextToVisit = new LinkedList<>();
        boolean[][] visited = new boolean[mapMatrix.length][mapMatrix[0].length];
        nextToVisit.add(start);

        while (!nextToVisit.isEmpty()) {
            Coordinate cur = nextToVisit.remove();

            // If not in map
            if (!isValidLocation(mapMatrix, cur.getX(), cur.getY())
                    || visited[cur.getY()][cur.getX()]
            ) {
                continue;
            }

            // If is a wall
            if (mapMatrix[cur.getY()][cur.getX()] == 0){
                visited[cur.getY()][cur.getX()] = true;
                continue;
            }

            // If is endpoint
            if (cur.getX() == end.getX() && cur.getY() == end.getY()) {
                return backtrackPath(cur);
            }

            for (int[] direction : DIRECTIONS) {
                Coordinate coordinate
                        = new Coordinate(
                        cur.getX() + direction[0],
                        cur.getY() + direction[1],
                        cur
                );
                nextToVisit.add(coordinate);
                visited[cur.getY()][cur.getX()] = true;
            }
        }

        return Collections.emptyList();
    }

    private static boolean isValidLocation(int[][] mapMatrix, int row, int col) {
        if (row < 0 || row >= mapMatrix[1].length || col < 0 || col >= mapMatrix.length) {
            return false;
        }

        return true;
    }


    public static double getIteneraryDuration(List<Coordinate>itinerary){
        return (itinerary.size()-1)*Constants.VELOCITY;
    }


}
