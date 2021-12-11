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

package com.mcmiddleearth.rpmanager.model;

import com.google.gson.annotations.SerializedName;

public class Predicate {
    private Float angle;
    private Integer blocking;
    private Integer broken;
    private Integer cast;
    private Float cooldown;
    private Float damage;
    private Integer damaged;
    private Integer lefthanded;
    private Float pull;
    private Integer pulling;
    private Integer charged;
    private Integer firework;
    private Integer throwing;
    private Float time;
    @SerializedName("custom_model_data") private Integer customModelData;

    public Float getAngle() {
        return angle;
    }

    public void setAngle(Float angle) {
        this.angle = angle;
    }

    public Integer getBlocking() {
        return blocking;
    }

    public void setBlocking(Integer blocking) {
        this.blocking = blocking;
    }

    public Integer getBroken() {
        return broken;
    }

    public void setBroken(Integer broken) {
        this.broken = broken;
    }

    public Integer getCast() {
        return cast;
    }

    public void setCast(Integer cast) {
        this.cast = cast;
    }

    public Float getCooldown() {
        return cooldown;
    }

    public void setCooldown(Float cooldown) {
        this.cooldown = cooldown;
    }

    public Float getDamage() {
        return damage;
    }

    public void setDamage(Float damage) {
        this.damage = damage;
    }

    public Integer getDamaged() {
        return damaged;
    }

    public void setDamaged(Integer damaged) {
        this.damaged = damaged;
    }

    public Integer getLefthanded() {
        return lefthanded;
    }

    public void setLefthanded(Integer lefthanded) {
        this.lefthanded = lefthanded;
    }

    public Float getPull() {
        return pull;
    }

    public void setPull(Float pull) {
        this.pull = pull;
    }

    public Integer getPulling() {
        return pulling;
    }

    public void setPulling(Integer pulling) {
        this.pulling = pulling;
    }

    public Integer getCharged() {
        return charged;
    }

    public void setCharged(Integer charged) {
        this.charged = charged;
    }

    public Integer getFirework() {
        return firework;
    }

    public void setFirework(Integer firework) {
        this.firework = firework;
    }

    public Integer getThrowing() {
        return throwing;
    }

    public void setThrowing(Integer throwing) {
        this.throwing = throwing;
    }

    public Float getTime() {
        return time;
    }

    public void setTime(Float time) {
        this.time = time;
    }

    public Integer getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(Integer customModelData) {
        this.customModelData = customModelData;
    }
}
