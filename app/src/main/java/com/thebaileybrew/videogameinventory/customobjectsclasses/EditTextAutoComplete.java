package com.thebaileybrew.videogameinventory.customobjectsclasses;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class EditTextAutoComplete extends AppCompatAutoCompleteTextView {

    public EditTextAutoComplete(Context context) {
        super(context);
    }

    public EditTextAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextAutoComplete(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        final InputConnection ic = super.onCreateInputConnection(outAttrs);
        if (ic != null & outAttrs.hintText != null) {
            //If hint is empty and the parent is a TextInputLayout
            //Then use its hint in 'extract mode'
            final ViewParent parent = getParent();
            if (parent instanceof TextInputLayout) {
                outAttrs.hintText = ((TextInputLayout) parent).getHint();
            }
        }
        return ic;
    };

}
