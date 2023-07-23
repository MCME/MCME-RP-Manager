/*
 * Copyright (C) 2022 MCME
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

package com.mcmiddleearth.rpmanager.gui.constants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Icons {
    public static final Icon NEW_PROJECT = UIManager.getIcon("FileView.fileIcon");
    public static final Icon OPEN_PROJECT = UIManager.getIcon("FileView.directoryIcon");
    public static final Icon SAVE_PROJECT = UIManager.getIcon("FileView.floppyDriveIcon");
    public static final Icon DELETE_ICON;
    public static final Icon EXPAND_ICON;
    public static final Icon RETRACT_ICON;

    static {
        try {
            BufferedImage deleteIcon = ImageIO.read(Icons.class.getResourceAsStream("/icons/delete.png"));
            DELETE_ICON = new ImageIcon(deleteIcon);
            BufferedImage expandIcon = ImageIO.read(Icons.class.getResourceAsStream("/icons/expand.png"));
            EXPAND_ICON = new ImageIcon(expandIcon);
            BufferedImage retractIcon = ImageIO.read(Icons.class.getResourceAsStream("/icons/retract.png"));
            RETRACT_ICON = new ImageIcon(retractIcon);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Icons() {}
}
