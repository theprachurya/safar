import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Date;

// Main class for the application
public class SafarAppV2 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SafarLogin loginApp = new SafarLogin();
            loginApp.setVisible(true);
        });
    }
}

// Database connection utility class
class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/train";
    private static final String USER = "root";
    private static final String PASSWORD = "hellow0rld@123"; // Replace with your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

// Login class
class SafarLogin extends JFrame {
    public SafarLogin() {
        setTitle("Safar - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        // Header for Welcome Screen
        JLabel header = new JLabel("Welcome to Safar", SwingConstants.CENTER);
        header.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        header.setForeground(Color.WHITE);
        header.setBackground(Color.RED);
        header.setOpaque(true);
        add(header, BorderLayout.NORTH);

        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        loginPanel.setBackground(Color.BLACK);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        loginPanel.add(new JLabel("Username:", JLabel.RIGHT));
        JTextField usernameField = new JTextField();
        loginPanel.add(usernameField);

        loginPanel.add(new JLabel("Password:", JLabel.RIGHT));
        JPasswordField passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        loginButton.setBackground(Color.RED);
        loginButton.setForeground(Color.WHITE);
        loginPanel.add(loginButton);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        signUpButton.setBackground(Color.GREEN);
        signUpButton.setForeground(Color.BLACK);
        loginPanel.add(signUpButton);

        add(loginPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel("Enter your credentials to proceed.", SwingConstants.CENTER);
        footer.setForeground(Color.WHITE);
        footer.setBackground(Color.BLACK);
        footer.setOpaque(true);
        add(footer, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
        
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both username and password.", 
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        
            try {
                if (validateLogin(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login Successful!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    new HomeScreen(username).setVisible(true); // Open HomeScreen after successful login
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials, please try again.", 
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        signUpButton.addActionListener(e -> new SignUpPanel().setVisible(true));
    }

    private boolean validateLogin(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if credentials are valid
            }
        }
    }
}

// Sign-Up class
class SignUpPanel extends JFrame {
    public SignUpPanel() {
        setTitle("Safar - Sign Up");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(7, 2, 10, 10));
        getContentPane().setBackground(Color.BLACK);

        // Add form fields
        add(new JLabel("Username:", JLabel.RIGHT));
        JTextField usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:", JLabel.RIGHT));
        JPasswordField passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Full Name:", JLabel.RIGHT));
        JTextField fullNameField = new JTextField();
        add(fullNameField);

        add(new JLabel("Email:", JLabel.RIGHT));
        JTextField emailField = new JTextField();
        add(emailField);

        add(new JLabel("Phone:", JLabel.RIGHT));
        JTextField phoneField = new JTextField();
        add(phoneField);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setBackground(Color.GREEN);
        signUpButton.setForeground(Color.BLACK);
        add(signUpButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.RED);
        cancelButton.setForeground(Color.WHITE);
        add(cancelButton);

        signUpButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String fullName = fullNameField.getText();
            String email = emailField.getText();
            String phone = phoneField.getText();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || 
                email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields.", 
                    "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                addUserToDatabase(username, password, fullName, email, phone);
                JOptionPane.showMessageDialog(null, "Sign Up Successful!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Sign Up Failed: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    private void addUserToDatabase(String username, String password, String fullName, 
                                String email, String phone) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, email, phone) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            
            pstmt.executeUpdate();
        }
    }
}

// BookedTicketsPanel class definition
class BookedTicketsPanel extends JFrame {
    public BookedTicketsPanel(String username) {
        setTitle("Safar - Booked Tickets");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        // Header
        JLabel header = new JLabel("Your Booked Tickets", SwingConstants.CENTER);
        header.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        header.setForeground(Color.WHITE);
        header.setBackground(Color.RED);
        header.setOpaque(true);
        add(header, BorderLayout.NORTH);

        // Text area for displaying booked tickets
        JTextArea ticketsTextArea = new JTextArea();
        ticketsTextArea.setEditable(false);
        ticketsTextArea.setBackground(Color.BLACK);
        ticketsTextArea.setForeground(Color.WHITE);
        ticketsTextArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        add(new JScrollPane(ticketsTextArea), BorderLayout.CENTER);

        // Fetch booked tickets from the database and display them
        StringBuilder ticketsInfo = new StringBuilder();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM bookings WHERE username = ?")) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ticketsInfo.append("Booking ID: ").append(rs.getInt("booking_id")).append("\n")
                           .append("Departure: ").append(rs.getString("departure")).append("\n")
                           .append("Arrival: ").append(rs.getString("arrival")).append("\n")
                           .append("Date: ").append(rs.getDate("date")).append("\n")
                           .append("Class: ").append(rs.getString("class")).append("\n")
                           .append("Train: ").append(rs.getString("train")).append("\n")
                           .append("Berth: ").append(rs.getString("berth_type")).append("\n")
                           .append("-------------\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ticketsInfo.append("Error fetching booked tickets.");
        }
        ticketsTextArea.setText(ticketsInfo.toString());
    }
}



class HomeScreen extends JFrame {
    public HomeScreen(String username) {
        setTitle("Safar - Home");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.BLACK);

        JLabel header = new JLabel("Welcome, " + username, SwingConstants.CENTER);
        header.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        header.setForeground(Color.WHITE);
        header.setBackground(Color.RED);
        header.setOpaque(true);
        add(header, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton bookTicketsButton = new JButton("Book Tickets");
        bookTicketsButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        bookTicketsButton.setBackground(Color.RED);
        bookTicketsButton.setForeground(Color.WHITE);
        buttonPanel.add(bookTicketsButton);

        JButton showTicketsButton = new JButton("Show Booked Tickets");
        showTicketsButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        showTicketsButton.setBackground(Color.GREEN);
        showTicketsButton.setForeground(Color.BLACK);
        buttonPanel.add(showTicketsButton);

        add(buttonPanel, BorderLayout.CENTER);

        bookTicketsButton.addActionListener(e -> {
            new StationSelectionPanel(username).setVisible(true);
            dispose();
        });

        showTicketsButton.addActionListener(e -> {
            BookedTicketsPanel bookedTicketsPanel = new BookedTicketsPanel(username);  // Ensure 'username' is a String

            new BookedTicketsPanel(username).setVisible(true);
            dispose();
        });
    }
}

class StationSelectionPanel extends JFrame {
    public StationSelectionPanel(String username) {
        setTitle("Safar - Station Selection");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 2, 10, 10));
        getContentPane().setBackground(Color.BLACK);

        add(new JLabel("Departure Station:", JLabel.RIGHT));
        String[] stations = {
            "Chennai Central (MAS)",
            "Arakkonam Junction (AJJ)",
            "Katpadi Junction (KPD)",
            "Jolarpettai Junction (JTJ)",
            "Bangalore Cantonment (BNC)",
            "Krantivira Sangolli Rayanna (Bangalore City - SBC)"
        };
        JComboBox<String> departureDropdown = new JComboBox<>(stations);
        add(departureDropdown);

        add(new JLabel("Arrival Station:", JLabel.RIGHT));
        JComboBox<String> arrivalDropdown = new JComboBox<>(stations);
        add(arrivalDropdown);

        add(new JLabel("Select Train:", JLabel.RIGHT));
        String[] trains = {"Vandebharat", "Lalbagh SF Exp", "Shatabdi", "Mysuru Express", "Brindavan", "Kaveri Express"};
        JComboBox<String> trainDropdown = new JComboBox<>(trains);
        add(trainDropdown);

        add(new JLabel("Departure Date:", JLabel.RIGHT));
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        add(dateSpinner);

        add(new JLabel("Class:", JLabel.RIGHT));
        String[] classes = {"1st AC (₹1500)", "2nd AC (₹1000)", "3rd AC (₹750)", "Sleeper (₹300)", "General (₹150)"};
        JComboBox<String> classDropdown = new JComboBox<>(classes);
        add(classDropdown);

        JButton nextButton = new JButton("Next");
        nextButton.setBackground(Color.RED);
        nextButton.setForeground(Color.WHITE);
        nextButton.addActionListener(e -> {
            String departure = (String) departureDropdown.getSelectedItem();
            String arrival = (String) arrivalDropdown.getSelectedItem();
            Date selectedDate = (Date) dateSpinner.getValue();
            String selectedClass = (String) classDropdown.getSelectedItem();
            String selectedTrain = (String) trainDropdown.getSelectedItem();

            if (departure.equals(arrival)) {
                JOptionPane.showMessageDialog(this, "Departure and Arrival stations cannot be the same.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            new BerthSelectionPanel(username, departure, arrival, selectedDate, selectedClass, selectedTrain).setVisible(true);
            dispose();
        });
        add(new JLabel(""));
        add(nextButton);
    }
}

class BerthSelectionPanel extends JFrame {
    private final String username;
    private final String departure;
    private final String arrival;
    private final Date date;
    private final String seatClass;
    private final String train;

    public BerthSelectionPanel(String username, String departure, String arrival, Date date, String seatClass, String train) {
        this.username = username;
        this.departure = departure;
        this.arrival = arrival;
        this.date = date;
        this.seatClass = seatClass;
        this.train = train;

        setTitle("Safar - Berth Selection");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));
        getContentPane().setBackground(Color.BLACK);

        add(new JLabel("Select Berth Type:", JLabel.RIGHT));
        JComboBox<String> berthTypeDropdown = new JComboBox<>(new String[] {"Lower", "Upper", "Middle", "Side Lower", "Side Upper"});
        add(berthTypeDropdown);

        JButton confirmButton = new JButton("Confirm Booking");
        confirmButton.setBackground(Color.GREEN);
        confirmButton.setForeground(Color.BLACK);
        confirmButton.addActionListener(e -> {
            String selectedBerthType = (String) berthTypeDropdown.getSelectedItem();
            try {
                addBookingToDatabase(selectedBerthType);
                JOptionPane.showMessageDialog(this, "Booking Confirmed!\nTrain: " + train + "\nDeparture: " + departure + "\nArrival: " + arrival + "\nBerth: " + selectedBerthType);
                new HomeScreen(username).setVisible(true);
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(new JLabel(""));
        add(confirmButton);
    }

    private void addBookingToDatabase(String berthType) throws SQLException {
        String sql = "INSERT INTO bookings (username, departure, arrival, date, class, train, berth_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, departure);
            pstmt.setString(3, arrival);
            pstmt.setDate(4, new java.sql.Date(date.getTime()));
            pstmt.setString(5, seatClass);
            pstmt.setString(6, train);
            pstmt.setString(7, berthType);
            pstmt.executeUpdate();
        }
    }
}
