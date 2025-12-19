package com.healthcare.view;

import com.healthcare.controller.HealthcareController;
import com.healthcare.model.Staff;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffPanel extends JPanel {
    private HealthcareController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField staffIDField, firstNameField, lastNameField, roleField, departmentField;
    private JTextField facilityIDField, emailField, phoneField, employmentStatusField;
    private JTextField startDateField, lineManagerField, accessLevelField;

    public StaffPanel(HealthcareController controller) {
        this.controller = controller;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        String[] columns = {"Staff ID", "First Name", "Last Name", "Role", "Department", "Facility ID", 
                          "Email", "Phone", "Employment Status", "Start Date", "Line Manager", "Access Level"};
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
            if (!e.getValueIsAdjusting()) loadSelectedStaff();
        });
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Staff"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = createFormPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder("Staff Details"));
        add(formPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = createButtonPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        add(buttonPanel, BorderLayout.NORTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addField(panel, gbc, row++, "Staff ID:", staffIDField = new JTextField(15));
        addField(panel, gbc, row++, "First Name:", firstNameField = new JTextField(15));
        addField(panel, gbc, row++, "Last Name:", lastNameField = new JTextField(15));
        addField(panel, gbc, row++, "Role:", roleField = new JTextField(15));
        addField(panel, gbc, row++, "Department:", departmentField = new JTextField(15));
        addField(panel, gbc, row++, "Facility ID:", facilityIDField = new JTextField(15));
        addField(panel, gbc, row++, "Email:", emailField = new JTextField(15));
        addField(panel, gbc, row++, "Phone:", phoneField = new JTextField(15));
        addField(panel, gbc, row++, "Employment Status:", employmentStatusField = new JTextField(15));
        addField(panel, gbc, row++, "Start Date:", startDateField = new JTextField(15));
        addField(panel, gbc, row++, "Line Manager:", lineManagerField = new JTextField(15));
        addField(panel, gbc, row++, "Access Level:", accessLevelField = new JTextField(15));

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
        JPanel panel = new JPanel(new FlowLayout());
        addButton(panel, "Add", e -> addStaff());
        addButton(panel, "Update", e -> updateStaff());
        addButton(panel, "Delete", e -> deleteStaff());
        addButton(panel, "Clear", e -> clearForm());
        addButton(panel, "Refresh", e -> refreshData());
        return panel;
    }

    private void addButton(JPanel panel, String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        panel.add(button);
    }

    private void addStaff() {
        try {
            Staff staff = createStaffFromForm();
            if (staff != null) {
                controller.addStaff(staff);
                refreshData();
                clearForm();
                JOptionPane.showMessageDialog(this, "Staff added!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStaff() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a staff member.");
            return;
        }
        try {
            String id = (String) tableModel.getValueAt(row, 0);
            controller.deleteStaff(id);
            controller.addStaff(createStaffFromForm());
            refreshData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Staff updated!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStaff() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a staff member.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete this staff member?", "Confirm", 
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.deleteStaff((String) tableModel.getValueAt(row, 0));
            refreshData();
            clearForm();
        }
    }

    private Staff createStaffFromForm() {
        if (staffIDField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Staff ID required.");
            return null;
        }
        return new Staff(staffIDField.getText().trim(), firstNameField.getText().trim(),
            lastNameField.getText().trim(), roleField.getText().trim(), departmentField.getText().trim(),
            facilityIDField.getText().trim(), emailField.getText().trim(), phoneField.getText().trim(),
            employmentStatusField.getText().trim(), startDateField.getText().trim(),
            lineManagerField.getText().trim(), accessLevelField.getText().trim());
    }

    private void loadSelectedStaff() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            staffIDField.setText((String) tableModel.getValueAt(row, 0));
            firstNameField.setText((String) tableModel.getValueAt(row, 1));
            lastNameField.setText((String) tableModel.getValueAt(row, 2));
            roleField.setText((String) tableModel.getValueAt(row, 3));
            departmentField.setText((String) tableModel.getValueAt(row, 4));
            facilityIDField.setText((String) tableModel.getValueAt(row, 5));
            emailField.setText((String) tableModel.getValueAt(row, 6));
            phoneField.setText((String) tableModel.getValueAt(row, 7));
            employmentStatusField.setText((String) tableModel.getValueAt(row, 8));
            startDateField.setText((String) tableModel.getValueAt(row, 9));
            lineManagerField.setText((String) tableModel.getValueAt(row, 10));
            accessLevelField.setText((String) tableModel.getValueAt(row, 11));
        }
    }

    private void clearForm() {
        staffIDField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        roleField.setText("");
        departmentField.setText("");
        facilityIDField.setText("");
        emailField.setText("");
        phoneField.setText("");
        employmentStatusField.setText("");
        startDateField.setText("");
        lineManagerField.setText("");
        accessLevelField.setText("");
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        for (Staff s : controller.getAllStaff()) {
            tableModel.addRow(new Object[]{s.getStaffID(), s.getFirstName(), s.getLastName(),
                s.getRole(), s.getDepartment(), s.getFacilityID(), s.getEmail(), s.getPhone(),
                s.getEmploymentStatus(), s.getStartDate(), s.getLineManager(), s.getAccessLevel()});
        }
    }
}



