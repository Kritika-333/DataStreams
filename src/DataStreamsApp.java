import javax.swing.SwingUtilities;

public class DataStreamsApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DataStreamsFrame f = new DataStreamsFrame();
            f.setVisible(true);
        });
    }
}
