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

public enum Position {
    @SerializedName("thirdperson_righthand") THIRDPERSON_RIGHTHAND,
    @SerializedName("thirdperson_lefthand") THIRDPERSON_LEFTHAND,
    @SerializedName("firstperson_righthand") FIRSTPERSON_RIGHTHAND,
    @SerializedName("firstperson_lefthand") FIRSTPERSON_LEFTHAND,
    @SerializedName("gui") GUI,
    @SerializedName("head") HEAD,
    @SerializedName("ground") GROUND,
    @SerializedName("fixed") FIXED,
}
