package com.example.calculator;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    Stack<Character> parentheses = new Stack<Character>();

    long onTouchDelActionDownTime;
    long onTouchDelActionUpTime;

    int val = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonBracket).setOnTouchListener(OnTouchBracket);
        findViewById(R.id.buttonDel).setOnTouchListener(OnTouchDel);
    }

    public void ButtonClick(View v) {
        Button btn = (Button) v;
        String btnText = btn.getText().toString();

        TextView textView = findViewById(R.id.textView);
        String textViewText = textView.getText().toString();

        if (btn.getId() == R.id.buttonEqual) {
            textView.setText(Arithmetic.Evaluate(textViewText));
        } else {
            textView.setText(textViewText + btnText);
        }
    }

    public View.OnTouchListener OnTouchBracket = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                TextView textView = findViewById(R.id.textView);
                String text = textView.getText().toString();
                int textLength = text.length();
                Character lastChar = (textLength != 0) ? text.charAt(textLength - 1) : null;
                Character secondLastChar = (textLength > 1) ? text.charAt(textLength - 2) : null;

                if (textLength == 0 || lastChar == '(' || ((IsDigit(secondLastChar) || secondLastChar == ')') && IsArithmeticOperator(lastChar))) {
                    parentheses.push('(');
                    textView.setText(text + "(");
                } else if (!parentheses.empty() && (IsDigit(lastChar) || lastChar == ')')) {
                    parentheses.pop();
                    textView.setText(text + ")");
                }
            }
            return false;
        }
    };

    public boolean IsDigit(Character character) {
        return (character != null && Character.isDigit(character));
    }

    public boolean IsArithmeticOperator(Character character) {
        if (character == null) return false;
        else {
            return character == Arithmetic.divideSymbol || character == Arithmetic.multiplySymbol || character == Arithmetic.additionSymbol || character == Arithmetic.subtractSymbol;
        }
    }

    public View.OnTouchListener OnTouchDel = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                onTouchDelActionDownTime = Calendar.getInstance().getTimeInMillis();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                onTouchDelActionUpTime = Calendar.getInstance().getTimeInMillis();

                TextView textView = findViewById(R.id.textView);
                String text = textView.getText().toString();
                int textLength = text.length();

                if (onTouchDelActionUpTime - onTouchDelActionDownTime >= 600) {
                    textView.setText("");
                    parentheses.clear();
                } else if (textLength != 0) {
                    textView.setText(text.substring(0, textLength - 1));
                    if (text.charAt(textLength - 1) == '(') parentheses.pop();
                    else if (text.charAt(textLength - 1) == ')') parentheses.push('(');
                }
            }

            return false;
        }
    };


}
