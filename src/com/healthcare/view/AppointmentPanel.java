package com.healthcare.view;

import com.healthcare.controller.HealthcareController;
import com.healthcare.model.Appointment;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class AppointmentPanel extends JPanel {
    private HealthcareController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField appointmentIDField, patientIDField, clinicianIDField, facilityIDField;
    private JTextField dateField, timeField, durationField, typeField, statusField, reasonField, notesField;
    private JTextField createdDateField, lastModifiedField;

    public AppointmentPanel(HealthcareController controller) {
        this.controller = controller;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Soft background for a cleaner "admin panel" appearance
        setBackground(new Color(245, 247, 250));
        String[] columns = {"Appointment ID", "Patient ID", "Clinician ID", "Facility ID", 
                          "Date", "Time", "Duration", "Type", "Status", "Reason", "Notes", "Created", "Last Modified"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(227, 242, 253));
        table.setSelectionForeground(Color.BLACK);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedAppointment();
        });
        // Style table header for a dashboard-like look
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setBackground(new Color(236, 239, 241));
        header.setForeground(new Color(55, 71, 79));
        header.setFont(header.getFont().deriveFont(Font.BOLD, 13f));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Appointments"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = createFormPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder("Appointment Details"));
        formPanel.setBackground(new Color(250, 250, 250));
        add(formPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = createButtonPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        buttonPanel.setBackground(new Color(245, 247, 250));
        add(buttonPanel, BorderLayout.NORTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addField(panel, gbc, row++, "Appointment ID:", appointmentIDField = new JTextField(15));
        addField(panel, gbc, row++, "Patient ID:", patientIDField = new JTextField(15));
        addField(panel, gbc, row++, "Clinician ID:", clinicianIDField = new JTextField(15));
        addField(panel, gbc, row++, "Facility ID:", facilityIDField = new JTextField(15));
        addField(panel, gbc, row++, "Date:", dateField = new JTextField(15));
        addField(panel, gbc, row++, "Time:", timeField = new JTextField(15));
        addField(panel, gbc, row++, "Duration (min):", durationField = new JTextField(15));
        addField(panel, gbc, row++, "Type:", typeField = new JTextField(15));
        addField(panel, gbc, row++, "Status:", statusField = new JTextField(15));
        addField(panel, gbc, row++, "Reason:", reasonField = new JTextField(15));
        addField(panel, gbc, row++, "Notes:", notesField = new JTextField(15));
        addField(panel, gbc, row++, "Created Date:", createdDateField = new JTextField(15));
        addField(panel, gbc, row++, "Last Modified:", lastModifiedField = new JTextField(15));

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        addButton(panel, "Add", e -> addAppointment());
        addButton(panel, "Update", e -> updateAppointment());
        addButton(panel, "Delete", e -> deleteAppointment());
        addButton(panel, "Clear", e -> clearForm());
        addButton(panel, "Refresh", e -> refreshData());
        return panel;
    }

    private void addButton(JPanel panel, String text, java.awt.event.ActionListener listener) {
        JButton button;
        if ("Delete".equals(text)) {
            button = createDangerButton(text);
        } else if ("Add".equals(text) || "Update".equals(text)) {
            button = createPrimaryButton(text);
        } else {
            button = createSecondaryButton(text);
        }
        button.addActionListener(listener);
        panel.add(button);
    }

    /**
     * Button styling helpers to match the overall dashboard theme
     */
    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, new Color(33, 150, 243), Color.WHITE);
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, new Color(236, 239, 241), new Color(55, 71, 79));
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, new Color(229, 57, 53), Color.WHITE);
        return button;
    }

    private void styleButton(JButton button, Color background, Color foreground) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(button.getFont().deriveFont(Font.BOLD, 12f));
    }

    private void addAppointment() {
        try {
            Appointment appointment = createAppointmentFromForm();
            if (appointment != null) {
                controller.addAppointment(appointment);
                refreshData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Appointment added!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAppointment() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment.");
            return;
        }
        try {
            String id = (String) tableModel.getValueAt(row, 0);
            controller.deleteAppointment(id);
            controller.addAppointment(createAppointmentFromForm());
            refreshData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Appointment updated!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAppointment() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete this appointment?", "Confirm", 
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.deleteAppointment((String) tableModel.getValueAt(row, 0));
            refreshData();
            clearForm();
        }
    }

    private Appointment createAppointmentFromForm() {
        if (appointmentIDField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Appointment ID required.");
            return null;
        }
        return new Appointment(appointmentIDField.getText().trim(), patientIDField.getText().trim(),
            clinicianIDField.getText().trim(), facilityIDField.getText().trim(),
            dateField.getText().trim(), timeField.getText().trim(), durationField.getText().trim(),
            typeField.getText().trim(), statusField.getText().trim(), reasonField.getText().trim(),
            notesField.getText().trim(), createdDateField.getText().trim(), lastModifiedField.getText().trim());
    }

    private void loadSelectedAppointment() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            appointmentIDField.setText((String) tableModel.getValueAt(row, 0));
            patientIDField.setText((String) tableModel.getValueAt(row, 1));
            clinicianIDField.setText((String) tableModel.getValueAt(row, 2));
            facilityIDField.setText((String) tableModel.getValueAt(row, 3));
            dateField.setText((String) tableModel.getValueAt(row, 4));
            timeField.setText((String) tableModel.getValueAt(row, 5));
            durationField.setText((String) tableModel.getValueAt(row, 6));
            typeField.setText((String) tableModel.getValueAt(row, 7));
            statusField.setText((String) tableModel.getValueAt(row, 8));
            reasonField.setText((String) tableModel.getValueAt(row, 9));
            notesField.setText((String) tableModel.getValueAt(row, 10));
            createdDateField.setText((String) tableModel.getValueAt(row, 11));
            lastModifiedField.setText((String) tableModel.getValueAt(row, 12));
        }
    }

    private void clearForm() {
        appointmentIDField.setText("");
        patientIDField.setText("");
        clinicianIDField.setText("");
        facilityIDField.setText("");
        dateField.setText("");
        timeField.setText("");
        durationField.setText("");
        typeField.setText("");
        statusField.setText("");
        reasonField.setText("");
        notesField.setText("");
        createdDateField.setText("");
        lastModifiedField.setText("");
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        for (Appointment a : controller.getAllAppointments()) {
            tableModel.addRow(new Object[]{a.getAppointmentID(), a.getPatientID(), a.getClinicianID(),
                a.getFacilityID(), a.getDate(), a.getTime(), a.getDurationMinutes(), a.getAppointmentType(),
                a.getStatus(), a.getReason(), a.getNotes(), a.getCreatedDate(), a.getLastModified()});
        }
    }
}



