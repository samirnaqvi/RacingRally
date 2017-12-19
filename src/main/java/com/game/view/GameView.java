package com.game.view;

import com.game.Main;
import com.game.controller.DataController;
import com.game.controller.PowerupController;
import com.game.model.enums.PowerupType;
import com.game.model.objects.PowerUp;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GameView {
    private static int x;
    private static int y;
    private static int enemyX;
    private static int enemyY;
    private static int deltaX;
    private static int travelled = 0;
    private static int lengthOfMap = DataController.map.getLength();
    private static AtomicInteger punchStrength = new AtomicInteger(25);
    private static AtomicInteger plusminusdelta = new AtomicInteger(10);
    private static AtomicInteger enemydelta = new AtomicInteger(10);
    private static AtomicInteger deltaPunch = new AtomicInteger(30);
    private static AtomicInteger enemydeltaPunch = new AtomicInteger(30);
    private static AtomicInteger timeInGame = new AtomicInteger(0);
    private static AtomicInteger score = new AtomicInteger(0);
    private static AtomicBoolean carIsComeback = new AtomicBoolean(false);
    private static AtomicInteger carRatio = new AtomicInteger(0);
    private static ImageView powerUpView;
    private static PowerUp currentPowerUp;
    private static AnchorPane sPane;
    private static volatile boolean mark = false;
    private static volatile double damageCoef = 1.5;
    private static volatile boolean pauseController = false;
    private static volatile boolean powerupFlag = true;
    private static volatile boolean curved = false;
    private static volatile boolean curves = true;
    private static boolean isBoosted = false;
    private static boolean userIsWinner = false;
    private static boolean game = false;
    private static Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            timeInGame.incrementAndGet();
        }
    }));
    private static GameView gameView;


    private static void processMapByDelta(final ImageView road1, final ImageView road2, final ImageView finish, final int plusminusdelta) {
        //Render new map after end of other part.
        if (road1.getY() >= -9.0 || road2.getY() >= 591.0) {
            road1.setY(-600);
            road2.setY(0);
            if (curved) {
                //Switch image of road if in curve.
                road1.setImage(road2.getImage());
                curved = false;
            }
        }
        Task updateMap = new Task() {
            @Override
            protected Object call() throws Exception {
                //dynamic render of map.
                road1.setY(road1.getY() + plusminusdelta);
                road2.setY(road2.getY() + plusminusdelta);
                finish.setY(-DataController.map.getLength() + travelled + 350);
                return null;
            }
        };
        Platform.runLater(updateMap);
    }


    //activating powerups method.
    private static void activatePowerup() {
        Task update = new Task() {
            @Override
            protected Object call() throws Exception {
                sPane.getChildren().remove(powerUpView);
                return null;
            }
        };
        Platform.runLater(update);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    currentPowerUp.setOnMap(false);
                    powerupFlag = true;
                    System.out.println("Booster eater created " + toString());
                    if (currentPowerUp.getType() == PowerupType.BOOSTER) {
                        isBoosted = true;
                        plusminusdelta.addAndGet(5);
                        Thread.sleep(3000);
                        plusminusdelta.addAndGet(-5);
                        isBoosted = false;
                        score.addAndGet(300);
                    } else if (currentPowerUp.getType() == PowerupType.PUNCHER) {
                        int cache = deltaPunch.get();
                        deltaPunch.addAndGet(punchStrength.get());
                        Thread.sleep(5000);
                        deltaPunch.set(cache);
                        score.addAndGet(300);
                    } else if (currentPowerUp.getType() == PowerupType.OFFROAD) {
                        isBoosted = true;
                        if (plusminusdelta.get() < 9) plusminusdelta.set(10);
                        Thread.sleep(5000);
                        isBoosted = false;
                        score.addAndGet(300);
                    }
                } catch (Exception e) {
                    DataController.exLogger.log(e.getMessage());
                } finally {
                    System.out.println("Thread eater ended " + toString());
                }
            }
        }).start();
    }


    //Method for calculation collision with powerups.
    private static boolean inRange(int xPwrUp) {
        if (x == xPwrUp + 50) return true;
        else if (x + 100 == xPwrUp) return true;
        else if (xPwrUp >= x && xPwrUp <= x + 100) return true;
        else return false;
    }

    //Method for placing powerup at map.
    private static void placePowerup(final ImageView powerup) {
        Random random = new Random();
        int x = random.nextInt(300) + 50;
        powerup.setFitHeight(50);
        powerup.setFitWidth(50);
        powerup.setX(x);
        powerupFlag = true;
        Task addNewPowerupTask = new Task() {
            @Override
            protected Object call() throws Exception {
                sPane.getChildren().add(powerup);
                return null;
            }
        };
        Platform.runLater(addNewPowerupTask);
    }

    //Method for generating random powerup and calling placing method.
    private static void generatePowerup() {

        currentPowerUp = PowerupController.randomGenerate();
        currentPowerUp.setOnMap(true);
        powerUpView = new ImageView(currentPowerUp.getImage());
        placePowerup(powerUpView);

    }

    public static Scene getScene() {
        try {
            gameView = new GameView();
            //setting timer cycle count
            timeline.setCycleCount(Animation.INDEFINITE);
            final AnchorPane pane = new AnchorPane();
            sPane = pane;
            pane.setFocusTraversable(true);
            pane.setMaxHeight(550);
            pane.setPrefHeight(550);
            //Loading game resources
            final ImageView start = new ImageView(
                    new javafx.scene.image.Image(
                            Main.class.getResource("/atstart.png").toURI().toString()));
            final ImageView road = new ImageView(
                    new javafx.scene.image.Image(
                            Main.class.getResource("/baseRoad.jpg").toURI().toString()));
            final ImageView roadTwo = new ImageView(
                    new javafx.scene.image.Image(
                            Main.class.getResource("/baseRoad.jpg").toURI().toString()));
            final ImageView leftCurve = new ImageView(
                    new javafx.scene.image.Image(
                            Main.class.getResource("/leftcurve.jpg").toURI().toString()));
            final ImageView rightCurve = new ImageView(
                    new javafx.scene.image.Image(
                            Main.class.getResource("/rightcurve.jpg").toURI().toString()));
            final ImageView finish = new ImageView(
                    new javafx.scene.image.Image(
                            Main.class.getResource("/finish.png").toURI().toString()));
            final AudioClip soundStart = new AudioClip(Main.class.getResource("/start.mp3").toURI().toString());
            final AudioClip soundDrifting = new AudioClip(Main.class.getResource("/drifting.wav").toURI().toString());
            final AudioClip soundCrash = new AudioClip(Main.class.getResource("/crash.wav").toURI().toString());
            final AudioClip soundBackground = new AudioClip(Main.class.getResource("/background.mp3").toURI().toString());
            final AudioClip win = new AudioClip(Main.class.getResource("/win.wav").toURI().toString());
            final AudioClip lost = new AudioClip(Main.class.getResource("/lost.wav").toURI().toString());
            final Label label = new Label((double) travelled / 100 + " KM \\ " + (double) DataController.map.getLength() / 100);
            final Label countdown = new Label("3");
            label.setAlignment(Pos.TOP_LEFT);
            label.setStyle("-fx-font-size: 11pt;\n" +
                    "    -fx-font-family: \"Segoe UI Semibold\";\n" +
                    "    -fx-text-fill: white;\n" +
                    "    -fx-opacity: 1;");
            countdown.setStyle("-fx-font-size: 50pt;\n" +
                    "    -fx-font-family: \"Segoe UI Semibold\";\n" +
                    "    -fx-text-fill: black;\n" +
                    "    -fx-opacity: 1;");
            road.setY(-50);
            roadTwo.setY(-650);
            finish.setY(-DataController.map.getLength() + travelled + 350);
            //Loading cars
            final ImageView car = new ImageView(DataController.car.getCarModel());
            final ImageView enemy = new ImageView(DataController.enemyCar.getCarModel());
            enemyX = 90;
            enemyY = 350;
            enemy.setX(enemyX);
            enemy.setY(enemyY);
            enemy.setFitWidth(100);
            enemy.setFitHeight(200);
            //Setuping difficulty
            if (DataController.difficulty == 1) enemydelta.set(10);
            else if (DataController.difficulty == 2) enemydelta.set(11);
            else if (DataController.difficulty == 3) enemydelta.set(11);
            x = 210;
            y = 350;
            car.setX(x);
            car.setY(y);
            car.setFitWidth(100);
            car.setFitHeight(200);
            car.setFocusTraversable(true);
            //Key handler
            Thread keyHandling = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        car.setOnKeyPressed(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(final KeyEvent event) {

                                if (event.getCode() == KeyCode.A && game) {
                                    deltaX = -plusminusdelta.get();
                                    if (x + deltaX > 0 - plusminusdelta.get()) x = x + deltaX;
                                } else if (event.getCode() == KeyCode.D && game) {
                                    deltaX = plusminusdelta.get();
                                    if (x + deltaX < 300 + plusminusdelta.get()) x = x + deltaX;
                                } else if (event.getCode() == KeyCode.END) {
                                    game = false;
                                    clear();
                                    soundBackground.stop();
                                    TitleView.changeScene(GameView.getScene());
                                    //pause mode
                                } else if (event.getCode() == KeyCode.ESCAPE) {
                                    synchronized (this) {
                                        if (!pauseController) {
                                            System.out.println("Pause mode");
                                            pauseController = true;
                                            Platform.runLater(new Task<Void>() {
                                                @Override
                                                protected Void call() throws Exception {
                                                    Dialog dialog = new Dialog();
                                                    dialog.initStyle(StageStyle.UTILITY);
                                                    dialog.setContentText("PAUSE");
                                                    dialog.setHeaderText("Pause");
                                                    dialog.setHeight(300);
                                                    ButtonType ok = new ButtonType("Main menu", ButtonBar.ButtonData.OK_DONE);
                                                    ButtonType resume = new ButtonType("Resume", ButtonBar.ButtonData.OK_DONE);
                                                    dialog.getDialogPane().getButtonTypes().add(ok);
                                                    dialog.getDialogPane().getButtonTypes().add(resume);
                                                    Optional<ButtonType> result = dialog.showAndWait();
                                                    if (result.get() == ok) {
                                                        pauseController = false;
                                                        game = false;
                                                        clear();
                                                        soundBackground.stop();
                                                        TitleView.changeScene(MainMenuView.getScene());
                                                    } else {
                                                        pauseController = false;
                                                    }
                                                    dialog.setOnCloseRequest(new EventHandler<DialogEvent>() {
                                                        @Override
                                                        public void handle(DialogEvent event) {
                                                            pauseController = false;
                                                        }
                                                    });
                                                    return null;
                                                }
                                            });
                                        } else {
                                            DataController.usLogger.log("Unpause");
                                            pauseController = false;
                                            notifyAll();
                                            DataController.usLogger.log("Notified");
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            });
            keyHandling.start();
            pane.getChildren().addAll(road, roadTwo, start, countdown, finish, car, enemy, label);
            start.setX(0);
            start.setY(0);
            countdown.setPadding(new Insets(180, 0, 0, 70));
            //Finish dialog
            final Task finishDialogTask = new Task() {
                @Override
                protected Object call() throws Exception {
                    Dialog dialog = new Dialog();
                    dialog.initStyle(StageStyle.UTILITY);
                    String imageToLoad;
                    //Selecting winner
                    if (userIsWinner) {
                        imageToLoad = "/won.png";
                        win.play();
                    } else {
                        imageToLoad = "/lose.png";
                        lost.play();
                    }
                    dialog.setContentText("You total score: " + score);
                    dialog.setHeaderText("You finished " + (userIsWinner ? "First !" : "Second!"));
                    try {
                        dialog.setGraphic(new ImageView(
                                new javafx.scene.image.Image(
                                        Main.class.getResource(imageToLoad).toURI().toString())));
                    } catch (URISyntaxException e) {
                        DataController.exLogger.log(e.getMessage());
                    }
                    dialog.setHeight(300);
                    ButtonType ok = new ButtonType("Main menu", ButtonBar.ButtonData.OK_DONE);
                    ButtonType restart = new ButtonType("Restart", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().add(ok);
                    dialog.getDialogPane().getButtonTypes().add(restart);
                    Optional<ButtonType> result = dialog.showAndWait();
                    if (result.get() == ok) {
                        //Menu redirect + writing stats to Data Controller
                        if (null != DataController.user && score.get() > DataController.user.getHighestScore()) {
                            DataController.user.setHighestScore(score.get());
                            DataController.writeStats();
                        }
                        clear();
                        TitleView.changeScene(MainMenuView.getScene());
                    } else {
                        clear();
                        TitleView.changeScene(GameView.getScene());
                    }
                    dialog.setOnCloseRequest(new EventHandler<DialogEvent>() {
                        @Override
                        public void handle(DialogEvent event) {
                            //Back to menu when close by clicking on cross.
                            if (null != DataController.user && score.get() > DataController.user.getHighestScore()) {
                                DataController.user.setHighestScore(score.get());
                                DataController.writeStats();
                            }
                            clear();
                            TitleView.changeScene(MainMenuView.getScene());
                        }
                    });
                    return null;
                }
            };
            final LinkedList<String> listCountdown = new LinkedList<>();
            listCountdown.addAll(Arrays.asList(new String[]{"3", "2", "1", "GO!"}));
            //Main thread of game.
            Thread main = new Thread(new Runnable() {
                @Override
                public void run() {
                    lengthOfMap = DataController.map.getLength();
                    System.out.println("Thread main started");
                    try {
                        soundStart.play();
                        for (int i = 0; i < 5; i++) {
                            Platform.runLater(new Task<Void>() {
                                @Override
                                protected Void call() throws Exception {
                                    if (listCountdown.size() == 0) {
                                        pane.getChildren().remove(start);
                                        pane.getChildren().remove(countdown);
                                    } else {
                                        countdown.setText(listCountdown.getFirst());
                                        listCountdown.removeFirst();
                                    }
                                    return null;
                                }
                            });
                            Thread.sleep(1500);
                        }
                        soundBackground.play();
                    } catch (Exception e) {
                        DataController.exLogger.log(e.getMessage());
                    }
                    game = true;
                    timeInGame.set(0);
                    DataController.usLogger.log("Game started.");
                    while (game) {
                        try {
                            if (!pauseController) {
                                timeline.play();
                                /**
                                 * AI FUNCTION: Returning to road from offroad.
                                 */
                                if (enemyX <= 65 || enemyX >= 245)
                                    enemyY = enemyY + plusminusdelta.get() - enemydelta.get() + 1;
                                else enemyY = enemyY + plusminusdelta.get() - enemydelta.get();

                                /**
                                 * CURVES DETECTOR
                                 */
                                if (curves) {
                                    //for sinX
                                    if (lengthOfMap == 13300 && travelled == 4300) {
                                        road.setImage(leftCurve.getImage());
                                        System.out.println("curve 1");
                                        curved = true;
                                    } else if (lengthOfMap == 13300 && travelled == 7800) {
                                        road.setImage(rightCurve.getImage());
                                        curves = false;
                                        System.out.println("curve 2");
                                        curved = true;
                                        //for tanX
                                    } else if (lengthOfMap == 16200 && travelled == 8000) {
                                        road.setImage(rightCurve.getImage());
                                        curved = true;
                                        curves = false;
                                        //for y=x^3
                                    } else if (lengthOfMap == 13100 && travelled == 6200) {
                                        road.setImage(rightCurve.getImage());
                                        curved = true;
                                        curves = false;
                                        //for y=sqrt(x)
                                    } else if (lengthOfMap == 15100 && travelled == 4000) {
                                        roadTwo.setImage(rightCurve.getImage());
                                        curved = true;
                                        curves = false;
                                    }
                                }
                                /**
                                 * Game tick rate (1000ms (1second) \ 33 ms = 30.3~ s = 30FPS
                                 */
                                Thread.sleep(33);
                                Task updateLabels = new Task() {
                                    @Override
                                    protected Object call() throws Exception {
                                        //Updating scores, differences and path.
                                        label.setText(
                                                (double) travelled / 1000 +
                                                        " KM \\ " +
                                                        (double) DataController.map.getLength() / 1000 + "\n" +
                                                        "Your difference: " + (-(y - enemyY)) + "\n"
                                                        + "Score: " + score.get() + "\n"
                                                        + "Time: " + timeInGame.get() + "s.");
                                        return null;
                                    }
                                };
                                Platform.runLater(updateLabels);
                                if (!mark && DataController.difficulty >= 2)
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            System.out.println("Thread cheat started");
                                            mark = true;
                                            /**
                                             * AI FUNCTION: Hard AI will boost himself sometimes.
                                             */
                                            int timesEnemyBoost = DataController.map.getLength() / DataController.difficulty / 1000;
                                            while (timesEnemyBoost != 0 && game) {
                                                if (!pauseController) try {
                                                    Thread.sleep(1400);
                                                    System.out.println("speed");
                                                    int cache = enemydelta.get();
                                                    enemydelta.set(enemydelta.get() + DataController.difficulty - 1);
                                                    Thread.sleep(500);
                                                    timesEnemyBoost--;
                                                    enemydelta.set(cache);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }).start();

                                if (enemyY < (DataController.map.getLength() - travelled + 350)) enemy.setY(enemyY);
                                /**
                                 * Render methods
                                 */
                                enemy.setX(enemyX);
                                car.setX(x);
                                car.setRotate(carRatio.get());
                                processMapByDelta(road, roadTwo, finish, plusminusdelta.get());
                                /**
                                 * AI FUNCTION: 'Catch-up' processing.
                                 */
                                if (-(enemyY - y) > 100 && enemyX + 100 < x + 20) enemyX += 1;
                                /**
                                 * AI FUNCTION: Hard AI will comeback instantly from offroad.
                                 */
                                if (enemyX <= 65 && DataController.difficulty == 3 && carIsComeback.compareAndSet(false, true)) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            System.out.println("Thread returning from ground started");
                                            while (game)
                                                if (!pauseController)
                                                    try {
                                                        Thread.sleep(1200);
                                                        while (enemyX <= 65) {
                                                            Thread.sleep(33);
                                                            enemyX += 1;
                                                        }
                                                    } catch (InterruptedException e) {
                                                        DataController.exLogger.log(e.getMessage());
                                                    }
                                            carIsComeback.compareAndSet(true, false);
                                        }
                                    }).start();
                                }
                                /**
                                 * AI FUNCTION: Car will comeback from -100 X coordinate and
                                 * becomes more 'armored'
                                 */
                                if (enemyX < -100 && carIsComeback.compareAndSet(false, true)) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!pauseController) try {
                                                System.err.println("car is coming back from -100");
                                                for (int i = 0; i < 10; i++) {
                                                    Thread.sleep(33);
                                                    enemyX += 12;
                                                    enemyY -= 0.1;
                                                    if (enemyX + 100 == x) {
                                                        if (x + enemydeltaPunch.get() < 0) x = 0;
                                                        else if (x + enemydeltaPunch.get() > 300) x = 300;
                                                        else x += enemydeltaPunch.get() * 3;
                                                    }
                                                }
                                            } catch (Exception e) {
                                                DataController.exLogger.log(e.getMessage());
                                            }
                                        }
                                    }).start();
                                }
                                /**
                                 * Car collision processing
                                 */
                                if (x <= enemyX + 100 && Math.abs(y - enemyY) <= 200) {
                                    soundCrash.play();
                                    score.addAndGet(100);
                                    if (!carIsComeback.get()) {
                                        if (deltaPunch.get() - punchStrength.get() == enemydeltaPunch.get()) {
                                            enemyY += 15;
                                            /**
                                             * Drift processing
                                             */
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    while (game)
                                                        if (!pauseController)
                                                            try {
                                                                for (int i = 0; i < 3; i++) {
                                                                    enemydelta.set(enemydelta.decrementAndGet());
                                                                    Thread.sleep(1000);
                                                                }
                                                                for (int i = 0; i < 3; i++) {
                                                                    Thread.sleep(1500);
                                                                    enemydelta.set(enemydelta.incrementAndGet());
                                                                }
                                                            } catch (InterruptedException e) {
                                                                DataController.exLogger.log(e.getMessage());
                                                            }
                                                }
                                            }).start();
                                            //Processing with PUNCHER BOOSTER
                                            enemyY += 12 * damageCoef;
                                            enemyX -= deltaPunch.get() * 3;
                                        } else {
                                            //Without
                                            enemyY += 7 * damageCoef;
                                            enemyX -= deltaPunch.get() * 3;
                                        }
                                        x += deltaPunch.get() * damageCoef;
                                    } else {
                                        //Armored enemy punch processing.
                                        enemyX -= deltaPunch.get();
                                        enemyY -= 3 * damageCoef;
                                        double wannabeX = x + deltaPunch.get() * 2 * damageCoef;
                                        if (wannabeX > 300) wannabeX = 300;
                                        x = (int) wannabeX;
                                    }
                                    damageCoef += 0.15;
                                    final int localRatio = (int) (deltaPunch.get() - 5 * damageCoef);
                                    if (carRatio.get() == 0)
                                    /**
                                     * Drift after punch animation.
                                     */
                                        new Thread(new Runnable() {

                                            @Override
                                            public void run() {
                                                Platform.runLater(driftSoundPlayingTask);
                                                if (!pauseController) try {
                                                    for (int i = 0; i >= -localRatio; i--) {
                                                        Thread.sleep(33);
                                                        carRatio.set(i);
                                                        x += 1;
                                                    }
                                                    for (int i = -localRatio; i <= 0; i++) {
                                                        Thread.sleep(33);
                                                        carRatio.set(i);
                                                        x -= 1;
                                                    }
                                                } catch (InterruptedException e) {
                                                    DataController.exLogger.log(e.getMessage());
                                                }
                                            }

                                            Task driftSoundPlayingTask = new Task() {
                                                @Override
                                                protected Object call() throws Exception {
                                                    soundDrifting.play();
                                                    return null;
                                                }
                                            };
                                        }).start();
                                }
                                if (travelled != lengthOfMap) {
                                    /**
                                     * Offroad speed and path processing
                                     */
                                    if (!isBoosted) if (x >= 65 && x <= 245) {
                                        plusminusdelta.set(10);
                                        score.addAndGet(2);
                                        if (travelled % 10 != 0) travelled = (((travelled / 10)) * 10);
                                    } else {
                                        plusminusdelta.set(8);
                                        score.addAndGet(1);
                                    }
                                    travelled += plusminusdelta.get();
                                    //Powerup processing each timer.
                                    if (null != currentPowerUp && currentPowerUp.isOnMap()) {
                                        powerUpView.setY(powerUpView.getY() + plusminusdelta.get());
                                        if (powerUpView.getY() >= 350 && inRange((int) powerUpView.getX()) && powerupFlag) {
                                            powerupFlag = false;
                                            activatePowerup();
                                            System.out.println(currentPowerUp.getType());
                                        }
                                    }
                                }
                                /**
                                 * AI FUNCTION: Easy AI will boost himself at 4000 & 4700 metres traveled
                                 */
                                if (travelled == (travelled / 1000) * 1000) {
                                    if (!isBoosted) generatePowerup();
                                    if (travelled == 4000 && DataController.difficulty == 1) {
                                        enemydelta.incrementAndGet();
                                    } else if (travelled == 4700 && DataController.difficulty == 1) {
                                        enemydelta.decrementAndGet();
                                    }
                                }
                                /**
                                 * Calculating final scores and game ending
                                 */
                                if (travelled >= lengthOfMap && travelled != 0) {
                                    if (y < enemyY) userIsWinner = true;
                                    score.addAndGet(2000);
                                    double coef = 1.5;
                                    if (DataController.difficulty == 1) coef = 1.5;
                                    if (DataController.difficulty == 2) coef = 1.25;
                                    if (DataController.difficulty == 3) coef = 1.1;
                                    score.set((int) (score.get() / coef));
                                    double hundred = 1;
                                    while (hundred != 0) {
                                        Thread.sleep(100);
                                        soundBackground.setVolume(hundred);
                                        hundred = hundred - 0.25;
                                    }
                                    DataController.usLogger.log("Game is over.");
                                    soundBackground.stop();
                                    Platform.runLater(finishDialogTask);
                                    break;
                                }
                            } else {
                                timeline.stop();
                            }
                        } catch (Exception e) {
                            DataController.exLogger.log(e.getMessage());
                            System.err.println(e.getMessage());
                        }
                    }
                }
            });
            main.start();
            return new Scene(pane);
        } catch (Exception e) {
            DataController.exLogger.log(e.getMessage());
            System.exit(0);
            return null;
        }
    }

    private static void clear() {
        DataController.usLogger.log("Game session cleared.");
        score.set(0);
        travelled = 0;
        lengthOfMap = 0;
        curved = false;
        curves = true;
        mark = false;
        carIsComeback.set(false);
        isBoosted = false;
        damageCoef = 1.5;
        userIsWinner = false;
        game = false;
        timeInGame.set(0);
    }

}

