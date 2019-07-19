package net.dynamicdev.anticheat.check;

public class CheckResult
{
    private Result result;
    private String message;
    private int data;
    
    public CheckResult(final Result result, final String message, final int data) {
        this(result, message);
        this.data = data;
    }
    
    public CheckResult(final Result result, final String message) {
        this(result);
        this.message = message;
    }
    
    public CheckResult(final Result result) {
        this.result = result;
    }
    
    public boolean failed() {
        return this.result == Result.FAILED;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public Result getResult() {
        return this.result;
    }
    
    public int getData() {
        return this.data;
    }
    
    public enum Result
    {
        PASSED, 
        FAILED;
    }
}
