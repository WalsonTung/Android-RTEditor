/*
 * Copyright (C) 2015-2018 Emanuel Moecklin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onegravity.rteditor.effects;

import android.text.Spannable;

import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.spans.RTSpan;
import com.onegravity.rteditor.spans.TodolistSpan;
import com.onegravity.rteditor.utils.Helper;
import com.onegravity.rteditor.utils.Paragraph;
import com.onegravity.rteditor.utils.Selection;

import java.util.ArrayList;
import java.util.List;

public class TodolistEffect extends ParagraphEffect<Boolean,TodolistSpan> {

    private ParagraphSpanProcessor<Boolean> mSpans2Process = new ParagraphSpanProcessor();

    @Override
    public void applyToSelection(RTEditText editor, Selection selectedParagraphs, Boolean enable) {
        final Spannable str = editor.getText();

        mSpans2Process.clear();

        // a manual for loop is faster than the for-each loop for an ArrayList:
        // see https://developer.android.com/training/articles/perf-tips.html#Loops
        ArrayList<Paragraph> paragraphs = editor.getParagraphs();
        for (int i = 0, size = paragraphs.size(); i < size; i++) {
            Paragraph paragraph = paragraphs.get(i);

            // find existing TodolistSpan and add them to mSpans2Process to be removed
            List<RTSpan<Boolean>> existingSpans = getSpans(str, paragraph, SpanCollectMode.SPAN_FLAGS);
            mSpans2Process.removeSpans(existingSpans, paragraph);

            // if the paragraph is selected then we sure have a checkbox
            boolean hasExistingSpans = !existingSpans.isEmpty();
            boolean hasTodoList = paragraph.isSelected(selectedParagraphs) ? enable : hasExistingSpans;

            // if we have a bullet then apply a new span
            if (hasTodoList) {
                int margin = Helper.getLeadingMarging();
                boolean checked = false;
                if(hasExistingSpans){
                    checked = existingSpans.get(0).getValue();
                }
                TodolistSpan todoListSpan = new TodolistSpan(checked, margin, paragraph.isEmpty(), paragraph.isFirst(), paragraph.isLast());
                mSpans2Process.addSpan(todoListSpan, paragraph);
                // if the paragraph has number spans, then remove them
                Effects.NUMBER.findSpans2Remove(str, paragraph, mSpans2Process);
                // if the paragraph has bullet spans, then remove them
                Effects.BULLET.findSpans2Remove(str, paragraph, mSpans2Process);
            }
        }

        // add or remove spans
        mSpans2Process.process(str);
    }
}
