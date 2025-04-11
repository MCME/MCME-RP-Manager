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

package com.mcmiddleearth.rpmanager.gui.components;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IconButton extends JButton {
    public IconButton(Icon icon) {
        super(icon);
        configure();
    }

    public IconButton(Action a) {
        super(a);
        configure();
    }

    public IconButton(String text, Icon icon) {
        super(text, icon);
        configure();
    }

    private void configure() {
        this.setText("");
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setFocusPainted(true);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                IconButton.this.setBorderPainted(true);
                IconButton.this.setContentAreaFilled(true);
                IconButton.this.revalidate();
                IconButton.this.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                IconButton.this.setBorderPainted(false);
                IconButton.this.setContentAreaFilled(false);
                IconButton.this.revalidate();
                IconButton.this.repaint();
            }
        });
    }
}
