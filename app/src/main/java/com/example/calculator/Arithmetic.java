package com.example.calculator;

import java.util.Calendar;
import java.util.Stack;
import java.util.regex.Pattern;

public  class Arithmetic
{
    public static final char divideSymbol = '÷';
    public  static final char multiplySymbol = '×';
    public  static final char additionSymbol = '+';
    public  static final char subtractSymbol = '-';
    private static class Parentheses
    {
        public int StartIndex;
        public int EndIndex;
        public int Length;

        //finds the start and ending index of the outermost parentheses in 'expression'
        public boolean Find(String expression)
        {
            Stack<Character> parentheses = new Stack<Character>();
            boolean parenthesesExists = false;

            for (int i = 0; i < expression.length(); i++)
            {
                char character = expression.charAt(i);

                if (character == '(')
                {
                    parentheses.push(character);

                    if (!parenthesesExists)
                    {
                        parenthesesExists = true;
                        StartIndex = i;
                    }
                }

                else if (character == ')')
                {
                    parentheses.pop();
                    if (parentheses.size() == 0)
                    {
                        EndIndex = i;
                        break;
                    }
                }
            }
            return parenthesesExists;
        }


        //finds the start and ending index of the outermost parentheses in 'expression' from 'startIndex' to 'endIndex'
        public boolean Find(StringBuilder expression, int startIndex, int endIndex)
        {
            Stack<Character> parentheses = new Stack<Character>();
            boolean parenthesesExists = false;

            for (int i = startIndex; i <= endIndex; i++)
            {
                char character = expression.charAt(i);

                if (character == '(')
                {
                    parentheses.push(character);

                    if (!parenthesesExists)
                    {
                        parenthesesExists = true;
                        StartIndex = i;
                    }
                }

                else if (character == ')')
                {
                    parentheses.pop();
                    if (parentheses.size() == 0)
                    {
                        EndIndex = i;
                        break;
                    }
                }
            }

            CalculateLength();

            return parenthesesExists;
        }

        private void CalculateLength()
        {
            Length = EndIndex - StartIndex + 1;
        }

    }

    public static String Evaluate(String expression)
    {
        String result = BODMAS(new StringBuilder(expression), 0, expression.length() - 1);

        return result;
    }

    private static String BODMAS(StringBuilder expression, int startIndex, int endIndex)
    {
//        String subExpr = expression.toString(startIndex, GetCharsCount(startIndex, endIndex));
        String subExpr = expression.substring(startIndex, endIndex+1);

        Parentheses parentheses = new Parentheses();

        while (parentheses.Find(expression, startIndex, endIndex))
        {
            String result = BODMAS(expression, parentheses.StartIndex + 1, parentheses.EndIndex - 1);
            endIndex -= parentheses.Length - result.length();
            subExpr = expression.substring(startIndex, endIndex+1);
        }

        {
            String result = DMAS(subExpr);
            if (startIndex != 0)
                expression.replace(startIndex-1,endIndex+2, result );
                //expression.Replace("(" + subExpr + ")", result, startIndex - 1, endIndex + 1 - (startIndex - 1) + 1);
            else
                expression.replace(startIndex, endIndex+1, result);
                //expression.Replace(subExpr, result, startIndex, endIndex - startIndex + 1);

            return result;
        }
    }

    private static String DMAS(String expression){
        return EvaluateAdditionAndSubtraction(EvaluateDivisionAndMultiplication(expression));
    }


    private static String GetOperand1(int indexOfOperator, String expression)
    {
        String operand1 = "";
        int i;
        for (i = indexOfOperator - 1; expression.charAt(i) != additionSymbol && expression.charAt(i) != subtractSymbol; i--)
        {
            operand1 = String.valueOf(expression.charAt(i)) + operand1;
            //operand1 = operand1.Insert(0, expression.charAt(i).toString());
        }
        operand1 = String.valueOf(expression.charAt(i)) + operand1;
        //operand1 = operand1.Insert(0, expression.charAt(i).toString());

        return operand1;
    }

    private static String GetOperand2(int indexOfOperator, String expression)
    {
        String operand2 = "";

        int i = indexOfOperator + 1;
        if (expression.charAt(i) == additionSymbol || expression.charAt(i) == subtractSymbol)
        {
            operand2 += expression.charAt(i);
            i++;
        }
        while (i != expression.length() &&  expression.charAt(i)!=additionSymbol && expression.charAt(i)!=subtractSymbol && expression.charAt(i)!=divideSymbol && expression.charAt(i)!=multiplySymbol /*!new[] { additionSymbol, subtractSymbol, multiplySymbol, divideSymbol }.Contains(expression.charAt(i))*/ )
        {
            operand2 += expression.charAt(i);
            //if (i == expression.length() - 1) break;

            i++;
        }

        return operand2;
    }

    private static String GetResultOfOperation(String operand1, char Operator, String operand2)
    {
        double operand1AsDouble = Double.parseDouble(operand1);
        double operand2AsDouble = Double.parseDouble(operand2);
        String result = "";
        switch (Operator)
        {
            case divideSymbol: result = String.valueOf(operand1AsDouble / operand2AsDouble); break;
            case multiplySymbol: result = String.valueOf(operand1AsDouble * operand2AsDouble); break;
            case additionSymbol: result = String.valueOf(operand1AsDouble + operand2AsDouble); break;
            case subtractSymbol: result = String.valueOf(operand1AsDouble - operand2AsDouble); break;
            default:  break;
        }
        return (result.charAt(0) != subtractSymbol) ? ("+"+result) : result;
    }


    private static String EvaluateDivisionAndMultiplication(String expression)
    {
        if (expression.charAt(0) != additionSymbol && expression.charAt(0) != subtractSymbol) expression = "+" + expression;

        String result = "";
        boolean hasDivide = (expression.indexOf(divideSymbol, 1) != -1);
        boolean hasMultiply = (expression.indexOf(multiplySymbol, 1) != -1);

        int indexOfDivide = expression.indexOf(divideSymbol, 1);
        int indexOfMultiply = expression.indexOf(multiplySymbol, 1);
        while (hasDivide || hasMultiply)
        {
            char operatorToEvaluate;
            int indexOfOperatorToEvaluate;

            if (hasDivide && hasMultiply) indexOfOperatorToEvaluate = (indexOfDivide < indexOfMultiply) ? indexOfDivide : indexOfMultiply;
            else indexOfOperatorToEvaluate = hasDivide ? indexOfDivide : indexOfMultiply;

            operatorToEvaluate = expression.charAt(indexOfOperatorToEvaluate);

            String operand1 = GetOperand1(indexOfOperatorToEvaluate, expression);
            String operand2 = GetOperand2(indexOfOperatorToEvaluate, expression);
            String resultOfOperation = GetResultOfOperation(operand1, operatorToEvaluate, operand2);


            int operand1Start = indexOfOperatorToEvaluate - operand1.length();
            int operand2End = indexOfOperatorToEvaluate + operand2.length();

            // Remove the expression we just calculated, then insert in its' place the result
            expression = expression.replaceFirst(Pattern.quote(expression.substring(operand1Start, operand2End+1)), resultOfOperation);
            //expression = expression.Remove(operand1Start, operand2End - operand1Start + 1).Insert(operand1Start, resultOfOperation);


            hasDivide = (expression.indexOf(divideSymbol, 1) != -1);
            hasMultiply = (expression.indexOf(multiplySymbol, 1) != -1);

            indexOfDivide = expression.indexOf(divideSymbol, 1);
            indexOfMultiply = expression.indexOf(multiplySymbol, 1);
        }
        result = expression;

        return result;
    }

    private static String EvaluateAdditionAndSubtraction(String expression)
    {
        if (expression.charAt(0) != additionSymbol && expression.charAt(0) != subtractSymbol) expression = "+" + expression;

        String result = "";
        boolean hasPlus = (expression.indexOf(additionSymbol, 1) != -1);
        boolean hasMinus = (expression.indexOf(subtractSymbol, 1) != -1);

        int indexOfPlus = expression.indexOf(additionSymbol, 1);
        int indexOfMinus = expression.indexOf(subtractSymbol, 1);
        while (hasPlus || hasMinus)
        {
            char operatorToEvaluate;
            int indexOfOperatorToEvaluate;

            if (hasPlus && hasMinus) indexOfOperatorToEvaluate = (indexOfPlus < indexOfMinus) ? indexOfPlus : indexOfMinus;
            else indexOfOperatorToEvaluate = hasPlus ? indexOfPlus : indexOfMinus;

            operatorToEvaluate = expression.charAt(indexOfOperatorToEvaluate);

            String operand1 = GetOperand1(indexOfOperatorToEvaluate, expression);
            String operand2 = GetOperand2(indexOfOperatorToEvaluate, expression);
            String resultOfOperation = GetResultOfOperation(operand1, operatorToEvaluate, operand2);


            int operand1Start = indexOfOperatorToEvaluate - operand1.length();
            int operand2End = indexOfOperatorToEvaluate + operand2.length();

            // Remove the expression we just calculated, then insert in its' place the result

            expression = expression.replaceFirst(Pattern.quote(expression.substring(operand1Start, operand2End+1)), resultOfOperation);
            //expression = expression.Remove(operand1Start, operand2End - operand1Start + 1).Insert(operand1Start, resultOfOperation);


            hasPlus = (expression.indexOf(additionSymbol, 1) != -1);
            hasMinus = (expression.indexOf(subtractSymbol, 1) != -1);

            indexOfPlus = expression.indexOf(additionSymbol, 1);
            indexOfMinus = expression.indexOf(subtractSymbol, 1);
        }
        result = expression;

        return result;
    }



}