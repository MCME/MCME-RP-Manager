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

package com.mcmiddleearth.rpmanager.gui.panes;

import com.mcmiddleearth.rpmanager.events.ChangeEvent;
import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;
import com.mcmiddleearth.rpmanager.gui.MainWindow;
import com.mcmiddleearth.rpmanager.gui.actions.Action;
import com.mcmiddleearth.rpmanager.gui.components.CollapsibleSection;
import com.mcmiddleearth.rpmanager.gui.components.IconButton;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.gui.constants.Icons;
import com.mcmiddleearth.rpmanager.gui.modals.AddConditionModal;
import com.mcmiddleearth.rpmanager.model.Case;
import com.mcmiddleearth.rpmanager.utils.BlockStateUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CaseWhenEditPane extends VerticalBox {
    private final String fileName;
    private final Case theCase;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public CaseWhenEditPane(String fileName, Case theCase) {
        this.fileName = fileName;
        this.theCase = theCase;

        setBorder(new EmptyBorder(0, 10, 0, 0));

        this.add(new JButton(new Action("Add condition", "Add new condition to this case") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addCondition();
            }
        }));

        List<Map<String, Object>> conditions = getConditionList();
        boolean collapsed = conditions.size() != 1;
        for (Map<String, Object> condition : conditions) {
            WhenEditPane whenEditPane = new WhenEditPane(fileName, condition);
            whenEditPane.addChangeListener(this::onChange);
            this.add(new CollapsibleSection("Condition", whenEditPane, collapsed,
                    removeConditionButton(condition)));
            this.add(new JSeparator());
        }
    }

    private void onChange(ChangeEvent changeEvent) {
        eventDispatcher.dispatchEvent(changeEvent);
    }

    public void addChangeListener(EventListener<ChangeEvent> listener) {
        eventDispatcher.addEventListener(listener, ChangeEvent.class);
    }

    private List<Map<String, Object>> getConditionList() {
        return theCase.getWhen() == null ? new LinkedList<>() : theCase.getWhen().getOR() == null ?
                theCase.getWhen().getValue() == null ?
                        new LinkedList<>() : new LinkedList<>(Collections.singletonList(theCase.getWhen().getValue())) :
                theCase.getWhen().getOR();
    }

    private void setConditionList(List<Map<String, Object>> conditions) {
        if (conditions.size() == 1) {
            theCase.getWhen().setValue(conditions.get(0));
            theCase.getWhen().setOR(null);
        } else {
            theCase.getWhen().setValue(null);
            theCase.getWhen().setOR(conditions);
        }
    }

    private void addCondition() {
        Map<String, List<String>> possibleStates = BlockStateUtils.getPossibleStates(fileName);
        new AddConditionModal(MainWindow.getInstance(), possibleStates, condition -> {
            List<Map<String, Object>> conditions = getConditionList();
            conditions.add(condition);
            setConditionList(conditions);
            WhenEditPane whenEditPane = new WhenEditPane(fileName, condition);
            whenEditPane.addChangeListener(this::onChange);
            this.add(new CollapsibleSection("Condition", whenEditPane, false,
                    removeConditionButton(condition)));
            this.add(new JSeparator());
            onChange(new ChangeEvent(this, theCase));
            revalidate();
            repaint();
        });
    }

    private JButton removeConditionButton(Map<String, Object> condition) {
        return new IconButton(new Action("-", Icons.DELETE_ICON, "Remove condition") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                removeCondition(condition);
            }
        });
    }

    private void removeCondition(Map<String, Object> condition) {
        List<Map<String, Object>> conditions = getConditionList();
        conditions.remove(condition);
        setConditionList(conditions);

        for (int i = getComponentCount() - 1; i >= 0; --i) {
            if (getComponent(i) instanceof CollapsibleSection collapsibleSection &&
                    collapsibleSection.getContent() instanceof WhenEditPane whenEditPane &&
                    whenEditPane.getCondition() == condition) {
                remove(i+1);
                remove(i);
            }
        }

        revalidate();
        repaint();

        onChange(new ChangeEvent(this, theCase));
    }
}
