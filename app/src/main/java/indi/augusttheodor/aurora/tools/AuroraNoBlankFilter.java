package indi.augusttheodor.aurora.tools;

import android.text.InputFilter;
import android.text.Spanned;

public class AuroraNoBlankFilter implements InputFilter { //过滤空白符
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        return source.toString().replace(" ","");
    }
}
