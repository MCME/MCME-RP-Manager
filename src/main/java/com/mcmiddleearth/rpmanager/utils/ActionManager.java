/*
 * Copyright (C) 2023 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mcmiddleearth.rpmanager.utils;

public class ActionManager {
    private static final int MEMORY_SIZE = 61;
    private final ActionHolder[] actions = new ActionHolder[MEMORY_SIZE];
    private int startIndex = 0;
    private int current = 0;
    private final Runnable refreshAction;

    public ActionManager(Runnable refreshAction) {
        this.refreshAction = refreshAction;
    }

    public void submit(Action undoAction, Action redoAction) {
        for (int i = current; i != startIndex; i = increment(i)) {
            actions[i] = null;
        }
        actions[current] = new ActionHolder(undoAction, redoAction);
        doRedo(false);
    }

    public void undo() {
        if (current != startIndex) {
            current = decrement(current);
            try {
                actions[current].undoAction.run();
                refresh();
            } catch (Exception e) {
                //TODO error dialog
                throw new RuntimeException(e);
            }
        }
    }

    public void refresh() {
        refreshAction.run();
    }

    public void redo() {
        doRedo(true);
    }

    private void doRedo(boolean refresh) {
        if (actions[current] != null) {
            try {
                actions[current].redoAction.run();
                if (refresh) {
                    refresh();
                }
            } catch (Exception e) {
                //TODO error dialog
                throw new RuntimeException(e);
            }
            current = increment(current);
            actions[current] = null;
            if (current == startIndex) {
                startIndex = increment(startIndex);
            }
        }
    }

    private static int increment(int index) {
        return (index + 1) % MEMORY_SIZE;
    }

    private static int decrement(int index) {
        return (index + MEMORY_SIZE - 1) % MEMORY_SIZE;
    }

    private static class ActionHolder {
        private final Action undoAction;
        private final Action redoAction;

        public ActionHolder(Action undoAction, Action redoAction) {
            this.undoAction = undoAction;
            this.redoAction = redoAction;
        }
    }
}
