package com.chanx.rpn;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;


public class RPNCalculator {
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    private final Deque<BigDecimal> numbers = new ArrayDeque<>();
    private final Deque<BigDecimal> numberLogs = new ArrayDeque<>();
    private final Deque<OperationEnum> operatorLogs = new ArrayDeque<>();

    public RPNCalculator() {
    }

    public String compute(String input) {
        int position = 0;

        for (var val : input.trim().split(" ")) {
            if (isDecimal(val)) {
                numbers.push(new BigDecimal(val));
                operatorLogs.push(OperationEnum.NUMBER);
                position += val.length() + 1;
                continue;
            }

            try {
                OperationEnum opt = parseOperation(val);
                operate(opt);
                position += val.length() + 1;
            } catch (InsufficientParametersException e) {
                System.out.printf("operator %s (position: %d): insufficient parameters\n", val, position);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        }
        return getDisplayedStack();
    }

    public static boolean isDecimal(String str) {
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void undo() {
        if (numbers.isEmpty()) {
            return;
        }

        numbers.pop();
        if (!operatorLogs.isEmpty()) {
            OperationEnum opt = operatorLogs.pop();
            switch (opt) {
                case NUMBER:
                    break;
                case SQRT:
                    numbers.push(numberLogs.pop());
                    break;
                default:
                    numbers.push(numberLogs.pop());
                    numbers.push(numberLogs.pop());
            }
        }
    }

    private void sqrt() {
        BigDecimal number = numbers.pop();
        numberLogs.push(number);
        numbers.push(number.sqrt(MATH_CONTEXT));
    }

    private void clearStack() {
        numbers.clear();
        numberLogs.clear();
        operatorLogs.clear();
    }

    public String formatDecimal(BigDecimal value) {
        return value.setScale(10, RoundingMode.FLOOR).stripTrailingZeros().toPlainString();
    }

    public String getDisplayedStack() {
        StringBuilder ret = new StringBuilder("stack:");
        Iterator<BigDecimal> it = numbers.descendingIterator();
        while (it.hasNext()) {
            ret.append(" ").append(formatDecimal(it.next()));
        }
        return ret.toString();
    }

    private void basicOperate(OperationEnum operation) {
        BigDecimal num2 = numbers.pop();
        BigDecimal num1 = numbers.pop();
        BigDecimal result = basicOperateResult(operation, num1, num2);
        numbers.push(result);
        numberLogs.push(num2);
        numberLogs.push(num1);
        operatorLogs.push(operation);
    }

    private BigDecimal basicOperateResult(OperationEnum operation, BigDecimal number1, BigDecimal number2) {
        return switch (operation) {
            case ADD -> number1.add(number2, MATH_CONTEXT);
            case SUB -> number1.subtract(number2, MATH_CONTEXT);
            case MUL -> number1.multiply(number2, MATH_CONTEXT);
            case DIV -> number1.divide(number2, MATH_CONTEXT);
            default -> null;
        };
    }

    private OperationEnum parseOperation(String opt) throws InvalidOperationException {
        return switch (opt) {
            case "+" -> OperationEnum.ADD;
            case "-" -> OperationEnum.SUB;
            case "*" -> OperationEnum.MUL;
            case "/" -> OperationEnum.DIV;
            case "sqrt" -> OperationEnum.SQRT;
            case "undo" -> OperationEnum.UNDO;
            case "clear" -> OperationEnum.CLEAR;
            default -> throw new InvalidOperationException(opt);
        };
    }

    private void operate(OperationEnum value) throws InsufficientParametersException {
        switch (value) {
            case CLEAR:
                clearStack();
                break;
            case SQRT:
                if (numbers.isEmpty()) {
                    throw new InsufficientParametersException();
                }
                sqrt();
                operatorLogs.push(OperationEnum.SQRT);
                break;
            case UNDO:
                undo();
                break;
            default:
                if (numbers.size() == 1 || numbers.size() == 0) {
                    throw new InsufficientParametersException();
                }
                basicOperate(value);
        }
    }
}
