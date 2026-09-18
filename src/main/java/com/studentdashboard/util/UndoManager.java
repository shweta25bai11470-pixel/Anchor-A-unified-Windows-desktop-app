package com.studentdashboard.util;

import java.util.ArrayDeque;
import java.util.Deque;

public class UndoManager {
    private static final int MAX = 20;
    private final Deque<Runnable> stack = new ArrayDeque<>();

    public void push(Runnable undoAction) {
        if (undoAction == null)
            return;
        stack.push(undoAction);
        while (stack.size() > MAX) {
            stack.removeLast();
        }
    }

    public void undo() {
        if (!stack.isEmpty()) {
            stack.pop().run();
        }
    }

    public boolean canUndo() {
        return !stack.isEmpty();
    }
}