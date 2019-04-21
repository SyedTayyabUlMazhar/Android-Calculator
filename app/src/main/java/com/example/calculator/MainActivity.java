package com.example.calculator;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
{
    Stack<Character> parentheses = new Stack<>();

    long onTouchDelActionDownTime;
    long onTouchDelActionUpTime;

    int val = 0;

    int screenWidthInPixel;
    int set1Width;
    int set2Width;
    int orangeSlideWindowWidth;

    View[] set1; // includes all the black buttons in acvtivity_main
    View[] set2; // includes all the grey buttons in acvtivity_main
    View orangeSlideWindow; // includes the orange sliding window in acvtivity_main

    boolean orangeSlideWindowOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.screenWidthInPixel = getScreenWidth();
        this.set1Width = (int) Math.round(screenWidthInPixel * 0.25);
        this.set2Width = (int) Math.round(screenWidthInPixel * 0.20);
        this.orangeSlideWindowWidth = (int) Math.round(screenWidthInPixel * 0.05);

        set1 = new Button[]{findViewById(R.id.buttonPoint), findViewById(R.id.buttonZero), findViewById(R.id.buttonEqual),
                findViewById(R.id.buttonOne), findViewById(R.id.buttonTwo), findViewById(R.id.buttonThree),
                findViewById(R.id.buttonFour), findViewById(R.id.buttonFive), findViewById(R.id.buttonSix),
                findViewById(R.id.buttonSeven), findViewById(R.id.buttonEight), findViewById(R.id.buttonNine)};

        set2 = new Button[]{findViewById(R.id.buttonBracket), findViewById(R.id.buttonSubtract), findViewById(R.id.buttonAdd),
                findViewById(R.id.buttonMultiply), findViewById(R.id.buttonDivide), findViewById(R.id.buttonDel)};


        orangeSlideWindow = findViewById(R.id.activity_main_orangeSlidingWindow);


        setWidth(set1Width, set1);
        setWidth(set2Width, set2);
        setWidth(orangeSlideWindowWidth, orangeSlideWindow, findViewById(R.id.extra_arithmetic_button_openClose));


        findViewById(R.id.buttonBracket).setOnTouchListener(OnTouchBracket);
        findViewById(R.id.buttonDel).setOnTouchListener(OnTouchDel);
        findViewById(R.id.extra_arithmetic_button_openClose).setOnTouchListener(onTouchOpenCloseButton);
        findViewById(R.id.activity_main_orangeSlidingWindow).setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
    }

    public void ButtonClick(View v)
    {
        Button btn = (Button) v;
        String btnText = btn.getText().toString();

        TextView textView = findViewById(R.id.textView);
        String textViewText = textView.getText().toString();

        if (btn.getId() == R.id.buttonEqual)
        {
            textView.setText(Arithmetic.Evaluate(textViewText));
        }
        else
        {
            textView.setText(textViewText + btnText);
        }
    }

    public View.OnTouchListener OnTouchBracket = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                TextView textView = findViewById(R.id.textView);
                String text = textView.getText().toString();
                int textLength = text.length();
                Character lastChar = (textLength != 0) ? text.charAt(textLength - 1) : null;
                Character secondLastChar = (textLength > 1) ? text.charAt(textLength - 2) : null;

                if (textLength == 0 || lastChar == '(' || ((IsDigit(secondLastChar) || secondLastChar == ')') && IsArithmeticOperator(lastChar)))
                {
                    parentheses.push('(');
                    textView.setText(text + "(");
                }
                else if (!parentheses.empty() && (IsDigit(lastChar) || lastChar == ')'))
                {
                    parentheses.pop();
                    textView.setText(text + ")");
                }
            }
            return false;
        }
    };

    public boolean IsDigit(Character character)
    {
        return (character != null && Character.isDigit(character));
    }

    public boolean IsArithmeticOperator(Character character)
    {
        if (character == null) return false;
        else
        {
            return character == Arithmetic.divideSymbol || character == Arithmetic.multiplySymbol || character == Arithmetic.additionSymbol || character == Arithmetic.subtractSymbol;
        }
    }

    public int getScreenWidth()
    {
        int width = 0;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        width = displayMetrics.widthPixels;

        return width;
    }

    public void setWidth(int width, View... views)
    {
        for (View view : views)
        {
            view.getLayoutParams().width = width;
            view.requestLayout();
        }
    }

    public View.OnTouchListener OnTouchDel = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {

            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                onTouchDelActionDownTime = Calendar.getInstance().getTimeInMillis();
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                onTouchDelActionUpTime = Calendar.getInstance().getTimeInMillis();

                TextView textView = findViewById(R.id.textView);
                String text = textView.getText().toString();
                int textLength = text.length();

                if (onTouchDelActionUpTime - onTouchDelActionDownTime >= 600)
                {
                    textView.setText("");
                    parentheses.clear();
                }
                else if (textLength != 0)
                {
                    textView.setText(text.substring(0, textLength - 1));
                    if (text.charAt(textLength - 1) == '(') parentheses.pop();
                    else if (text.charAt(textLength - 1) == ')') parentheses.push('(');
                }
            }

            return false;
        }};

    public View.OnTouchListener onTouchOpenCloseButton = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(final View v, MotionEvent event)
        {
            int closedWidthInPixels = orangeSlideWindowWidth;
            int openedWidthInPixels = screenWidthInPixel;

            final View orangeSlidingWindow = findViewById(R.id.activity_main_orangeSlidingWindow);

            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                if (orangeSlideWindowOpen)
                {
                    slideWindow(orangeSlidingWindow, openedWidthInPixels, closedWidthInPixels);
                    orangeSlideWindowOpen = false;
                }
                else
                {
                    slideWindow(orangeSlidingWindow, closedWidthInPixels, openedWidthInPixels);
                    orangeSlideWindowOpen = true;
                }
            }

            return true;
        }
    };


    private void slideWindow(final View v, int slideStartWidth, int slideEndWidth)
    {
        ValueAnimator widthAnimator = ValueAnimator.ofInt(slideStartWidth, slideEndWidth);
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int animatedValue = (int) animation.getAnimatedValue();
                v.getLayoutParams().width = animatedValue;
                v.requestLayout();
            }
        });
        widthAnimator.setDuration(700);
        widthAnimator.start();
    }
}
