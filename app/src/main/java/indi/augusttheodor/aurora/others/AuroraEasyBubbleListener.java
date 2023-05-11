package indi.augusttheodor.aurora.others;

public abstract class AuroraEasyBubbleListener implements DragBubbleView.OnBubbleStateListener {
    @Override
    public void onDrag() {

    }

    @Override
    public void onMove() {

    }

    @Override
    public void onRestore() {

    }

    @Override
    public abstract void onDismiss(); //就剩Dismiss可调用
}
