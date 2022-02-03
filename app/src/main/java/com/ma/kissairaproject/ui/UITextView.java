package com.ma.kissairaproject.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ma.kissairaproject.R;
import com.ma.kissairaproject.utilities.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UITextView extends ConstraintLayout {
    private static final String TAG = "UITextView";
    @BindView(R.id.iconIV)
    public ImageView iconIV;
    @BindView(R.id.labelTV)
    public TextView labelTV;
    @BindView(R.id.textView)
    public TextView textView;


    String label = "";
    int icon = R.drawable.user_icon;
    int iconTint = 0;
    String text = "";


    private View rootView;

    public UITextView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public UITextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public UITextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        getLayoutParams().width = LayoutParams.MATCH_PARENT;
//        getLayoutParams().height = LayoutParams.MATCH_PARENT;
    }

    void init(@Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.ui_text_view, this, true);
        ButterKnife.bind(this);


        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.UITextView);
        label = ta.getString(R.styleable.UITextView_label);
        icon = ta.getResourceId(R.styleable.UITextView_icon_id,R.drawable.user_icon);
        iconTint = ta.getColor(R.styleable.UITextView_icon_tint, 0);
        text = ta.getString(R.styleable.UITextView_text);
        ta.recycle();

        labelTV.setText(label);
        if (icon != 0) {
            if(iconTint!=0){
                Drawable drawable= Utils.setTint( getContext(), icon, iconTint);
                iconIV.setImageDrawable(drawable);
            }else{
                iconIV.setImageResource(icon);
                RequestOptions options=new RequestOptions();
                options.centerInside();
                Glide.with(getContext())
                        .load(icon)
                        .apply(options)
                        .into(iconIV);
            }
        }
        textView.setText(text);
    }
}
