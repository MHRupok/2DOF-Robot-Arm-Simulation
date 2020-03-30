package myvreplib;

import coppelia.IntW;
import java.lang.*;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
//import static myvreplib.converters.pixels;

public class MyVrepLib {

    int Q = 0;
    int X = 0, Y = 0;
    int start = 0;
    byte[][] pixels = null;
    int H = 0;
    int W = 0;
    public int image_height = 0;
    public int image_width = 0;
    public static int start_x = 0;
    public static int start_y = 0;
    public int theEnd = 0;
    public int array_is_live = 0;
    public int complete = 0;

    static MyVrep robot = null;

    MyVrepLib() throws InterruptedException, IOException {
        //control cn = new control(this);   // ei object pathaise control e
        //cn.setVisible(true);

        System.out.println("Connecting with vrep...");
        try {
            robot = new MyVrep("127.0.0.1", 19997);
            //////initial try/////

            IntW Motor_3 = robot.getObjHandle("motor_3");
            robot.setJointPos(Motor_3, 1);

            ///////////
            robot.startSimulation();
        } catch (Exception ex) {
            System.out.println("Error=" + ex.getMessage());

        }

        //  robot.setJointPos(Motor_3, 1);
        ////////////////////pen up down//////
        //robot.setJointPos(Motor_3, 0);
        //robot.setJointPos(Motor_3, 1);
        //////////////////////////
//        IntW Motor_2 = robot.getObjHandle("motor_2");
        //       robot.setJointPos(Motor_3, 0);
        //robot.setJointPosDeg(h2, 70);
//        Thread.sleep(5000);
//        robot.setJointPos(Motor_3, 20);
//        Thread.sleep(10000);
        //robot.setJointPosDeg(h, 40);
        //robot.setJointPosDeg(h2, 70);
        //Thread.sleep(5000);
//        
        //robot.setJointPosDeg(h, 0);
//        robot.setJointPosDeg(h2, 1000);
//        Thread.sleep(1000);
//
        //  robot.close();
        ///////////////////////
        //    inverse_kinematics ik = new inverse_kinematics();
        // ik.calculate_theta(0, 100);
//        while (true) {
//            Scanner sc = new Scanner(System.in);
//
//            System.out.println("X and Y: ");
//            X = sc.nextInt();
//            Y = sc.nextInt();
//
//            ik.calculate_theta(X, Y);
//
//            robot.setJointPosDeg(Motor_1, ik.angle_for_motor_1);
//            robot.setJointPosDeg(Motor_2, ik.angle_for_motor_2);
//
//            Thread.sleep(3000);
//
//            if (Q == 1) {
//                System.out.println("STOP BOT");
//                robot.setJointPosDeg(Motor_1, 0);
//                robot.setJointPosDeg(Motor_2, 0);
//                break;
//            }
//
//        }
//        robot.close();
////////////////////////////////////// initial point /////////////////////
    }

    void goBot(int m1, int m2) throws InterruptedException {

        inverse_kinematics ik = new inverse_kinematics();

        IntW Motor_1 = robot.getObjHandle("motor_1");
        IntW Motor_2 = robot.getObjHandle("motor_2");

        ik.calculate_theta(m1, m2);

        robot.setJointPosDeg(Motor_1, ik.angle_for_motor_1);
        robot.setJointPosDeg(Motor_2, ik.angle_for_motor_2);
        Thread.sleep(1);

    }

    void penUp() throws InterruptedException {
        IntW Motor_3 = robot.getObjHandle("motor_3");
        robot.setJointPos(Motor_3, 1);

    }

    void penDown() throws InterruptedException {
        IntW Motor_3 = robot.getObjHandle("motor_3");
        robot.setJointPos(Motor_3, 0);

    }

    public static void main(String[] args) throws InterruptedException, IOException {

//        new MyVrepLib();
//        new converters();
////////////////////////////////////////////// MAIN //////////////////////////////////
        converters c = new converters();

        MyVrepLib ms = new MyVrepLib();

        ms.penUp();
        c.find_point();
        ms.goBot(start_x, start_x);
        System.out.println("Start Point e gese!!");
        Thread.sleep(1000);
        ms.penDown();
        c.traversing(start_x, start_y);

        while (c.not_finished != 0) {
            System.out.println("IN WHILE LOOP!");
            c.not_finished = 0;
            c.find_point();
            ms.penUp();

            c.traversing(start_x, start_y);
            if (c.TheEnd == 100) {

                break;
            }

        }
        c.find_point();
        ms.penUp();
        c.traversing(start_x, start_y);
        ms.goBot(250, 0);
        Thread.sleep(1000);
        System.out.println("Last array: ");

        System.out.println("Completed!!!!");
        
    }

}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////// INVERSE KINEMATICS ///////////////////////////////////////////////////
class inverse_kinematics {

    int link_1 = 200;
    int link_2 = 200;
    double link_3 = 0;

    double theta_1 = 0;
    double theta_2 = 0;
    double theta_3 = 0;

    double angle_b = 0;
    double angle_c = 0;

    double initial_motor_1_position = 90;
    double initial_motor_2_position = 0;

    double angle_for_motor_1 = 0;
    double angle_for_motor_2 = 0;
    int quadent = 0;

    void calculate_theta(int x, int y) {

        if (x < 0) {
            x = x * (-1);
            quadent = 1;
        }
        int sqx = x * x;
        int sqy = y * y;
        link_3 = Math.sqrt(sqx + sqy);

        System.out.println("distance is: " + link_3);

        double sq_a = link_1 * link_1;
        double sq_b = link_2 * link_2;
        double sq_c = link_3 * link_3;

        angle_b = Math.toDegrees(Math.acos((sq_c + sq_a - sq_b) / (2 * link_3 * link_1)));
        angle_c = Math.toDegrees(Math.acos((link_1 * link_1 + link_2 * link_2 - link_3 * link_3) / (2 * link_1 * link_2)));

        if (quadent == 1) {
            theta_1 = Math.toDegrees(Math.asin(y / link_3));
            theta_1 = 180 - theta_1;
        } else {
            theta_1 = Math.toDegrees(Math.asin(y / link_3));
        }

        System.out.println("theta_1: " + theta_1);
        System.out.println("angle_b: " + angle_b);
        theta_2 = theta_1 - angle_b;
        theta_3 = 180 - angle_c;

        angle_for_motor_1 = initial_motor_1_position + theta_2;
        angle_for_motor_2 = initial_motor_2_position + theta_3;
        System.out.println("link_1 degree: " + angle_for_motor_1 + " link_2 degree: " + angle_for_motor_2);
    }

}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////
class converters {

    MyVrepLib mv = new MyVrepLib();
    //public static byte[][]pixels = new byte[250][];
    public int new_x = 0;
    public int new_y = 0;
    public int not_finished = 0;
    public int TheEnd = 0;
    public int foundPointCounter = 0;
    MyVrepLib pen = new MyVrepLib();

    //////////////
    BufferedImage image = ImageIO.read(new File("bangladesh.png"));

    public converters() throws IOException, InterruptedException {

        //byte[][] pixels = new byte[image.getWidth()][image.getHeight()];
        mv.pixels = new byte[image.getWidth()][image.getHeight()];

        mv.image_height = image.getHeight();
        mv.image_width = image.getWidth();
        ////////////////////Imge Flip//////////////////
        
        
        
        /////////////////////////////////////////
        for (int x = 0; x < image.getWidth(); x++) {
           // mv.pixels[x] = new byte[image.getHeight()];

            for (int y = 0; y < image.getHeight(); y++) {
                mv.pixels[y][x] = (byte) (image.getRGB(x, y) == 0xFFFFFFFF ? 0 : 1);

            }

        }
        
        

        

//        for (int y = 0; y < mv.image_height; y++) {
//            //pixels[x] = new byte[mv.image_height];
//
//            for (int x = 0; x < mv.image_width; x++) {
////                System.out.println("check="+x+":"+y+" nullcheck="+ (mv.pixels==null));
////                System.out.println("check2: "+mv.pixels.length+" :: "+mv.pixels[0].length);
////                
////                
//////                mv.pixels[x][y]=0;
//
//                mv.pixels[x][y] = (byte) (image.getRGB(x, y) == 0xFFFFFFFF ? 0 : 1);
//            }
//        }
//        for (int y = 0; y < image.getWidth(); y++) {
//            
//
//            for (int x = 0; x < image.getHeight(); x++) {
//                System.out.print(mv.pixels[x][y]);
//            }
//            System.out.println(" ");
//        }
//        
//        Thread.sleep(20000);
    }

    void find_point() throws InterruptedException, IOException {
        //System.out.println("Testing method is working!");
        mv.theEnd = 1;
        int point_flag = 0;

        if (foundPointCounter % 2 == 0) {
            for (int y = 0; y < mv.image_width; y++) {
                for (int x = 0; x < mv.image_height; x++) {
                    if (mv.pixels[x][y] == 1) {
                        mv.start_x = x;
                        mv.start_y = y;
                        mv.pixels[x][y] = 0;
                        point_flag = 1;
                        mv.theEnd = 5;
                        mv.array_is_live = 1;
                        System.out.println("Point Found: " + x + " " + y + " " + mv.theEnd);

                        break;
                    }

                }
                if (point_flag == 1) {

                    break;
                } else {
                    mv.array_is_live = 0;
                }

            }
        } else {
            for (int x = 0; x < mv.image_height; x++) {
                for (int y = 0; y < mv.image_width; y++) {
                    if (mv.pixels[x][y] == 1) {
                        mv.start_x = x;
                        mv.start_y = y;
                        mv.pixels[x][y] = 0;
                        point_flag = 1;
                        mv.theEnd = 5;
                        mv.array_is_live = 1;
                        System.out.println("Point Found: " + x + " " + y + " " + mv.theEnd);

                        break;
                    }

                }
                if (point_flag == 1) {

                    break;
                } else {
                    mv.array_is_live = 0;
                }

            }
        }

    }

    void traversing(int j, int k) throws InterruptedException, IOException {

        
        
        if (mv.pixels[j][k] == 0 && mv.pixels[j + 1][k] == 0 && mv.pixels[j - 1][k] == 0
                && mv.pixels[j][k + 1] == 0 && mv.pixels[j][k - 1] == 0 && mv.pixels[j + 1][k + 1] == 0
                && mv.pixels[j - 1][k + 1] == 0 && mv.pixels[j + 1][k - 1] == 0 && mv.pixels[j - 1][k - 1] == 0) {

            System.out.println("PEN UP!!");

            pen.penUp();

            find_point();
            if (mv.array_is_live == 1) {
                System.out.println("stop er bhetro point: " + mv.start_x + " " + mv.start_y);
            } else {
                System.out.println("ROBOT STOPPED!!!!!");
                TheEnd = 100;
                return;
            }

        }

        if (mv.pixels[j + 1][k] == 1) {
            mv.pixels[j+1][k] = 0;
            new_x = j + 1;
            new_y = k;
            System.out.println("Current node: " + new_x + " " + new_y);
            pen.penDown();
            mv.goBot(new_x, new_y);

            traversing(new_x, new_y);
        } else if (mv.pixels[j - 1][k] == 1) {
            mv.pixels[j-1][k] = 0;
            new_x = j - 1;
            new_y = k;
            System.out.println("Current node: " + new_x + " " + new_y);
            pen.penDown();
            mv.goBot(new_x, new_y);

            traversing(new_x, new_y);
        } else if (mv.pixels[j][k + 1] == 1) {
            mv.pixels[j][k+1] = 0;
            new_x = j;
            new_y = k + 1;
            System.out.println("Current node: " + new_x + " " + new_y);
            pen.penDown();
            mv.goBot(new_x, new_y);

            traversing(new_x, new_y);
        } else if (mv.pixels[j][k - 1] == 1) {
            mv.pixels[j][k-1] = 0;
            new_x = j;
            new_y = k - 1;
            System.out.println("Current node: " + new_x + " " + new_y);
            pen.penDown();
            mv.goBot(new_x, new_y);
            traversing(new_x, new_y);
        } else if (mv.pixels[j + 1][k + 1] == 1) {
            mv.pixels[j+1][k+1] = 0;
            new_x = j + 1;
            new_y = k + 1;
            System.out.println("Current node: " + new_x + " " + new_y);
            pen.penDown();
            mv.goBot(new_x, new_y);
            traversing(new_x, new_y);
        } else if (mv.pixels[j + 1][k - 1] == 1) {
            mv.pixels[j+1][k-1] = 0;
            new_x = j + 1;
            new_y = k - 1;
            System.out.println("Current node: " + new_x + " " + new_y);
            pen.penDown();
            mv.goBot(new_x, new_y);
            traversing(new_x, new_y);
        } else if (mv.pixels[j - 1][k + 1] == 1) {
            mv.pixels[j-1][k+1] = 0;
            new_x = j - 1;
            new_y = k + 1;
            System.out.println("Current node: " + new_x + " " + new_y);
            pen.penDown();
            mv.goBot(new_x, new_y);
            traversing(new_x, new_y);
        } else if (mv.pixels[j - 1][k - 1] == 1) {
            mv.pixels[j-1][k-1] = 0;
            new_x = j - 1;
            new_y = k - 1;
            System.out.println("Current node: " + new_x + " " + new_y);
            pen.penDown();
            mv.goBot(new_x, new_y);
            traversing(new_x, new_y);
        } else {
            find_point();
            if (mv.array_is_live == 1) {
                System.out.println("New point: " + mv.start_x + " " + mv.start_y);
                //traversing(mv.start_x, mv.start_y);
                not_finished = 1;
            } else {

                return;
            }
            System.out.println("Now zero from current node !");

        }

    }

    void follow_the_image(int j, int k) throws InterruptedException {
        while (true) {
            new_x = j;
            new_y = k;

            mv.pixels[j][k] = 0;
            if (mv.pixels[j][k] == 0 && mv.pixels[j + 1][k] == 0 && mv.pixels[j - 1][k] == 0
                    && mv.pixels[j][k + 1] == 0 && mv.pixels[j][k - 1] == 0 && mv.pixels[j + 1][k + 1] == 0
                    && mv.pixels[j - 1][k + 1] == 0 && mv.pixels[j + 1][k - 1] == 0 && mv.pixels[j - 1][k - 1] == 0) {
                mv.theEnd = 1;
                System.out.println("STOP BOT");
                break;

            }

            if (mv.pixels[j + 1][k] == 1) {
                mv.pixels[j][k] = 0;
                new_x = j + 1;
                new_y = k;
                System.out.println("Current node: " + new_x + " " + new_y);
                // mv.goBot(new_x, new_y);
                // traversing(new_x, new_y);
            } else if (mv.pixels[j - 1][k] == 1) {
                mv.pixels[j][k] = 0;
                new_x = j - 1;
                new_y = k;
                System.out.println("Current node: " + new_x + " " + new_y);
                //mv.goBot(new_x, new_y);
                //traversing(new_x, new_y);
            } else if (mv.pixels[j][k + 1] == 1) {
                mv.pixels[j][k] = 0;
                new_x = j;
                new_y = k + 1;
                System.out.println("Current node: " + new_x + " " + new_y);
                //mv.goBot(new_x, new_y);
                //traversing(new_x, new_y);
            } else if (mv.pixels[j][k - 1] == 1) {
                mv.pixels[j][k] = 0;
                new_x = j;
                new_y = k - 1;
                System.out.println("Current node: " + new_x + " " + new_y);
                //mv.goBot(new_x, new_y);
                //traversing(new_x, new_y);
            } else if (mv.pixels[j + 1][k + 1] == 1) {
                mv.pixels[j][k] = 0;
                new_x = j + 1;
                new_y = k + 1;
                System.out.println("Current node: " + new_x + " " + new_y);
                //mv.goBot(new_x, new_y);
                //traversing(new_x, new_y);
            } else if (mv.pixels[j + 1][k - 1] == 1) {
                mv.pixels[j][k] = 0;
                new_x = j + 1;
                new_y = k - 1;
                System.out.println("Current node: " + new_x + " " + new_y);
                //mv.goBot(new_x, new_y);
                // traversing(new_x, new_y);
            } else if (mv.pixels[j - 1][k + 1] == 1) {
                mv.pixels[j][k] = 0;
                new_x = j - 1;
                new_y = k + 1;
                System.out.println("Current node: " + new_x + " " + new_y);
                //mv.goBot(new_x, new_y);
                //traversing(new_x, new_y);
            } else if (mv.pixels[j - 1][k - 1] == 1) {
                mv.pixels[j][k] = 0;
                new_x = j - 1;
                new_y = k - 1;
                System.out.println("Current node: " + new_x + " " + new_y);
                //mv.goBot(new_x, new_y);
                //traversing(new_x, new_y);
            }
            mv.goBot(new_x, new_y);
        }
    }

}
