package com.chatportal.gui;

import com.chatportal.dao.QueryDAO;
import com.chatportal.model.Query;
import com.chatportal.model.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class TeacherDashboard extends JFrame {
    private User user;
    private JList<String> queryList;
    private DefaultListModel<String> listModel;
    private JComboBox<String> subjectComboBox;
    private QueryDAO queryDAO;
    private JTextArea responseArea;

    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color SECONDARY_COLOR = new Color(240, 248, 255);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font CONTENT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public TeacherDashboard(User user) {
        this.user = user;
        this.queryDAO = new QueryDAO();
        initializeUI();
        refreshQueries();
    }

    private void initializeUI() {
        setTitle("Teacher Dashboard - " + user.getUsername());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(SECONDARY_COLOR);

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername());
        welcomeLabel.setFont(TITLE_FONT);
        welcomeLabel.setForeground(Color.WHITE);

        subjectComboBox = new JComboBox<>(new String[]{"All", "MATH101", "COA101", "DSA101", "APP101", "OS101", "DTM101"});
        subjectComboBox.setFont(CONTENT_FONT);
        subjectComboBox.setPreferredSize(new Dimension(150, 30));
        subjectComboBox.addActionListener(e -> refreshQueries());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Filter by Subject: "));
        filterPanel.add(subjectComboBox);

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.EAST);
        return topPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        centerPanel.setBackground(SECONDARY_COLOR);

        listModel = new DefaultListModel<>();
        queryList = new JList<>(listModel);
        queryList.setFont(CONTENT_FONT);
        queryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        queryList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane queryScroll = new JScrollPane(queryList);
        queryScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                "Student Queries",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                TITLE_FONT,
                PRIMARY_COLOR
        ));

        centerPanel.add(queryScroll, BorderLayout.CENTER);

        JButton refreshButton = createStyledButton("Refresh Queries");
        refreshButton.addActionListener(e -> refreshQueries());
        centerPanel.add(refreshButton, BorderLayout.EAST);

        return centerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        bottomPanel.setBackground(SECONDARY_COLOR);

        responseArea = new JTextArea();
        responseArea.setFont(CONTENT_FONT);
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane responseScroll = new JScrollPane(responseArea);
        responseScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                "Your Response",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                TITLE_FONT,
                PRIMARY_COLOR
        ));
        responseScroll.setPreferredSize(new Dimension(0, 200));

        JButton replyButton = createStyledButton("Send Response");
        replyButton.addActionListener(e -> replyToQuery());

        bottomPanel.add(responseScroll, BorderLayout.CENTER);
        bottomPanel.add(replyButton, BorderLayout.EAST);

        return bottomPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(CONTENT_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    private void refreshQueries() {
        listModel.clear();
        List<Query> queries = queryDAO.getAllQueries();

        if (queries != null && !queries.isEmpty()) {
            String selectedSubject = (String) subjectComboBox.getSelectedItem();

            if (selectedSubject != null && !selectedSubject.equals("All")) {
                for (Query query : queries) {
                    if (query.getSubjectCode().equals(selectedSubject)) {
                        String queryDisplay = query.getSubjectCode() + ": " + query.getContent();
                        listModel.addElement(queryDisplay);
                    }
                }
            } else {
                for (Query query : queries) {
                    String queryDisplay = query.getSubjectCode() + ": " + query.getContent();
                    listModel.addElement(queryDisplay);
                }
            }
            if (listModel.isEmpty()) {
                listModel.addElement("No queries for this subject.");
            }
        } else {
            listModel.addElement("No queries available.");
        }
    }

    private void replyToQuery() {
        String selectedValue = queryList.getSelectedValue();
        if (selectedValue == null) {
            JOptionPane.showMessageDialog(this, "Please select a query to reply.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String subjectCode = selectedValue.split(":")[0].trim();
        String response = responseArea.getText().trim();

        if (response.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Response cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Query> allQueries = queryDAO.getAllQueries();
        for (Query query : allQueries) {
            if (query.getSubjectCode().equals(subjectCode) && query.getContent().equals(selectedValue.split(":")[1].trim())) {
                boolean success = queryDAO.updateQueryResponse(query.getId(), response);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Response submitted successfully!");
                    responseArea.setText("");
                    refreshQueries();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to submit response.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Selected query not found.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}