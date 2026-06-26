package ru.ilezzov.pluginBlank.version;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import lombok.Getter;
import lombok.Setter;
import ru.ilezzov.pluginBlank.logger.PluginLogger;
import ru.ilezzov.pluginBlank.model.Response;
import ru.ilezzov.pluginBlank.properties.PluginProperties;
import ru.ilezzov.pluginBlank.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static ru.ilezzov.pluginBlank.message.console.ErrorConstants.*;

public class VersionManager {
    private final PluginLogger logger;
    private final PluginProperties properties;

    @Getter
    @Setter
    private VersionData versionData;

    @Getter
    private VersionType versionType;

    public VersionManager(final PluginLogger pluginLogger, final PluginProperties properties) {
        this.logger = pluginLogger;
        this.properties = properties;
        this.loadVersionData();
    }

    public void loadVersionData() {
        final Response<VersionData> versionDataResponse = loadVersionDate(properties.versionFileUrl());

        if (versionDataResponse.success()) {
            this.versionData = versionDataResponse.data();

            final Response<VersionType> versionTypeResponse = this.identifyVersionType(versionData, properties.currentVersion());

            if (versionTypeResponse.success()) {
                this.versionType = versionTypeResponse.data();
                return;
            } else {
                this.logger.error(VERSION_CHECK_FAILED.formatted(versionTypeResponse.error()), versionTypeResponse.error());
            }

        } else {
            final Exception e = versionDataResponse.error();

            if (e != null) {
                this.logger.error(VERSION_DATA_NOT_LOADED.formatted(versionDataResponse.message()), e);
            } else {
                this.logger.error(VERSION_DATA_NOT_LOADED.formatted(versionDataResponse.message()));
            }
        }
        this.versionType = VersionType.UNREACHABLE;
    }

    private Response<VersionData> loadVersionDate(final String versionFileUrl) {
        try {
            final URI uri = URI.create(versionFileUrl);
            final URL url = uri.toURL();
            final URLConnection connection = url.openConnection();

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (final InputStream in = connection.getInputStream()) {
                byte[] bytes = in.readAllBytes();
                String content = new String(bytes, StandardCharsets.UTF_8);

                if (!content.trim().startsWith("{")) {
                    return Response.error(SYNTAX_ERROR.formatted("github returned non-json"));
                }

                final Gson gson = new Gson();
                final VersionData versionDate = gson.fromJson(content, VersionData.class);

                return Response.ok(versionDate);
            }
        } catch (final UnknownHostException e) {
            return Response.error(NO_NETWORK_CONNECT_ERROR, e);
        } catch (final ConnectException e) {
            return Response.error(CONNECT_REJECTED_ERROR, e);
        } catch (java.net.SocketTimeoutException e) {
            return Response.error(CRITICAL_REQUEST_ERROR.formatted("Connection timed out"), e);
        } catch (JsonParseException e) {
            return Response.error(STRUCTURE_ERROR, e);
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                return Response.error(NOT_FOUND_ERROR, e);
            }
            return Response.error(IO_ERROR.formatted("version.json"), e);
        } catch (Exception e) {
            return Response.error(CRITICAL_REQUEST_ERROR.formatted(e.getMessage()), e);
        }
    }

    private Response<VersionType> identifyVersionType(final VersionData versionData, final String currentVersion) {
        if (versionData == null) {
            return Response.ok(VersionType.UNREACHABLE);
        }

        final String latestVersion = versionData.getLatest().getVersion();
        final String minRequiredVersion = versionData.getCompatibility().getMinRequiredVersion();
        final List<String> blacklistVersions = versionData.getCompatibility().getBlacklistedVersions();

        if (blacklistVersions.contains(currentVersion)) {
            return Response.ok(VersionType.BLACKLIST);
        }

        final Response<Integer> equalsMinRequiredAndCurrent = Utils.equalsVersion(minRequiredVersion, currentVersion);

        if (!equalsMinRequiredAndCurrent.success()) {
            return Response.error(equalsMinRequiredAndCurrent.message(), equalsMinRequiredAndCurrent.error());
        }

        int equalsStatus = equalsMinRequiredAndCurrent.data();

        if (equalsStatus == -1) {
            return Response.ok(VersionType.OUTDATED);
        }

        final Response<Integer> equalsLatestAndCurrent = Utils.equalsVersion(latestVersion, currentVersion);

        if (!equalsLatestAndCurrent.success()) {
            return Response.error(equalsLatestAndCurrent.message(), equalsLatestAndCurrent.error());
        }

        equalsStatus = equalsLatestAndCurrent.data();

        if (equalsStatus == -1) {
            return Response.ok(VersionType.SUPPORTED);
        }

        return Response.ok(VersionType.LATEST);
    }
}
