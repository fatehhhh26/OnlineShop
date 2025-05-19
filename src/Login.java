import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame implements ActionListener {
    JLabel user_label, Password_label, message;
    JTextField Input_User;
    JPasswordField Input_Password;
    JButton Submit, Cancel;

    public Login() {
        setTitle("Online Shop - Login");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // Header
        JLabel header = new JLabel("ONLINE SHOP SYSTEM", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBounds(50, 10, 250, 25);
        add(header);

        user_label = new JLabel("Username:");
        Input_User = new JTextField();
        user_label.setBounds(50, 50, 80, 25);
        Input_User.setBounds(140, 50, 150, 25);

        Password_label = new JLabel("Password:");
        Input_Password = new JPasswordField();
        Password_label.setBounds(50, 85, 80, 25);
        Input_Password.setBounds(140, 85, 150, 25);

        Submit = new JButton("Login");
        Submit.setBounds(80, 125, 80, 30);
        Submit.setBackground(new Color(0, 123, 255));
        Submit.setForeground(Color.WHITE);

        Cancel = new JButton("Cancel");
        Cancel.setBounds(180, 125, 80, 30);
        Cancel.setBackground(new Color(108, 117, 125));
        Cancel.setForeground(Color.WHITE);

        Submit.addActionListener(this);
        Cancel.addActionListener(this);

        message = new JLabel("", JLabel.CENTER);
        message.setBounds(50, 165, 250, 25);
        message.setForeground(Color.RED);

        add(user_label);
        add(Password_label);
        add(Input_User);
        add(Input_Password);
        add(Submit);
        add(Cancel);
        add(message);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Submit) {
            String username = Input_User.getText();
            String password = String.valueOf(Input_Password.getPassword());

            if (username.equals("admin") && password.equals("123")) {
                message.setForeground(Color.GREEN);
                message.setText("Login berhasil!");

                // Close login window and open main system
                this.dispose();
                new OnlineShopSystem();
            } else if (username.equals("user") && password.equals("123")) {
                message.setForeground(Color.GREEN);
                message.setText("Login berhasil!");

                this.dispose();
                new CustomerInterface();
            } else {
                message.setForeground(Color.RED);
                message.setText("Username atau password salah!");
            }
        } else if (e.getSource() == Cancel) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Login();
        });
    }
}