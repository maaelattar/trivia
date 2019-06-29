package com.example.trivia;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionTextView;
    private TextView questionCounterTextView;
    private TextView highestScoreTextView;
    private TextView scoreTextView;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;

    private int scoreCounter;
    private Score score;

    private Prefs prefs;

    private boolean canPressButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score();
        prefs = new Prefs(MainActivity.this);

        canPressButton = true;

        nextButton = findViewById(R.id.next_btn);
        prevButton = findViewById(R.id.prev_btn);
        trueButton = findViewById(R.id.true_btn);
        falseButton = findViewById(R.id.false_btn);
        questionCounterTextView = findViewById(R.id.counter_text);
        questionTextView = findViewById(R.id.question_textview);
        highestScoreTextView = findViewById(R.id.highest_score_text);
        scoreTextView = findViewById(R.id.score_text);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);

        scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));

        currentQuestionIndex = prefs.getState();
        highestScoreTextView.setText(MessageFormat.format("Highest Score: {0}", String.valueOf(prefs.getHighScore())));
        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTextView.setText(MessageFormat.format("{0} / {1}", currentQuestionIndex, questionArrayList.size()));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (canPressButton) {
            switch (v.getId()) {
                case R.id.prev_btn:
                    if (currentQuestionIndex > 0) {
                        currentQuestionIndex = (currentQuestionIndex) % questionList.size();
                        ;
                    }
                    updateQuestion();
                    break;
                case R.id.next_btn:
                    goNext();
                    break;
                case R.id.true_btn:
                    checkAnswer(true);
                    updateQuestion();
                    break;
                case R.id.false_btn:
                    checkAnswer(false);
                    updateQuestion();
                    break;


            }
        }
    }

    private void addPoints(){
        scoreCounter += 100;
        score.setScore(scoreCounter);
        scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));

    }
    private void deductPoints(){
        scoreCounter -= 100;
        if (scoreCounter  > 0){
            score.setScore(scoreCounter);
            scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
        }
        else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
            scoreTextView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
        }

    }
    private void checkAnswer(boolean userChooseCorrect) {
        canPressButton = false;
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageId = 0;
        if (userChooseCorrect == answerIsTrue) {
            addPoints();
            fadeView();
            toastMessageId = R.string.correct_answer;
        }
        else {
            deductPoints();
            shakeAnimation();
            toastMessageId = R.string.wrong_answer;
        }
        Toast.makeText(MainActivity.this, toastMessageId, Toast.LENGTH_SHORT).show();
    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText(MessageFormat.format("{0} / {1}", currentQuestionIndex, questionList.size()));
    }

    private void fadeView(){
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                canPressButton = true;
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                canPressButton = true;
                goNext();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void goNext() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();
    }

    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }
}
