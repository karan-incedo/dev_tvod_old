package com.viewlift.firetvcustomkeyboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomKeyboard
        extends RelativeLayout
        implements View.OnFocusChangeListener,
        View.OnKeyListener {

    private static final float LETTER_SPACING = 0.01f;
    private final int animationDuration = 400;

    private final Context context;
    private final TextView tvSelectUc;
    private final TextView tvSelectLc;
    private final TextView tvSelectNum;
    private final TextView tvSelectSc;

    private LinearLayout keyboardSelectLayout;

    private LinearLayout specialCharLayout;
    private LinearLayout upperCaseLayout;
    private LinearLayout lowerCaseLayout;
    private LinearLayout numberLayout;

    private CurrentlySelectedKeyboardLayoutEnum currentLayoutEnum =
            CurrentlySelectedKeyboardLayoutEnum.UPPERCASE;
    private boolean isRetainSelectedLayout = true;

    public CustomKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        System.out.println("anas2");

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View keyboardView = inflater.inflate(R.layout.custom_keyboard_layout, this, true);

        CharSequence text = ((TextView) keyboardView.findViewById(R.id.tv_select_uc)).getText();

        tvSelectUc = (TextView) keyboardView.findViewById(R.id.tv_select_uc);
        tvSelectLc = (TextView) keyboardView.findViewById(R.id.tv_select_lc);
        tvSelectNum = (TextView) keyboardView.findViewById(R.id.tv_select_num);
        tvSelectSc = (TextView) keyboardView.findViewById(R.id.tv_select_sc);

        upperCaseLayout = (LinearLayout) keyboardView.findViewById(R.id.appcms_uc_keyboard_layout);
        lowerCaseLayout = (LinearLayout) keyboardView.findViewById(R.id.appcms_lc_keyboard_layout);
        numberLayout = (LinearLayout) keyboardView.findViewById(R.id.appcms_num_keyboard_layout);
        specialCharLayout = (LinearLayout) keyboardView.findViewById(R.id.appcms_sc_keyboard_layout);
        setFontFamily(upperCaseLayout);
        setFontFamily(lowerCaseLayout);
        setFontFamily(numberLayout);
        setFontFamily(specialCharLayout);

        keyboardSelectLayout = (LinearLayout) keyboardView.findViewById(R.id.appcms_keyboard_select_layout);

        tvSelectUc.setOnFocusChangeListener(this);
        tvSelectLc.setOnFocusChangeListener(this);
        tvSelectNum.setOnFocusChangeListener(this);
        tvSelectSc.setOnFocusChangeListener(this);

        tvSelectUc.setOnKeyListener(this);
        tvSelectLc.setOnKeyListener(this);
        tvSelectNum.setOnKeyListener(this);
        tvSelectSc.setOnKeyListener(this);

    }

    public void setFocusOnGroup(){
        if(null != tvSelectUc){
            tvSelectUc.requestFocus();
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        int i = view.getId();
        if (i == R.id.tv_select_uc) {
            if (hasFocus) {
                if (currentLayoutEnum.equals(CurrentlySelectedKeyboardLayoutEnum.LOWERCASE)) {
                    performKeyboardDownAnimation(lowerCaseLayout, upperCaseLayout);
                } else if (currentLayoutEnum.equals(CurrentlySelectedKeyboardLayoutEnum.SPECIAL_CHARACTERS)) {
                    performKeyboardDownAnimation(specialCharLayout, upperCaseLayout);
                } else if (currentLayoutEnum.equals(
                        CurrentlySelectedKeyboardLayoutEnum.NUMBER)) {
                    performKeyboardDownAnimation(numberLayout, upperCaseLayout);
                }
                currentLayoutEnum = CurrentlySelectedKeyboardLayoutEnum.UPPERCASE;
                isRetainSelectedLayout = false;
            }

        } else if (i == R.id.tv_select_lc) {
            if (hasFocus) {
                if (currentLayoutEnum.equals(CurrentlySelectedKeyboardLayoutEnum.UPPERCASE)) {
                    performKeyboardUpAnimation(upperCaseLayout, lowerCaseLayout);
                } else if (currentLayoutEnum.equals(
                        CurrentlySelectedKeyboardLayoutEnum.NUMBER)) {
                    performKeyboardDownAnimation(numberLayout, lowerCaseLayout);
                }
                isRetainSelectedLayout = false;
                currentLayoutEnum = CurrentlySelectedKeyboardLayoutEnum.LOWERCASE;
            }

        } else if (i == R.id.tv_select_num) {
            if (hasFocus) {
                if (currentLayoutEnum.equals(CurrentlySelectedKeyboardLayoutEnum.LOWERCASE)) {
                    performKeyboardUpAnimation(lowerCaseLayout, numberLayout);
                } else if (currentLayoutEnum.equals(
                        CurrentlySelectedKeyboardLayoutEnum.SPECIAL_CHARACTERS)) {
                    performKeyboardDownAnimation(specialCharLayout, numberLayout);
                } else if (currentLayoutEnum.equals(
                        CurrentlySelectedKeyboardLayoutEnum.UPPERCASE)) {
                    performKeyboardDownAnimation(upperCaseLayout, numberLayout);
                }
                isRetainSelectedLayout = false;
                currentLayoutEnum = CurrentlySelectedKeyboardLayoutEnum.NUMBER;
            }

        } else if (i == R.id.tv_select_sc) {
            if (hasFocus) {
                if (currentLayoutEnum.equals(CurrentlySelectedKeyboardLayoutEnum.NUMBER)) {
                    performKeyboardUpAnimation(numberLayout, specialCharLayout);
                }
                isRetainSelectedLayout = false;
                currentLayoutEnum = CurrentlySelectedKeyboardLayoutEnum.SPECIAL_CHARACTERS;
            }

        } else if (i == R.id.appcms_keyboard_select_layout) {
            if (isRetainSelectedLayout) {
                switch (currentLayoutEnum) {
                    case UPPERCASE:
                        tvSelectUc.requestFocus();
                        break;
                    case LOWERCASE:
                        tvSelectLc.requestFocus();
                        break;
                    case NUMBER:
                        tvSelectNum.requestFocus();
                        break;
                    case SPECIAL_CHARACTERS:
                        tvSelectSc.requestFocus();
                        break;
                }
            }
            manageFocusOnKeyboardSelection();

        }
        manageFocusOnKeyboardSelection();
    }

    /**
     * Method is used to perform an "UP" animation on the Keyboard layout
     *
     * @param toBeHidden the layout which is to be hidden
     * @param toBeShown  the layout which us to be shown
     */
    private void performKeyboardUpAnimation(LinearLayout toBeHidden, LinearLayout toBeShown) {
        TranslateAnimation translateAnimation =
                new TranslateAnimation(0, 0, 0, Utils.convertDpToPixel(-60, context));
        translateAnimation.setDuration(animationDuration);
        translateAnimation.setFillAfter(true);
        toBeHidden.startAnimation(translateAnimation);
        toBeHidden.setVisibility(View.INVISIBLE);

        TranslateAnimation translateAnimation2 =
                new TranslateAnimation(0, 0, Utils.convertDpToPixel(60, context), 0);
        translateAnimation2.setDuration(animationDuration);
        translateAnimation2.setFillAfter(true);
        toBeShown.startAnimation(translateAnimation2);
        toBeShown.setVisibility(View.VISIBLE);
    }

    /**
     * Method is used to perform an "DOWN" animation on the Keyboard layout
     *
     * @param toBeHidden the layout which is to be hidden
     * @param toBeShown  the layout which us to be shown
     */
    private void performKeyboardDownAnimation(LinearLayout toBeHidden, LinearLayout toBeShown) {
        TranslateAnimation translateAnimation =
                new TranslateAnimation(0, 0, 0, Utils.convertDpToPixel(60, context));
        translateAnimation.setDuration(animationDuration);
        translateAnimation.setFillAfter(true);
        toBeHidden.startAnimation(translateAnimation);
        toBeHidden.setVisibility(View.INVISIBLE);

        TranslateAnimation translateAnimation2 =
                new TranslateAnimation(0, 0, Utils.convertDpToPixel(-60, context), 0);
        translateAnimation2.setDuration(animationDuration);
        translateAnimation2.setFillAfter(true);
        toBeShown.startAnimation(translateAnimation2);
        toBeShown.setVisibility(View.VISIBLE);
    }


    /**
     * Method is used to manage the focus on the keyboard selector layout.
     */
    @SuppressWarnings("deprecation")
    private void manageFocusOnKeyboardSelection() {
        switch (currentLayoutEnum) {
            case UPPERCASE:
                ((TextView) keyboardSelectLayout.getChildAt(0))
                        .setTextColor(getResources().getColor(android.R.color.white));
                ((TextView) keyboardSelectLayout.getChildAt(1))
                        .setTextColor(getResources().getColor(android.R.color.black));
                ((TextView) keyboardSelectLayout.getChildAt(2))
                        .setTextColor(getResources().getColor(android.R.color.black));
                ((TextView) keyboardSelectLayout.getChildAt(3))
                        .setTextColor(getResources().getColor(android.R.color.black));
                break;
            case LOWERCASE:
                ((TextView) keyboardSelectLayout.getChildAt(1))
                        .setTextColor(getResources().getColor(android.R.color.white));
                ((TextView) keyboardSelectLayout.getChildAt(0))
                        .setTextColor(getResources().getColor(android.R.color.black));
                ((TextView) keyboardSelectLayout.getChildAt(2))
                        .setTextColor(getResources().getColor(android.R.color.black));
                ((TextView) keyboardSelectLayout.getChildAt(3))
                        .setTextColor(getResources().getColor(android.R.color.black));
                break;
            case NUMBER:
                ((TextView) keyboardSelectLayout.getChildAt(2))
                        .setTextColor(getResources().getColor(android.R.color.white));
                ((TextView) keyboardSelectLayout.getChildAt(0))
                        .setTextColor(getResources().getColor(android.R.color.black));
                ((TextView) keyboardSelectLayout.getChildAt(1))
                        .setTextColor(getResources().getColor(android.R.color.black));
                ((TextView) keyboardSelectLayout.getChildAt(3))
                        .setTextColor(getResources().getColor(android.R.color.black));
                break;
            case SPECIAL_CHARACTERS:
                ((TextView) keyboardSelectLayout.getChildAt(3))
                        .setTextColor(getResources().getColor(android.R.color.white));
                ((TextView) keyboardSelectLayout.getChildAt(0))
                        .setTextColor(getResources().getColor(android.R.color.black));
                ((TextView) keyboardSelectLayout.getChildAt(1))
                        .setTextColor(getResources().getColor(android.R.color.black));
                ((TextView) keyboardSelectLayout.getChildAt(2))
                        .setTextColor(getResources().getColor(android.R.color.black));
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    public void setFocusColor(String color) {
       GradientDrawable gradientDrawable =
               (GradientDrawable) ContextCompat.getDrawable(context , R.drawable.appcms_search_key_background_focused);
        gradientDrawable.setColor(Color.parseColor(color));
        gradientDrawable.mutate();

        GradientDrawable buttonDrawable =
                (GradientDrawable) ContextCompat.getDrawable(context , R.drawable.btn_bg_focused);
        buttonDrawable.setColor(Color.parseColor(color));
        buttonDrawable.mutate();
        invalidate();
    }

    Typeface face;
    public void setFontFamily(LinearLayout linearLayout){
        if(null == face){
            face = Typeface.createFromAsset(context.getAssets(),
                    "SanFranciscoDisplay-Regular.otf");
        }

        int childCount = linearLayout.getChildCount();
        for(int i=0;i<childCount;i++){
            if( linearLayout.getChildAt(i) instanceof  TextView ||
                    linearLayout.getChildAt(i) instanceof  Button){
                ((TextView)linearLayout.getChildAt(i)).setTypeface(face);
                ((TextView)linearLayout.getChildAt(i)).setLetterSpacing(LETTER_SPACING);
            }
        }
    }

    private enum CurrentlySelectedKeyboardLayoutEnum {
        UPPERCASE,
        LOWERCASE,
        NUMBER,
        SPECIAL_CHARACTERS
    }
}
