public class MapRenderer extends Thread{
    private final World world;

    public MapRenderer(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        while (true){
            world.drawMap();
            try {
                Thread.sleep((long) 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
