package tp3_2;

import java.io.Serializable;

public class CalculatorRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private double a;
    private double b;
    private String op;
    private String clientId;

    public CalculatorRequest(double a, double b, String op, String clientId) {
        this.a = a;
        this.b = b;
        this.op = op;
        this.clientId = clientId;
    }

    public double getA() { return a; }
    public double getB() { return b; }
    public String getOp() { return op; }
    public String getClientId() { return clientId; }

    @Override
    public String toString() {
        return "CalculatorRequest[a=" + a + ", op=" + op + ", b=" + b + ", clientId=" + clientId + "]";
    }
}
