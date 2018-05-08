package com.viewlift.firetvcustomkeyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import rx.functions.Action1;

public class CustomKeyboard extends RelativeLayout implements View.OnFocusChangeListener,
        View.OnKeyListener {

    private static final float LETTER_SPACING = 0.01f;
    private static ColorStateList txtColor;
    private final int animationDuration = 400;

    private final Context context;
    private final TextView tvSelectUc;
    private final TextView tvSelectLc;
    private final TextView tvSelectNum;
    private final TextView tvSelectSc;
    Typeface face;
    private LinearLayout keyboardSelectLayout;
    private LinearLayout specialCharLayout;
    private LinearLayout upperCaseLayout;
    private LinearLayout lowerCaseLayout;
    private LinearLayout numberLayout;
    private CurrentlySelectedKeyboardLayoutEnum currentLayoutEnum =
            CurrentlySelectedKeyboardLayoutEnum.UPPERCASE;
    private boolean isRetainSelectedLayout = true;

    @SuppressLint("CutPasteId")
    @SuppressWarnings("ConstantConditions")
    public CustomKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        System.out.println("anas2");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View keyboardView = inflater.inflate(R.layout.custom_keyboard_layout, this, true);

        @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    public void setFocusOnGroup() {
        if (null != tvSelectUc) {
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

            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    String focusedColor = null;
    @SuppressWarnings("unused")
    public void setFocusColor(String color) {
        focusedColor = color;
        GradientDrawable gradientDrawable =
                (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.appcms_search_key_background_focused);
        gradientDrawable.setColor(Color.parseColor(color));
        gradientDrawable.mutate();

        GradientDrawable buttonDrawable =
                (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.btn_bg_focused);
        buttonDrawable.setColor(Color.parseColor(color));
        buttonDrawable.mutate();
        invalidate();
    }


    public ColorStateList getButtonTextColorDrawable(String defaultColor, String focusedColor){
        String focusStateTextColor = focusedColor;
        String unFocusStateTextColor = defaultColor;

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_focused},
                new int[] {android.R.attr.state_selected},
                new int[] {android.R.attr.state_pressed},
                new int[] {}
        };

        int[] colors = new int[] {
                Color.parseColor(focusStateTextColor),
                Color.parseColor(focusStateTextColor),
                Color.parseColor(focusStateTextColor),
                Color.parseColor(unFocusStateTextColor)
        };
        txtColor = new ColorStateList(states, colors);
        setTextColor();
        return txtColor;
    }


    public void setFontFamily(LinearLayout linearLayout) {
        if (null == face) {
            face = Typeface.createFromAsset(context.getAssets(),
                    "SanFranciscoDisplay-Regular.otf");
        }

        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (linearLayout.getChildAt(i) instanceof TextView ||
                    linearLayout.getChildAt(i) instanceof Button) {
                ((TextView) linearLayout.getChildAt(i)).setTypeface(face);
                ((TextView) linearLayout.getChildAt(i)).setLetterSpacing(LETTER_SPACING);
            }
        }
    }

    private Action1<Boolean> spaceBtnFocusListener;
    public void setonButtonFocusChangeListener(Action1<Boolean> action1) {
        spaceBtnFocusListener = action1;
    }

    OnFocusChangeListener onSpaceFocusChangeListener = new OnFocusChangeListener(){
        @Override
        public void onFocusChange(View view, boolean b) {
            if(null != spaceBtnFocusListener)
            spaceBtnFocusListener.call(true);
        }
    };

    OnFocusChangeListener onBtnFocusChangeListener = new OnFocusChangeListener(){
        @Override
        public void onFocusChange(View view, boolean b) {
            if(null != spaceBtnFocusListener)
                    spaceBtnFocusListener.call(false);
        }
    };

    private enum CurrentlySelectedKeyboardLayoutEnum {
        UPPERCASE,
        LOWERCASE,
        NUMBER,
        SPECIAL_CHARACTERS
    }

    private void setTextColor(){
        try {
            ((Button) findViewById(R.id.btn_uc_key_space)).setTextColor(txtColor);
            ((Button) findViewById(R.id.btn_lc_key_space)).setTextColor(txtColor);
            ((Button) findViewById(R.id.btn_num_key_space)).setTextColor(txtColor);
            ((Button) findViewById(R.id.btn_sc_key_space)).setTextColor(txtColor);

            ((Button) findViewById(R.id.btn_uc_key_space)).setOnFocusChangeListener(onSpaceFocusChangeListener);
            ((Button) findViewById(R.id.btn_lc_key_space)).setOnFocusChangeListener(onSpaceFocusChangeListener);
            ((Button) findViewById(R.id.btn_num_key_space)).setOnFocusChangeListener(onSpaceFocusChangeListener);
            ((Button) findViewById(R.id.btn_sc_key_space)).setOnFocusChangeListener(onSpaceFocusChangeListener);

            ((TextView) findViewById(R.id.tv_key_uc_a)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_a)).setOnFocusChangeListener(onBtnFocusChangeListener);
            ((TextView) findViewById(R.id.tv_key_uc_b)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_c)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_d)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_e)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_f)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_g)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_h)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_i)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_j)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_k)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_l)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_m)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_n)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_o)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_p)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_q)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_r)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_s)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_t)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_u)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_v)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_w)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_x)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_y)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_uc_z)).setTextColor(txtColor);

            ((TextView) findViewById(R.id.tv_key_lc_a)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_a)).setOnFocusChangeListener(onBtnFocusChangeListener);
            ((TextView) findViewById(R.id.tv_key_lc_b)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_c)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_d)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_e)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_f)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_g)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_h)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_i)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_j)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_k)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_l)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_m)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_n)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_o)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_p)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_q)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_r)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_s)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_t)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_u)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_v)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_w)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_x)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_y)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_lc_z)).setTextColor(txtColor);

            ((TextView) findViewById(R.id.tv_key_num_1)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_num_1)).setOnFocusChangeListener(onBtnFocusChangeListener);
            ((TextView) findViewById(R.id.tv_key_num_2)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_num_3)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_num_4)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_num_5)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_num_6)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_num_7)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_num_8)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_num_9)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_num_0)).setTextColor(txtColor);

            ((TextView) findViewById(R.id.tv_key_sc_exclamation)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_exclamation)).setOnFocusChangeListener(onBtnFocusChangeListener);
            ((TextView) findViewById(R.id.tv_key_sc_at_the_rate)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_cent)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_colon)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_hash)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_dollar)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_percentage)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_exponential)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_g)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_estrics)).setTextColor(txtColor);

            ((TextView) findViewById(R.id.tv_key_sc_left_parenthesis)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_right_parenthesis)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_underscore)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_hyphen)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_plus)).setTextColor(txtColor);

            ((TextView) findViewById(R.id.tv_key_sc_ewual)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_pipe)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_semi_colon)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_single_quote)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_double_quote)).setTextColor(txtColor);

            ((TextView) findViewById(R.id.tv_key_sc_comma)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_period)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_question_mark)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_slash)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_euro)).setTextColor(txtColor);

            ((TextView) findViewById(R.id.tv_key_sc_pound)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_yen)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_cent)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_left_angle)).setTextColor(txtColor);
            ((TextView) findViewById(R.id.tv_key_sc_right_angle)).setTextColor(txtColor);

            if(null != focusedColor) {
                ((ImageButton) findViewById(R.id.btn_uc_key_backspace)).getBackground().
                        setTint(context.getResources().getColor(R.color.appcms_btn_default_border_color));
                ((ImageButton) findViewById(R.id.btn_lc_key_backspace)).getBackground().
                        setTint(context.getResources().getColor(R.color.appcms_btn_default_border_color));
                ((ImageButton) findViewById(R.id.btn_num_key_backspace)).getBackground().
                        setTint(context.getResources().getColor(R.color.appcms_btn_default_border_color));
                ((ImageButton) findViewById(R.id.btn_sc_key_backspace)).getBackground().
                        setTint(context.getResources().getColor(R.color.appcms_btn_default_border_color));

                ((ImageButton) findViewById(R.id.btn_uc_key_backspace)).setOnFocusChangeListener(focusChangeListener);
                ((ImageButton) findViewById(R.id.btn_lc_key_backspace)).setOnFocusChangeListener(focusChangeListener);
                ((ImageButton) findViewById(R.id.btn_num_key_backspace)).setOnFocusChangeListener(focusChangeListener);
               ((ImageButton) findViewById(R.id.btn_sc_key_backspace)).setOnFocusChangeListener(focusChangeListener);
            }
        }catch (Exception e){

        }

    }

    OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if(null != view) {

                if(null != spaceBtnFocusListener)
                        spaceBtnFocusListener.call(false);

                if (b) {
                    ((ImageButton) view).getBackground().
                            setTint(Color.parseColor(focusedColor));
                } else {
                    ((ImageButton) view).getBackground().
                            setTint(context.getResources().getColor(R.color.appcms_btn_default_border_color));
                }
            }
        }
    };
}
