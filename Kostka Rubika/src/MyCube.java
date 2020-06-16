import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.pickfast.PickCanvas;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyCube extends Applet implements KeyListener {

    public GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
    public Canvas3D canvas = new Canvas3D(config);
    public SimpleUniverse universe = new SimpleUniverse(canvas);
    public TransformGroup transformGroups[] = new TransformGroup[27];
    public int position[][][] = new int[3][3][3];
    public Color colors[][] = new Color[27][6];
    public BranchGroup group[] = new BranchGroup[27];
    public BranchGroup mainGroup = new BranchGroup();
    public TransformGroup boxTransformGroup = new TransformGroup();
    public Box cube;
    public PickCanvas pickCanvas = new PickCanvas(canvas, mainGroup);
    public int rotationX[]= new int[]{0,4,1,5,0};
    public int rotationZ[]= new int[]{4,2,5,3,4};
    public int rotationY[]= new int[]{0,3,1,2,0};
    public List<Integer> moves = new ArrayList();
    ;


    public Transform3D rotation = new Transform3D();

    public void init() {
        draw();
    }

    public void draw() {

        setLayout(new BorderLayout());
        add("Center", canvas);
        positionViewer();


        mainGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        mainGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        mainGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
        mainGroup.setCapability(BranchGroup.ALLOW_DETACH);


        fillCube();
        setPosition();
        universe.addBranchGraph(mainGroup);
        canvas.addKeyListener(this);
    }

    //pozycjonowanie kostki
    public void setPosition() {
        int iterator = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    position[i][j][k] = iterator;
                    createCube(i - 1, j - 1, k - 1, iterator);
                    iterator++;
                }
            }
        }
    }

    // Kolorowanie kostki
    public void fillCube() {
        //Lista kolorów
        List<Color> list = new ArrayList<>(List.of(Color.BLUE, Color.WHITE, Color.ORANGE.darker().darker(),
                Color.RED, Color.GREEN, Color.yellow));

        for (int i = 0; i < 27; i++) {
            for (int j = 0; j < 6; j++) {
                colors[i][j] = list.get(j);
            }
            group[i] = new BranchGroup();
            group[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            group[i].setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
            group[i].setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
            group[i].setCapability(Group.ALLOW_CHILDREN_WRITE);
            group[i].setCapability(BranchGroup.ALLOW_DETACH);

        }
    }

    //tworzenie kostki
    public void createCube(float x, float y, float z, int i) {
        Transform3D transform = new Transform3D();
        Vector3f vector3f = new Vector3f(x, y, z);
        transform.setTranslation(vector3f);
        transformGroups[i] = new TransformGroup();

        boxTransformGroup = new TransformGroup();
        cube = new Box(0.47f, 0.47f, 0.47f, Primitive.ALLOW_CHILDREN_WRITE, getAppearance(new Color3f(Color.white)));
        cube.getShape(0).setAppearance(castColor(colors[i][0]));
        cube.getShape(1).setAppearance(castColor(colors[i][1]));
        cube.getShape(2).setAppearance(castColor(colors[i][2]));
        cube.getShape(3).setAppearance(castColor(colors[i][3]));
        cube.getShape(4).setAppearance(castColor(colors[i][4]));
        cube.getShape(5).setAppearance(castColor(colors[i][5]));

        boxTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroups[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroups[i].setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroups[i].setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        transformGroups[i].setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);

        boxTransformGroup.addChild(cube);
        boxTransformGroup.setTransform(transform);
        transformGroups[i].addChild(boxTransformGroup);
        group[i].addChild(transformGroups[i]);
        mainGroup.addChild(group[i]);
    }


    public void positionViewer() {
        ViewingPlatform vp = universe.getViewingPlatform();

        OrbitBehavior orbit = new OrbitBehavior(canvas, OrbitBehavior.REVERSE_ROTATE);
        orbit.setSchedulingBounds(new BoundingSphere());

        Transform3D t3d = new Transform3D();
        t3d.set(new Vector3f(0.0f, 0.0f, 17.0f));

        vp.getViewPlatformTransform().setTransform(t3d);
        vp.setViewPlatformBehavior(orbit);
    }


    public Appearance castColor(Color color) {
        return getAppearance(new Color3f(color));
    }

    public Appearance getAppearance(Color3f color) {
        Appearance ap = new Appearance();
        ColoringAttributes ca = new ColoringAttributes(color, ColoringAttributes.NICEST);
        ap.setColoringAttributes(ca);
        return ap;
    }

    public void rotatingAmiation(TransformGroup group, double angle, char axis) throws InterruptedException {
        Transform3D t3d = new Transform3D();
        t3d.set(new Vector3f(0.0f, 0.0f, 0.0f));
        switch (axis) {
            case 'y':
                rotation.rotY(angle);
                break;
            case 'z':
                rotation.rotZ(angle);
                break;
            case 'x':
                rotation.rotX(angle);
                break;
        }
        group.getTransform(t3d);
        t3d.mul(rotation);
        group.setTransform(t3d);
        TimeUnit.MILLISECONDS.sleep(1);

    }


    public void rotate(TransformGroup Cube, int x, int y, int z, int i, double angle, int[] pos, char axis) throws InterruptedException {
        Color buffor;
        rotatingAmiation(Cube, angle, axis);
        if (i == 29) {
            Transform3D transformation = new Transform3D();
            transformation.setScale(0);
            Cube.setTransform(transformation);
            group[position[x][y][z]].detach();
            mainGroup.removeChild(group[position[x][y][z]]);

            buffor = colors[position[x][y][z]][pos[4]];
            if (angle > 0) {
                colors[position[x][y][z]][pos[0]] = colors[position[x][y][z]][pos[1]];
                colors[position[x][y][z]][pos[1]] = colors[position[x][y][z]][pos[2]];
                colors[position[x][y][z]][pos[2]] = colors[position[x][y][z]][pos[3]];
                colors[position[x][y][z]][pos[3]] = buffor;
            } else {
                colors[position[x][y][z]][pos[0]] = colors[position[x][y][z]][pos[3]];
                colors[position[x][y][z]][pos[3]] = colors[position[x][y][z]][pos[2]];
                colors[position[x][y][z]][pos[2]] = colors[position[x][y][z]][pos[1]];
                colors[position[x][y][z]][pos[1]] = buffor;
            }
            createCube(x - 1, y - 1, z - 1, position[x][y][z]);
        }
    }
    

    @Override
    public void keyTyped(KeyEvent e) {

    }


//                  Ustawianie bloczków

    public void rotateUpOrDown(int x1, int z1, int y1, int x2, int z2, int degree) throws InterruptedException {
        for (int i = 0; i < 30; i++) {
            if (i == 29) {
                int temp = position[0][y1][0];
                position[0][y1][0] = position[x1][y1][z1];
                position[x1][y1][z1] = position[2][y1][2];
                position[2][y1][2] = position[x2][y1][z2];
                position[x2][y1][z2] = temp;
                temp = position[0][y1][1];
                position[0][y1][1] = position[1][y1][z1];
                position[1][y1][z1] = position[2][y1][1];
                position[2][y1][1] = position[1][y1][z2];
                position[1][y1][z2] = temp;
            }
            // ANIMACJA

            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    rotate(transformGroups[position[j][y1][k]], j, y1, k, i,-Math.PI * degree / 60,rotationY, 'y');

                }
            }
        }

    }

    public void rotateLeftOrRight(int x1, int y1, int z1, int y2, int z2, int z3, int z4, int degree) throws InterruptedException {

        for (int i = 0; i < 30; i++) {
            if (i == 29) {
                int temp = position[x1][2][z1];
                position[x1][2][z1] = position[x1][2][z2];
                position[x1][2][z2] = position[x1][0][z2];
                position[x1][0][z2] = position[x1][0][z1];
                position[x1][0][z1] = temp;
                temp = position[x1][y1][z3];
                position[x1][y1][z3] = position[x1][y2][z4];
                position[x1][y2][z4] = position[x1][z4][y2];
                position[x1][z4][y2] = position[x1][z3][y1];
                position[x1][z3][y1] = temp;
            }
//
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    rotate(transformGroups[position[x1][j][k]],  x1, j, k, i,-Math.PI * degree / 60, rotationX, 'x');

                }
            }
        }
    }

    public void rotateFrontOrRare(int x1, int y1, int x2, int y2, int z1, int degree) throws InterruptedException {
        {

            for (int i = 0; i < 30; i++) {
                if (i == 29) {
                    int temp = position[0][2][z1];
                    position[0][2][z1] = position[x1][y1][z1];
                    position[x1][y1][z1] = position[2][0][z1];
                    position[2][0][z1] = position[x2][y2][z1];
                    position[x2][y2][z1] = temp;
                    temp = position[1][2][z1];
                    position[1][2][z1] = position[x1][1][z1];
                    position[x1][1][z1] = position[1][0][z1];
                    position[1][0][z1] = position[x2][1][z1];
                    position[x2][1][z1] = temp;

                }
//
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 3; k++) {
                        rotate(transformGroups[position[j][k][z1]],  j, k, z1, i,-Math.PI * degree / 60,rotationZ, 'z');

                    }
                }
            }
        }
    }


    //          STEROWANIE
    @Override
    public void keyPressed(KeyEvent e) {

        try {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_1:
                    rotateLeftOrRight(2, 2, 2, 1, 0, 1, 0, -1);
                    moves.add(0);
                    break;
                case KeyEvent.VK_2:
                    rotateLeftOrRight(2, 1, 0, 2, 2, 0, 1, 1);
                    moves.add(1);
                    break;
                case KeyEvent.VK_3:
                    rotateLeftOrRight(0, 2, 2, 1, 0, 1, 0, -1);
                    moves.add(2);
                    break;
                case KeyEvent.VK_4:
                    rotateLeftOrRight(0, 1, 0, 2, 2, 0, 1, 1);
                    moves.add(3);
                    break;
                case KeyEvent.VK_Q:
                    rotateUpOrDown(2, 0, 2, 0, 2, -1);
                    moves.add(4);
                    break;
                case KeyEvent.VK_W:
                    rotateUpOrDown(0, 2, 2, 2, 0, 1);
                    moves.add(5);
                    break;
                case KeyEvent.VK_E:
                    rotateUpOrDown(2, 0, 0, 0, 2, -1);
                    moves.add(6);
                    break;
                case KeyEvent.VK_R:
                    rotateUpOrDown(0, 2, 0, 2, 0, 1);
                    moves.add(7);
                    break;
                case KeyEvent.VK_A:
                    rotateFrontOrRare(2, 2, 0, 0, 2, -1);
                    moves.add(8);
                    break;
                case KeyEvent.VK_S:
                    rotateFrontOrRare(0, 0, 2, 2, 2, 1);
                    moves.add(9);
                    break;
                case KeyEvent.VK_D:
                    rotateFrontOrRare(2, 2, 0, 0, 0, -1);
                    moves.add(10);
                    break;
                case KeyEvent.VK_F:
                    rotateFrontOrRare(0, 0, 2, 2, 0, 1);
                    moves.add(11);
                    break;
                case KeyEvent.VK_SPACE:
                    backTotheBeginning(moves);
            }


        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    public void backTotheBeginning(List<Integer> list) throws InterruptedException {
        Collections.reverse(list);
        for(int i = 0; i<list.size(); i++){
            switch (list.get(i)){
                case 1:
                    rotateLeftOrRight(2, 2, 2, 1, 0, 1, 0, -1);
                    break;
                case 0:
                    rotateLeftOrRight(2, 1, 0, 2, 2, 0, 1, 1);
                    break;
                case 3:
                    rotateLeftOrRight(0, 2, 2, 1, 0, 1, 0, -1);
                    break;
                case 2:
                    rotateLeftOrRight(0, 1, 0, 2, 2, 0, 1, 1);
                    break;
                case 5:
                    rotateUpOrDown(2, 0, 2, 0, 2, -1);
                    break;
                case 4:
                    rotateUpOrDown(0, 2, 2, 2, 0, 1);
                    break;
                case 7:
                    rotateUpOrDown(2, 0, 0, 0, 2, -1);
                    break;
                case 6:
                    rotateUpOrDown(0, 2, 0, 2, 0, 1);
                    break;
                case 9:
                    rotateFrontOrRare(2, 2, 0, 0, 2, -1);
                    break;
                case 8:
                    rotateFrontOrRare(0, 0, 2, 2, 2, 1);
                    break;
                case 11:
                    rotateFrontOrRare(2, 2, 0, 0, 0, -1);
                    break;
                case 10:
                    rotateFrontOrRare(0, 0, 2, 2, 0, 1);
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }


}

