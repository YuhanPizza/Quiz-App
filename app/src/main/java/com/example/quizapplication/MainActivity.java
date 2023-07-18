package com.example.quizapplication;

import java.io.IOException;
import java.io.Serializable;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.ProgressBar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements QuestionFragment.OnAnswerSelectedListener {
    private static final String RESULT_FILE_CORRECT_ANSWERS = "correct_answers.txt";
    private static final String RESULT_FILE_TOTAL_QUESTIONS = "total_questions.txt";
    private List<Question> questionBank;
    private int currentQuestionIndex;
    private int numCorrectAnswers;
    private int numTotalQuestions;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;

    private static final String SHARED_PREFS_KEY = "QuizAppPrefs";
    private static final String CORRECT_ANSWERS_KEY = "CorrectAnswers";
    private static final String TOTAL_QUESTIONS_KEY = "TotalQuestions";
    private static final String APP_LANGUAGE_KEY = "AppLanguage";


    private static final String CURRENT_QUESTION_INDEX_KEY = "CurrentQuestionIndex";
    private static final String NUM_CORRECT_ANSWERS_KEY = "NumCorrectAnswers";
    private static final String NUM_TOTAL_QUESTIONS_KEY = "NumTotalQuestions";
    private static final String QUESTION_BANK_KEY = "QuestionBank";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);

        if (savedInstanceState != null) {
            currentQuestionIndex = savedInstanceState.getInt(CURRENT_QUESTION_INDEX_KEY);
            numCorrectAnswers = savedInstanceState.getInt(NUM_CORRECT_ANSWERS_KEY);
            numTotalQuestions = savedInstanceState.getInt(NUM_TOTAL_QUESTIONS_KEY);
            questionBank = (List<Question>) savedInstanceState.getSerializable(QUESTION_BANK_KEY);
        } else {
            questionBank = new ArrayList<>();
            loadQuestionBank();
            numCorrectAnswers = 0;
            numTotalQuestions = questionBank.size();
        }

        progressBar = findViewById(R.id.progress_bar); //finds the progressbar in the layout.
        progressBar.setMax(numTotalQuestions + 1); //controls the size of the progress bar
        setSupportActionBar(findViewById(R.id.toolbar)); //loads the main menu toolbar

        updateQuestion();
    }
    //controls the configuration of the layout when changing orientation
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Update the UI or handle other updates here
        updateQuestion();
    }

    //loads the questionBank
    private void loadQuestionBank(){
        // true or false questions
        questionBank.add(new Question("Question 1: Is the sky blue?", true, getRandomColor()));
        questionBank.add(new Question("Question 2: Are dogs mammals?", true, getRandomColor()));
        questionBank.add(new Question("Question 3: Is the Earth flat?", false , getRandomColor()));
        questionBank.add(new Question("Question 4: Is the sun made of cheese?", false, getRandomColor()));
        questionBank.add(new Question("Question 5: Do birds have feathers?", true, getRandomColor()));
        questionBank.add(new Question("Question 6: Water boils at 100 degrees Celsius.", true, getRandomColor()));
        questionBank.add(new Question("Question 7: The human body has 206 bones.", true, getRandomColor()));
        questionBank.add(new Question("Question 8: Dolphins are mammals.", true, getRandomColor()));
        questionBank.add(new Question("Question 9: The currency of Japan is the Yuan.", false, getRandomColor()));
        questionBank.add(new Question("Question 10: Albert Einstein discovered the theory of relativity.", true, getRandomColor()));
        questionBank.add(new Question("Question 11: The Sahara Desert is the largest desert in the world.", true, getRandomColor()));
        questionBank.add(new Question("Question 12: Bees communicate by dancing.", true, getRandomColor()));
        questionBank.add(new Question("Question 13: The Statue of Liberty was a gift from France to the United States.", true, getRandomColor()));
        questionBank.add(new Question("Question 14: The planet Mars is larger than the Earth.", false, getRandomColor()));
        questionBank.add(new Question("Question 15: The Nile River is the longest river in the world.", true, getRandomColor()));
        questionBank.add(new Question("Question 16: Birds are the only animals that can fly.", false, getRandomColor()));
        questionBank.add(new Question("Question 17: The Mona Lisa was painted by Vincent van Gogh.", false, getRandomColor()));
        questionBank.add(new Question("Question 18: Diamonds are the hardest known substance.", true, getRandomColor()));
        questionBank.add(new Question("Question 19: Brazil is the largest country in South America.", true, getRandomColor()));
        questionBank.add(new Question("Question 20: The Eiffel Tower is located in London.", false, getRandomColor()));


        // Shuffle the question bank
        Collections.shuffle(questionBank);
    }
    //inflates the main menu toolbar
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //supposedly sets the app language
    private void setAppLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Save the selected language code to preferences for persistence
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APP_LANGUAGE_KEY, languageCode);
        editor.apply();
    }

    //updates the question if there are still more questions in the questionbank
    private void updateQuestion() {
        if (currentQuestionIndex < questionBank.size()) {
            Question currentQuestion = questionBank.get(currentQuestionIndex);
            loadQuestionFragment(currentQuestion);
        } else {
            showQuizFinishedDialog();
        }
    }

    //loads the fragment that the questions are placed on.
    private void loadQuestionFragment(Question question) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        QuestionFragment questionFragment = QuestionFragment.newInstance(question);
        questionFragment.setOnAnswerSelectedListener(this);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, questionFragment);
        fragmentTransaction.commit();
    }
    //saves the instance generally used so that the progress is not reset when changing orientation
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_QUESTION_INDEX_KEY, currentQuestionIndex);
        outState.putInt(NUM_CORRECT_ANSWERS_KEY, numCorrectAnswers);
        outState.putInt(NUM_TOTAL_QUESTIONS_KEY, numTotalQuestions);
        outState.putSerializable(QUESTION_BANK_KEY, (Serializable) questionBank);
    }
    //function that i used to randomize color of question backgrounds
    private int getRandomColor() {
        int[] colorArray = {
                ContextCompat.getColor(this, R.color.color1),
                ContextCompat.getColor(this, R.color.color2),
                ContextCompat.getColor(this, R.color.color3),
                ContextCompat.getColor(this,R.color.color4),
                ContextCompat.getColor(this,R.color.color5),
        };

        // Generate a random index within the range of the color array
        int randomIndex = new Random().nextInt(colorArray.length);

        // Retrieve the color at the random index
        return colorArray[randomIndex];
    }

    //checks the answer of that question if it is correct or not controls incrementation of score.
    public void checkAnswer(boolean userAnswer) {
        Question currentQuestion = questionBank.get(currentQuestionIndex);
        boolean isCorrect = currentQuestion.isAnswer() == userAnswer;
        showAnswerToast(isCorrect);
        if (isCorrect) {
            numCorrectAnswers++;
        }
        currentQuestionIndex++;
        progressBar.setProgress(currentQuestionIndex + 1);
        updateQuestion();
    }

    //display correct or incorrect answer
    private void showAnswerToast(boolean isCorrect) {
        int messageResId = isCorrect ? R.string.toast_correct : R.string.toast_incorrect;
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    //after quiz is finished controls the dialog box and its options.
    private void showQuizFinishedDialog() {
        int score = numCorrectAnswers;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_quiz_finished_title);
        builder.setMessage(getString(R.string.dialog_quiz_finished_message, score, questionBank.size()));
        builder.setPositiveButton(R.string.dialog_save_results, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveQuizResults();
                resetQuiz();
            }
        });
        builder.setNegativeButton(R.string.dialog_ignore_results, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetQuiz();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                resetQuiz();
            }
        });
        builder.show();
    }

    //stores the quiz results in a txt file.
    private void saveQuizResults() {
        // Get the previous saved results from the text file
        int previousCorrectAnswers = readSavedResultFromFile(RESULT_FILE_CORRECT_ANSWERS);
        int previousTotalQuestions = readSavedResultFromFile(RESULT_FILE_TOTAL_QUESTIONS);

        // Increment the current results with the previous results
        int totalCorrectAnswers = previousCorrectAnswers + numCorrectAnswers;
        int totalQuestions = previousTotalQuestions + numTotalQuestions;

        // Save the updated results to the text file
        saveResultToFile(RESULT_FILE_CORRECT_ANSWERS, totalCorrectAnswers);
        saveResultToFile(RESULT_FILE_TOTAL_QUESTIONS, totalQuestions);
    }

    //function to save results to file
    private void saveResultToFile(String fileName, int value) {
        try {
            // Open the text file in private mode to overwrite the previous value
            FileOutputStream fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            // Write the new value as a string to the file
            fileOutputStream.write(String.valueOf(value).getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //function used to read the txt files. from previous quiz results.
    private int readSavedResultFromFile(String fileName) {
        int value = 0;
        try {
            // Open the text file for reading
            FileInputStream fileInputStream = openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // Read the value from the file
            String line = bufferedReader.readLine();
            if (line != null && !line.isEmpty()) {
                value = Integer.parseInt(line);
            }

            // Close the readers
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    //resets quiz.
    private void resetQuiz() {
        currentQuestionIndex = 0;
        numCorrectAnswers = 0;
        Collections.shuffle(questionBank);
        progressBar.setProgress(0);
        updateQuestion();
    }

    private void loadQuizResults() {
        numCorrectAnswers = sharedPreferences.getInt(CORRECT_ANSWERS_KEY, 0);
        numTotalQuestions = sharedPreferences.getInt(TOTAL_QUESTIONS_KEY, 0);

        // If the total questions is 0, set it to the initial value
        if (numTotalQuestions == 0) {
            numTotalQuestions = questionBank.size();
            saveResultToFile(RESULT_FILE_TOTAL_QUESTIONS, numTotalQuestions);
        }

        resetQuiz();
    }

    private void clearQuizResults() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(CORRECT_ANSWERS_KEY);
        editor.remove(TOTAL_QUESTIONS_KEY);
        editor.apply();
    }
    private void showQuizResults() {
        int savedCorrectAnswers = readSavedResultFromFile(RESULT_FILE_CORRECT_ANSWERS);
        int savedTotalQuestions = readSavedResultFromFile(RESULT_FILE_TOTAL_QUESTIONS);

        String message = getString(R.string.dialog_average_message_with_results, savedCorrectAnswers, savedTotalQuestions);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_average_title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.dialog_ok, null);
        builder.show();
    }
    private void changeTotalQuestions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_change_total_questions_title);
        builder.setMessage(R.string.dialog_change_total_questions_message);
        builder.setPositiveButton(R.string.dialog_change, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showTotalQuestionsDialog();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, null);
        builder.show();
    }

    private void showTotalQuestionsDialog() {
        final String[] totalQuestionsOptions = getResources().getStringArray(R.array.total_questions_options);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_select_total_questions_title);
        builder.setSingleChoiceItems(totalQuestionsOptions, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedOption = totalQuestionsOptions[which];
                int totalQuestions = Integer.parseInt(selectedOption);
                updateTotalQuestions(totalQuestions);
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void updateTotalQuestions(int totalQuestions) {
        loadQuestionBank();
        if (totalQuestions <= 20 ) {
            numTotalQuestions = totalQuestions;
            questionBank = new ArrayList<>(questionBank.subList(0, totalQuestions));
            progressBar.setMax(questionBank.size()+ 1);
            resetQuiz();
        } else {
            Toast.makeText(this, R.string.toast_invalid_total_questions, Toast.LENGTH_SHORT).show();
        }
    }
    private void resetSavedResults() {
        clearQuizResults();
        resetResultFiles();
        Toast.makeText(this, R.string.toast_results_cleared, Toast.LENGTH_SHORT).show();
    }
    private void resetResultFiles() {
        deleteFile(RESULT_FILE_CORRECT_ANSWERS);
        deleteFile(RESULT_FILE_TOTAL_QUESTIONS);
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadQuizResults();
        setAppLanguageFromPrefs();
    }

    private void setAppLanguageFromPrefs() {
        String languageCode = sharedPreferences.getString(APP_LANGUAGE_KEY, "");
        if (!languageCode.isEmpty()) {
            setAppLanguage(languageCode);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveQuizResults();
    }

    @Override
    public void onAnswerSelected(boolean userAnswer) {
        checkAnswer(userAnswer);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_get_average) {
            showQuizResults();
            return true;
        } else if (itemId == R.id.menu_reset_saved_results) {
            resetSavedResults();
            return true;
        } else if (itemId == R.id.menu_change_total_questions) {
            changeTotalQuestions();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}