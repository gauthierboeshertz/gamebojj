package ch.epfl.gameboj.gui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.event.*;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import java.util.Date;
import java.util.EventObject;
import ch.epfl.gameboj.GameBoy;
import ch.epfl.gameboj.Preconditions;
import ch.epfl.gameboj.component.cartridge.Cartridge;
import ch.epfl.gameboj.component.lcd.LcdController;
import ch.epfl.gameboj.component.lcd.LcdImage;
import ch.epfl.gameboj.gui.SceneCaptureUtility.PlaybackSettings;
import javafx.application.Application;
import javafx.application.Application.Parameters;
import javafx.embed.swing.SwingFXUtils;
import ch.epfl.gameboj.component.Joypad;
import ch.epfl.gameboj.component.Joypad.Key;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.animation.AnimationTimer;

public final class Main extends Application {

   
/**
 * Map utilisée pour les "lettres" du joypad c'est à dire les touches ou on ne peut pas utiliser les KeyCode
 */
    private static final HashMap<String, Joypad.Key> mapLetters = new HashMap<>(
            4);
    /**
     * Map utilisée pour les touches direcctionnelles du joypad
     */
    private static final HashMap<KeyCode, Joypad.Key> mapArrows = new HashMap<>(
            4);

    // private static final HashMap<>
    public static void main(String[] args) {
        Application.launch(args);
    }

    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage stage) throws Exception {

        if (getParameters().getRaw().size() != 1) {
            System.exit(1);
        }

        mapLetters.put("a", Key.A);
        mapLetters.put("b", Key.B);
        mapLetters.put("s", Key.START);
        mapLetters.put(" ", Key.SELECT);

        mapArrows.put(KeyCode.UP, Key.UP);
        mapArrows.put(KeyCode.DOWN, Key.DOWN);
        mapArrows.put(KeyCode.RIGHT, Key.RIGHT);
        mapArrows.put(KeyCode.LEFT, Key.LEFT);

        String gameName = getParameters().getRaw().get(0);
        File romFile = new File(gameName);
        GameBoy gb = new GameBoy(Cartridge.ofFile(romFile));
        Joypad joypad = gb.joypad();

        // Group root = new Group();
        ImageView imageView = new ImageView();
        BorderPane border = new BorderPane(imageView);
        Scene scene = new Scene(border);
        // imageView.setFitHeight( 2*LcdController.LCD_HEIGHT);
        // imageView.setFitWidth(2 * LcdController.LCD_WIDTH);
        imageView.setImage(
                ImageConverter.convert(gb.getLcdController().currentImage()));

        imageView.fitWidthProperty().bind(scene.widthProperty());
        imageView.fitHeightProperty().bind(scene.heightProperty());

        stage.setWidth(2 * LcdController.LCD_WIDTH);
        stage.setHeight(2 * LcdController.LCD_HEIGHT);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.minWidthProperty().bind(scene.heightProperty());
        stage.minHeightProperty().bind(scene.widthProperty());
        stage.setTitle("gameboj");
        stage.show();
        Date d = new Date();
    
    
 scene.setOnKeyPressed(e -> {
            Joypad.Key key = mapArrows.get(e.getCode());
            Joypad.Key keyLetter = mapLetters.get(e.getText());
            if (key != null) {
                joypad.keyPressed(key);
            } else {
                if (mapLetters.get(e.getText()) != null) {
                    joypad.keyPressed(keyLetter);
                }
                else {
                  
                    if (e.getText().equals("i")) {
                        LcdImage li = gb.getLcdController().currentImage();
                        BufferedImage i = new BufferedImage(li.getWidth(),
                                li.getHeight(), BufferedImage.TYPE_INT_RGB);
                        
                        try {         
                            ImageIO.write(SwingFXUtils.fromFXImage(ImageConverter.convert(li), i), "png", new File("screenshot taken " + d + ".png"));
                            
                        } catch (IOException e1) {

                        }
                    }
                }
                if(e.getText().equals("l")) {
                    
                }

            }
        });

        scene.setOnKeyReleased(e -> {
            Joypad.Key key = mapArrows.get(e.getCode());
            Joypad.Key keyLetter = mapLetters.get(e.getText());
            if (key != null) {
                joypad.keyReleased(key);
            } else {
                if (mapLetters.get(e.getText()) != null) {
                    joypad.keyReleased(keyLetter);
                } else {

                }

            }
        });
        

        long start = System.nanoTime();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsed = now - start;
                long elapsedCycles = (long) (elapsed
                        * GameBoy.CYCLES_PER_NANOSECOND);

                gb.runUntil(elapsedCycles);
                imageView.setImage(ImageConverter
                        .convert(gb.getLcdController().currentImage()));

            }
        };
        timer.start();

    }

}
