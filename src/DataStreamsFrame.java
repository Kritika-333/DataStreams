import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamsFrame extends JFrame {
    private final JTextArea leftArea = new JTextArea();
    private final JTextArea rightArea = new JTextArea();
    private final JTextField searchField = new JTextField(30);
    private final JButton loadBtn = new JButton("Load File...");
    private final JButton searchBtn = new JButton("Search");
    private final JButton quitBtn = new JButton("Quit");
    private Path currentFile = null;
    private final JFileChooser chooser = new JFileChooser();

    public DataStreamsFrame() {
        super("Lab 09 — Java Data Streams");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8,8));

        // top: controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search string:"));
        top.add(searchField);
        top.add(searchBtn);
        top.add(loadBtn);
        top.add(quitBtn);
        add(top, BorderLayout.NORTH);

        // center: two text areas side-by-side
        leftArea.setEditable(false);
        rightArea.setEditable(false);
        leftArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        rightArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane leftScroll = new JScrollPane(leftArea);
        JScrollPane rightScroll = new JScrollPane(rightArea);
        leftScroll.setBorder(BorderFactory.createTitledBorder("Original file"));
        rightScroll.setBorder(BorderFactory.createTitledBorder("Filtered (matches)"));
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, rightScroll);
        split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER);

        // file chooser filter
        chooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt", "text"));

        // button actions
        loadBtn.addActionListener(e -> loadFile());
        searchBtn.addActionListener(e -> doSearch());
        quitBtn.addActionListener(e -> {
            int ans = JOptionPane.showConfirmDialog(this, "Quit?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (ans == JOptionPane.YES_OPTION) System.exit(0);
        });

        // enable search only after a file is loaded
        searchBtn.setEnabled(false);

        setSize(1000,700);
        setLocationRelativeTo(null);
    }

    private void loadFile() {
        int res = chooser.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;
        currentFile = chooser.getSelectedFile().toPath();
        leftArea.setText(""); rightArea.setText("");
        // read file and show left area with line numbers
        try {
            List<String> lines = Files.readAllLines(currentFile, StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lines.size(); i++) {
                sb.append(String.format("%4d: %s%n", i+1, lines.get(i)));
            }
            leftArea.setText(sb.toString());
            searchBtn.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Loaded file: " + currentFile.getFileName());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            currentFile = null;
            searchBtn.setEnabled(false);
        }
    }

    private void doSearch() {
        if (currentFile == null) { JOptionPane.showMessageDialog(this, "Load a file first."); return; }
        String query = searchField.getText();
        if (query == null || query.isBlank()) { JOptionPane.showMessageDialog(this, "Enter a search string."); return; }
        // Use Streams: Files.lines(path) and filter with lambda
        rightArea.setText("");
        try (Stream<String> lines = Files.lines(currentFile, StandardCharsets.UTF_8)) {
            List<String> matches = lines
                    .filter(line -> line.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            if (matches.isEmpty()) {
                rightArea.setText("(no matches found for \"" + query + "\")");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String ln : matches) {
                    sb.append(ln).append(System.lineSeparator());
                }
                rightArea.setText(sb.toString());
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error searching file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
