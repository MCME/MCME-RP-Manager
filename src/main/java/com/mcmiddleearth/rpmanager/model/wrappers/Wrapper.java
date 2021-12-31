/*
 * Copyright (C) 2021 MCME
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

package com.mcmiddleearth.rpmanager.model.wrappers;

public abstract class Wrapper<T> {
    private String filePath;
    private T current;
    private T urps;
    private T vanilla;

    public Wrapper(String filePath, T current, T urps, T vanilla) {
        this.filePath = filePath;
        this.current = current;
        this.urps = urps;
        this.vanilla = vanilla;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public T getCurrent() {
        return current;
    }

    public void setCurrent(T current) {
        this.current = current;
    }

    public T getUrps() {
        return urps;
    }

    public void setUrps(T urps) {
        this.urps = urps;
    }

    public T getVanilla() {
        return vanilla;
    }

    public void setVanilla(T vanilla) {
        this.vanilla = vanilla;
    }
}
