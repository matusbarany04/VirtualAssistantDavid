package com.david.game.debug;

public class Debugger {
    public static boolean assertEquals(Object ob, Object s) throws StudentNotFoundException{
        throw new StudentNotFoundException("hey");
    }

    static class StudentNotFoundException extends Exception {

        public StudentNotFoundException(String message) {
            super(message);
        }
    }
}