package ru.ilezzov.pluginblank.version;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;
import ru.ilezzov.pluginblank.Main;
import ru.ilezzov.pluginblank.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

import static ru.ilezzov.pluginblank.messages.ConsoleMessages.errorOccurred;

@Getter
public class VersionManager {
    private final Logger logger = Main.getPluginLogger();

    private final String pluginVersion;
    private final String urlToFileVersion;
    private final String currentVersion;

    private final JSONObject versionJson;

    public VersionManager(final String pluginVersion, final String urlToFileVersion) {
        this.pluginVersion = pluginVersion;
        this.urlToFileVersion = urlToFileVersion;
        this.versionJson = loadJsonVersion();
        this.currentVersion = getPluginCurrentVersion();
    }

    public VersionType check() {
        final JSONObject pluginVersionJson = this.versionJson.getJSONObject(pluginVersion);
        return VersionType.valueOf(pluginVersionJson.getString("type"));
    }

    public String getCurrentVersionUrl() {
        return versionJson.getJSONObject("latest").getString("link");
    }

    private String getPluginCurrentVersion() {
        return versionJson.getJSONObject("latest").getString("version");
    }

    private JSONObject loadJsonVersion() {
        return new JSONObject(Objects.requireNonNull(getVersionDataFromGitHub()));
    }

    private String getVersionDataFromGitHub() {
        try {
            final HttpClient httpClient = HttpClient.newHttpClient();
            final HttpRequest httpRequest = HttpRequest.newBuilder(new URI(this.urlToFileVersion))
                    .GET()
                    .build();
            final HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            return httpResponse.body();
        } catch (URISyntaxException e) {
            logger.info(errorOccurred("Invalid link to the GitHub file. link = ".concat(urlToFileVersion)));
        } catch (IOException | InterruptedException e) {
            logger.info(errorOccurred("Couldn't send a request to get the plugin version"));
        }

        Main.disablePlugin();
        return null;
    }

}
