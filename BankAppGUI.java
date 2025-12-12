import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * Bank Account with file-based storage + Swing GUI (Improved Theme)
 */
class BankAccount {
    private int accountNumber;
    private String accountHolderName;
    private double balance;

    public BankAccount(int accountNumber, String accountHolderName, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount should be greater than 0.");
        }
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount should be greater than 0.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        balance -= amount;
    }

    public String toFileString() {
        return accountNumber + "|" + accountHolderName + "|" + balance;
    }

    public static BankAccount fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 3) return null;
        int accNo = Integer.parseInt(parts[0]);
        String name = parts[1];
        double bal = Double.parseDouble(parts[2]);
        return new BankAccount(accNo, name, bal);
    }

    @Override
    public String toString() {
        return "Account Number: " + accountNumber +
                ", Name: " + accountHolderName +
                ", Balance: ₹" + String.format("%.2f", balance);
    }
}

public class BankAppGUI {

    private static final String FILE_NAME = "accounts.txt";
    private static Map<Integer, BankAccount> accounts = new HashMap<>();
    private static int nextAccountNumber = 1001;

    private JFrame frame;
    private JTextArea outputArea;

    // Theme colors
    private final Color BG_DARK = new Color(25, 28, 35);
    private final Color BG_PANEL = new Color(32, 36, 45);
    private final Color ACCENT = new Color(76, 175, 80);    // green
    private final Color ACCENT_DARK = new Color(56, 142, 60);
    private final Color TEXT_PRIMARY = new Color(236, 239, 241);
    private final Color TEXT_SECONDARY = new Color(189, 189, 189);

    public BankAppGUI() {
        loadAccountsFromFile();
        initUI();
    }

    private void initUI() {
        frame = new JFrame("Bank Account Balance Checker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 450);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BG_DARK);

        // ---------- HEADER ----------
        JLabel titleLabel = new JLabel("Bank Account Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel subtitleLabel = new JLabel("Create accounts, deposit, withdraw & check balances");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_DARK);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // ---------- LEFT BUTTON PANEL ----------
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBackground(BG_PANEL);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton createBtn = createStyledButton("Create New Account");
        JButton depositBtn = createStyledButton("Deposit Amount");
        JButton withdrawBtn = createStyledButton("Withdraw Amount");
        JButton balanceBtn = createStyledButton("Check Balance");
        JButton viewAllBtn = createStyledButton("View All Accounts");

        buttonPanel.add(createBtn);
        buttonPanel.add(depositBtn);
        buttonPanel.add(withdrawBtn);
        buttonPanel.add(balanceBtn);
        buttonPanel.add(viewAllBtn);

        // ---------- OUTPUT AREA ----------
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputArea.setBackground(new Color(18, 18, 18));
        outputArea.setForeground(TEXT_PRIMARY);
        outputArea.setCaretColor(TEXT_PRIMARY);
        outputArea.setSelectionColor(new Color(56, 142, 60));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 65)),
                "Activity Log",
                0, 0,
                new Font("Segoe UI", Font.PLAIN, 12),
                TEXT_SECONDARY
        ));
        scrollPane.getViewport().setBackground(new Color(18, 18, 18));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);

        // Listeners
        createBtn.addActionListener(e -> createAccount());
        depositBtn.addActionListener(e -> depositAmount());
        withdrawBtn.addActionListener(e -> withdrawAmount());
        balanceBtn.addActionListener(e -> checkBalance());
        viewAllBtn.addActionListener(e -> viewAllAccounts());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveAccountsToFile();
            }
        });

        frame.setVisible(true);

        log("Application started. " + accounts.size() + " account(s) loaded.");
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_DARK);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT);
            }
        });

        return btn;
    }

    // ------------- Utility log -------------

    private void log(String message) {
        outputArea.append(message + "\n");
    }

    // ------------- Operations -------------

    private void createAccount() {
        String name = JOptionPane.showInputDialog(frame,
                "Enter account holder name:",
                "Create New Account",
                JOptionPane.PLAIN_MESSAGE);

        if (name == null) return;

        name = name.trim();
        if (name.isEmpty()) {
            showError("Name cannot be empty.");
            return;
        }

        int accNo = nextAccountNumber++;
        BankAccount newAcc = new BankAccount(accNo, name, 0.0);
        accounts.put(accNo, newAcc);
        saveAccountsToFile();

        String msg = "Account created successfully! Account Number: " + accNo;
        showInfo(msg, "Success");
        log(msg);
    }

    private void depositAmount() {
        Integer accNo = askAccountNumber("Deposit Amount");
        if (accNo == null) return;

        BankAccount acc = accounts.get(accNo);
        if (acc == null) {
            showError("Account not found.");
            return;
        }

        Double amount = askAmount("Enter amount to deposit:");
        if (amount == null) return;

        try {
            acc.deposit(amount);
            saveAccountsToFile();
            String msg = "₹" + amount + " deposited to Account " + accNo +
                    ". New Balance: ₹" + String.format("%.2f", acc.getBalance());
            showInfo(msg, "Deposit Successful");
            log(msg);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void withdrawAmount() {
        Integer accNo = askAccountNumber("Withdraw Amount");
        if (accNo == null) return;

        BankAccount acc = accounts.get(accNo);
        if (acc == null) {
            showError("Account not found.");
            return;
        }

        Double amount = askAmount("Enter amount to withdraw:");
        if (amount == null) return;

        try {
            acc.withdraw(amount);
            saveAccountsToFile();
            String msg = "₹" + amount + " withdrawn from Account " + accNo +
                    ". New Balance: ₹" + String.format("%.2f", acc.getBalance());
            showInfo(msg, "Withdrawal Successful");
            log(msg);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void checkBalance() {
        Integer accNo = askAccountNumber("Check Balance");
        if (accNo == null) return;

        BankAccount acc = accounts.get(accNo);
        if (acc == null) {
            showError("Account not found.");
            return;
        }

        String msg = "Account Number: " + acc.getAccountNumber() +
                "\nAccount Holder: " + acc.getAccountHolderName() +
                "\nCurrent Balance: ₹" + String.format("%.2f", acc.getBalance());
        showInfo(msg, "Balance Details");
        log("Checked balance for Account " + accNo + ": ₹" +
                String.format("%.2f", acc.getBalance()));
    }

    private void viewAllAccounts() {
        if (accounts.isEmpty()) {
            showInfo("No accounts found.", "Information");
            log("No accounts to display.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("----- All Accounts -----\n");
        for (BankAccount acc : accounts.values()) {
            sb.append(acc.toString()).append("\n");
        }

        outputArea.append(sb.toString());
        showInfo("All accounts listed in Activity Log.", "All Accounts");
    }

    // ------------- Helper dialogs -------------

    private Integer askAccountNumber(String title) {
        String accInput = JOptionPane.showInputDialog(frame,
                "Enter account number:",
                title,
                JOptionPane.PLAIN_MESSAGE);

        if (accInput == null) return null;

        accInput = accInput.trim();
        if (accInput.isEmpty()) {
            showError("Account number cannot be empty.");
            return null;
        }

        try {
            return Integer.parseInt(accInput);
        } catch (NumberFormatException ex) {
            showError("Invalid account number.");
            return null;
        }
    }

    private Double askAmount(String message) {
        String amtInput = JOptionPane.showInputDialog(frame,
                message,
                "Amount",
                JOptionPane.PLAIN_MESSAGE);

        if (amtInput == null) return null;

        amtInput = amtInput.trim();
        if (amtInput.isEmpty()) {
            showError("Amount cannot be empty.");
            return null;
        }

        try {
            return Double.parseDouble(amtInput);
        } catch (NumberFormatException ex) {
            showError("Invalid amount.");
            return null;
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg, String title) {
        JOptionPane.showMessageDialog(frame, msg, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ------------- File Handling -------------

    private static void loadAccountsFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int maxAccNo = 1000;
            while ((line = br.readLine()) != null) {
                BankAccount acc = BankAccount.fromFileString(line);
                if (acc != null) {
                    accounts.put(acc.getAccountNumber(), acc);
                    if (acc.getAccountNumber() > maxAccNo) {
                        maxAccNo = acc.getAccountNumber();
                    }
                }
            }
            nextAccountNumber = maxAccNo + 1;
        } catch (IOException e) {
            System.out.println("Error reading accounts file: " + e.getMessage());
        }
    }

    private static void saveAccountsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (BankAccount acc : accounts.values()) {
                bw.write(acc.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts to file: " + e.getMessage());
        }
    }

    // ------------- Main -------------

    public static void main(String[] args) {
        // Try Nimbus for better default look
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(BankAppGUI::new);
    }
}
