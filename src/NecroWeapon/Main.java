package NecroWeapon;
import java.io.*;
// import java.util.Scanner; // Import the Scanner class to read text files
import java.awt.image.BufferedImage;
import javax.imageio.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class Main implements ActionListener {
    
    JFrame frame;
    JPanel contentPane;
    JTextField imagePathField;
    JButton openFileBrowser;
    ButtonGroup radioGroup;
    JRadioButton autoDetectButton;
    JRadioButton gameToSplitButton;
    JRadioButton splitToGameButton;
    JRadioButton templateToSplitButton;
    JButton convert;
    
    int mode = 0; //0 = auto, 1 = to split, 2 = to game
    
    public Main() {
        
        /* Create and set up the frame */
        frame = new JFrame("Weapon Template Helper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        /* Create a content pane with a BoxLayout and empty borders */
        contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        contentPane.setBackground(Color.white);
        
        //todo add all the things!
        //we need:
        //textbox with image path
        //button that opens file browser
        //two radial buttons (game to split, split to game)
        //convert button!
        
        //path field
        imagePathField = new JTextField(20);
        imagePathField.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        contentPane.add(imagePathField);
        
        //open browser button
        openFileBrowser = new JButton("Open file browser...");
        openFileBrowser.setActionCommand("browser");
        openFileBrowser.setAlignmentX(JButton.CENTER_ALIGNMENT);
        openFileBrowser.addActionListener(this);
        contentPane.add(openFileBrowser);
        
        //radial buttons
        //i just realised theyre actually called radio buttons
        //oops
        radioGroup = new ButtonGroup();
        
        autoDetectButton = new JRadioButton("Auto-detect");
        autoDetectButton.setActionCommand("modeAuto");
        autoDetectButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        autoDetectButton.setSelected(true);
        autoDetectButton.addActionListener(this);
        radioGroup.add(autoDetectButton);
        contentPane.add(autoDetectButton);
        
        gameToSplitButton = new JRadioButton("Game to Split");
        gameToSplitButton.setActionCommand("modeGameToSplit");
        gameToSplitButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        gameToSplitButton.addActionListener(this);
        radioGroup.add(gameToSplitButton);
        contentPane.add(gameToSplitButton);
        
        splitToGameButton = new JRadioButton("Split to Game");
        splitToGameButton.setActionCommand("modeSplitToGame");
        splitToGameButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        splitToGameButton.addActionListener(this);
        radioGroup.add(splitToGameButton);
        contentPane.add(splitToGameButton);
        
        templateToSplitButton = new JRadioButton("Template To Split");
        templateToSplitButton.setActionCommand("modeTemplateToSplit");
        templateToSplitButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        templateToSplitButton.addActionListener(this);
        radioGroup.add(templateToSplitButton);
        contentPane.add(templateToSplitButton);
        
        //todo remove
        // JRadioButton debugButton = new JRadioButton("Debug");
        // debugButton.setActionCommand("modeDebug");
        // debugButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        // debugButton.addActionListener(this);
        // radioGroup.add(debugButton);
        // contentPane.add(debugButton);
        
        //open browser button
        convert = new JButton("Convert");
        convert.setActionCommand("convert");
        convert.setAlignmentX(JButton.CENTER_ALIGNMENT);
        convert.addActionListener(this);
        contentPane.add(convert);
        
        /* Add content pane to frame */
        frame.setContentPane(contentPane);
        
        /* Size and display frame */
        frame.pack();
        frame.setVisible(true);
    }
    
    void handleError(Exception e, String message) {
        imagePathField.setText("");
        StackTraceElement[] stackTrace = e.getStackTrace();
        String st = "";
        for (int index = 0; index < stackTrace.length; index++) {
            st = st + stackTrace[index] + "\n";
        }
        JOptionPane.showMessageDialog(frame, message+"\n"+e+"\n\nStack trace:\n"+st);
    }
    
    public void actionPerformed(ActionEvent event) {
        String eventName = event.getActionCommand();
        System.out.println(eventName);
        if (eventName.equals("browser")) {
            File selectedFile;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                imagePathField.setText(selectedFile.getAbsolutePath());
            }
        } else if (eventName.equals("modeAuto")) {
            mode = 0;
        } else if (eventName.equals("modeGameToSplit")) {
            mode = 1;
        } else if (eventName.equals("modeSplitToGame")) {
            mode = 2;
        } else if (eventName.equals("modeTemplateToSplit")) {
            mode = 3;
        } else if (eventName.equals("convert")) {
            try {
                File sourceFile = new File(imagePathField.getText()); //file specified
                //okay we need to get the target file set up
                //im just going to do split-name
                String name = sourceFile.getName();
                BufferedImage rawImage = ImageIO.read(sourceFile);
                int targetMode;
                if (mode == 0) {
                    int width = rawImage.getWidth();
                    int height = rawImage.getHeight();
                    if (height == width) {
                        targetMode = 3;
                    } else if (height > width) { //game, probably.
                        targetMode = 1;
                    } else { //split, probably.
                        targetMode = 2;
                    }
                } else {
                    targetMode = mode;
                }
                System.out.println(targetMode);
                if (targetMode == 1) {
                    //game to split
                    File targetFile = new File(sourceFile.getParentFile(), "split-"+sourceFile.getName()); //output file
                    SpriteData sprite = new SpriteData(rawImage, 1); //sprite data
                    ImageIO.write(sprite.exportSplit(),"png", targetFile);
                } else if (targetMode == 2) {
                    //split to game
                    File targetFile = new File(sourceFile.getParentFile(), "merged-"+sourceFile.getName()); //output file
                    SpriteData sprite = new SpriteData(rawImage, 2); //sprite data
                    ImageIO.write(sprite.exportNecro(),"png", targetFile);
                } else {
                    //1x1 template to split!
                    File targetFile = new File(sourceFile.getParentFile(), "split-"+sourceFile.getName()); //output file
                    SpriteData sprite = new SpriteData(rawImage, 3); //sprite data
                    ImageIO.write(sprite.exportSplit(),"png", targetFile);
                }
            } catch(IOException e) { //catch io issues
                handleError(e, "IO Error! Make sure file path is correct and file exists!\nError:");
            } catch(Exception e) { //catch any issues
                handleError(e, "Error:");
            }
        }
    }
    
    private static void runGUI(){
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        Main gui = new Main();
    }
    
    public static void main(String[] args) {
        //run gui
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                runGUI();
            }
        });
    }
}
