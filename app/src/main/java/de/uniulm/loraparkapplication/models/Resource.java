package de.uniulm.loraparkapplication.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
*  Class that is used for the status handling of resources (e.g. data from APIs and their status)
 */
public class Resource<T> {

    @NonNull
    public final Status status;
    @Nullable public final T data;
    @Nullable public final String message;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    public enum Status { SUCCESS, ERROR, LOADING }
}