package ru.ilezzov.pluginBlank.properties;

import ru.ilezzov.pluginBlank.model.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static ru.ilezzov.pluginBlank.message.console.ErrorConstants.IO_ERROR;

public class PropertiesManager {
    public static Response<PluginProperties> loadProperties(final String fileName) {
        final Properties properties = new Properties();

        try (final InputStream in = PropertiesManager.class.getClassLoader().getResourceAsStream(fileName)) {
            properties.load(in);

            return Response.ok(
                    loadValuesFromProperties(properties)
            );
        } catch (final IOException e) {
            return Response.error(IO_ERROR.formatted(fileName), e);
        }
    }

    private static PluginProperties loadValuesFromProperties(final Properties properties) {
        return new PluginProperties(
                properties.getProperty("current-version"),
                properties.getProperty("version-file-url"),
                Integer.parseInt(properties.getProperty("bstats")),
                properties.getProperty("main-command"),
                properties.getProperty("website")
        );
    }
}
