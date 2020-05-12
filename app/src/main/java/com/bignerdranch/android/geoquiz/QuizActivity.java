package com.bignerdranch.android.geoquiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_INDEX_CHEAT = "indexCheat";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String CHECK_COUNT_ANSWER = "countAnswer";

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    private Button mCheatButton;
    private TextView mCheckCountAnswer;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),};

    private boolean[] mQuestionAnswered = {false, false, false, false, false ,false};

    private int countPlus = 0;
    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    private int countShowAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate(Bundle) called");


        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(KEY_INDEX_CHEAT, false);
            mQuestionAnswered = savedInstanceState.getBooleanArray(KEY_INDEX_CHEAT);
            countShowAnswer = savedInstanceState.getInt(CHECK_COUNT_ANSWER);
        }

        mQuestionTextView = findViewById(R.id.question_text_view);
        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mNextButton = findViewById(R.id.next_button);
        mPrevButton = findViewById(R.id.prev_button);
        mCheatButton = findViewById(R.id.cheat_button);
        mCheckCountAnswer = findViewById(R.id.checkCountAnswer);

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
            }
        });


        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
                mIsCheater = false;
                mTrueButton.setEnabled(true);
                mFalseButton.setEnabled(true);
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((mCurrentIndex - 1) < 0) {
                    mCurrentIndex = mQuestionBank.length - 1;
                } else mCurrentIndex = mCurrentIndex - 1;
                updateQuestion();
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);
            }
        });

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
                mTrueButton.setEnabled(true);
                mFalseButton.setEnabled(true);
            }
        });

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start CheatActivity
                if (countShowAnswer < 3) {
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                    Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue, countShowAnswer);
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                }else
                    mCheatButton.setEnabled(false);
            }
        });

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            mQuestionAnswered[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
            countShowAnswer = CheatActivity.wasCountShown(data);
            mCheckCountAnswer.setText(countShowAnswer + "/3");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(KEY_INDEX_CHEAT, mIsCheater);
        savedInstanceState.putBooleanArray(KEY_INDEX_CHEAT, mQuestionAnswered);
        savedInstanceState.putInt(CHECK_COUNT_ANSWER, countShowAnswer);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        // Log.d(TAG, "Updating question text", new Exception());
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        boolean userCheated = mQuestionAnswered[mCurrentIndex];

        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
        int messageResId = 0;
        if (mIsCheater || userCheated) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                countPlus += 1;
                if (mCurrentIndex == mQuestionBank.length - 1) {
                    double result = ((double) countPlus / (double) (mQuestionBank.length)) * 100;
                    String rez = result + "";
                    mQuestionTextView.setText(rez);
                }
            } else {
                messageResId = R.string.incorrect_toast;
                if (mCurrentIndex == mQuestionBank.length - 1) {
                    double result = ((double) countPlus / (double) (mQuestionBank.length)) * 100;
                    String rez = result + "";
                    mQuestionTextView.setText(rez);
                }
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
}

