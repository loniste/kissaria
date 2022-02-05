package com.hakik.audio_player;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class AudioPlayerView extends ConstraintLayout {
    public static final int DEFAULT_CANCEL_BOUNDS = 8; //8dp
    private static final String TAG = "AudioPlayerView";
    private final AnimatorListenerAdapter animatorListenerAdapter = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation, boolean isReverse) {
            Log.d(TAG, "onAnimationEnd: called 987654");
        }

        @Override
        public void onAnimationResume(Animator animation) {
            Log.d(TAG, "onAnimationResume: called 987654");
        }

        @Override
        public void onAnimationStart(Animator animation) {
            Log.d(TAG, "onAnimationStart: called 987654");

        }

        @Override
        public void onAnimationPause(Animator animation) {
            Log.d(TAG, "onAnimationPause: called 987654");
        }

        @Override
        public void onAnimationStart(Animator animation, boolean isReverse) {
            Log.d(TAG, "onAnimationStart: called 987654");
        }
    };
    public boolean isPlaying = false;
    View audioCursor;
    RoundCornerProgressBar progress;
    LottieAnimationView play_pause;
    View bgContainer;

    int xDelta = 0;
    TextView leftTV;
    float mAmplitude = 0.2f;
    float mFrequency = 10f;
    int audioCursorNormalSize;
    int audioCursorMaxSize;
    OnAudioActionListener onAudioActionListener;
    private int audioCursorMinX;
    private int audioCursorMaxX;
    private ImageView smallBlinkingMic, basketImg;
    private Context context;
    private boolean canRecord = true;
    private File recordFile;
    private String recordUrl;
    private MediaPlayer mp;
    private ValueAnimator audioValueAnimator;
    private float fraction = -1;
    private int audioDuration;
    private CountDownTimer cdt;
    View.OnTouchListener onCursorTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final int x = (int) motionEvent.getRawX();
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    scaleCursor(true);
                    xDelta = (int) (x - audioCursor.getX());
                    if (onAudioActionListener != null) {
                        onAudioActionListener.onActionDown();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (getAudioCursorMinX() <= x - xDelta && x - xDelta <= audioCursorMaxX) {
                        audioCursor.setX(x - xDelta);
                        fraction = ((float) (x - xDelta - audioCursorMinX)) / (audioCursorMaxX - audioCursorMinX);
                        progress.setProgress(fraction * 100.0f);
                        if (cdt != null) {
                            cdt.setFraction(fraction);
                        }
                        if (mp != null)
                            mp.seekTo((int) (fraction * mp.getDuration()));
                        if (audioValueAnimator != null) {
                            audioValueAnimator.setCurrentFraction(fraction);
                        }

                    }
//                        audioCursor.getParent().requestLayout();
//                        ((View) audioCursor.getParent()).invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    scaleCursor(false);

                    if (audioValueAnimator == null) {
                        progress.setProgress(0);
                        audioCursor.setX(audioCursorMinX);

                    }
                    if (onAudioActionListener != null) {
                        onAudioActionListener.onActionUp();
                    }
//                        recordView.onActionUp((RecordButton) v);
                    break;
            }
            return true;
        }
    };
    View.OnTouchListener onProgressTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final int x = (int) (motionEvent.getRawX() - audioCursorNormalSize / 2.0f);
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    scaleCursor(true);
                    if (onAudioActionListener != null) {
                        onAudioActionListener.onActionDown();
                    }
                case MotionEvent.ACTION_MOVE:
                    if (getAudioCursorMinX() <= x && x <= audioCursorMaxX) {
                        audioCursor.setX(x);
                        fraction = ((float) (x - audioCursorMinX)) / (audioCursorMaxX - audioCursorMinX);
                        progress.setProgress(fraction * 100.0f);
                        if (cdt != null) {
                            cdt.setFraction(fraction);
                        }
                        if (mp != null)
                            mp.seekTo((int) (fraction * mp.getDuration()));
                        if (audioValueAnimator != null) {
                            audioValueAnimator.setCurrentFraction(fraction);
                        }

                    }
//                        audioCursor.getParent().requestLayout();
//                        ((View) audioCursor.getParent()).invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    scaleCursor(false);
                    if (audioValueAnimator == null) {
                        progress.setProgress(0);
                        audioCursor.setX(audioCursorMinX);

                    }
                    if (onAudioActionListener != null) {
                        onAudioActionListener.onActionUp();
                    }
//                        recordView.onActionUp((RecordButton) v);
                    break;
            }
            return true;
        }
    };
    private int backgroundId = -1;
    private boolean isPrepared = false;

    public AudioPlayerView(Context context) {
        super(context);
        this.context = context;
        init(context, null, -1, -1);
    }

    public AudioPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs, -1, -1);
    }

    public AudioPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context, attrs, defStyleAttr, -1);
    }






    private void scaleCursor(boolean forward) {

        UtilsAudioPlayer.getValueAnimator(forward, 100, new Interpolator() {
                    @Override
                    public float getInterpolation(float x) {
                        return (float) ((-1 * Math.exp(-x / mAmplitude) * Math.cos(mFrequency * x) + 1) / (-1 * Math.exp(-1 / mAmplitude) * Math.cos(mFrequency * 1) + 1));
                    }
                },
                progress -> {
                    audioCursor.setScaleX((2 - 1) * progress + 1);
                    audioCursor.setScaleY((2 - 1) * progress + 1);
                }).start();
    }

    public int getAudioCursorMinX() {
        return audioCursorMinX;
    }

    public void setAudioCursorMinX(int audioCursorMinX) {
        this.audioCursorMinX = audioCursorMinX;
    }

    public int getAudioCursorMaxX() {
        return audioCursorMaxX;
    }

    public void setAudioCursorMaxX(int audioCursorMaxX) {
        this.audioCursorMaxX = audioCursorMaxX;
    }

    public void setRecordFile(File recordFile,OnAudioActionListener onAudioActionListener) {
        this.recordFile = recordFile;
        this.onAudioActionListener = onAudioActionListener;

        updateChrono(recordFile.getPath());
    }

    public void setRecordUrl(String url, OnAudioActionListener onAudioActionListener) {
        this.recordUrl = url;
        Log.d(TAG, "setRecordPath: called");
        this.onAudioActionListener = onAudioActionListener;
        updateChrono(recordUrl);
    }



    private void updateChrono(String url) {
        if (mp != null) {
            mp.release();
            mp = null;
        }
        mp = new MediaPlayer();
        Log.d(TAG, "updateChrono: called");


//        mp.setAudioAttributes(new AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_MEDIA)
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
//                .build());
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
//            mp.setDataSource(recordFile.getPath());
            mp.setDataSource(url);
            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    isPrepared = true;
                    leftTV.setText(getHumanTimeText(mp.getDuration()));
                    cdt = new CountDownTimer(mp.getDuration(), 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            leftTV.setText(getHumanTimeText(millisUntilFinished));
                        }

                        @Override
                        public void onFinish() {
                            leftTV.setText(getHumanTimeText(mp.getDuration()));
                        }
                    };
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        View view = View.inflate(context, R.layout.audio_player, null);
        addView(view);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        viewGroup.setClipChildren(false);

        audioCursor = view.findViewById(R.id.audioCursor);
        leftTV = view.findViewById(R.id.leftTV);
        progress = findViewById(R.id.progress);
        play_pause = findViewById(R.id.playIV);

        bgContainer = findViewById(R.id.bgContainer);


        if (attrs != null && defStyleAttr == -1 && defStyleRes == -1) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AudioPlayer,
                    defStyleAttr, defStyleRes);


            backgroundId = typedArray.getResourceId(R.styleable.AudioPlayer_audio_player_background, -1);
//            String slideToCancelText = typedArray.getString(R.styleable.RecordView_slide_to_cancel_text);
//            int slideMarginRight = (int) typedArray.getDimension(R.styleable.RecordView_slide_to_cancel_margin_right, 30);
//            int counterTimeColor = typedArray.getColor(R.styleable.RecordView_counter_time_color, -1);
//            int arrowColor = typedArray.getColor(R.styleable.RecordView_slide_to_cancel_arrow_color, -1);


            typedArray.recycle();
        }

        initiateAudio();
//        recordFile = new File("/storage/emulated/0/audioos/abe1f816-ef67-4685-9f87-bfdb63f1aced.3gp");
        recordFile = new File("/storage/emulated/0/audioos/3e7818b3-ef5d-4542-ba37-072dcd118537.3gp");
//        setRecordFile(recordFile);


    }

    private void initiateAudio() {
        if (backgroundId != -1)
            bgContainer.setBackground(getResources().getDrawable(backgroundId));
        audioCursor.post(() -> {
            audioCursorNormalSize = audioCursor.getWidth();
            audioCursorMaxSize = (int) (audioCursorNormalSize * 2.0f);
            progress.post(() -> {
                audioCursorMinX = (int) (progress.getX() - audioCursorNormalSize / 2.0f);
                audioCursorMaxX = (int) (progress.getX() + progress.getWidth() - audioCursorNormalSize / 2.0f);
                audioCursor.setX(audioCursorMinX);
            });
        });
        audioCursor.setOnTouchListener(onCursorTouchListener);
        progress.setOnTouchListener(onProgressTouchListener);
//        progress.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return true;
//            }
//        });
        progress.setProgress(0);

        play_pause.setSpeed(2);
        play_pause.pauseAnimation();
        play_pause.setMinFrame(2);
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play_pause.loop(true);
                if (isPlaying) {
                    play(recordFile.getPath(), false, null);
                    if (audioValueAnimator != null)
                        audioValueAnimator.pause();
                    setPlayButton(false);
                } else /*if not playing*/ {
                    if (recordFile == null) return;
                    play(recordFile.getPath(), true, new OnPlayListener() {
                        @Override
                        public void onPlay(int duration) {
                            audioDuration = duration;
                            if (audioValueAnimator != null && audioValueAnimator.isPaused()) {
                                audioValueAnimator.resume();
                                Log.d(TAG, "onPlay: audioValueAnimator resumed 351");
                            } else /* == if (audioValueAnimator == null || !audioValueAnimator.isPaused())*/ {
                                audioValueAnimator = UtilsAudioPlayer.getValueAnimator(true, audioDuration, null, progress -> {
                                    AudioPlayerView.this.progress.setProgress(progress * 100);
                                    Log.d(TAG, "onPlay: progress: 3511: " + progress);
                                    audioCursor.setX((audioCursorMaxX - audioCursorMinX) * progress + audioCursorMinX);
//                                    if (progress == 1) {
//                                        AudioPlayerView.this.progress.setProgress(0);
//                                        audioCursor.setX(audioCursorMinX);
//                                        audioValueAnimator.setCurrentFraction(0);
//                                        audioValueAnimator.pause();
//                                        fraction = -1;
//                                        Log.d(TAG, "onPlay: progress == 1, 987654");
//                                    }
                                    Log.d(TAG, "onPlay: audioValueAnimator.getValues();:  " + Arrays.toString(audioValueAnimator.getValues()));

                                    if (!audioValueAnimator.isPaused()) {
                                        Log.d(TAG, "onPlay: not paused");
                                        if (fraction != -1) {
                                            Log.d(TAG, "onPlay: fraction!=-1");
                                            float tempFraction = fraction;
                                            fraction = -1;
                                            cdt.setFraction(tempFraction);
                                            audioValueAnimator.setCurrentFraction(tempFraction);

                                        } else {
                                            Log.d(TAG, "onPlay: fraction==-1");
                                        }
                                    } else {
                                        Log.d(TAG, "onPlay: paused");
                                    }
                                });
//                                audioValueAnimator.addListener(animatorListenerAdapter);
                                audioValueAnimator.start();
                            }
                        }

                        @Override
                        public void onComplete() {
                            setPlayButton(false);
                            progress.setProgress(0);
                            audioCursor.setX(audioCursorMinX);

                        }
                    });
                    if (isPrepared)
                        setPlayButton(true);
                }
            }
        });
    }

    private void setPlayButton(boolean play) {
        if (!play) {
            play_pause.setMinFrame(35);
            play_pause.setMaxFrame(66);
            play_pause.removeAllAnimatorListeners();
            play_pause.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if (valueAnimator.getAnimatedFraction() == 1) {
                        play_pause.pauseAnimation();
                    }
                }
            });
            play_pause.resumeAnimation();
            isPlaying = false;
        } else {
            play_pause.setMinFrame(1);
            play_pause.setMaxFrame(35);
            play_pause.removeAllUpdateListeners();
            play_pause.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if (valueAnimator.getAnimatedFraction() == 1) {
                        play_pause.pauseAnimation();
                    }
                }
            });
            play_pause.playAnimation();
            isPlaying = true;
        }
    }

    public void play(String path, boolean play, OnPlayListener onPlayListener) {
        //set up MediaPlayer
        if (mp == null) {
//            throw new IllegalArgumentException("MadiaPlayer should not be null");
            Toast.makeText(getContext(), "No audio to load", Toast.LENGTH_SHORT).show();
        }
        try {
            if (play) {

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mp.seekTo(0);
                        if (onPlayListener != null) {
                            onPlayListener.onComplete();
                        }
                    }
                });
                if (isPrepared) {
                    mp.start();
                    if (cdt.isPaused())
                        cdt.resume();
                    else
                        cdt.start();
                    if (onPlayListener != null) {
                        onPlayListener.onPlay(mp.getDuration());
                    }
                } else {
                    Toast.makeText(this.getContext(), "Still loading audio", Toast.LENGTH_SHORT).show();
                }

            } else {
                if (mp.isPlaying()) {
                    mp.pause();
                    cdt.pause();
                } else {
                    Log.d(TAG, "play: not playing");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnAudioActionListener {
        void onActionDown();

        void onActionUp();
    }

    interface OnPlayListener {
        void onPlay(int duration);

        void onComplete();
    }


}


