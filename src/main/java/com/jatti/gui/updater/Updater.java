package com.jatti.gui.updater;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStreamReader;

public class Updater {

    private static final String VERSION = "1.6.1";

    public static void checkForUpdates() {

        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpGet get = new HttpGet("https://api.github.com/repos/whippytools/bukkit-inventories/releases");
            HttpResponse response = httpClient.execute(get);
            int i = 0;
            StringBuilder builder = new StringBuilder();
            String version = "";
            try (InputStreamReader reader = new InputStreamReader(response.getEntity().getContent())) {

                while ((i = reader.read()) != -1) {
                    char next = (char) i;
                    builder.append(next);
                }
            } finally {
                version = builder.toString().split(",")[9].substring(7).replace("\"", "");
            }

            if (!(VERSION.equals(version))) {
                System.out.println("[Bukkit-Inventories] You are using old version of the API: " + VERSION + " use the newest: " + version + " !");
            } else {
                System.out.println("[Bukkit-Inventories] You are using the newest version of the API <3");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
