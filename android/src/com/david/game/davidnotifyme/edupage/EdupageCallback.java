package com.david.game.davidnotifyme.edupage;

public interface EdupageCallback<T> {
    String onComplete(Result.Success<T> result);
}