package org.example;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;

public class Map {

    private final String API_URL = "https://geocode-maps.yandex.ru/1.x/?format=json&apikey=%s&geocode=%s";
    private final String API_KEY = "4d77bc05-9f6a-47ea-b9e6-aeb5e4a65b37";
    private static String tt1;
    private static String tt2;

    public void get_image_of_map(String adres, long user) {
        File outputfile = new File("D:\\Learning\\Git\\JAVA_BOT\\data_MAP\\" + user + ".png");
        if (!outputfile.isFile()) {
            get_location(adres);
            try {

                // https://static-maps.yandex.ru/1.x/?ll=37.620070,55.753630&spn=0.002,0.002&size=300,300&l=map
                String mapUrl = "https://static-maps.yandex.ru/1.x/?ll="
                        + tt2 + "," + tt1 + "&spn=0.002,0.002&size=300,300&l=map";
                // Open stream to the map image URL
                URL url = new URL(mapUrl);
                InputStream stream = url.openStream();

                // Read the map image into a BufferedImage
                BufferedImage mapImage = ImageIO.read(stream);

                // Close the stream
                stream.close();

                // Save the map image to disk

                ImageIO.write(mapImage, "png", outputfile);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void get_location(String storeName) {
        String city = "Ростов-на-Дону"; // replace with user input

        String encodedAddress = URLEncoder.encode(storeName + ", " + city, StandardCharsets.UTF_8);
        String apiUrl = String.format(API_URL, API_KEY, encodedAddress);

        try {
            String json = IOUtils.toString(new URL(apiUrl), StandardCharsets.UTF_8);
            JSONObject jsonObj = new JSONObject(json);

            JSONArray featureMember = jsonObj.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember");

            if (featureMember.length() > 0) {
                JSONObject firstObject = featureMember.getJSONObject(0);
                JSONObject point = firstObject.getJSONObject("GeoObject").getJSONObject("Point");

                String coordinates = point.getString("pos");
                //System.out.println("The coordinates of " + storeName + " in " + city + " are " + coordinates);
                tt2 = coordinates.split(" ")[0];
                tt1 = coordinates.split(" ")[1];
            } else {
                System.out.println("Store not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
