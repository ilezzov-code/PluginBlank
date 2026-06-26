package ru.ilezzov.pluginBlank.model;


public record Response<T>(boolean success, String message, T data, Exception error) {
    public static <T> Response<T> ok() {
        return new Response<>(true, "OK", null, null);
    }

    public static <T> Response<T> ok(final T data) {
        return new Response<>(true, "OK", data, null);
    }

    public static <T> Response<T> ok(final String message, final T data) {
        return new Response<>(true, message, data, null);
    }

    public static <T> Response<T> ok(final String message) {
        return new Response<>(true, message, null, null);
    }

    public static <T> Response<T> error(final String message) {
        return new Response<>(false, message, null, null);
    }

    public static <T> Response<T> error(final Exception e) {
        return new Response<>(false, null, null, e);
    }

    public static <T> Response<T> error(final String message, final T data) {
        return new Response<>(false, message, data, null);
    }

    public static <T> Response<T> error(final String message, final Exception e) {
        return new Response<>(false, message, null, e);
    }

    public boolean hasData() {
        return data != null;
    }
}
