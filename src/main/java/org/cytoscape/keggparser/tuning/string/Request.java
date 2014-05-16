package org.cytoscape.keggparser.tuning.string;


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class Request {
    private static final String requestTemplate = "http://database]/[access]/[format]/[request]?[parameter]=[value]";
    private String database = "string-db.org";
    private String access = "api";
    private String format = "psi-mi-tab";
    private String request  = "interactionsList";
    private String parameter = "identifiers";
    private String parameter2 = "species";
    private String value = "mefv%0Apycard";
    private String value2 = "9606";

    public String stringRequest(){
        String query = String.format("http://%s/%s/%s/%s?%s=%s&%s=%s", database, access, format,
                request, parameter, value, parameter2, value2);
        System.out.println(query);
        try {
            URL url = new URL(query);
            URLConnection uc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            PrintWriter writer = new PrintWriter(new File("String/mefv.xml"));
            String inputLine, result = "";

            while ((inputLine = in.readLine()) != null) {
                result += inputLine;
                writer.append(inputLine + "\n");
            }
            in.close();
            writer.close();
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void stringImageRequest(){
        String query = String.format("http://%s/%s/%s/%s?%s=%s&%s=%s", database, access, format,
                request, parameter, value, parameter2, value2);
        System.out.println(query);
        try {
            URL url = new URL(query);
            URLConnection uc = url.openConnection();
//            URLImageSource imageSource = new URLImageSource(uc);
//            ImageConsumer imageConsumer = new ImageFilter();
//            imageSource.startProduction(imageConsumer);

            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel imagePanel = new JPanel();
            JLabel imageLabel = new JLabel(new ImageIcon(url));
            imagePanel.add(imageLabel);
            frame.add(imagePanel);
            frame.setResizable(true);
            frame.setVisible(true);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawNetwork(Image image){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel imagePanel = new JPanel();
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        imagePanel.add(imageLabel);
        frame.add(imagePanel);
        frame.setSize(image.getWidth(null) + 20, image.getHeight(null) + 20);
        frame.setResizable(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Request request = new Request();
        System.out.println(request.stringRequest());
//        request.stringImageRequest();

    }
}
