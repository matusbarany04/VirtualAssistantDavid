package com.david.game.davidnotifyme.edupage.internet;

public interface EdupageCallback<T> {
    String onComplete(Result.Success<T> result);
}