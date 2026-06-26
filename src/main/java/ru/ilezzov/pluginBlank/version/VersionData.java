package ru.ilezzov.pluginBlank.version;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

@Getter
public class VersionData {
    private Latest latest;
    private Compatibility compatibility;
    private List<History> history;

    @Getter
    public static class Latest {
        private String version;

        @SerializedName("release_date")
        private String releaseDate;

        @SerializedName("download_url")
        private String downloadUrl;

        private List<String> changes;
    }

    @Getter
    public static class Compatibility {
        @SerializedName("min_required_version")
        private String minRequiredVersion;

        @SerializedName("blacklisted_versions")
        private List<String> blacklistedVersions;
    }

    @Getter
    public static class History {
        private String version;
        private List<String> changes;
    }
}
