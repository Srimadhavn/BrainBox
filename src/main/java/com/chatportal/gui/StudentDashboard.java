package com.chatportal.gui;

import com.chatportal.dao.QueryDAO;
import com.chatportal.model.Query;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentDashboard extends JFrame {
    private JTextArea queryArea;
    private JComboBox<String> subjectComboBox;
    private JButton submitButton;
    private JTextArea queriesDisplayArea;
    private final int userId;
    private final Color primaryColor = new Color(41, 128, 185);
    private final Color secondaryColor = new Color(52, 152, 219);
    private final Color accentColor = new Color(230, 126, 34);

    public StudentDashboard(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID: " + userId);
        }

        this.userId = userId;
        initializeUI();
        displayUserQueries();
    }

    private void initializeUI() {
        setTitle("Student Dashboard");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel mainPanel = createMainPanel();
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(25, 25)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, primaryColor, getWidth(), getHeight(), secondaryColor);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Student Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        return mainPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.weightx = 0.4;
        gbc.weighty = 1.0;
        contentPanel.add(createInputPanel(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        contentPanel.add(createQueriesDisplayPanel(), gbc);

        return contentPanel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(false);

        subjectComboBox = new JComboBox<>(new String[]{"MATH101", "COA101","DSA101" ,"APP101","OS101" ,"DTM101"} );
        styleComboBox(subjectComboBox);
        inputPanel.add(createLabeledComponent("Select Subject:", subjectComboBox));
        inputPanel.add(Box.createVerticalStrut(20));

        queryArea = new JTextArea(8, 20);
        styleTextArea(queryArea);
        inputPanel.add(createLabeledComponent("Enter Query:", new JScrollPane(queryArea)));
        inputPanel.add(Box.createVerticalStrut(20));

        submitButton = new JButton("Submit Query");
        styleButton(submitButton);
        submitButton.addActionListener(e -> submitQuery());
        inputPanel.add(submitButton);

        return inputPanel;
    }

    private JScrollPane createQueriesDisplayPanel() {
        queriesDisplayArea = new JTextArea();
        queriesDisplayArea.setEditable(false);
        styleTextArea(queriesDisplayArea);

        JScrollPane scrollPane = new JScrollPane(queriesDisplayArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        return scrollPane;
    }

    private JPanel createLabeledComponent(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(Color.WHITE);

        panel.add(label, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.DARK_GRAY);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(10, Color.WHITE),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleTextArea(JTextArea textArea) {
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(10, Color.WHITE),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(accentColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new RoundBorder(10, accentColor));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void submitQuery() {
        String content = queryArea.getText().trim();
        String subjectCode = (String) subjectComboBox.getSelectedItem();

        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Query content cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        QueryDAO queryDAO = new QueryDAO();
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return queryDAO.addQuery(userId, subjectCode, content);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(StudentDashboard.this, "Query submitted successfully!");
                        queryArea.setText("");
                        displayUserQueries();
                    } else {
                        JOptionPane.showMessageDialog(StudentDashboard.this, "Failed to submit query.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StudentDashboard.this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void displayUserQueries() {
        QueryDAO queryDAO = new QueryDAO();
        List<Query> queries = queryDAO.getUserQueries(userId);

        StringBuilder queryText = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Query query : queries) {
            queryText.append(query.getTimestamp().toGMTString())
                    .append(" - ")
                    .append(query.getSubjectCode())
                    .append("\nYour Query: ")
                    .append(query.getContent())
                    .append(" \n");

            if (query.getTeacherResponse() != null && !query.getTeacherResponse().isEmpty()) {
                queryText.append("Response: ").append(query.getTeacherResponse()).append("\n");
            }
            queryText.append("\n");
        }

        queriesDisplayArea.setText(queryText.toString());
        queriesDisplayArea.setCaretPosition(0);
    }


    private static class RoundBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 2, radius);
        }
    }

    private static class ModernScrollBarUI extends BasicScrollBarUI {
        private final Dimension d = new Dimension();

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return new JButton() {
                @Override
                public Dimension getPreferredSize() {
                    return d;
                }
            };
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return new JButton() {
                @Override
                public Dimension getPreferredSize() {
                    return d;
                }
            };
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200));
            g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
            g2.dispose();
        }
    }
}
