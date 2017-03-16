package com.hado.menuoptionsbaloon;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.ArrayList;
import java.util.List;

import static com.hado.menuoptionsbaloon.CommonDimen.DIVIDE;

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

    public static long DURATION_BEGINNING_ANIMATION = 900;

    private EaseOutBack easeOutBack;

    private List<IconOption> iconOptions = new ArrayList<>();

    private Board board;

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

    public void setOptions(@NonNull List<IconOption> options) {
        iconOptions = options;
        DURATION_BEGINNING_ANIMATION = (iconOptions.size() * 150) + 150;
        init();
        invalidate();
    }

    private void init() {
        board = new Board(getContext(), iconOptions.size());
        setLayerType(LAYER_TYPE_SOFTWARE, board.boardPaint);
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
            if (iconOptions.size() > 0) {
                board.drawBoard(canvas);
            }
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

        for (int i = 0; i < iconOptions.size(); i++) {
            iconOptions.get(i).endY = Board.BASE_LINE - IconOption.NORMAL_SIZE;
            iconOptions.get(i).beginY = Board.BOARD_BOTTOM + 150;
            iconOptions.get(i).currentX = i == 0 ? Board.BOARD_X + DIVIDE : iconOptions.get(i - 1).currentX + iconOptions.get(i - 1).currentSize + DIVIDE;
        }
    }

    private void beforeAnimateEnding() {
        board.beginHeight = Board.BOARD_HEIGHT_NORMAL;
        board.endHeight = Board.BOARD_HEIGHT_NORMAL;

        board.beginY = Board.BOARD_Y;
        board.endY = Board.BOARD_BOTTOM + 150;

        easeOutBack = EaseOutBack.newInstance(DURATION_BEGINNING_EACH_ITEM, Math.abs(board.beginY - board.endY), 0);

        for (int i = 0; i < iconOptions.size(); i++) {
            iconOptions.get(i).endY = Board.BOARD_BOTTOM + 150;
            iconOptions.get(i).beginY = Board.BASE_LINE - IconOption.NORMAL_SIZE;
            iconOptions.get(i).currentX = i == 0 ? Board.BOARD_X + DIVIDE : iconOptions.get(i - 1).currentX + iconOptions.get(i - 1).currentSize + DIVIDE;
        }
    }


    private void beforeAnimateChoosing() {
        board.beginHeight = board.getCurrentHeight();
        board.endHeight = Board.BOARD_HEIGHT_MINIMAL;

        for (int i = 0; i < iconOptions.size(); i++) {
            iconOptions.get(i).beginSize = iconOptions.get(i).currentSize;

            if (i == currentPosition) {
                iconOptions.get(i).endSize = IconOption.CHOOSE_SIZE;
            } else {
                iconOptions.get(i).endSize = IconOption.MINIMAL_SIZE;
            }
        }
    }

    private void beforeAnimateNormalBack() {
        board.beginHeight = board.getCurrentHeight();
        board.endHeight = Board.BOARD_HEIGHT_NORMAL;

        for (int i = 0; i < iconOptions.size(); i++) {
            iconOptions.get(i).beginSize = iconOptions.get(i).currentSize;
            iconOptions.get(i).endSize = IconOption.NORMAL_SIZE;
        }
    }


    private void calculateInSessionChoosingAndEnding(float interpolatedTime) {
        board.setCurrentHeight(board.beginHeight + (int) (interpolatedTime * (board.endHeight - board.beginHeight)));

        for (int i = 0; i < iconOptions.size(); i++) {
            iconOptions.get(i).currentSize = calculateSize(i, interpolatedTime);
            iconOptions.get(i).currentY = Board.BASE_LINE - iconOptions.get(i).currentSize;
        }
        calculateCoordinateX();
        invalidate();
    }

    private void calculateInSessionBeginning(float interpolatedTime) {
        float currentTime = interpolatedTime * DURATION_BEGINNING_ANIMATION;

        if (currentTime > 0) {
            board.currentY = board.endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime, DURATION_BEGINNING_EACH_ITEM));
        }

        for (int i = 0; i < iconOptions.size(); i++) {
            int interval = (i + 1) * 100;
            if (currentTime >= interval) {
                iconOptions.get(i).currentY = iconOptions.get(i).endY + easeOutBack.getCoordinateYFromTime(Math.min(currentTime - interval, DURATION_BEGINNING_EACH_ITEM));
            }
        }

        invalidate();
    }

    private void calculateInSessionEnding(float interpolatedTime) {
        float currentTime = interpolatedTime * DURATION_BEGINNING_ANIMATION;

        if (currentTime > 0) {
            board.currentY = board.endY - easeOutBack.getCoordinateYFromTime(Math.min(currentTime, DURATION_BEGINNING_EACH_ITEM));
        }

        for (int i = 0; i < iconOptions.size(); i++) {
            int interval = (i + 1) * 100;
            if (currentTime >= interval) {
                iconOptions.get(i).currentY = iconOptions.get(i).endY - easeOutBack.getCoordinateYFromTime(Math.min(currentTime - interval, DURATION_BEGINNING_EACH_ITEM));
            }
        }

        invalidate();
    }

    private int calculateSize(int position, float interpolatedTime) {
        int changeSize = iconOptions.get(position).endSize - iconOptions.get(position).beginSize;
        return iconOptions.get(position).beginSize + (int) (interpolatedTime * changeSize);
    }

    private void calculateCoordinateX() {
        iconOptions.get(0).currentX = Board.BOARD_X + DIVIDE;
        iconOptions.get(iconOptions.size() - 1).currentX = Board.BOARD_X + Board.BOARD_WIDTH - DIVIDE - iconOptions.get(iconOptions.size() - 1).currentSize;

        for (int i = 1; i < currentPosition; i++) {
            iconOptions.get(i).currentX = iconOptions.get(i - 1).currentX + iconOptions.get(i - 1).currentSize + DIVIDE;
        }

        for (int i = iconOptions.size() - 2; i > currentPosition; i--) {
            iconOptions.get(i).currentX = iconOptions.get(i + 1).currentX - iconOptions.get(i).currentSize - DIVIDE;
        }

        if (currentPosition != 0 && currentPosition != iconOptions.size() - 1) {
            if (currentPosition <= (iconOptions.size() / 2 - 1)) {
                iconOptions.get(currentPosition).currentX = iconOptions.get(currentPosition - 1).currentX + iconOptions.get(currentPosition - 1).currentSize + DIVIDE;
            } else {
                iconOptions.get(currentPosition).currentX = iconOptions.get(currentPosition + 1).currentX - iconOptions.get(currentPosition).currentSize - DIVIDE;
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
            mOnItemSelected.onItemSelected(iconOptions.get(position), position);
        }
    }

    public int getItemCount() {
        return iconOptions.size();
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
                for (int i = 0; i < iconOptions.size(); i++) {
                    if (event.getX() > iconOptions.get(i).currentX && event.getX() < iconOptions.get(i).currentX + iconOptions.get(i).currentSize) {
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
