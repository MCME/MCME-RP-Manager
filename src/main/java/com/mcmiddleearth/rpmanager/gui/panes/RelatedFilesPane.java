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

package com.mcmiddleearth.rpmanager.gui.panes;

import com.mcmiddleearth.rpmanager.events.EventDispatcher;
import com.mcmiddleearth.rpmanager.events.EventListener;
import com.mcmiddleearth.rpmanager.events.ListDoubleClickEvent;
import com.mcmiddleearth.rpmanager.gui.components.FastScrollPane;
import com.mcmiddleearth.rpmanager.gui.components.VerticalBox;
import com.mcmiddleearth.rpmanager.model.BlockModel;
import com.mcmiddleearth.rpmanager.model.internal.LayerRelatedFiles;
import com.mcmiddleearth.rpmanager.model.internal.RelatedFiles;
import com.mcmiddleearth.rpmanager.model.internal.SelectedFileData;
import com.mcmiddleearth.rpmanager.utils.GsonProvider;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class RelatedFilesPane extends JPanel {
    private RelatedFiles relatedFiles;
    private final JPanel previewOuterPane;
    private final VerticalBox relatedModelsPane;
    private final VerticalBox relatedTexturesPane;
    private final List<JList<SelectedFileData>> relatedModelsLists = new ArrayList<>();
    private final List<JList<SelectedFileData>> relatedTexturesLists = new ArrayList<>();
    private boolean suppressSelectionEvents = false;
    private final EventDispatcher eventDispatcher = new EventDispatcher();

    public RelatedFilesPane() {
        setLayout(new BorderLayout());

        previewOuterPane = new JPanel();
        previewOuterPane.setLayout(new BorderLayout());

        relatedModelsPane = new VerticalBox();

        relatedTexturesPane = new VerticalBox();

        JTabbedPane selectFilePane = new JTabbedPane();
        selectFilePane.addTab("Related models", new FastScrollPane(relatedModelsPane));
        selectFilePane.addTab("Related textures", new FastScrollPane(relatedTexturesPane));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
                new FastScrollPane(previewOuterPane),
                selectFilePane);
        splitPane.setDividerSize(10);
        splitPane.setOneTouchExpandable(false);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);
    }

    public void setRelatedFiles(RelatedFiles relatedFiles) {
        this.relatedFiles = relatedFiles;
        previewOuterPane.removeAll();
        relatedModelsPane.removeAll();
        relatedTexturesPane.removeAll();
        relatedModelsLists.clear();
        relatedTexturesLists.clear();
        if (relatedFiles != null) {
            for (LayerRelatedFiles layerRelatedFiles : relatedFiles.getRelatedModels()) {
                JLabel label = new JLabel(layerRelatedFiles.getLayerName());
                label.setFont(label.getFont().deriveFont(label.getFont().getStyle() | Font.BOLD, 15.0f));
                relatedModelsPane.add(label);
                relatedModelsPane.add(new JSeparator(JSeparator.HORIZONTAL));
                JList<SelectedFileData> relatedModelsList = new JList<>(layerRelatedFiles.getRelatedFiles()
                        .toArray(SelectedFileData[]::new));
                relatedModelsList.setCellRenderer(new SelectedFileDataListCellRenderer());
                relatedModelsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                relatedModelsList.addListSelectionListener(this::onModelSelectionChanged);
                relatedModelsList.addMouseListener(new ListMouseAdapter());
                relatedModelsPane.add(relatedModelsList);
                relatedModelsLists.add(relatedModelsList);
            }
            for (LayerRelatedFiles layerRelatedFiles : relatedFiles.getRelatedTextures()) {
                JLabel label = new JLabel(layerRelatedFiles.getLayerName());
                label.setFont(label.getFont().deriveFont(label.getFont().getStyle() | Font.BOLD, 15.0f));
                relatedTexturesPane.add(label);
                relatedTexturesPane.add(new JSeparator(JSeparator.HORIZONTAL));
                JList<SelectedFileData> relatedTexturesList = new JList<>(layerRelatedFiles.getRelatedFiles()
                        .toArray(SelectedFileData[]::new));
                relatedTexturesList.setCellRenderer(new SelectedFileDataListCellRenderer());
                relatedTexturesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                relatedTexturesList.addListSelectionListener(this::onTextureSelectionChanged);
                relatedTexturesList.addMouseListener(new ListMouseAdapter());
                relatedTexturesPane.add(relatedTexturesList);
                relatedTexturesLists.add(relatedTexturesList);
            }
        }
        invalidate();
        repaint();
    }

    public void addListDoubleClickEventListener(EventListener<ListDoubleClickEvent> eventListener) {
        eventDispatcher.addEventListener(eventListener, ListDoubleClickEvent.class);
    }

    @SuppressWarnings("unchecked")
    private void onModelSelectionChanged(ListSelectionEvent event) {
        if (!suppressSelectionEvents) {
            try {
                suppressSelectionEvents = true;
                for (JList<SelectedFileData> relatedTexturesList : relatedTexturesLists) {
                    relatedTexturesList.setSelectedValue(null, false);
                }
                for (JList<SelectedFileData> relatedModelList : relatedModelsLists) {
                    if (relatedModelList != event.getSource()) {
                        relatedModelList.setSelectedValue(null, false);
                    }
                }
                onSelectionChanged(((JList<SelectedFileData>) event.getSource()).getSelectedValue());
            } finally {
                suppressSelectionEvents = false;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void onTextureSelectionChanged(ListSelectionEvent event) {
        if (!suppressSelectionEvents) {
            try {
                suppressSelectionEvents = true;
                for (JList<SelectedFileData> relatedModelsList : relatedModelsLists) {
                    relatedModelsList.setSelectedValue(null, false);
                }
                for (JList<SelectedFileData> relatedTexturesList : relatedTexturesLists) {
                    if (relatedTexturesList != event.getSource()) {
                        relatedTexturesList.setSelectedValue(null, false);
                    }
                }
                onSelectionChanged(((JList<SelectedFileData>) event.getSource()).getSelectedValue());
            } finally {
                suppressSelectionEvents = false;
            }
        }
    }

    private void onSelectionChanged(SelectedFileData selectedFileData) {
        Object value = selectedFileData == null ? null : selectedFileData.getData();
        previewOuterPane.removeAll();
        if (value instanceof BlockModel blockModel) {
            JTextArea previewArea = new JTextArea(0, 0);
            previewArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            previewArea.setEditable(false);
            ((DefaultCaret) previewArea.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
            previewArea.setText(GsonProvider.getGson().toJson(blockModel));
            previewOuterPane.add(previewArea, BorderLayout.CENTER);
        } else if (value instanceof BufferedImage image) {
            int scale = Math.min(256 / image.getWidth(), 256 / image.getHeight());
            Image scaled = scale > 0 ? image.getScaledInstance(
                    scale * image.getWidth(), scale * image.getHeight(), Image.SCALE_FAST) : image;
            JLabel imageLabel = new JLabel();
            previewOuterPane.add(imageLabel, BorderLayout.CENTER);
            imageLabel.setIcon(new ImageIcon(scaled));
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
        }
        invalidate();
        repaint();
    }

    private static class SelectedFileDataListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value != null) {
                String name = ((SelectedFileData) value).getName();
                setText(name);
                setToolTipText(name);
            }
            return this;
        }
    }

    private class ListMouseAdapter extends MouseAdapter {
        @SuppressWarnings("unchecked")
        @Override
        public void mouseClicked(MouseEvent e) {
            JList<SelectedFileData> list = (JList<SelectedFileData>) e.getSource();
            if (e.getClickCount() >= 2 && e.getButton() == MouseEvent.BUTTON1) {
                Rectangle r = list.getCellBounds(0, list.getLastVisibleIndex());
                if (r != null && r.contains(e.getPoint())) {
                    SelectedFileData data = list.getSelectedValue();
                    eventDispatcher.dispatchEvent(new ListDoubleClickEvent(list, data));
                }
            }
        }
    }
}
