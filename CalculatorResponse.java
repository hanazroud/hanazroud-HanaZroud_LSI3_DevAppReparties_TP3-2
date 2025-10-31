package tp3_2;

import java.io.Serializable;

public class CalculatorResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean ok;
    private double result;
    private String message;

    public CalculatorResponse(boolean ok, double result, String message) {
        this.ok = ok;
        this.result = result;
        this.message = message;
    }

    public boolean isOk() { return ok; }
    public double getResult() { return result; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "CalculatorResponse[ok=" + ok + ", result=" + result + ", message=" + message + "]";
    }
}
