/*
 * Copyright (c) Faisal Khan (https://github.com/faisalcodes)
 * Created on 21/2/2022.
 * All rights reserved.
 */

package com.quranapp.android.widgets.chapterCard;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.peacedesign.android.utils.Dimen;
import com.quranapp.android.R;
import com.quranapp.android.utils.extensions.ContextKt;
import com.quranapp.android.utils.extensions.LayoutParamsKt;
import com.quranapp.android.utils.extensions.TextViewKt;
import com.quranapp.android.utils.extensions.ViewPaddingKt;
import com.quranapp.android.utils.sharedPrefs.SPFavouriteChapters;
import com.quranapp.android.views.reader.ChapterIcon;

import java.util.Locale;

public class ChapterCard extends ConstraintLayout {
    private int mChapterNo;
    private Runnable mOnFavBtnClickListener;

    public ChapterCard(@NonNull Context context) {
        this(context, null);
    }

    public ChapterCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChapterCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        ViewPaddingKt.updatePaddings(this, Dimen.dp2px(getContext(), 10));

        View serialView = createSerialView();
        View nameView = createNameView();
        View rightView = createRightView();
        View favIcon = createFavIcon();

        LayoutParams p1 = (LayoutParams) serialView.getLayoutParams();
        p1.topToTop = PARENT_ID;
        p1.bottomToBottom = PARENT_ID;
        p1.startToStart = PARENT_ID;
        p1.endToStart = nameView.getId();
        serialView.setLayoutParams(p1);

        LayoutParams p2 = (LayoutParams) nameView.getLayoutParams();
        p2.topToTop = PARENT_ID;
        p2.bottomToBottom = PARENT_ID;
        p2.startToEnd = serialView.getId();
        if (rightView != null) {
            p2.endToStart = rightView.getId();
        } else {
            p2.endToEnd = PARENT_ID;
        }
        p2.horizontalWeight = 1;
        nameView.setLayoutParams(p2);

        if (rightView != null) {
            LayoutParams p3 = (LayoutParams) rightView.getLayoutParams();
            p3.topToTop = PARENT_ID;
            p3.bottomToBottom = PARENT_ID;
            p3.startToEnd = nameView.getId();

            if (favIcon != null) {
                p3.endToStart = favIcon.getId();
            } else {
                p3.endToEnd = PARENT_ID;
            }

            rightView.setLayoutParams(p3);
        }

        if (favIcon != null) {
            LayoutParams p3 = (LayoutParams) favIcon.getLayoutParams();
            p3.topToTop = PARENT_ID;
            p3.bottomToBottom = PARENT_ID;

            assert rightView != null;
            p3.startToEnd = rightView.getId();
            p3.endToEnd = PARENT_ID;
            favIcon.setLayoutParams(p3);
        }
    }

    private View createSerialView() {
        AppCompatTextView v = new AppCompatTextView(getContext());
        v.setId(R.id.chapterCardSerial);
        v.setBackgroundResource(R.drawable.dr_bg_reader_chapter_serial);
        v.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        v.setGravity(Gravity.CENTER);
        v.setMaxLines(1);
        v.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.color_reader_spinner_item_label));
        TextViewKt.setTextSizePx(v, R.dimen.dmnCommonSize2);

        int size = Dimen.dp2px(getContext(), 35);
        v.setLayoutParams(new ConstraintLayout.LayoutParams(size, size));

        addView(v);
        return v;
    }

    private View createNameView() {
        AppCompatTextView v = new AppCompatTextView(getContext());
        v.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
        v.setId(R.id.chapterCardName);
        LayoutParams p = new LayoutParams(0, WRAP_CONTENT);
        LayoutParamsKt.updateMarginHorizontal(p, Dimen.dp2px(getContext(), 10));
        v.setLayoutParams(p);

        addView(v);
        return v;
    }

    @Nullable
    protected View createRightView() {
        ChapterIcon v = new ChapterIcon(getContext());
        v.setId(R.id.chapterCardIcon);
        TextViewKt.setTextSizePx(v, R.dimen.dmnChapterIcon2);
        v.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        v.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.color_reader_spinner_item_label));
        addView(v);
        return v;
    }

    @Nullable
    protected View createFavIcon() {
        ImageView v = new AppCompatImageView(getContext());
        v.setId(R.id.chapterCardFavIcon);
        v.setBackgroundResource(R.drawable.dr_bg_hover_round);

        updateFavIcon();
        ViewPaddingKt.updatePaddings(v, ContextKt.dp2px(getContext(), 5));

        final int dimen = ContextKt.getDimenPx(getContext(), R.dimen.dmnActionButtonSmall);
        LayoutParams params = new LayoutParams(dimen, dimen);
        params.leftMargin = ContextKt.dp2px(getContext(), 5);
        v.setLayoutParams(params);

        v.setOnClickListener(v1 -> {
            if (mChapterNo <= 0) return;
            if (mOnFavBtnClickListener != null) mOnFavBtnClickListener.run();
        });

        addView(v);
        return v;
    }

    private void updateFavIcon() {
        View favIcon = findViewById(R.id.chapterCardFavIcon);

        if (!(favIcon instanceof ImageView)) return;

        ((ImageView) favIcon).setImageResource(
            SPFavouriteChapters.INSTANCE.isAddedToFavorites(getContext(), mChapterNo)
                ? R.drawable.icon_star_filled
                : R.drawable.icon_star_outlined
        );
    }

    public void setOnFavoriteButtonClickListener(Runnable listener) {
        mOnFavBtnClickListener = listener;
    }

    public void setChapterNumber(int chapterNo) {
        mChapterNo = chapterNo;

        View serial = findViewById(R.id.chapterCardSerial);
        if (serial instanceof TextView) {
            ((TextView) serial).setText(String.format(Locale.getDefault(), "%d", chapterNo));
        }

        View icon = findViewById(R.id.chapterCardIcon);
        if (icon instanceof ChapterIcon) {
            ((ChapterIcon) icon).setChapterNumber(chapterNo);
        }

        updateFavIcon();
    }

    public void setName(String chapterName, String chapterTransl) {
        View name = findViewById(R.id.chapterCardName);
        if (name instanceof TextView) {
            ((TextView) name).setText(makeName(getContext(), chapterName, chapterTransl));
        }
    }

    private CharSequence makeName(Context ctx, String chapterName, String chapterTransl) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        String SANS_SERIF = "sans-serif";
        ColorStateList nameClr = ContextCompat.getColorStateList(ctx, R.color.color_reader_spinner_item_label);

        SpannableString nameSS = new SpannableString(chapterName);
        setSpan(nameSS, new TextAppearanceSpan(SANS_SERIF, Typeface.BOLD, -1, nameClr, null));
        ssb.append(nameSS);

        if (!TextUtils.isEmpty(chapterTransl)) {
            ColorStateList translClr = ContextCompat.getColorStateList(ctx, R.color.color_reader_spinner_item_label2);
            int dimen2 = ContextKt.getDimenPx(ctx, R.dimen.dmnCommonSize2);

            SpannableString translSS = new SpannableString(chapterTransl);
            setSpan(translSS, new TextAppearanceSpan(SANS_SERIF, Typeface.NORMAL, dimen2, translClr, null));
            ssb.append("\n").append(translSS);
        }
        return ssb;
    }

    private void setSpan(SpannableString ss, Object obj) {
        ss.setSpan(obj, 0, ss.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
