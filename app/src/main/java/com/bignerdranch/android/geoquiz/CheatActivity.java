package com.bignerdranch.android.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN =
            "com.bignerdranch.android.geoquiz.answer_shown";
    private static final String KEY_CHEATER = "cheater";
    private static final String CHECK_COUNT_ANSWER = "countAnswer";


    private boolean mAnswerIsTrue;
    private boolean mIsCheater;
    private TextView mAnswerTextView;
    private TextView mNameApi;
    private Button mShowAnswerButton;
    private int buildNumber;
    private int countAnswer;


    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int countAnswer) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(CHECK_COUNT_ANSWER, countAnswer);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    public static int wasCountShown(Intent result) {
        return result.getIntExtra(CHECK_COUNT_ANSWER, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        countAnswer = getIntent().getIntExtra(CHECK_COUNT_ANSWER, 0);
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mNameApi = findViewById(R.id.nameAPI);
        buildNumber = Build.VERSION.SDK_INT;
        mNameApi.setText("API level: " + buildNumber);

        if (savedInstanceState != null) {
            mIsCheater = savedInstanceState.getBoolean(KEY_CHEATER, mIsCheater);
            countAnswer = savedInstanceState.getInt(CHECK_COUNT_ANSWER, countAnswer);
            mAnswerTextView.setText("" + mIsCheater);
        }

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countAnswer+=1;
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                mIsCheater = true;
                setAnswerShownResult(mIsCheater, countAnswer);
            }
        });
    }

    private void setAnswerShownResult(boolean isAnswerShown, int checkCountAnswer) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        data.putExtra(CHECK_COUNT_ANSWER, checkCountAnswer);
        setResult(RESULT_OK, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_CHEATER, mIsCheater);
        savedInstanceState.putInt(CHECK_COUNT_ANSWER, countAnswer);
    }
}
