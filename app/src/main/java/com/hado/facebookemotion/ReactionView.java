package com.hado.facebookemotion;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import static com.hado.facebookemotion.CommonDimen.DIVIDE;

/**
 * Created by Hado on 26-Nov-16.
 */

public class ReactionView extends View {

    enum StateDraw {
        BEGIN,
        END,
        CHOOSING,
        NORMAL
    }

    public static final long DURATION_ANIMATION = 200;

    public static final long DURATION_BEGINNING_EACH_ITEM = 300;

    public static final long DURATION_BEGINNING_ANIMATION = 900;

    private EaseOutBack easeOutBack;

    private Board board;

    private IconOption[] iconOptions = new IconOption[6];

    private StateDraw state = StateDraw.BEGIN;

    private int currentPosition = 0;

    private OnItemSelected mOnItemSelected;

    public ReactionView(Context context) {
        super(context);
        init();
    }

    public ReactionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReactionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        board = new Board(getContext());
        setLayerType(LAYER_TYPE_SOFTWARE, board.boardPaint);

        iconOptions[0] = new IconOption(getContext(), "Like", R.drawable.like);
        iconOptions[1] = new IconOption(getContext(), "Love", R.drawable.love);
        iconOptions[2] = new IconOption(getContext(), "Haha", R.drawable.haha);
        iconOptions[3] = new IconOption(getContext(), "Wow", R.drawable.wow);
        iconOptions[4] = new IconOption(getContext(), "Cry", R.drawable.cry);
        iconOptions[5] = new IconOption(getContext(), "Angry", R.drawable.angry);

        initElement();
    }

    private void initElement() {
        board.currentY = CommonDimen.HEIGHT_VIEW_REACTION + 10;
        for (IconOption e : iconOptions) {
            e.currentY = board.currentY + CommonDimen.DIVIDE;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (state != null) {
            board.drawBoard(canvas);
            for (IconOption iconOption : iconOptions) {
                iconOption.drawEmotion(canvas);
            }
        }
    }

    private void beforeAnimateBeginning() {
        board.beginHeight = Board.BOARD_HEIGHT_NORMAL;
        board.endHeight = Board.BOARD_HEIGHT_NORMAL;

        board.beginY = Board.BOARD_BOTTOM + 150;
        board.endY = Board.BOARD_Y;

        easeOutBack = EaseOutBack.newInstance(DURATION_BEGINNING_EACH_ITEM, Math.abs(board.beginY - board.endY), 0);

        for (int i = 0; i < iconOptions.length; i++) {
            iconOptions[i].endY = Board.BASE_LINE - IconOption.NORMAL_SIZE;
            iconOptions[i].beginY = Board.BOARD_BOTTOM + 150;
            iconOptions[i].currentX = i == 0 ? Board.BOARD_X + DIVIDE : iconOptions[i - 1].currentX + iconOptions[i - 1].currentSize + DIVIDE;
        }
    }

    private void beforeAnimateEnding() {
        board.beginHeight = Board.BOARD_HEIGHT_NORMAL;
        board.endHeight = Board.BOARD_HEIGHT_NORMAL;

        board.beginY = Board.BOARD_Y;
        board.endY = Board.BOARD_BOTTOM + 150;

        easeOutBack = EaseOutBack.newInstance(DURATION_BEGINNING_EACH_ITEM, Math.abs(board.beginY - board.endY), 0);

        for (int i = 0; i < iconOptions.length; i++) {
            iconOptions[i].endY = Board.BOARD_BOTTOM + 150;
            iconOptions[i].beginY = Board.BASE_LINE - IconOption.NORMAL_SIZE;
            iconOptions[i].currentX = i == 0 ? Board.BOARD_X + DIVIDE : iconOptions[i - 1].currentX + iconOptions[i - 1].currentSize + DIVIDE;
        }
    }


    private void beforeAnimateChoosing() {
        board.beginHeight = board.getCurrentHeight();
        board.endHeight = Board.BOARD_HEIGHT_MINIMAL;

        for (int i = 0; i < iconOptions.length; i++) {
            iconOptions[i].beginSize = iconOptions[i].currentSize;

            if (i == currentPosition) {
                iconOptions[i].endSize = IconOption.CHOOSE_SIZE;
            } else {
                iconOptions[i].endSize = IconOption.MINIMAL_SIZE;
            }
        }
    }

    private void beforeAnimateNormalBack() {
        board.beginHeight = board.getCurrentHeight();
        board.endHeight = Board.BOARD_HEIGHT_NORMAL;

        for (int i = 0; i < iconOptions.length; i++) {
            iconOptions[i].beginSize = iconOptions[i].currentSize;
            iconOptions[i].endSize = IconOption.NORMAL_SIZE;
        }
    }


    private void calculateInSessionChoosingAndEnding(float interpolatedTime) {
        board.setCurrentHeight(board.beginHeight + (int) (interpolatedTime * (board.endHeight - board.beginHeight)));

        for (int i = 0; i < iconOptions.length; i++) {
            iconOptions[i].currentSize = calculateSize(i, interpolatedTime);
            iconOptions[i].currentY = Board.BASE_LINE - iconOptions[i].currentSize;
        }
        calculateCoordinateX();
        invalidate();
    }

    private void calculateInSessionBeginning(float interpolatedTime) {
        float currentTime = interpolatedTime * DURATION_BEGINNING_ANIMATION;

        if (currentTime > 0) {
            board.currentY = board.endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 100) {
            iconOptions[0].currentY = iconOptions[0].endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 100, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 200) {
            iconOptions[1].currentY = iconOptions[1].endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 200, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 300) {
            iconOptions[2].currentY = iconOptions[2].endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 300, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 400) {
            iconOptions[3].currentY = iconOptions[3].endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 400, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 500) {
            iconOptions[4].currentY = iconOptions[4].endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 500, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 600) {
            iconOptions[5].currentY = iconOptions[5].endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 600, DURATION_BEGINNING_EACH_ITEM));
        }

        invalidate();
    }

    private void calculateInSessionEnding(float interpolatedTime) {
        float currentTime = interpolatedTime * DURATION_BEGINNING_ANIMATION;

        if (currentTime > 0) {
            board.currentY = board.endY - easeOutBack.getCoordinateYFromTime(Math.min(currentTime, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 100) {
            iconOptions[0].currentY = iconOptions[0].endY - easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 100, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 200) {
            iconOptions[1].currentY = iconOptions[1].endY - easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 200, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 300) {
            iconOptions[2].currentY = iconOptions[2].endY - easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 300, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 400) {
            iconOptions[3].currentY = iconOptions[3].endY - easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 400, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 500) {
            iconOptions[4].currentY = iconOptions[4].endY - easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 500, DURATION_BEGINNING_EACH_ITEM));
        }

        if (currentTime >= 600) {
            iconOptions[5].currentY = iconOptions[5].endY - easeOutBack.getCoordinateYFromTime(Math.min(currentTime - 600, DURATION_BEGINNING_EACH_ITEM));
        }

        invalidate();
    }

    private int calculateSize(int position, float interpolatedTime) {
        int changeSize = iconOptions[position].endSize - iconOptions[position].beginSize;
        return iconOptions[position].beginSize + (int) (interpolatedTime * changeSize);
    }

    private void calculateCoordinateX() {
        iconOptions[0].currentX = Board.BOARD_X + DIVIDE;
        iconOptions[iconOptions.length - 1].currentX = Board.BOARD_X + Board.BOARD_WIDTH - DIVIDE - iconOptions[iconOptions.length - 1].currentSize;

        for (int i = 1; i < currentPosition; i++) {
            iconOptions[i].currentX = iconOptions[i - 1].currentX + iconOptions[i - 1].currentSize + DIVIDE;
        }

        for (int i = iconOptions.length - 2; i > currentPosition; i--) {
            iconOptions[i].currentX = iconOptions[i + 1].currentX - iconOptions[i].currentSize - DIVIDE;
        }

        if (currentPosition != 0 && currentPosition != iconOptions.length - 1) {
            if (currentPosition <= (iconOptions.length / 2 - 1)) {
                iconOptions[currentPosition].currentX = iconOptions[currentPosition - 1].currentX + iconOptions[currentPosition - 1].currentSize + DIVIDE;
            } else {
                iconOptions[currentPosition].currentX = iconOptions[currentPosition + 1].currentX - iconOptions[currentPosition].currentSize - DIVIDE;
            }
        }
    }

    public void show() {
        state = StateDraw.BEGIN;
        setVisibility(VISIBLE);
        beforeAnimateBeginning();
        startAnimation(new BeginningAnimation());
    }

    public void hide() {
        state = StateDraw.END;
        setVisibility(INVISIBLE);
        beforeAnimateNormalBack();
        startAnimation(new EndingAnimation());
    }

    private void selected(int position) {
        if (currentPosition == position && state == StateDraw.CHOOSING) return;

        state = StateDraw.CHOOSING;
        currentPosition = position;

        startAnimation(new ChooseEmotionAnimation());

        if (mOnItemSelected != null) {
            mOnItemSelected.onItemSelected(iconOptions[position], position);
        }
    }

    public void backToNormal() {
        state = StateDraw.NORMAL;
        startAnimation(new ChooseEmotionAnimation());
    }

    public void setOnItemSelected(OnItemSelected mOnItemSelected) {
        this.mOnItemSelected = mOnItemSelected;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handled = true;
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < iconOptions.length; i++) {
                    if (event.getX() > iconOptions[i].currentX && event.getX() < iconOptions[i].currentX + iconOptions[i].currentSize) {
                        selected(i);
                        break;
                    }
                }
                handled = true;
                break;
            case MotionEvent.ACTION_UP:
                backToNormal();
                handled = true;
                break;
        }
        return handled;
    }

    class ChooseEmotionAnimation extends Animation {
        public ChooseEmotionAnimation() {
            if (state == StateDraw.CHOOSING) {
                beforeAnimateChoosing();
            } else if (state == StateDraw.NORMAL) {
                beforeAnimateNormalBack();
            }
            setDuration(DURATION_ANIMATION);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            calculateInSessionChoosingAndEnding(interpolatedTime);
        }
    }

    class BeginningAnimation extends Animation {

        public BeginningAnimation() {
            beforeAnimateBeginning();
            setDuration(DURATION_BEGINNING_ANIMATION);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            calculateInSessionBeginning(interpolatedTime);
        }
    }

    class EndingAnimation extends Animation {

        public EndingAnimation() {
            beforeAnimateEnding();
            setDuration(DURATION_BEGINNING_ANIMATION);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            calculateInSessionEnding(interpolatedTime);
        }
    }

    public interface OnItemSelected {
        void onItemSelected(IconOption iconOption, int index);
    }
}
