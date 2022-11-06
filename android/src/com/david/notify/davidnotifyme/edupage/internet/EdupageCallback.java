package com.david.notify.davidnotifyme.edupage.internet;

public interface EdupageCallback<T> {
    String onComplete(Result.Success<T> result);
}