package com.problem.solution.data.matrixthreading;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

  private ProgressBar progressBar;
  private Button startStopProgressBar;
  private TextView currentTextView;

  private static Handler handler;
  private Thread thread;

  enum PROGRESS_STATE{
    START,
    STOP
  }

  private final static int PROGRESS_BAR_MAX = 100;
  private final static int SLEEP_TIME_IN_MILLIS = 1000;
  private static final int COUNT_UPDATE = 101;
  private static final int START_PROGRESS = 100;

  private boolean isThreadInterrupted = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    instantiateViews();
    setThread();
    setStartProgressBarListener();
  }

  @Override
  protected void onResume() {
    super.onResume();

    setHandler();
  }

  private void instantiateViews() {
    progressBar = findViewById(R.id.progress_bar);
    startStopProgressBar = findViewById(R.id.start_progress_bar);
    startStopProgressBar.setTag(PROGRESS_STATE.START);
    currentTextView = findViewById(R.id.current_text_view);
    progressBar.setMax(PROGRESS_BAR_MAX);
  }

  private void setThread() {
    thread = new Thread(new Runnable() {
      @Override
      public void run() {
        for (int i=0;i<100;i++) {
          if (isThreadInterrupted) {
            return;
          }

          progressBar.setProgress(i);
          try {
            Thread.sleep(SLEEP_TIME_IN_MILLIS);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          Message msg = new Message();
          msg.what = COUNT_UPDATE;
          msg.arg1 = i;
          handler.sendMessage(msg);
        }
      }
    });
  }

  private void setStartProgressBarListener() {
    startStopProgressBar.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          if (startStopProgressBar.getTag() == PROGRESS_STATE.START) {
            isThreadInterrupted = false;
            // Set stop text
            startStopProgressBar.setText("STOP");
            startStopProgressBar.setTag(PROGRESS_STATE.STOP);
            handler.sendEmptyMessage(START_PROGRESS);
          } else if (startStopProgressBar.getTag() == PROGRESS_STATE.STOP) {
            // Set start text
            startStopProgressBar.setText("START");
            startStopProgressBar.setTag(PROGRESS_STATE.START);
            isThreadInterrupted = true;
          }
        }
      });
  }

  @SuppressLint("HandlerLeak")
  private void setHandler() {
    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == START_PROGRESS) {
          thread.start();
        } else if (msg.what == COUNT_UPDATE) {
          currentTextView.setText("Count: " + msg.arg1);
        }
      }
    };
  }
}
