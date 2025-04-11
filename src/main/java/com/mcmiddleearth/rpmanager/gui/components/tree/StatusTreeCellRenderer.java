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

package com.mcmiddleearth.rpmanager.gui.components.tree;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class StatusTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
                                                  int row, boolean hasFocus) {
        Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        Color background = getBackgroundColor(component);
        if (value instanceof StaticTreeNode node) {
            if (node.getStatus() == StaticTreeNode.NodeStatus.ADDED) {
                component.setForeground(getAddedColor(background));
            } else if (node.getStatus() == StaticTreeNode.NodeStatus.MODIFIED) {
                component.setForeground(getModifiedColor(background));
            } else if (node.getStatus() == StaticTreeNode.NodeStatus.UNTRACKED) {
                component.setForeground(getUntrackedColor(background));
            }
        }
        return component;
    }

    private Color getAddedColor(Color background) {
        return background == null ? null : isDark(background) ?
                new Color(63, 255, 63) :
                new Color(0, 95, 0);
    }

    private Color getModifiedColor(Color background) {
        return background == null ? null : isDark(background) ?
                new Color(63, 63, 255) :
                new Color(0, 0, 95);
    }

    private Color getUntrackedColor(Color background) {
        return background == null ? null : isDark(background) ?
                new Color(255, 63, 63) :
                new Color(95, 0, 0);
    }

    private static boolean isDark(Color color) {
        return (color.getRed() + color.getGreen() + color.getBlue()) / 3 < 128;
    }

    private static Color getBackgroundColor(Component component) {
        Color background = component.getBackground();
        while (component.getParent() != null && background == null) {
            component = component.getParent();
            background = component.getBackground();
        }
        if (background == null) {
            background = UIManager.getColor("Panel.background");
        }
        return background;
    }
}
