package NecroWeapon;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.io.*;
import java.awt.Color;
public class SpriteData {
    //so how do we store stuff internally
    //hmm
    //i'm thinking:
    //store every frame as a seperate bufferedimage
    //rgb channels of aux and shine as seperate ones
    private BufferedImage mainLayer;
    private BufferedImage glowLayer;
    private BufferedImage[] shineLayers; //0 = r, 1 = g, 2 = b.
    private BufferedImage[] auxLayers; //same as above
    private BufferedImage staffGem; //and absolutely nothing else
    
    private int width; //convenience value
    private int height;
    
    //split one bufferedImage into 3 monochrome images, one for each color channel, and returns them in an array
    //this exact array can be put back into mergeRGB
    //[0] = red, [1] = green, [2] = blue
    public static BufferedImage[] splitRGB(BufferedImage rawImage) {
        int width = rawImage.getWidth();
        int height = rawImage.getHeight();
        int type = rawImage.getType();
        BufferedImage[] rgb = new BufferedImage[]{
            new BufferedImage(width, height, type),
            new BufferedImage(width, height, type),
            new BufferedImage(width, height, type),
        };
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = rawImage.getRGB(x, y);
                Color c = new Color(argb, true);
                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();
                int a = c.getAlpha();
                rgb[0].setRGB(x, y, new Color(r, r, r, a).getRGB());
                rgb[1].setRGB(x, y, new Color(g, g, g, a).getRGB());
                rgb[2].setRGB(x, y, new Color(b, b, b, a).getRGB());
            }
        }
        return(rgb);
    }
    
    //merge 3 images r, g and b channels respectively into one image and returns the resulting image
    //takes the first image's alpha value for the final alpha
    //make sure all 3 images are the same size!
    public static BufferedImage mergeRGB(BufferedImage[] rgb) {
        int width = rgb[0].getWidth();
        int height = rgb[0].getHeight();
        int type = rgb[0].getType();
        BufferedImage merged = new BufferedImage(width, height, type);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int r = new Color(rgb[0].getRGB(x, y), true).getRed();
                int g = new Color(rgb[1].getRGB(x, y), true).getGreen();
                int b = new Color(rgb[2].getRGB(x, y), true).getBlue();
                int a = new Color(rgb[0].getRGB(x, y), true).getAlpha();
                merged.setRGB(x, y, new Color(r, g, b, a).getRGB());
            }
        }
        return(merged);
    }
    
    //returns image but all r, g and b values are 0
    //alpha is perserved
    public static BufferedImage silhouette(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int type = image.getType();
        BufferedImage dark = new BufferedImage(width, height, type);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int a = new Color(image.getRGB(x, y), true).getAlpha();
                dark.setRGB(x, y, new Color(0, 0, 0, a).getRGB());
            }
        }
        return(dark);
    }
    
    //create a white outline image
    public static BufferedImage outline(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int type = image.getType();
        BufferedImage glow = new BufferedImage(width, height, type);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int a = new Color(image.getRGB(x, y), true).getAlpha();
                if (a == 0) {
                    //get the four neighboring colors, check if they have a positive alpha value
                    boolean hasAlpha = false;
                    //left
                    if (x - 1 > -1) { //make sure we dont hop out of bounds!
                        int t = new Color(image.getRGB(x - 1, y), true).getAlpha();
                        if (t > 0) {
                            hasAlpha = true;
                        }
                    }
                    if (!hasAlpha) {
                        //right
                        if (x + 1 < width ) { //make sure we dont hop out of bounds!
                            int t = new Color(image.getRGB(x + 1, y), true).getAlpha();
                            if (t > 0) {
                                hasAlpha = true;
                            }
                        }
                        if (!hasAlpha) {
                            //up?
                            if (y - 1 > -1 ) { //make sure we dont hop out of bounds!
                                int t = new Color(image.getRGB(x, y - 1), true).getAlpha();
                                if (t > 0) {
                                    hasAlpha = true;
                                }
                            }
                            if (!hasAlpha) {
                                //down?
                                if (y + 1 < height ) { //make sure we dont hop out of bounds!
                                    int t = new Color(image.getRGB(x, y + 1), true).getAlpha();
                                    if (t > 0) {
                                        hasAlpha = true;
                                    }
                                }
                            }
                        }
                    }
                    if (hasAlpha) {
                        glow.setRGB(x, y, new Color(255, 255, 255, 255).getRGB());
                    }
                }
                // glow.setRGB(x, y, new Color(255, 255, 255, 255).getRGB());
            }
        }
        return(glow);
    }
    
    //stick second image onto first image
    //moddifies the first image directly, so doesnt return anything
    public static void attachImage(BufferedImage base, BufferedImage add, int offX, int offY) {
        // int width = add.getWidth();
        // int height = add.getHeight();
        // for (int x = 0; x < width; x++) {
        //     for (int y = 0; y < height; y++) {
        //         base.setRGB(x+offX, y+offY, add.getRGB(x, y));
        //     }
        // }
        base.getGraphics().drawImage(add, offX, offY, null);
        base.getGraphics().dispose();
    }
    
    //for template images
    //returns a bufferedimage with increased clearance if necessary
    public static BufferedImage makeGlowClearance(BufferedImage image) {
        boolean extendLeft = false;
        boolean extendRight = false;
        boolean extendUp = false;
        boolean extendDown = false;
        int width = image.getWidth();
        int height = image.getHeight();
        //top and bottom edges
        for (int x = 0; x < width; x++) {
            int top = new Color(image.getRGB(x, 0), true).getAlpha();
            if (top > 0) {
                extendUp = true;
            }
            int bottom = new Color(image.getRGB(x, height - 1), true).getAlpha();
            if (bottom > 0) {
                extendDown = true;
            }
            if (extendUp && extendDown) {
                break;
            }
        }
        //left and right edges
        for (int y = 0; y < height; y++) {
            int left = new Color(image.getRGB(0, y), true).getAlpha();
            if (left > 0) {
                extendLeft = true;
            }
            int right = new Color(image.getRGB(width - 1, y), true).getAlpha();
            if (right > 0) {
                extendRight = true;
            }
            if (extendLeft && extendRight) {
                break;
            }
        }
        if (extendLeft || extendDown || extendUp || extendRight) { //this is a reference to something
            int targetWidth = width;
            int targetHeight = height;
            int offX = 0;
            int offY = 0;
            if (extendLeft) {targetWidth++;offX++;}
            if (extendRight) {targetWidth++;}
            if (extendUp) {targetHeight++;offY++;}
            if (extendDown) {targetHeight++;}
            BufferedImage expanded = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            attachImage(expanded, image, offX, offY);
            return(expanded);
        }
        //no extra clearance needed
        return(image);
    }
    
    //returns a compiled BufferedImage in the format CotND uses
    //throwing this at the game should work.
    public BufferedImage exportNecro() {
        BufferedImage necro = new BufferedImage(width, height*5, BufferedImage.TYPE_INT_ARGB);
        attachImage(necro, mainLayer, 0, 0);
        attachImage(necro, mergeRGB(shineLayers), 0, height);
        attachImage(necro, glowLayer, 0, height*2);
        attachImage(necro, mergeRGB(auxLayers), 0, height*3);
        attachImage(necro, staffGem, 0, height*4);
        return(necro);
    }
    
    //returns a compiled BufferedImage in this program's "split" format
    //this format is easier for spriters to understand and edit (hopefully)
    public BufferedImage exportSplit() {
        BufferedImage split = new BufferedImage(width*5, height*2, BufferedImage.TYPE_INT_ARGB);
        attachImage(split, mainLayer, 0, 0);
        attachImage(split, shineLayers[0], width, 0);
        attachImage(split, shineLayers[1], width*2, 0);
        attachImage(split, shineLayers[2], width*3, 0);
        attachImage(split, glowLayer, 0, height);
        attachImage(split, auxLayers[0], width, height);
        attachImage(split, auxLayers[1], width*2, height);
        attachImage(split, auxLayers[2], width*3, height);
        attachImage(split, staffGem, width*4, 0);
        return(split);
    }
    
    //parses a Crypt of the Necrodancer weapon template image or a split-format weapon template image into a SpriteData object
    //1 for necrodancer's format, 2 for this program's split format, 3 for a 1x1 generic template
    //of note: shine and aux layers are split into 3 in this object
    public SpriteData(BufferedImage rawImage, int type) { //true is split, false is game
        //convert to a specific format for out purposes.
        int w = rawImage.getWidth();
        int h = rawImage.getHeight();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(rawImage, 0, 0, null);
        image.getGraphics().dispose();
        if (type == 2) {
            width = image.getWidth() / 5;
            height = image.getHeight() / 2;
            mainLayer = image.getSubimage(0,0,width,height);
            shineLayers = new BufferedImage[]{
                image.getSubimage(width,0,width,height),
                image.getSubimage(width*2,0,width,height),
                image.getSubimage(width*3,0,width,height),
            };
            glowLayer = image.getSubimage(0,height,width,height);
            auxLayers = new BufferedImage[]{
                image.getSubimage(width,height,width,height),
                image.getSubimage(width*2,height,width,height),
                image.getSubimage(width*3,height,width,height),
            };
            staffGem = image.getSubimage(width*4,0,width,height);
        } else if (type == 1) { //game format
            width = image.getWidth();
            height = image.getHeight() / 5;
            mainLayer = image.getSubimage(0,0,width,height);
            shineLayers = splitRGB(image.getSubimage(0,height,width,height));
            glowLayer = image.getSubimage(0,height*2,width,height);
            auxLayers = splitRGB(image.getSubimage(0,height*3,width,height));
            staffGem = image.getSubimage(0,height*4,width,height);
        } else { //1x1 template
            //expand if needed
            image = makeGlowClearance(image);
            width = image.getWidth();
            height = image.getHeight();
            mainLayer = image;
            BufferedImage silhouette = silhouette(image);
            shineLayers =  new BufferedImage[]{silhouette,silhouette,silhouette};
            glowLayer = outline(image);
            auxLayers =  new BufferedImage[]{silhouette,silhouette,silhouette};
            staffGem = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
    }
    
    //legacy constructor
    public SpriteData(BufferedImage rawImage, boolean isSplit) { //true is split, false is game
        if (isSplit) {
            width = rawImage.getWidth() / 5;
            height = rawImage.getHeight() / 2;
            mainLayer = rawImage.getSubimage(0,0,width,height);
            shineLayers = new BufferedImage[]{
                rawImage.getSubimage(width,0,width,height),
                rawImage.getSubimage(width*2,0,width,height),
                rawImage.getSubimage(width*3,0,width,height),
            };
            glowLayer = rawImage.getSubimage(0,height,width,height);
            auxLayers = new BufferedImage[]{
                rawImage.getSubimage(width,height,width,height),
                rawImage.getSubimage(width*2,height,width,height),
                rawImage.getSubimage(width*3,height,width,height),
            };
            staffGem = rawImage.getSubimage(width*4,0,width,height);
        } else { //game format
            width = rawImage.getWidth();
            height = rawImage.getHeight() / 5;
            mainLayer = rawImage.getSubimage(0,0,width,height);
            shineLayers = splitRGB(rawImage.getSubimage(0,height,width,height));
            glowLayer = rawImage.getSubimage(0,height*2,width,height);
            auxLayers = splitRGB(rawImage.getSubimage(0,height*3,width,height));
            staffGem = rawImage.getSubimage(0,height*4,width,height);
        }
    }
    
    //convenience constructor
    //handles file memes for you
    //(i didnt end up using this lol)
    public SpriteData(String imagePath, boolean isSplit) throws IOException {
        this(ImageIO.read(new File(imagePath)), isSplit);
    }
    
    //dont think i need to explain these two lol
    public int getWidth(){return(width);}
    public int getHeight(){return(height);}
}
