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

package com.mcmiddleearth.rpmanager.gui.components;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.util.function.Consumer;

public class NumberInput extends TextInput {
    public NumberInput(Float value, Consumer<Float> setter, Consumer<TextInput> onChange) {
        super(value == null ? "" : value.toString(),
                s -> setter.accept(s == null || s.isEmpty() ? 0.0f : Float.parseFloat(s)),
                onChange, new Document());
    }

    private static class Document extends PlainDocument {
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            String text = getText(0, getLength());
            text = text.substring(0, offs) + str + text.substring(offs);
            if (!text.isEmpty()) {
                try {
                    Float.parseFloat(text);
                } catch (NumberFormatException ignored) {
                    System.out.println(text);
                    return;
                }
            }
            super.insertString(offs, str, a);
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException {
            String text = getText(0, getLength());
            text = text.substring(0, offs) + text.substring(offs + len);
            if (!text.isEmpty()) {
                try {
                    Float.parseFloat(text);
                } catch (NumberFormatException ignored) {
                    System.out.println(text);
                    return;
                }
            }
            super.remove(offs, len);
        }

        @Override
        public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            String t = getText(0, getLength());
            t = t.substring(0, offset) + text + t.substring(offset + length);
            if (!t.isEmpty()) {
                try {
                    Float.parseFloat(t);
                } catch (NumberFormatException ignored) {
                    System.out.println(t);
                    return;
                }
            }
            super.replace(offset, length, text, attrs);
        }
    }
}
