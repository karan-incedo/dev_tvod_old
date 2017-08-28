package com.viewlift.views.customviews;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

public class AsteriskPasswordTransformation extends PasswordTransformationMethod {

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private static class PasswordCharSequence implements CharSequence {

        private CharSequence source;

        PasswordCharSequence(CharSequence source) {
            this.source = source;
        }

        @Override
        public int length() {
            return this.source.length();
        }

        @Override
        public char charAt(int i) {
            return '*';
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return source.subSequence(start, end);
        }
    }
}
