/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package myvreplib;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
class converter {
    
    converter() throws IOException{
            
        BufferedImage image = ImageIO.read(new File("newImage.png"));
        byte[][] pixels = new byte[image.getWidth()][];

        for (int x = 0; x < image.getWidth(); x++) {
            pixels[x] = new byte[image.getHeight()];

            for (int y = 0; y < image.getHeight(); y++) {
                pixels[x][y] = (byte) (image.getRGB(x, y) == 0xFFFFFFFF ? 0 : 1);
            }
        }

        for (int x = 0; x < image.getWidth(); x++) {
            

            for (int y = 0; y < image.getHeight(); y++) {
                System.out.print(pixels[y][x]);
            }
            System.out.println(" ");
        }
    }
    
    public static void main(String[] args) throws IOException {
        new converter();
    }
}
