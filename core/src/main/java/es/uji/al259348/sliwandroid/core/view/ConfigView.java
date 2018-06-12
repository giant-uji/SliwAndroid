package es.uji.al259348.sliwandroid.core.view;

public interface ConfigView extends View {

    void onError(Throwable throwable);

    void onNextStep(String msg);
    void onStepProgressUpdated(int progress);
    void onAllStepsFinished();
    void onConfigFinished();
    void onConfigFailed();
}
