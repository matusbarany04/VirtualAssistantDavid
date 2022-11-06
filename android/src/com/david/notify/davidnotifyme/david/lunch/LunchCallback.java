package com.david.notify.davidnotifyme.david.lunch;

public interface LunchCallback<T> {
    String onComplete(Result<T> result);
}