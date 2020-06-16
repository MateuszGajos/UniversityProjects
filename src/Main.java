import com.sun.j3d.utils.applet.MainFrame;

public class Main {
    public static void main(String[] args) {

        MyCube cube = new MyCube();
        cube.addKeyListener(cube);

         new MainFrame(cube, 640, 520);
    }
}
