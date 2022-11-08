package com.chanx.rpn;

public class InsufficientParametersException extends CalculatorException {
    public InsufficientParametersException() {
        super(" Insufficient Parameters");
    }
}
