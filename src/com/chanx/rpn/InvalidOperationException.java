package com.chanx.rpn;

public class InvalidOperationException extends CalculatorException{

    public InvalidOperationException(String operation) {
        super("Invalid operation: " + operation);
    }
}
