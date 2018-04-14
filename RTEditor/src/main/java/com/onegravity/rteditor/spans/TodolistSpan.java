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
package com.onegravity.rteditor.spans;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.onegravity.rteditor.RTEditText;
import com.onegravity.rteditor.effects.Effects;
import com.onegravity.rteditor.utils.ImageUtils;

public class TodolistSpan extends BaseListItemSpan implements LeadingMarginSpan, RTSpan<Boolean>, RTParagraphSpan<Boolean>,ClickableSpanPart {
    private final static String BASE64_IMG_CHECKED = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABoAAAAaCAYAAACpSkzOAAABwElEQVRIS72W7VnCMBDHLw0DMAJuELjyXSYQJlA2gAl8nABGYANhAvlOU7OBbGAHaDmfqylPjC0ttpKPbXK/u3/uJQIAQCnV7/V6z0T0BAB9/tbBSoQQmzRNX4wxiWCIlPKNeR0YLzNhsiybiDAMV0S04B1CiDUA7KIo2reBhmF4DwAPrl2BiJ8sF0OiKFq2AfhnnSASBpGNZtImkvF4rIhomqbpmu+EbfK30+n0ntsvQFpr8ddo7D1/2ERiyFxrvWV750C6ACHiKwBMXUcLxzsDISIDGOSuvdZ60llEnmQFKMmybGiMOXYGKpOMiJZxHHOZ5Ku1dHWSdQJqIlkjkO19C7cm3JuukGwex/HGL5FK6bzel/epogDZyGg0WgghVp7BrdZ6VlaHlSBE5Ep2G+wZppQaSCn5v9vhOcvuXGe86L87j1+wiMgVPvC8y2FSSq4XbpjumhVd4KqIbH/iseHPJW4t/rdKyRolwwWY6/RFyRqBeFMD2EXJGoNqYLWSXQWqgB1tL8tnTt36ld5BEAwPh4MpO2hl5NoZBEEwq9rnn+WRTkScWHl632aU/8fjxI71xx+Pk5s9t1i/WzwgvwAownKAMg9TjwAAAABJRU5ErkJggg==";
    private final static String BASE64_IMG_UNCHEKED="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABoAAAAaCAYAAACpSkzOAAAA4UlEQVRIS+2W0Q2CMBCG7ygDMEpNy7tM4Ag6gk5gnEBHcAUn0HdK7CYwAHDmiCASX7BIfGgfm/b/8v+55H4EAJBSRmEY7oloAwAR301wCkQ8l2V5sNYWyBAhxJV5E4h/krBVVSWotT4S0ZZfIOIJAC7GmJsLVGu9BIBVXxeVUjnHxRBjzM4FMPzbM1EwiJ5uElcnQ1Acx7Ku63uj34KyLMMp3bRanREPGhuvj25sYt17H52Pzg/D1zPwB9EFQbBI09S6e3gp8EonIu4jzeKbZ5X/opzwCiei9Vs5ma1uzVUgH8q9xnET1/juAAAAAElFTkSuQmCC";

    private final int mGapWidth;
    private final boolean mIgnoreSpan;
    private boolean mChecked = false;


    public TodolistSpan( boolean checked, int gapWidth, boolean isEmpty, boolean isFirst, boolean isLast) {
        mChecked = checked;
        mGapWidth = gapWidth;
        mIgnoreSpan = isEmpty && isLast && !isFirst;
    }

    public TodolistSpan( boolean checked,int gapWidth, boolean ignoreSpan) {
        mChecked = checked;
        mGapWidth = gapWidth;
        mIgnoreSpan = ignoreSpan;
    }

    @Override
    public int getLeadingMargin(boolean b) {
        return mIgnoreSpan ? 0 : mGapWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom,
                                  CharSequence text, int start, int end, boolean first, Layout l) {

        Spannable spanned = (Spannable) text;
        if (!mIgnoreSpan && spanned.getSpanStart(this) == start) {
            // set paint
            Paint.Style oldStyle = p.getStyle();
            p.setStyle(Paint.Style.FILL);

            // draw the bullet point
            int size = Math.max(Math.round(determineTextSize(spanned, start, end, p.getTextSize()) / 1.2f), 12);
            draw(c, p, x, dir, top, bottom, size);

            // restore paint
            p.setStyle(oldStyle);
            if(mChecked){
                spanned.setSpan( new StrikethroughSpan(),0, spanned.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }else{
                StrikethroughSpan[] spans =  spanned.getSpans(0,spanned.length(), StrikethroughSpan.class);
                for(StrikethroughSpan span : spans){
                    spanned.removeSpan(span);
                }

            }
        }

    }

    @Override
    public RTParagraphSpan<Boolean> createClone() {
        return new TodolistSpan(mChecked, mGapWidth,mIgnoreSpan);
    }

    @Override
    public Boolean getValue() {
        return mChecked;
    }

    private Rect mRect = null;

    private void draw(Canvas c, Paint p, int x, int dir, int top, int bottom, int size) {
        Bitmap bitmap = ImageUtils.stringToBitmap(mChecked ? BASE64_IMG_CHECKED : BASE64_IMG_UNCHEKED);
        Rect srcRect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
      //  Rect destRect = new Rect(0,0,size,size);
        mRect = calcRect(x + dir,(int)((top + bottom - size) / 2.0f),size,size);
        c.save();
     //   c.translate(x + dir , (top + bottom - size) / 2.0f);
        c.drawBitmap(bitmap,srcRect,mRect,p);
        c.restore();
    }

    private Rect calcRect(int x,int y,int width,int height){
        return new Rect(x,y,x+width,y+height);
    }


    @Override
    public void onClick(TextView textView, String pressedText, MotionEvent event, Spannable spannable, int start, int end) {
        int size = (mRect.right - mRect.left);
        int x = (int)event.getX() - size;
        int y = (int)event.getY() - textView.getBaseline()  - size;
        int right = x + 2*size;
        int bottom = y + 2*size;
        Rect clickRect = new Rect(x,y,right,bottom);
        if(mRect != null && mRect.intersect(clickRect)){
            mChecked = !mChecked;
            RTEditText richText = (RTEditText)textView;
            richText.applyEffect(Effects.TODOLIST,true);
        }
    }
}
