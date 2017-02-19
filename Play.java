
import java.awt.EventQueue;
import javax.swing.JFrame;

public class Play extends JFrame {

    public Play() {
        
        initUI();
    }
    
    private void initUI() {
        
        add(new Board());
        
        setTitle("Pacman");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 420);
        setLocationRelativeTo(null);
        setVisible(true);        
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            Play ex = new Play();
            ex.setVisible(true);
        });
    }
}