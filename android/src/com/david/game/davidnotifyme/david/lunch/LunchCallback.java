package com.david.game.davidnotifyme.david.lunch;

public interface LunchCallback<T> {
    String onComplete(Result<T> result);
}