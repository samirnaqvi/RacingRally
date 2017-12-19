package com.game.controller;

import com.game.Main;
import com.game.model.User;
import com.game.model.objects.Car;
import com.game.model.objects.Map;
import javafx.scene.media.AudioClip;

import java.util.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.net.URL;
import java.sql.Connection;


public class DataController {//sets path for the game log and error log
    private static final String PATH = System.getProperty("user.home") + "/.rallyracing2d/users/";
    private static final String EXLOGFILEPATH = System.getProperty("user.home") + "/.rallyracing2d/tmpex.txt";
    private static final String /USLOGFILEPATH = System.getProperty("user.home") + "/.rallyracing2d/tmpus.txt";

    public static Car car;//instantiates all the objets including the loggers
    public static Car enemyCar;
    public static User user;
    public static Connection databaseConnection;
    public static List<User> usersFromDatabase;
    public static Map map;
    public static int difficulty;
    public static ArrayList<User> loadedUsers = new ArrayList<>();
    public static GameLogger exLogger = new GameLogger(EXLOGFILEPATH);
    public static GameLogger usLogger = new GameLogger(USLOGFILEPATH);

    public static User[] findUsers() {
        try {
            File file = new File(PATH + "/Guest.usr");//Creates A New File for the logs
            if (!file.exists()) {
                User[] users = new User[1];
                file = new File(PATH);
                file.mkdir();
                File guestUser = new File(PATH + "Guest.usr");
                guestUser.createNewFile();
                FileWriter fileWriter = new FileWriter(guestUser);
                fileWriter.write(Base64.getEncoder().encodeToString("2016rallyracing:Guest:0".getBytes()));
                fileWriter.close();
                users[0] = new User("Guest");
                loadedUsers.add(users[0]);
                return users;
            } else {
                file = new File(PATH);
                File[] files = file.listFiles();
                if (null != files && files.length != 0) {
                    User[] users = new User[files.length];
                    int counter = 0;
                    for (File file1 : files) {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file1));
                        String metaData = new String(Base64.getDecoder().decode(bufferedReader.readLine()));
                        if (!metaData.equals("null")) {
                            StringTokenizer stringTokenizer = new StringTokenizer(metaData, ":");
                            String codeword = stringTokenizer.nextToken();
                            if (codeword.equals("2016rallyracing")) {
                                String name = stringTokenizer.nextToken();// saves name and score to users, so that it can be printed in log
                                int score = Integer.parseInt(stringTokenizer.nextToken());
                                users[counter++] = new User(name, score);
                            }
                        }
                    }
                    loadedUsers.addAll(Arrays.asList(users));
                    return users;
                } else System.out.println("No files in user directory.");// if file is not there, prints this message to avoid error
            }
//
        } catch (Exception e) {

            DataController.exLogger.log(e.getMessage());
            return null;
        }
        return null;
    }

    public static void writeStats() {// This Method logs the User's Statistics to the game file
        try {
            File file = new File(PATH + user.getName() + ".usr");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(Base64.getEncoder().encodeToString(("2016rallyracing:"
                    + user.getName() + ":"
                    + user.getHighestScore()
                    + "").getBytes()));
            fileWriter.close();
        } catch (IOException e) {
            DataController.exLogger.log(e.getMessage());
        }

    }

    public static void sendLogs(String userMessage) {// This Method Emails the logs and the player's description of the error to racingrally0@gmail.com.
        String SMTP_HOST_NAME = "smtp.gmail.com";
        String SMTP_PORT = "465";
        String SMTP_FROM_ADDRESS = "racingrally2d@gmail.com";
        String SMTP_TO_ADDRESS = "racingrally0@gmail.com";
        String ip = "";
        try {
           URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
          ip = in.readLine();
        } catch (IOException e) {
            exLogger.log(e.getMessage());
        }
        String subject = "logs "+ip;
        String fileAttachment1 = EXLOGFILEPATH;
        String fileAttachment2 = USLOGFILEPATH;
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.debug", "false");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("racingrally2d@gmail.com", "Caussey01");
                    }
                });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(SMTP_FROM_ADDRESS));
            //create the message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            //fill message
            messageBodyPart.setText(userMessage);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            //Part two is attachment
            //Adding first file
            messageBodyPart = new MimeBodyPart();
            DataSource source1 = new FileDataSource(fileAttachment1);
            messageBodyPart.setDataHandler(new DataHandler(source1));
            messageBodyPart.setFileName("exception log.txt");
            multipart.addBodyPart(messageBodyPart);
            //Adding second file
            messageBodyPart = new MimeBodyPart();
            DataSource source2 = new FileDataSource(fileAttachment2);
            messageBodyPart.setDataHandler(new DataHandler(source2));
            messageBodyPart.setFileName("usual log.txt");
            multipart.addBodyPart(messageBodyPart);
            //Put parts in message
            msg.setContent(multipart);
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(SMTP_TO_ADDRESS));
            msg.setSubject(subject);
             msg.setContent(content, "text/plain");
            Transport.send(msg);
           //Testing only System.out.println("success....................................");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
