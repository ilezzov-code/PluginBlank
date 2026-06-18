package ru.ilezzov.pluginBlank.message;

public final class ErrorConstants {
    private ErrorConstants() {}

    public static String PROPERTIES_NOT_LOADED = "Couldn't load plugin properties: %s";
    public static String VERSION_DATA_NOT_LOADED = "Failed to retrieve information about the current version of the plugin: %s";
    public static String VERSION_CHECK_FAILED = "Failed to check the plugin version: error";

    public static final String RESOURCE_NOT_FOUD_ERROR = "Internal resource '%s' not found in the source JAR file";
    public static final String REQUIRED_KEY_MISSING_ERROR = "Required config key '%s' is missing";
    public static final String STRUCTURE_ERROR = "Structure error: The root configuration element must be an object";
    public static final String SYNTAX_ERROR = "Syntax error in JSON/YAML: %s";
    public static final String ACCESS_DENIED_ERROR = "Denied access when writing a file: %s";
    public static final String IO_ERROR = "Critical I/O error when working with the '%s' file";
    public static final String COMPARING_VERSION_ERROR = "Error when comparing config versions";
    public static final String URI_SYNTAX_ERROR = "Syntax error in URI: %s";
    public static final String INVALID_FORMAT_VERSION_ERROR = "Invalid config version format: a number was expected (e.g., 1.0.1), but a string was found";
    public static final String NO_NETWORK_CONNECT_ERROR = "No network access: Couldn't connect to the update server";
    public static final String CONNECT_REJECTED_ERROR = "Connection rejected: check the firewall or proxy settings";
    public static final String NOT_FOUND_ERROR = "The version file could not be retrieved. The server returned a 404 error.";
    public static final String CRITICAL_REQUEST_ERROR = "Critical request initialization error: %s";
}
