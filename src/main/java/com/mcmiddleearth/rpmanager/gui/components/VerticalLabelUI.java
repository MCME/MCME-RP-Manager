/*
 * Copyright (C) 2024 MCME
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
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;

public class VerticalLabelUI extends BasicLabelUI {
    static {
        labelUI = new VerticalLabelUI(false);
    }

    private final boolean clockwise;

    public VerticalLabelUI(boolean clockwise) {
        this.clockwise = clockwise;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension dimension = super.getPreferredSize(c);
        return new Dimension(dimension.height, dimension.width);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        JLabel label = (JLabel) c;
        String text = label.getText();
        Icon icon = label.isEnabled() ? label.getIcon() : label.getDisabledIcon();
        if (icon != null || text != null) {
            FontMetrics fontMetrics = g.getFontMetrics();
            Rectangle paintViewR = new Rectangle();
            Rectangle paintIconR = new Rectangle();
            Rectangle paintTextR = new Rectangle();
            Insets paintViewInsets = label.getInsets();
            paintViewR.x = paintViewInsets.left;
            paintViewR.y = paintViewInsets.top;
            paintViewR.height = label.getWidth() - (paintViewInsets.left + paintViewInsets.right);
            paintViewR.width = label.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);
            String clippedText = layoutCL(label, fontMetrics, text, icon, paintViewR, paintIconR, paintTextR);

            Graphics2D g2 = (Graphics2D) g;
            var transform = g2.getTransform();
            if (clockwise) {
                g2.rotate(Math.PI / 2.0);
                g2.translate(0, -label.getWidth());
            } else {
                g2.rotate(-Math.PI / 2.0);
                g2.translate(-label.getHeight(), 0);
            }

            if (icon != null) {
                icon.paintIcon(label, g, paintIconR.x, paintIconR.y);
            }

            if (text != null) {
                int textX = paintTextR.x;
                int textY = paintTextR.y + fontMetrics.getAscent();

                if (label.isEnabled()) {
                    paintEnabledText(label, g, clippedText, textX, textY);
                } else {
                    paintDisabledText(label, g, clippedText, textX, textY);
                }
            }

            g2.setTransform(transform);
        }
    }
}
