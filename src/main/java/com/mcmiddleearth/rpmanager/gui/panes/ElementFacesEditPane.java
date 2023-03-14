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

package com.mcmiddleearth.rpmanager.gui.panes;

import com.mcmiddleearth.rpmanager.events.ChangeEvent;
import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.CollapsibleSection;
import com.mcmiddleearth.rpmanager.gui.components.IconButton;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.gui.constants.Icons;
import com.mcmiddleearth.rpmanager.model.*;
import com.mcmiddleearth.rpmanager.utils.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.Override;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ElementFacesEditPane extends VerticalBox {
    private final Element element;
    private final List<Pair<Face, FaceDefinition>> faces;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public ElementFacesEditPane(Element element) {
        this.element = element;
        this.faces = element.getFaces() == null ?
                new LinkedList<>() :
                new LinkedList<>(element.getFaces().entrySet().stream().map(Pair::new).toList());

        if (!faces.isEmpty()) {
            boolean collapsed = faces.size() != 1;
            for (Pair<Face, FaceDefinition> entry : faces) {
                this.add(createFaceEntry(entry, collapsed));
            }
        }
    }

    public void addFaceEntry() {
        Pair<Face, FaceDefinition> entry = new Pair<>(null, new FaceDefinition());
        faces.add(entry);
        this.add(createFaceEntry(entry, false));
        revalidate();
        repaint();
        onFacesChange();
    }

    private CollapsibleSection createFaceEntry(Pair<Face, FaceDefinition> entry, boolean collapsed) {
        FaceEditPane faceEditPane = new FaceEditPane(entry);
        faceEditPane.addChangeListener(e -> onFacesChange());
        return new CollapsibleSection("Face", faceEditPane, collapsed, removeFaceButton(entry));
    }

    private JButton removeFaceButton(Pair<Face, FaceDefinition> entry) {
        return new IconButton(new Action("-", Icons.DELETE_ICON, "Remove face") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int index = faces.indexOf(entry);
                faces.remove(index);
                remove(index);
                revalidate();
                repaint();
                onFacesChange();
            }
        });
    }

    private void onFacesChange() {
        Map<Face, FaceDefinition> map = new LinkedHashMap<>();
        for (Pair<Face, FaceDefinition> entry : faces) {
            if (entry.getLeft() != null) {
                map.put(entry.getLeft(), entry.getRight());
            }
        }
        if (map.isEmpty()) {
            map = null;
        }
        element.setFaces(map);
        onChange();
    }

    private void onChange() {
        eventDispatcher.dispatchEvent(new ChangeEvent(this, null));
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }
}
