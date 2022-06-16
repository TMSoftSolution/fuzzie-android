package sg.com.fuzzie.android.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.content.res.Resources.Theme;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import sg.com.fuzzie.android.R.attr;
import sg.com.fuzzie.android.R.color;
import sg.com.fuzzie.android.R.dimen;
import sg.com.fuzzie.android.R.drawable;
import sg.com.fuzzie.android.R.style;
import sg.com.fuzzie.android.R.styleable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.util.Objects;

public final class FZSlideButton extends View {

    private float mDesiredSliderHeightDp;
    private float mDesiredSliderWidthDp;
    private int mDesiredSliderHeight;
    private int mDesiredSliderWidth;
    private int mAreaHeight;
    private int mAreaWidth;
    private int mActualAreaWidth;
    private int mBorderRadius;
    private int mActualAreaMargin;
    private int mOriginAreaMargin;
    private CharSequence text;
    private int typeFace;
    private int mTextSize;
    private String textFont;
    private float mTextYPosition;
    private float mTextXPosition;
    private int outerColor;
    private int innerColor;
    private int textColor;
    private int mPosition;
    private float mPositionPerc;
    private float mPositionPercInv;
    private int mIconMargin;
    private int mArrowMargin;
    private float mArrowAngle;
    private int mTickMargin;
    private VectorDrawableCompat mDrawableArrow = null;
    private Drawable mDrawableTick = null;
    private boolean mFlagDrawTick;
    private int mIcon;
    private Paint mOuterPaint = null;
    private Paint mInnerPaint = null;
    private Paint mTextPaint = null;
    private RectF mInnerRect;
    private RectF mOuterRect;
    private float mGraceValue;
    private float mLastX;
    private boolean mFlagMoving;
    private boolean mIsCompleted;
    private boolean isLocked;
    private boolean isRotateIcon;
    @Nullable
    private FZSlideButton.OnSlideAnimationEventListener onAnimationEventListener;
    @Nullable
    private FZSlideButton.OnSlideCompleteListener onSlideCompleteListener;
    @Nullable
    private FZSlideButton.OnSlideResetListener onSlideResetListener;
    @Nullable
    private FZSlideButton.OnSlideUserFailedListener onSlideUserFailedListener;


    public final CharSequence getText() {
        return this.text;
    }

    public final void setText(@NonNull CharSequence value) {
        this.text = value;
        this.invalidate();
    }

    public final int getTypeFace() {
        return this.typeFace;
    }

    public final void setTypeFace(int value) {
        this.typeFace = value;
        this.mTextPaint.setTypeface(Typeface.create("sans-serif-light", value));
        this.invalidate();
    }

    public final String getTextFont(){
        return this.textFont;

    }

    public final void setTextFont(String textFont){
        this.textFont = textFont;
        this.mTextPaint.setTypeface(Typeface.createFromAsset(this.getContext().getAssets(), textFont));
        this.invalidate();
    }

    public final int getOuterColor() {
        return this.outerColor;
    }

    public final void setOuterColor(int value) {
        this.outerColor = value;
        this.mOuterPaint.setColor(value);
        this.mDrawableArrow.setTint(value);
        this.invalidate();
    }

    public final int getInnerColor() {
        return this.innerColor;
    }

    public final void setInnerColor(int value) {
        this.innerColor = value;
        this.mInnerPaint.setColor(value);
        this.invalidate();
    }

    public final int getTextColor() {
        return this.textColor;
    }

    public final void setTextColor(int value) {
        this.textColor = value;
        this.mTextPaint.setColor(value);
        this.invalidate();
    }

    private void setPosition(int value) {
        this.mPosition = value;
        if (this.mAreaWidth - this.mAreaHeight == 0) {
            this.mPositionPerc = 0.0F;
            this.mPositionPercInv = 1.0F;
        } else {
            this.mPositionPerc = (float)value / (float)(this.mAreaWidth - this.mAreaHeight);
            this.mPositionPercInv = (float)1 - (float)value / (float)(this.mAreaWidth - this.mAreaHeight);
        }
    }

    public final boolean isLocked() {
        return this.isLocked;
    }

    public final void setLocked(boolean var1) {
        this.isLocked = var1;
    }

    public final boolean isRotateIcon() {
        return this.isRotateIcon;
    }

    public final void setRotateIcon(boolean var1) {
        this.isRotateIcon = var1;
    }

    @Nullable
    public final FZSlideButton.OnSlideAnimationEventListener getonAnimationEventListener() {
        return this.onAnimationEventListener;
    }

    public final void setonAnimationEventListener(@Nullable FZSlideButton.OnSlideAnimationEventListener var1) {
        this.onAnimationEventListener = var1;
    }

    @Nullable
    public final FZSlideButton.OnSlideCompleteListener getOnSlideCompleteListener() {
        return this.onSlideCompleteListener;
    }

    public final void setOnSlideCompleteListener(@Nullable FZSlideButton.OnSlideCompleteListener var1) {
        this.onSlideCompleteListener = var1;
    }

    @Nullable
    public final FZSlideButton.OnSlideResetListener getOnSlideResetListener() {
        return this.onSlideResetListener;
    }

    public final void setOnSlideResetListener(@Nullable FZSlideButton.OnSlideResetListener var1) {
        this.onSlideResetListener = var1;
    }

    @Nullable
    public final FZSlideButton.OnSlideUserFailedListener getOnSlideUserFailedListener() {
        return this.onSlideUserFailedListener;
    }

    public final void setOnSlideUserFailedListener(@Nullable FZSlideButton.OnSlideUserFailedListener var1) {
        this.onSlideUserFailedListener = var1;
    }

    private VectorDrawableCompat parseVectorDrawableCompat(Resources res, int resId, Theme theme) throws Throwable {
        XmlResourceParser parser = res.getXml(resId);
        AttributeSet attrs = Xml.asAttributeSet((XmlPullParser) parser);

        int type;
        for(type = parser.next(); type != 2 && type != 1; type = parser.next()) {
            ;
        }

        if (type != 2) {
            throw (Throwable)(new XmlPullParserException("No start tag found"));
        } else {
            return VectorDrawableCompat.createFromXmlInner(res, (XmlPullParser)parser, attrs, theme);
        }
    }

    @SuppressLint("SwitchIntDef")
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int var10000;
        switch(widthMode) {
            case Integer.MIN_VALUE:
                var10000 = Math.min(this.mDesiredSliderWidth, widthSize);
                break;
            case 0:
                var10000 = this.mDesiredSliderWidth;
                break;
            case 1073741824:
                var10000 = widthSize;
                break;
            default:
                var10000 = this.mDesiredSliderWidth;
        }

        int width = var10000;
        this.setMeasuredDimension(width, this.mDesiredSliderHeight);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.mAreaWidth = w;
        this.mAreaHeight = h;
        if (this.mBorderRadius == -1) {
            this.mBorderRadius = h / 2;
        }

        this.mTextXPosition = (float)this.mAreaWidth / (float)2;
        this.mTextYPosition = (float)this.mAreaHeight / (float)2 - (this.mTextPaint.descent() + this.mTextPaint.ascent()) / (float)2;
    }

    protected void onDraw(@Nullable Canvas canvas) {
        super.onDraw(canvas);
        if (canvas != null) {
            this.mOuterRect.set((float)this.mActualAreaWidth, 0.0F, (float)this.mAreaWidth - (float)this.mActualAreaWidth, (float)this.mAreaHeight);
            canvas.drawRoundRect(this.mOuterRect, (float)this.mBorderRadius, (float)this.mBorderRadius, this.mOuterPaint);
            float ratio = (float)(this.mAreaHeight - 2 * this.mActualAreaMargin) / (float)this.mAreaHeight;
            this.mInnerRect.set((float)(this.mActualAreaMargin + this.mPosition), (float)this.mActualAreaMargin, (float)(this.mAreaHeight + this.mPosition) - (float)this.mActualAreaMargin, (float)this.mAreaHeight - (float)this.mActualAreaMargin);
            canvas.drawRoundRect(this.mInnerRect, (float)this.mBorderRadius * ratio, (float)this.mBorderRadius * ratio, this.mInnerPaint);
            this.mTextPaint.setAlpha((int)((float)255 * this.mPositionPercInv));
            canvas.drawText(this.text.toString(), this.mTextXPosition, this.mTextYPosition, this.mTextPaint);
            if (this.isRotateIcon) {
                this.mArrowAngle = (float)-180 * this.mPositionPerc;
                canvas.rotate(this.mArrowAngle, this.mInnerRect.centerX(), this.mInnerRect.centerY());
            }

            this.mDrawableArrow.setBounds((int)this.mInnerRect.left + this.mArrowMargin, (int)this.mInnerRect.top + this.mArrowMargin, (int)this.mInnerRect.right - this.mArrowMargin, (int)this.mInnerRect.bottom - this.mArrowMargin);
            if (this.mDrawableArrow.getBounds().left <= this.mDrawableArrow.getBounds().right && this.mDrawableArrow.getBounds().top <= this.mDrawableArrow.getBounds().bottom) {
                this.mDrawableArrow.draw(canvas);
            }

            if (this.isRotateIcon) {
                canvas.rotate((float)-1 * this.mArrowAngle, this.mInnerRect.centerX(), this.mInnerRect.centerY());
            }

            this.mDrawableTick.setBounds(this.mActualAreaWidth + this.mTickMargin, this.mTickMargin, this.mAreaWidth - this.mTickMargin - this.mActualAreaWidth, this.mAreaHeight - this.mTickMargin);
            if (VERSION.SDK_INT >= 21) {
                this.mDrawableTick.setTint(this.innerColor);
            } else {
                Drawable var10000 = this.mDrawableTick;
                if (this.mDrawableTick == null) {
                    throw new ClassCastException("null cannot be cast to non-null type android.support.graphics.drawable.AnimatedVectorDrawableCompat");
                }

                ((AnimatedVectorDrawableCompat)var10000).setTint(this.innerColor);
            }

            if (this.mFlagDrawTick) {
                this.mDrawableTick.draw(canvas);
            }

        }
    }

    public boolean performClick() {
        return super.performClick();
    }

    public boolean onTouchEvent(@Nullable MotionEvent event) {
        if (event != null && this.isEnabled()) {
            FZSlideButton.OnSlideUserFailedListener var10000;
            switch(event.getAction()) {
                case 0:
                    if (this.checkInsideButton(event.getX(), event.getY())) {
                        this.mFlagMoving = true;
                        this.mLastX = event.getX();
                        this.getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        var10000 = this.onSlideUserFailedListener;
                        if (this.onSlideUserFailedListener != null) {
                            var10000.onSlideFailed(this, true);
                        }
                    }

                    this.performClick();
                    break;
                case 1:
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    if (this.mPosition > 0 && this.isLocked || this.mPosition > 0 && this.mPositionPerc < this.mGraceValue) {
                        ValueAnimator positionAnimator = ValueAnimator.ofInt(this.mPosition, 0);
                        positionAnimator.setDuration(300L);
                        positionAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
                            public final void onAnimationUpdate(ValueAnimator it) {
                                FZSlideButton var10000 = FZSlideButton.this;
                                Object var10001 = it.getAnimatedValue();
                                if (var10001 == null) {
                                    throw new ClassCastException("null cannot be cast to non-null type kotlin.Int");
                                } else {
                                    var10000.setPosition((Integer)var10001);
                                    FZSlideButton.this.invalidateArea();
                                }
                            }
                        }));
                        positionAnimator.start();
                    } else if (this.mPosition > 0 && this.mPositionPerc >= this.mGraceValue) {
                        this.setEnabled(false);
                        this.startAnimationComplete();
                    } else if (this.mFlagMoving && this.mPosition == 0) {
                        var10000 = this.onSlideUserFailedListener;
                        if (this.onSlideUserFailedListener != null) {
                            var10000.onSlideFailed(this, false);
                        }
                    }

                    this.mFlagMoving = false;
                    break;
                case 2:
                    if (this.mFlagMoving) {
                        float diffX = event.getX() - this.mLastX;
                        this.mLastX = event.getX();
                        this.increasePosition((int)diffX);
                        this.invalidateArea();
                    }
            }

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private void invalidateArea() {
        this.invalidate((int)this.mOuterRect.left, (int)this.mOuterRect.top, (int)this.mOuterRect.right, (int)this.mOuterRect.bottom);
    }

    private boolean checkInsideButton(float x, float y) {
        return (float)0 < y && y < (float)this.mAreaHeight && (float)this.mPosition < x && x < (float)(this.mAreaHeight + this.mPosition);
    }

    private void increasePosition(int inc) {
        this.setPosition(this.mPosition + inc);
        if (this.mPosition < 0) {
            this.setPosition(0);
        }

        if (this.mPosition > this.mAreaWidth - this.mAreaHeight) {
            this.setPosition(this.mAreaWidth - this.mAreaHeight);
        }

    }

    private void startAnimationComplete() {
        AnimatorSet animSet = new AnimatorSet();
        ValueAnimator finalPositionAnimator = ValueAnimator.ofInt(this.mPosition, this.mAreaWidth - this.mAreaHeight);
        finalPositionAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator it) {
                FZSlideButton var10000 = FZSlideButton.this;
                Object var10001 = it.getAnimatedValue();
                if (var10001 == null) {
                    throw new ClassCastException("null cannot be cast to non-null type kotlin.Int");
                } else {
                    var10000.setPosition((Integer)var10001);
                    FZSlideButton.this.invalidateArea();
                }
            }
        }));
        ValueAnimator marginAnimator = ValueAnimator.ofInt(this.mActualAreaMargin, (int)(this.mInnerRect.width() / (float)2) + this.mActualAreaMargin);
        marginAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator it) {
                FZSlideButton var10000 = FZSlideButton.this;
                Object var10001 = it.getAnimatedValue();
                if (var10001 == null) {
                    throw new ClassCastException("null cannot be cast to non-null type kotlin.Int");
                } else {
                    var10000.mActualAreaMargin = (Integer)var10001;
                    FZSlideButton.this.invalidateArea();
                }
            }
        }));
        marginAnimator.setInterpolator((TimeInterpolator)(new AnticipateOvershootInterpolator(2.0F)));
        ValueAnimator areaAnimator = ValueAnimator.ofInt(0, (this.mAreaWidth - this.mAreaHeight) / 2);
        areaAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator it) {
                FZSlideButton var10000 = FZSlideButton.this;
                Object var10001 = it.getAnimatedValue();
                if (var10001 == null) {
                    throw new ClassCastException("null cannot be cast to non-null type kotlin.Int");
                } else {
                    var10000.mActualAreaWidth = (Integer)var10001;
                    if (VERSION.SDK_INT >= 21) {
                        FZSlideButton.this.invalidateOutline();
                    }

                    FZSlideButton.this.invalidateArea();
                }
            }
        }));
        ValueAnimator var10000;
        ValueAnimator tickAnimator;
        if (VERSION.SDK_INT <= 24) {
            var10000 = ValueAnimator.ofInt(0, 255);
            tickAnimator = var10000;
            tickAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator it) {
                    FZSlideButton.this.mTickMargin = FZSlideButton.this.mIconMargin;
                    FZSlideButton.this.mFlagDrawTick = true;
                    Drawable var10000 = FZSlideButton.this.mDrawableTick;
                    Object var10001 = it.getAnimatedValue();
                    if (var10001 == null) {
                        throw new ClassCastException("null cannot be cast to non-null type kotlin.Int");
                    } else {
                        var10000.setAlpha((Integer)var10001);
                        FZSlideButton.this.invalidateArea();
                    }
                }
            }));
        } else {
            var10000 = ValueAnimator.ofInt(0);
            tickAnimator = var10000;
            tickAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator it) {
                    if (!FZSlideButton.this.mFlagDrawTick) {
                        FZSlideButton.this.mTickMargin = FZSlideButton.this.mIconMargin;
                        FZSlideButton.this.mFlagDrawTick = true;
                        FZSlideButton.this.startTickAnimation();
                        FZSlideButton.this.invalidateArea();
                    }

                }
            }));
        }

        if (this.mPosition >= this.mAreaWidth - this.mAreaHeight) {
            animSet.playSequentially((Animator)marginAnimator, (Animator)areaAnimator, (Animator)tickAnimator);
        } else {
            animSet.playSequentially((Animator)finalPositionAnimator, (Animator)marginAnimator, (Animator)areaAnimator, (Animator)tickAnimator);
        }

        animSet.setDuration(300L);
        animSet.addListener((AnimatorListener)(new AnimatorListener() {
            public void onAnimationStart(@Nullable Animator p0) {
                FZSlideButton.OnSlideAnimationEventListener var10000 = FZSlideButton.this.getonAnimationEventListener();
                if (var10000 != null) {
                    var10000.onSlideCompleteAnimationStarted(FZSlideButton.this, FZSlideButton.this.mPositionPerc);
                }

            }

            public void onAnimationCancel(@Nullable Animator p0) {
            }

            public void onAnimationEnd(@Nullable Animator p0) {
                FZSlideButton.this.mIsCompleted = true;
                FZSlideButton.OnSlideAnimationEventListener var10000 = FZSlideButton.this.getonAnimationEventListener();
                if (var10000 != null) {
                    var10000.onSlideCompleteAnimationEnded(FZSlideButton.this);
                }

                FZSlideButton.OnSlideCompleteListener var2 = FZSlideButton.this.getOnSlideCompleteListener();
                if (var2 != null) {
                    var2.onSlideComplete(FZSlideButton.this);
                }

            }

            public void onAnimationRepeat(@Nullable Animator p0) {
            }
        }));
        animSet.start();
    }

    public final void completeSlider() {
        if (!this.mIsCompleted) {
            this.startAnimationComplete();
        }

    }

    public final void resetSlider() {
        if (this.mIsCompleted) {
            this.startAnimationReset();
        }

    }

    public final boolean isCompleted() {
        return this.mIsCompleted;
    }

    private void startAnimationReset() {
        this.mIsCompleted = false;
        AnimatorSet animSet = new AnimatorSet();
        ValueAnimator tickAnimator = ValueAnimator.ofInt(this.mTickMargin, this.mAreaWidth / 2);
        tickAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator it) {
                FZSlideButton var10000 = FZSlideButton.this;
                Object var10001 = it.getAnimatedValue();
                if (var10001 == null) {
                    throw new ClassCastException("null cannot be cast to non-null type kotlin.Int");
                } else {
                    var10000.mTickMargin = (Integer)var10001;
                    FZSlideButton.this.invalidateArea();
                }
            }
        }));
        ValueAnimator areaAnimator = ValueAnimator.ofInt(this.mActualAreaWidth, 0);
        areaAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator it) {
                FZSlideButton.this.mFlagDrawTick = false;
                FZSlideButton var10000 = FZSlideButton.this;
                Object var10001 = it.getAnimatedValue();
                if (var10001 == null) {
                    throw new ClassCastException("null cannot be cast to non-null type kotlin.Int");
                } else {
                    var10000.mActualAreaWidth = (Integer)var10001;
                    if (VERSION.SDK_INT >= 21) {
                        FZSlideButton.this.invalidateOutline();
                    }

                    FZSlideButton.this.invalidateArea();
                }
            }
        }));
        ValueAnimator positionAnimator = ValueAnimator.ofInt(this.mPosition, 0);
        positionAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator it) {
                FZSlideButton var10000 = FZSlideButton.this;
                Object var10001 = it.getAnimatedValue();
                if (var10001 == null) {
                    throw new ClassCastException("null cannot be cast to non-null type kotlin.Int");
                } else {
                    var10000.setPosition((Integer)var10001);
                    FZSlideButton.this.invalidateArea();
                }
            }
        }));
        ValueAnimator marginAnimator = ValueAnimator.ofInt(this.mActualAreaMargin, this.mOriginAreaMargin);
        marginAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator it) {
                FZSlideButton var10000 = FZSlideButton.this;
                Object var10001 = it.getAnimatedValue();
                if (var10001 == null) {
                    throw new ClassCastException("null cannot be cast to non-null type kotlin.Int");
                } else {
                    var10000.mActualAreaMargin = (Integer)var10001;
                    FZSlideButton.this.invalidateArea();
                }
            }
        }));
        marginAnimator.setInterpolator((TimeInterpolator)(new AnticipateOvershootInterpolator(2.0F)));
        ValueAnimator arrowAnimator = ValueAnimator.ofInt(this.mArrowMargin, this.mIconMargin);
        arrowAnimator.addUpdateListener((AnimatorUpdateListener)(new AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator it) {
                FZSlideButton var10000 = FZSlideButton.this;
                Object var10001 = it.getAnimatedValue();
                if (var10001 == null) {
                    throw new ClassCastException("null cannot be cast to non-null type kotlin.Int");
                } else {
                    var10000.mArrowMargin = (Integer)var10001;
                    FZSlideButton.this.invalidateArea();
                }
            }
        }));
        marginAnimator.setInterpolator((TimeInterpolator)(new OvershootInterpolator(2.0F)));
        animSet.playSequentially((Animator)tickAnimator, (Animator)areaAnimator, (Animator)positionAnimator, (Animator)marginAnimator, (Animator)arrowAnimator);
        animSet.setDuration(300L);
        animSet.addListener((AnimatorListener)(new AnimatorListener() {
            public void onAnimationStart(@Nullable Animator p0) {
                FZSlideButton.OnSlideAnimationEventListener var10000 = FZSlideButton.this.getonAnimationEventListener();
                if (var10000 != null) {
                    var10000.onSlideResetAnimationStarted(FZSlideButton.this);
                }

            }

            public void onAnimationCancel(@Nullable Animator p0) {
            }

            public void onAnimationEnd(@Nullable Animator p0) {
                FZSlideButton.this.setEnabled(true);
                FZSlideButton.this.stopTickAnimation();
                FZSlideButton.OnSlideAnimationEventListener var10000 = FZSlideButton.this.getonAnimationEventListener();
                if (var10000 != null) {
                    var10000.onSlideResetAnimationEnded(FZSlideButton.this);
                }

                FZSlideButton.OnSlideResetListener var2 = FZSlideButton.this.getOnSlideResetListener();
                if (var2 != null) {
                    var2.onSlideReset(FZSlideButton.this);
                }

            }

            public void onAnimationRepeat(@Nullable Animator p0) {
            }
        }));
        animSet.start();
    }

    private void startTickAnimation() {
        Drawable var10000;
        if (VERSION.SDK_INT >= 21) {
            var10000 = this.mDrawableTick;
            if (this.mDrawableTick == null) {
                throw new ClassCastException("null cannot be cast to non-null type android.graphics.drawable.AnimatedVectorDrawable");
            }

            ((AnimatedVectorDrawable)var10000).start();
        } else {
            var10000 = this.mDrawableTick;
            if (this.mDrawableTick == null) {
                throw new ClassCastException("null cannot be cast to non-null type android.support.graphics.drawable.AnimatedVectorDrawableCompat");
            }

            ((AnimatedVectorDrawableCompat)var10000).start();
        }

    }

    private void stopTickAnimation() {
        Drawable var10000;
        if (VERSION.SDK_INT >= 21) {
            var10000 = this.mDrawableTick;
            if (this.mDrawableTick == null) {
                throw new ClassCastException("null cannot be cast to non-null type android.graphics.drawable.AnimatedVectorDrawable");
            }

            ((AnimatedVectorDrawable)var10000).stop();
        } else {
            var10000 = this.mDrawableTick;
            if (this.mDrawableTick == null) {
                throw new ClassCastException("null cannot be cast to non-null type android.support.graphics.drawable.AnimatedVectorDrawableCompat");
            }

            ((AnimatedVectorDrawableCompat)var10000).stop();
        }

    }


    public FZSlideButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) throws Throwable {

        super(context, attrs, defStyleAttr);
        this.mDesiredSliderHeightDp = 60.0F;
        this.mDesiredSliderWidthDp = 280.0F;
        this.mBorderRadius = -1;
        this.text = (CharSequence)"";
        this.mTextYPosition = -1.0F;
        this.mTextXPosition = -1.0F;
        this.mPositionPercInv = 1.0F;
        this.mIcon = drawable.ic_slide_arrow;
        this.mOuterPaint = new Paint(1);
        this.mInnerPaint = new Paint(1);
        this.mTextPaint = new Paint(1);
        this.mGraceValue = 0.8F;
        this.isRotateIcon = true;
        TypedArray layoutAttrs = context.getTheme().obtainStyledAttributes(attrs, styleable.FZSlideButton, defStyleAttr, style.FZSlideButtonStyle);

        int actualOuterColor;
        int actualInnerColor;
        int actualTextColor;
        try {
            float var10002 = this.mDesiredSliderHeightDp;
            Resources var10003 = this.getResources();
            this.mDesiredSliderHeight = (int)TypedValue.applyDimension(1, var10002, var10003.getDisplayMetrics());
            var10002 = this.mDesiredSliderWidthDp;
            var10003 = this.getResources();
            this.mDesiredSliderWidth = (int)TypedValue.applyDimension(1, var10002, var10003.getDisplayMetrics());
            this.mDesiredSliderHeight = layoutAttrs.getDimensionPixelSize(styleable.FZSlideButton_slider_height, this.mDesiredSliderHeight);
            this.mBorderRadius = layoutAttrs.getDimensionPixelSize(styleable.FZSlideButton_border_radius, -1);
            int defaultOuter = ContextCompat.getColor(this.getContext(), color.colorPrimary);
            int defaultWhite = ContextCompat.getColor(this.getContext(), color.white);
            actualOuterColor = layoutAttrs.getColor(styleable.FZSlideButton_outer_color, defaultOuter);
            actualInnerColor = layoutAttrs.getColor(styleable.FZSlideButton_inner_color, defaultWhite);
            actualTextColor = layoutAttrs.getColor(styleable.FZSlideButton_text_color, defaultWhite);
            String var10001 = layoutAttrs.getString(styleable.FZSlideButton_text);
            assert var10001 != null;
            this.setText((CharSequence)var10001);
            this.setTypeFace(layoutAttrs.getInt(styleable.FZSlideButton_text_style, 0));
            if (layoutAttrs.getString(styleable.FZSlideButton_text_font) != null && !Objects.equals(layoutAttrs.getString(styleable.FZSlideButton_text_font), "")){
                this.setTextFont(layoutAttrs.getString(styleable.FZSlideButton_text_font));
            }
            this.isLocked = layoutAttrs.getBoolean(styleable.FZSlideButton_slider_locked, false);
            this.isRotateIcon = layoutAttrs.getBoolean(styleable.FZSlideButton_rotate_icon, true);
            this.mTextSize = layoutAttrs.getDimensionPixelSize(styleable.FZSlideButton_text_size, this.getResources().getDimensionPixelSize(dimen.slide_button_default_text_size));
            this.mOriginAreaMargin = layoutAttrs.getDimensionPixelSize(styleable.FZSlideButton_area_margin, this.getResources().getDimensionPixelSize(dimen.slide_button_default_area_margin));
            this.mActualAreaMargin = this.mOriginAreaMargin;
            this.mIcon = layoutAttrs.getResourceId(styleable.FZSlideButton_slider_icon, drawable.ic_slide_arrow);
        } finally {
            layoutAttrs.recycle();
        }

        this.mInnerRect = new RectF((float)(this.mActualAreaMargin + this.mPosition), (float)this.mActualAreaMargin, (float)(this.mAreaHeight + this.mPosition) - (float)this.mActualAreaMargin, (float)this.mAreaHeight - (float)this.mActualAreaMargin);
        this.mOuterRect = new RectF((float)this.mActualAreaWidth, 0.0F, (float)this.mAreaWidth - (float)this.mActualAreaWidth, (float)this.mAreaHeight);
        Resources var14 = context.getResources();
        int var15 = this.mIcon;
        Theme var10004 = context.getTheme();
        this.mDrawableArrow = this.parseVectorDrawableCompat(var14, var15, var10004);
        Drawable var12;
        if (VERSION.SDK_INT >= 21) {
            var12 = context.getResources().getDrawable(drawable.ic_animated_slide_check, context.getTheme());
            if (var12 == null) {
                throw new ClassCastException("null cannot be cast to non-null type android.graphics.drawable.AnimatedVectorDrawable");
            }

            var12 = (Drawable)((AnimatedVectorDrawable)var12);
        } else {
            AnimatedVectorDrawableCompat var13 = AnimatedVectorDrawableCompat.create(context, drawable.ic_animated_slide_check);

            var12 = (Drawable)var13;
        }

        this.mDrawableTick = var12;
        this.mTextPaint.setTextAlign(Align.CENTER);
        this.mTextPaint.setTextSize((float)this.mTextSize);
        this.setOuterColor(actualOuterColor);
        this.setInnerColor(actualInnerColor);
        this.setTextColor(actualTextColor);
        this.mIconMargin = context.getResources().getDimensionPixelSize(dimen.slide_button_default_icon_margin);
        this.mArrowMargin = this.mIconMargin;
        this.mTickMargin = this.mIconMargin;
        if (VERSION.SDK_INT >= 21) {
            this.setOutlineProvider((ViewOutlineProvider)(new FZSlideButton.SlideOutlineProvider()));
        }

    }



    public FZSlideButton(@NonNull Context context) throws Throwable {
        this(context, null);
    }


    public FZSlideButton(@NonNull Context context, @Nullable AttributeSet attrs) throws Throwable {
        this(context, attrs, attr.FZSlideButtonStyle);
    }


    public static void setAreaWidth(FZSlideButton $this, int var1) {
        $this.mAreaWidth = var1;
    }


    public static void setAreaHeight(FZSlideButton $this, int var1) {
        $this.mAreaHeight = var1;
    }


    public static void setBorderRadius(FZSlideButton $this, int var1) {
        $this.mBorderRadius = var1;
    }


    public static int getPosition(FZSlideButton $this) {
        return $this.mPosition;
    }


    public static int getActualAreaMargin(FZSlideButton $this) {
        return $this.mActualAreaMargin;
    }


    public static int getTickMargin(FZSlideButton $this) {
        return $this.mTickMargin;
    }


    public static void setPositionPerc(FZSlideButton $this, float var1) {
        $this.mPositionPerc = var1;
    }


    public static boolean getIsCompleted(FZSlideButton $this) {
        return $this.mIsCompleted;
    }


    public static int getArrowMargin(FZSlideButton $this) {
        return $this.mArrowMargin;
    }

    public interface OnSlideAnimationEventListener {
        void onSlideCompleteAnimationStarted(@NonNull FZSlideButton var1, float var2);

        void onSlideCompleteAnimationEnded(@NonNull FZSlideButton var1);

        void onSlideResetAnimationStarted(@NonNull FZSlideButton var1);

        void onSlideResetAnimationEnded(@NonNull FZSlideButton var1);
    }


    public interface OnSlideCompleteListener {
        void onSlideComplete(@NonNull FZSlideButton var1);
    }

    public interface OnSlideResetListener {
        void onSlideReset(@NonNull FZSlideButton var1);
    }

    public interface OnSlideUserFailedListener {
        void onSlideFailed(@NonNull FZSlideButton var1, boolean var2);
    }

    private final class SlideOutlineProvider extends ViewOutlineProvider {
        public void getOutline(@Nullable View view, @Nullable Outline outline) {
            if (view != null && outline != null) {
                outline.setRoundRect(FZSlideButton.this.mActualAreaWidth, 0, FZSlideButton.this.mAreaWidth - FZSlideButton.this.mActualAreaWidth, FZSlideButton.this.mAreaHeight, (float)FZSlideButton.this.mBorderRadius);
            }
        }
    }
}
