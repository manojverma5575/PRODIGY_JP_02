import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class Calculator implements ActionListener {
    private JFrame frame;
    private JTextField textField;
    private JPanel panel;

    public Calculator() {
        frame = new JFrame("Calculator");
        frame.setSize(350, 500);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.BLACK);

        textField = new JTextField("0");
        textField.setFont(new Font("Arial", Font.BOLD, 28));
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        textField.setEditable(false);
        textField.setBackground(Color.BLACK);
        textField.setForeground(Color.WHITE);
        textField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel = new JPanel();
        panel.setLayout(new GridLayout(6, 4, 10, 10));
        panel.setBackground(Color.BLACK);

        String[] buttons = {
                "AC", "X", "(", ")",
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "%", "0", "=", "+",
                "."
        };

        for (String text : buttons) {
            JButton button = new JButton(text);
            int fontSize = text.matches("[+\\-*/=%()]") ? 28 : 22;
            button.setFont(new Font("Arial", Font.BOLD, fontSize));
            button.setFocusPainted(false);
            button.setOpaque(true);

            if (text.equals("AC") || text.equals("X")) {
                button.setBackground(Color.DARK_GRAY);
                button.setForeground(Color.WHITE);
                button.setFont(new Font("Arial", Font.BOLD, 28));
            } else if (text.matches("[+\\-*/=%()]") || text.equals("=")) {
                button.setBackground(new Color(255, 140, 0));
                button.setForeground(Color.BLACK);
            } else {
                button.setBackground(Color.BLACK);
                button.setForeground(Color.WHITE);
            }

            button.addActionListener(this);
            panel.add(button);
        }

        frame.add(textField, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.matches("[0-9]") || command.equals(".")) {
            if (textField.getText().equals("0")) {
                textField.setText(command);
            } else {
                textField.setText(textField.getText() + command);
            }
        } else if (command.equals("X")) {
            String text = textField.getText();
            if (text.length() > 1) {
                textField.setText(text.substring(0, text.length() - 1));
            } else {
                textField.setText("0");
            }
        } else if (command.equals("AC")) {
            textField.setText("0");
        } else if (command.equals("=")) {
            try {
                String input = textField.getText();
                double result = evaluateExpression(input);
                textField.setText(result == (long) result ? String.valueOf((long) result) : String.format("%.2f", result));
            } catch (Exception ex) {
                textField.setText("Error");
            }
        } else {
            textField.setText(textField.getText() + command);
        }
    }

    // Custom Expression Evaluator
    private double evaluateExpression(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operations = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder num = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    num.append(expression.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(num.toString()));
            } else if (ch == '(') {
                operations.push(ch);
            } else if (ch == ')') {
                while (operations.peek() != '(') {
                    numbers.push(applyOp(operations.pop(), numbers.pop(), numbers.pop()));
                }
                operations.pop();
            } else if (isOperator(ch)) {
                while (!operations.isEmpty() && precedence(ch) <= precedence(operations.peek())) {
                    numbers.push(applyOp(operations.pop(), numbers.pop(), numbers.pop()));
                }
                operations.push(ch);
            }
        }

        while (!operations.isEmpty()) {
            numbers.push(applyOp(operations.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%';
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/' || op == '%') return 2;
        return 0;
    }

    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
            case '%': return a * (b / 100);
        }
        return 0;
    }

    public static void main(String[] args) {
        new Calculator();
    }
}
