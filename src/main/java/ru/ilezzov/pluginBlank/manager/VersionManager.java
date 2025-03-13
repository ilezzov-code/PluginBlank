package ru.ilezzov.pluginBlank.utils;

import lombok.Getter;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static ru.ilezzov.pluginBlank.Main.*;

@Getter
public class PluginVersionChecker {
    private final String localPluginVersion;
    private final String currentPluginVersion;

    public PluginVersionChecker(final String localPluginVersion) throws URISyntaxException, IOException, InterruptedException {
        this.localPluginVersion = localPluginVersion;
        this.currentPluginVersion = getCurrentPluginVersion();
    }

    public boolean check() throws URISyntaxException, IOException, InterruptedException {
        return localPluginVersion.equalsIgnoreCase(this.currentPluginVersion);
    }

    private String getCurrentPluginVersion() throws URISyntaxException, IOException, InterruptedException {
        final HttpClient httpClient = HttpClient.newHttpClient();
        final HttpRequest httpRequest = HttpRequest.newBuilder(new URI(URL_TO_FILE_VERSION))
                .GET()
                .build();

        final HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        httpClient.close();

        return httpResponse.body();
    }
}
