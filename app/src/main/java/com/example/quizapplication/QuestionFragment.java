package com.example.quizapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class QuestionFragment extends Fragment {

    private ViewGroup fragmentContainer;
    private TextView questionText;
    private Button trueButton;
    private Button falseButton;

    private Question question;
    private OnAnswerSelectedListener answerSelectedListener;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(Question question) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable("question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (Question) getArguments().getSerializable("question");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        fragmentContainer = view.findViewById(R.id.fragment_container);
        questionText = view.findViewById(R.id.question_text);
        trueButton = view.findViewById(R.id.trueButton);
        falseButton = view.findViewById(R.id.falseButton);

        if (question != null) {
            questionText.setText(question.getText());

            int backgroundColor = question.getColor();
            if (backgroundColor != 0) {
                fragmentContainer.setBackgroundColor(backgroundColor);
            }

            trueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerSelectedListener.onAnswerSelected(true);
                }
            });

            falseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerSelectedListener.onAnswerSelected(false);
                }
            });
        }

        return view;
    }

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(boolean userAnswer);
    }

    public void setOnAnswerSelectedListener(OnAnswerSelectedListener listener) {
        this.answerSelectedListener = listener;
    }
}