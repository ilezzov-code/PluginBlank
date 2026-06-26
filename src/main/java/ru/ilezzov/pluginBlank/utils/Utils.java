package ru.ilezzov.pluginBlank.utils;


import ru.ilezzov.pluginBlank.model.Response;

import static ru.ilezzov.pluginBlank.message.console.ErrorConstants.INVALID_FORMAT_VERSION_ERROR;

public class Utils {
    public static Response<Integer> equalsVersion(final String first, final String second) {
        final String[] firstSplit = first.split("\\.");
        final String[] secondSplit = second.split("\\.");

        final int maxVersionLength = Math.max(firstSplit.length, secondSplit.length);

        for (int i = 0; i < maxVersionLength; i++) {
            try {
                final int num1 = i < firstSplit.length ? Integer.parseInt(firstSplit[i]) : 0;
                final int num2 = i < secondSplit.length ? Integer.parseInt(secondSplit[i]) : 0;

                if (num1 > num2) {
                    return Response.ok(-1);
                }

                if (num1 < num2) {
                    return Response.ok(1);
                }

            } catch (final NumberFormatException e) {
                return Response.error(INVALID_FORMAT_VERSION_ERROR, e);
            }
        }
        return Response.ok(0);
    }
}
