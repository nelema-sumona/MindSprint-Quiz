import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class LoginFrame extends JFrame {
    JTextField userField;
    JPasswordField passField;
    JButton loginBtn;
    JLabel statusLabel;

    public LoginFrame() {
        setTitle("Login");
        setSize(350, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel jp = new JPanel();
        jp.setLayout(null); // absolute positioning
        add(jp);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 30, 80, 30);
        jp.add(userLabel);

        userField = new JTextField();
        userField.setBounds(140, 30, 130, 30);
        jp.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 80, 80, 30);
        jp.add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(140, 80, 130, 30);
        jp.add(passField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(110, 130, 100, 30);
        jp.add(loginBtn);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setBounds(50, 170, 250, 30);
        jp.add(statusLabel);

        // Login button action
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (username.isEmpty()) {
                statusLabel.setText("Enter username");
                return;
            }

            if (username.equals("admin") && password.equals("1234")) {
                new StudentInfoFrame(username).setVisible(true);
                this.dispose();
            } else {
                statusLabel.setText("Invalid username or password");
            }
        });
    }

    public static void main(String[] args) {
        new LoginFrame().setVisible(true);
    }
}



class StudentInfoFrame extends JFrame {
    JTextField nameField, idField;
    JLabel messageLabel;
    JButton submitBtn, startQuizBtn;
    String username;

    public StudentInfoFrame(String username) {
        this.username = username;

        setTitle("Student Information");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel jp = new JPanel();
        jp.setLayout(null); // IMPORTANT for using setBounds
        add(jp);

        JLabel nameLabel = new JLabel("Student Name:");
        nameLabel.setBounds(50, 30, 100, 30);
        jp.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(160, 30, 150, 30);
        jp.add(nameField);

        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setBounds(50, 80, 100, 30);
        jp.add(idLabel);

        idField = new JTextField();
        idField.setBounds(160, 80, 150, 30);
        jp.add(idField);

        submitBtn = new JButton("Submit");
        submitBtn.setBounds(130, 130, 100, 30);
        jp.add(submitBtn);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setBounds(50, 170, 280, 30);
        jp.add(messageLabel);

        startQuizBtn = new JButton("Start Quiz");
        startQuizBtn.setBounds(130, 210, 120, 30);
        startQuizBtn.setEnabled(false);
        jp.add(startQuizBtn);

        submitBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String id = idField.getText().trim();
            if (name.isEmpty() || id.isEmpty()) {
                messageLabel.setText("Please fill all fields.");
            } else {
                messageLabel.setText("Now start quiz");
                startQuizBtn.setEnabled(true);
            }
        });

        startQuizBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String id = idField.getText().trim();
            new QuizMaster(username, name, id).setVisible(true);
            this.dispose();
        });
    }
}

class QuizMaster extends JFrame implements ActionListener {
    JLabel questionLabel, timerLabel;
    JRadioButton option1, option2, option3, option4;
    ButtonGroup bg;
    JButton nextButton;
    java.util.List<Question> questions;
    int index = 0, score = 0;
    javax.swing.Timer timer;
    int timeLeft = 15;
    java.util.List<String> userAnswers = new ArrayList<>();
    String username, studentName, studentID;

    public QuizMaster(String username, String studentName, String studentID) {
        this.username = username;
        this.studentName = studentName;
        this.studentID = studentID;

        setTitle("Quiz Master");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        questionLabel = new JLabel("Question here");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 14));

        option1 = new JRadioButton();
        option2 = new JRadioButton();
        option3 = new JRadioButton();
        option4 = new JRadioButton();

        bg = new ButtonGroup();
        bg.add(option1);
        bg.add(option2);
        bg.add(option3);
        bg.add(option4);

        nextButton = new JButton("Next");
        nextButton.addActionListener(this);

        timerLabel = new JLabel("Time left: 15");
        timerLabel.setForeground(Color.RED);

        JPanel panel = new JPanel(new GridLayout(7, 1));
        panel.add(questionLabel);
        panel.add(option1);
        panel.add(option2);
        panel.add(option3);
        panel.add(option4);
        panel.add(timerLabel);
        panel.add(nextButton);

        add(panel);

        questions = loadQuestions("questions.txt");
        if (questions.size() > 0) showQuestion(index);

        startTimer();
    }

    public java.util.List<Question> loadQuestions(String fileName) {
        java.util.List<Question> list = new ArrayList<>();
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String q = scanner.nextLine().replace("Q:", "").trim();
                String a = scanner.nextLine().replace("A)", "").trim();
                String b = scanner.nextLine().replace("B)", "").trim();
                String c = scanner.nextLine().replace("C)", "").trim();
                String d = scanner.nextLine().replace("D)", "").trim();
                String ans = scanner.nextLine().split(":")[1].trim();
                if (scanner.hasNextLine()) scanner.nextLine(); // skip ---
                list.add(new Question(q, new String[]{a, b, c, d}, ans));
            }
            scanner.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not read questions.txt");
        }
        return list;
    }

    public void showQuestion(int i) {
        Question q = questions.get(i);
        questionLabel.setText("Q" + (i + 1) + ": " + q.question);
        option1.setText(q.options[0]);
        option2.setText(q.options[1]);
        option3.setText(q.options[2]);
        option4.setText(q.options[3]);
        bg.clearSelection();
        timeLeft = 15;
        timerLabel.setText("Time left: 15");

        nextButton.setText((i == questions.size() - 1) ? "Submit" : "Next");
    }

    public void startTimer() {
        timer = new javax.swing.Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time left: " + timeLeft);
            if (timeLeft == 0) {
                nextButton.doClick();
            }
        });
        timer.start();
    }

    public void actionPerformed(ActionEvent e) {
        timer.stop();
        Question q = questions.get(index);

        String selectedLetter = null;
        if (option1.isSelected()) selectedLetter = "A";
        else if (option2.isSelected()) selectedLetter = "B";
        else if (option3.isSelected()) selectedLetter = "C";
        else if (option4.isSelected()) selectedLetter = "D";

        userAnswers.add(selectedLetter);

        if (selectedLetter != null && q.correctAnswer.equalsIgnoreCase(selectedLetter)) {
            score++;
        }

        index++;
        if (index < questions.size()) {
            showQuestion(index);
            startTimer();
        } else {
            saveResultToFile();
            showReview();
        }
    }

    private void showReview() {
        this.setVisible(false);

        JFrame reviewFrame = new JFrame("Quiz Review");
        reviewFrame.setSize(600, 400);
        reviewFrame.setLocationRelativeTo(null);
        reviewFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        String[] columns = {"Q#", "Question", "Your Answer", "Correct Answer", "Result"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String yourAns = userAnswers.get(i);
            String correctAns = q.correctAnswer;
            String result = yourAns != null && yourAns.equalsIgnoreCase(correctAns) ? "✔" : "✘";
            model.addRow(new Object[]{
                i + 1,
                q.question,
                yourAns,
                correctAns,
                result
            });
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JLabel scoreLabel = new JLabel("Your score: " + score + "/" + questions.size());
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton restartBtn = new JButton("Restart Quiz");
restartBtn.addActionListener(ev -> {
    reviewFrame.dispose();
    new StudentInfoFrame(username).setVisible(true);
    QuizMaster.this.dispose();
});


        JPanel bottomPanel = new JPanel(null);
bottomPanel.setPreferredSize(new Dimension(600, 60));

scoreLabel.setBounds(20, 10, 300, 30);
restartBtn.setBounds(340, 10, 120, 30);
JButton viewDetailsBtn = new JButton("View Details");
viewDetailsBtn.setBounds(470, 10, 120, 30);

bottomPanel.add(scoreLabel);
bottomPanel.add(restartBtn);
bottomPanel.add(viewDetailsBtn);

// Add action to viewDetailsBtn
viewDetailsBtn.addActionListener(ae -> showStudentDetails());

        reviewFrame.setLayout(new BorderLayout());
        reviewFrame.add(scrollPane, BorderLayout.CENTER);
        reviewFrame.add(bottomPanel, BorderLayout.SOUTH);

        reviewFrame.setVisible(true);
    }

    private void showStudentDetails() {
    JFrame detailFrame = new JFrame("Student Details");
    detailFrame.setSize(350, 250);
    detailFrame.setLocationRelativeTo(null);
    detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    detailFrame.setLayout(null);

    double percentage = ((double) score / questions.size()) * 100;
    String status = (percentage >= 40) ? "PASS" : "FAIL";

JLabel userLabel = new JLabel("Username: " + username);
    userLabel.setBounds(30, 30, 300, 25);
    detailFrame.add(userLabel);

    JLabel nameLabel = new JLabel("Name: " + studentName);
    nameLabel.setBounds(30, 60, 300, 25);
    detailFrame.add(nameLabel);

    JLabel idLabel = new JLabel("ID: " + studentID);
    idLabel.setBounds(30, 90, 300, 25);
    detailFrame.add(idLabel);

    
    JLabel scoreLabel = new JLabel("Score: " + score + "/" + questions.size());
    scoreLabel.setBounds(30, 120, 300, 25);
    detailFrame.add(scoreLabel);

    JLabel resultLabel = new JLabel("Result: " + status);
    resultLabel.setBounds(30, 150, 300, 25);
    detailFrame.add(resultLabel);

    detailFrame.setVisible(true);
}


    private void resetQuiz() {
        index = 0;
        score = 0;
        userAnswers.clear();
        showQuestion(index);
        startTimer();
    }

    private void saveResultToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("result.txt", true))) {
            double percentage = ((double) score / questions.size()) * 100;
            String status = (percentage >= 40) ? "PASS" : "FAIL";

            writer.println("Username: " + username);
            writer.println("Student Name: " + studentName);
            writer.println("Student ID: " + studentID);
            writer.printf("Score: %d/%d (%.2f%%)%n", score, questions.size(), percentage);
            writer.println("Result: " + status);
            writer.println("---------------------------------------------------");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving results to file.");
        }
    }
}

class Question {
    String question;
    String[] options;
    String correctAnswer;

    public Question(String q, String[] opts, String ans) {
        question = q;
        options = opts;
        correctAnswer = ans;
    }
}
