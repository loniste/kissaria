package com.hakik.record_view;


public interface OnRecordListener {
    void onStart();
    void onCancel();
    void onFinish(long recordTime,boolean limitReached);
    void onLessThanSecond();
}
