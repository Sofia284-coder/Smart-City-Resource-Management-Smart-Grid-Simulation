package main.java.com.smartcity.gui;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import main.java.com.smartcity.models.*;
import main.java.com.smartcity.services.SmartGrid;
import main.java.com.smartcity.repositories.CityRepository;

public class SmartCityGUI extends JFrame {

    private JTabbedPane tabbedPane;
    private SmartGrid smartGrid;
    private CityRepository<CityResource> cityResourceRepository;
    private JComboBox<String> zoneComboBox;

    private Map<String, CityZone> cityZonesMap = new HashMap<>();
    // 
    private Map<String, SmartGrid> smartGridsMap = new HashMap<>();
    //

  
    private JTextArea allResourcesDisplayArea;
    private JTextArea energyDisplayArea;
    private JTextArea zoneResourcesDisplayArea;
    private JTextArea reportDisplayArea;
    private JTextArea alertDisplayArea; 

    // Table models for CRUD operations
    private DefaultTableModel busTableModel;
    private DefaultTableModel trainTableModel;
    private DefaultTableModel powerStationTableModel;
    private DefaultTableModel policeTableModel;
    private DefaultTableModel fireTableModel;
    private DefaultTableModel ambulanceTableModel;
    private DefaultTableModel householdConsumerTableModel;
    private DefaultTableModel industryConsumerTableModel;


public SmartCityGUI() {
    setTitle("Smart City Management System");
    setSize(1000, 700);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    smartGrid = new SmartGrid();
    cityResourceRepository = new CityRepository<>("city_resources.ser");

    // --- START: Initialize ALL GUI components (JTextAreas, DefaultTableModels, JTabbedPane) FIRST ---

    // Initialize display areas
    allResourcesDisplayArea = new JTextArea(15, 60);
    allResourcesDisplayArea.setEditable(false);
    energyDisplayArea = new JTextArea(5, 40);
    energyDisplayArea.setEditable(false);
    zoneResourcesDisplayArea = new JTextArea(20, 60);
    zoneResourcesDisplayArea.setEditable(false);
    reportDisplayArea = new JTextArea(20, 80);
    reportDisplayArea.setEditable(false);
    alertDisplayArea = new JTextArea(20, 80);
    alertDisplayArea.setForeground(Color.RED);
    alertDisplayArea.setEditable(false);

    // Initialize all DefaultTableModels
    String[] busColumnNames = {"ID", "Location", "Status", "Route", "Passengers"};
    busTableModel = new DefaultTableModel(busColumnNames, 0);

    String[] trainColumnNames = {"ID", "Location", "Status", "Route", "Coaches"};
    trainTableModel = new DefaultTableModel(trainColumnNames, 0);

    String[] powerStationColumnNames = {"ID", "Location", "Status", "Production Rate", "City Zone"};
    powerStationTableModel = new DefaultTableModel(powerStationColumnNames, 0);

    String[] policeColumnNames = {"ID", "Location", "Status", "Officers"};
    policeTableModel = new DefaultTableModel(policeColumnNames, 0);

    String[] fireColumnNames = {"ID", "Location", "Status", "Trucks"};
    fireTableModel = new DefaultTableModel(fireColumnNames, 0);

    String[] ambulanceColumnNames = {"ID", "Location", "Status", "Paramedics"};
    ambulanceTableModel = new DefaultTableModel(ambulanceColumnNames, 0);

    String[] householdConsumerColumnNames = {"ID", "Zone", "Consumption (kWh)"};
    householdConsumerTableModel = new DefaultTableModel(householdConsumerColumnNames, 0);

    String[] industryConsumerColumnNames = {"ID", "Zone", "Consumption (kWh)", "Industry Type"};
    industryConsumerTableModel = new DefaultTableModel(industryConsumerColumnNames, 0);


    
    tabbedPane = new JTabbedPane(); 
    tabbedPane.addTab("Resource Management", createResourceManagementPanel());
    tabbedPane.addTab("Smart Grid", createSmartGridPanel());
    tabbedPane.addTab("City Zones", createCityZonesPanel());
    tabbedPane.addTab("Reports", createReportsPanel());
    tabbedPane.addTab("Alerts", createAlertsPanel());

    

    
    add(tabbedPane, BorderLayout.CENTER); 

    
    loadAllData(); 


    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenuItem saveMenuItem = new JMenuItem("Save All Data");
    saveMenuItem.addActionListener(e -> saveAllData());
    JMenuItem loadMenuItem = new JMenuItem("Load All Data");
    loadMenuItem.addActionListener(e -> loadAllData());
    fileMenu.add(saveMenuItem);
    fileMenu.add(loadMenuItem);
    menuBar.add(fileMenu);
    setJMenuBar(menuBar);

    
}

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SmartCityGUI().setVisible(true);
        });
    }


    

    private JPanel createResourceManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTabbedPane resourceSubTabbedPane = new JTabbedPane();

        // 1. Bus Management Panel
        resourceSubTabbedPane.addTab("Buses", createBusCrudPanel());
        // 2. Train Management Panel
        resourceSubTabbedPane.addTab("Trains", createTrainCrudPanel());
        // 3. Power Station Management Panel
        resourceSubTabbedPane.addTab("Power Stations", createPowerStationCrudPanel());
        // 4. Emergency Services Management Panel (Sub-sub-tabbed pane)
        JTabbedPane emergencyServiceSubSubTabbedPane = new JTabbedPane();
        emergencyServiceSubSubTabbedPane.addTab("Police", createPoliceCrudPanel());
        emergencyServiceSubSubTabbedPane.addTab("Fire Department", createFireDepartmentCrudPanel());
        emergencyServiceSubSubTabbedPane.addTab("Ambulance", createAmbulanceCrudPanel());
        resourceSubTabbedPane.addTab("Emergency Services", emergencyServiceSubSubTabbedPane);


        panel.add(resourceSubTabbedPane, BorderLayout.CENTER);

        // Overall display for all resources on the right
        JPanel overallDisplayPanel = new JPanel(new BorderLayout());
        JLabel allResourcesLabel = new JLabel("All City Resources:");
        overallDisplayPanel.add(allResourcesLabel, BorderLayout.NORTH);
        overallDisplayPanel.add(new JScrollPane(allResourcesDisplayArea), BorderLayout.CENTER);
        panel.add(overallDisplayPanel, BorderLayout.EAST); // Place overall display on the right

        return panel;
    }

    // --- CRUD Panel Factories (unchanged logic, only map calls removed) ---

    private JPanel createBusCrudPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); // Add gaps
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField idField = new JTextField(10);
        JTextField locationField = new JTextField(10);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Operational", "Maintenance"});
        JTextField routeField = new JTextField(10);
        JTextField passengersField = new JTextField(10);

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        formPanel.add(new JLabel("Route:"));
        formPanel.add(routeField);
        formPanel.add(new JLabel("Passengers:"));
        formPanel.add(passengersField);

        panel.add(formPanel, BorderLayout.NORTH);

        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add Bus");
        JButton updateButton = new JButton("Update Bus");
        JButton deleteButton = new JButton("Delete Bus");
        JButton clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Table display
        JTable busTable = new JTable(this.busTableModel);
        JScrollPane scrollPane = new JScrollPane(busTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- Action Listeners ---
        addButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String location = locationField.getText().trim();
                String status = (String) statusComboBox.getSelectedItem();
                String route = routeField.getText().trim();
                int passengers = Integer.parseInt(passengersField.getText().trim());

                if (id.isEmpty() || location.isEmpty() || route.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (cityResourceRepository.getResourceById(id) != null) {
                    JOptionPane.showMessageDialog(this, "Resource with this ID already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Bus newBus = new Bus(id, location, status, route, passengers);
                cityResourceRepository.addResource(newBus);
                updateAllDisplays();
                clearFields(idField, locationField, routeField, passengersField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for passengers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String location = locationField.getText().trim();
                String status = (String) statusComboBox.getSelectedItem();
                String route = routeField.getText().trim();
                int passengers = Integer.parseInt(passengersField.getText().trim());

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter an ID to update.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Bus existingBus = (Bus) cityResourceRepository.getResourceById(id);
                if (existingBus == null || !(existingBus instanceof Bus)) {
                    JOptionPane.showMessageDialog(this, "Bus with ID " + id + " not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update properties
                existingBus.setLocation(location);
                existingBus.setStatus(status);
                existingBus.setRoute(route);
                existingBus.setNumberOfPassengers(passengers); // Add setter to Bus class if not present

                cityResourceRepository.updateResource(existingBus);
                updateAllDisplays();
                clearFields(idField, locationField, routeField, passengersField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for passengers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an ID to delete.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cityResourceRepository.removeResourceById(id)) {
                updateAllDisplays();
                clearFields(idField, locationField, routeField, passengersField);
            } else {
                JOptionPane.showMessageDialog(this, "Bus with ID " + id + " not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearFields(idField, locationField, routeField, passengersField));

        // Table selection listener to populate fields for update/delete
        busTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && busTable.getSelectedRow() != -1) {
                int selectedRow = busTable.getSelectedRow();
                idField.setText((String) busTableModel.getValueAt(selectedRow, 0));
                locationField.setText((String) busTableModel.getValueAt(selectedRow, 1));
                statusComboBox.setSelectedItem((String) busTableModel.getValueAt(selectedRow, 2));
                routeField.setText((String) busTableModel.getValueAt(selectedRow, 3));
                passengersField.setText(String.valueOf(busTableModel.getValueAt(selectedRow, 4)));
            }
        });

        return panel;
    }

    private JPanel createTrainCrudPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField idField = new JTextField(10);
        JTextField locationField = new JTextField(10);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Operational", "Maintenance"});
        JTextField routeField = new JTextField(10);
        JTextField coachesField = new JTextField(10);

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        formPanel.add(new JLabel("Route:"));
        formPanel.add(routeField);
        formPanel.add(new JLabel("Coaches:"));
        formPanel.add(coachesField);

        panel.add(formPanel, BorderLayout.NORTH);

        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add Train");
        JButton updateButton = new JButton("Update Train");
        JButton deleteButton = new JButton("Delete Train");
        JButton clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Table display
        
        JTable trainTable = new JTable(this.trainTableModel);
        JScrollPane scrollPane = new JScrollPane(trainTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action Listeners (Similar logic to Bus CRUD)
        addButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String location = locationField.getText().trim();
                String status = (String) statusComboBox.getSelectedItem();
                String route = routeField.getText().trim();
                int coaches = Integer.parseInt(coachesField.getText().trim());

                if (id.isEmpty() || location.isEmpty() || route.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                 if (cityResourceRepository.getResourceById(id) != null) {
                    JOptionPane.showMessageDialog(this, "Resource with this ID already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Train newTrain = new Train(id, location, status, route, coaches);
                cityResourceRepository.addResource(newTrain);
                updateAllDisplays();
                clearFields(idField, locationField, routeField, coachesField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for coaches.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
             try {
                String id = idField.getText().trim();
                String location = locationField.getText().trim();
                String status = (String) statusComboBox.getSelectedItem();
                String route = routeField.getText().trim();
                int coaches = Integer.parseInt(coachesField.getText().trim());

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter an ID to update.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Train existingTrain = (Train) cityResourceRepository.getResourceById(id);
                if (existingTrain == null || !(existingTrain instanceof Train)) {
                    JOptionPane.showMessageDialog(this, "Train with ID " + id + " not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                existingTrain.setLocation(location);
                existingTrain.setStatus(status);
                existingTrain.setRoute(route);
                existingTrain.setNumberOfCoaches(coaches); // Add setter to Train class if not present

                cityResourceRepository.updateResource(existingTrain);
                updateAllDisplays();
                clearFields(idField, locationField, routeField, coachesField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for coaches.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an ID to delete.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cityResourceRepository.removeResourceById(id)) {
                updateAllDisplays();
                clearFields(idField, locationField, routeField, coachesField);
            } else {
                JOptionPane.showMessageDialog(this, "Train with ID " + id + " not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearFields(idField, locationField, routeField, coachesField));

        trainTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && trainTable.getSelectedRow() != -1) {
                int selectedRow = trainTable.getSelectedRow();
                idField.setText((String) trainTableModel.getValueAt(selectedRow, 0));
                locationField.setText((String) trainTableModel.getValueAt(selectedRow, 1));
                statusComboBox.setSelectedItem((String) trainTableModel.getValueAt(selectedRow, 2));
                routeField.setText((String) trainTableModel.getValueAt(selectedRow, 3));
                coachesField.setText(String.valueOf(trainTableModel.getValueAt(selectedRow, 4)));
            }
        });

        return panel;
    }

    private JPanel createPowerStationCrudPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        zoneComboBox = new JComboBox<>();

        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField idField = new JTextField(10);
        JTextField locationField = new JTextField(10);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Operational", "Maintenance", "Outage"});
        JTextField productionField = new JTextField(10);

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        formPanel.add(new JLabel("Production Rate (kWh):"));
        formPanel.add(productionField);
        formPanel.add(new JLabel("City Zone:"));
        formPanel.add(zoneComboBox);

        panel.add(formPanel, BorderLayout.NORTH);

        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add Power Station");
        JButton updateButton = new JButton("Update Power Station");
        JButton deleteButton = new JButton("Delete Power Station");
        JButton triggerOutageButton = new JButton("Trigger Outage");
        JButton clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(triggerOutageButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Table display
        JTable powerStationTable = new JTable(this.powerStationTableModel);
        JScrollPane scrollPane = new JScrollPane(powerStationTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Update zone combo box whenever this tab is selected or relevant event happens
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == panel) {
                updateZoneComboBox(zoneComboBox);
            }
        });
        // Also update on initial load
        updateZoneComboBox(zoneComboBox);


        // Action Listeners
        addButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String location = locationField.getText().trim();
                String status = (String) statusComboBox.getSelectedItem();
                double production = Double.parseDouble(productionField.getText().trim());
                String zoneName = (String) zoneComboBox.getSelectedItem();

                if (id.isEmpty() || location.isEmpty() || zoneName == null) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (cityResourceRepository.getResourceById(id) != null) {
                    JOptionPane.showMessageDialog(this, "Resource with this ID already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                CityZone zone = findOrCreateCityZone(zoneName); // Use findOrCreateCityZone
                PowerStation newPs = new PowerStation(id, location, status, production, zone);
                cityResourceRepository.addResource(newPs);
                smartGrid.addPowerStation(newPs);
                updateAllDisplays();
                clearFields(idField, locationField, productionField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for production rate.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String location = locationField.getText().trim();
                String status = (String) statusComboBox.getSelectedItem();
                double production = Double.parseDouble(productionField.getText().trim());
                String zoneName = (String) zoneComboBox.getSelectedItem();

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter an ID to update.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PowerStation existingPs = (PowerStation) cityResourceRepository.getResourceById(id);
                if (existingPs == null || !(existingPs instanceof PowerStation)) {
                    JOptionPane.showMessageDialog(this, "Power Station with ID " + id + " not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update properties
                existingPs.setLocation(location);
                existingPs.setStatus(status);
                existingPs.setProductionRate(production);
                existingPs.setCityZone(findOrCreateCityZone(zoneName)); // Update zone reference

                cityResourceRepository.updateResource(existingPs); // Update in repository
                smartGrid.removePowerStation(id); // Remove old one from grid (to update production rate)
                smartGrid.addPowerStation(existingPs); // Add updated one back
                updateAllDisplays();
                clearFields(idField, locationField, productionField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for production rate.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an ID to delete.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cityResourceRepository.removeResourceById(id)) {
                smartGrid.removePowerStation(id); // Also remove from smart grid
                updateAllDisplays();
                clearFields(idField, locationField, productionField);
            } else {
                JOptionPane.showMessageDialog(this, "Power Station with ID " + id + " not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        triggerOutageButton.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Power Station ID to trigger outage.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            PowerStation ps = (PowerStation) cityResourceRepository.getResourceById(id);
            if (ps != null && ps instanceof PowerStation) {
                ps.triggerOutage(); // This will print to console and call alertEmergencyServices on zone
                // Append to GUI alert area
                alertDisplayArea.append(ps.sendEmergencyAlert() + "\n");
                if (ps.getCityZone() != null) {
                    alertDisplayArea.append("Triggering emergency services in zone: " + ps.getCityZone().getZoneName() + "\n");
                    for (EmergencyService es : ps.getCityZone().getResourceHub().getEmergencyServices()) {
                        alertDisplayArea.append("   - " + es.sendEmergencyAlert() + "\n");
                    }
                }
                updateAllDisplays(); // Update map and table for status change
            } else {
                JOptionPane.showMessageDialog(this, "Power Station with ID " + id + " not found.", "Outage Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearFields(idField, locationField, productionField));

        powerStationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && powerStationTable.getSelectedRow() != -1) {
                int selectedRow = powerStationTable.getSelectedRow();
                idField.setText((String) powerStationTableModel.getValueAt(selectedRow, 0));
                locationField.setText((String) powerStationTableModel.getValueAt(selectedRow, 1));
                statusComboBox.setSelectedItem((String) powerStationTableModel.getValueAt(selectedRow, 2));
                productionField.setText(String.valueOf(powerStationTableModel.getValueAt(selectedRow, 3)));
                zoneComboBox.setSelectedItem((String) powerStationTableModel.getValueAt(selectedRow, 4));
            }
        });

        return panel;
    }

    private JPanel createPoliceCrudPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField idField = new JTextField(10);
        JTextField locationField = new JTextField(10);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Operational", "Responding", "Idle"});
        JTextField officersField = new JTextField(10);

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        formPanel.add(new JLabel("Officers:"));
        formPanel.add(officersField);

        panel.add(formPanel, BorderLayout.NORTH);

        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add Police");
        JButton updateButton = new JButton("Update Police");
        JButton deleteButton = new JButton("Delete Police");
        JButton clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Table display
        JTable policeTable = new JTable(this.policeTableModel);
        JScrollPane scrollPane = new JScrollPane(policeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action Listeners (Similar logic to Bus CRUD)
        addButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String location = locationField.getText().trim();
                String status = (String) statusComboBox.getSelectedItem();
                int officers = Integer.parseInt(officersField.getText().trim());

                if (id.isEmpty() || location.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (cityResourceRepository.getResourceById(id) != null) {
                    JOptionPane.showMessageDialog(this, "Resource with this ID already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Police newPolice = new Police(id, location, status, officers);
                cityResourceRepository.addResource(newPolice);
                // Assign to zone's emergency services if a zone is specified (not handled here yet)
                // For now, let's just add to a default zone or handle this in CityZone panel
                updateAllDisplays();
                clearFields(idField, locationField, officersField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for officers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String location = locationField.getText().trim();
                String status = (String) statusComboBox.getSelectedItem();
                int officers = Integer.parseInt(officersField.getText().trim());

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter an ID to update.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Police existingPolice = (Police) cityResourceRepository.getResourceById(id);
                if (existingPolice == null || !(existingPolice instanceof Police)) {
                    JOptionPane.showMessageDialog(this, "Police unit with ID " + id + " not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                existingPolice.setLocation(location);
                existingPolice.setStatus(status);
                existingPolice.setNumberOfOfficers(officers); // Add setter to Police class if not present

                cityResourceRepository.updateResource(existingPolice);
                updateAllDisplays();
                clearFields(idField, locationField, officersField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for officers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an ID to delete.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cityResourceRepository.removeResourceById(id)) {
                updateAllDisplays();
                clearFields(idField, locationField, officersField);
            } else {
                JOptionPane.showMessageDialog(this, "Police unit with ID " + id + " not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearFields(idField, locationField, officersField));

        policeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && policeTable.getSelectedRow() != -1) {
                int selectedRow = policeTable.getSelectedRow();
                idField.setText((String) policeTableModel.getValueAt(selectedRow, 0));
                locationField.setText((String) policeTableModel.getValueAt(selectedRow, 1));
                statusComboBox.setSelectedItem((String) policeTableModel.getValueAt(selectedRow, 2));
                officersField.setText(String.valueOf(policeTableModel.getValueAt(selectedRow, 3)));
            }
        });
        return panel;
    }

    private JPanel createFireDepartmentCrudPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField idField = new JTextField(10);
        JTextField locationField = new JTextField(10);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Operational", "Responding", "Idle"});
        JTextField trucksField = new JTextField(10);

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        formPanel.add(new JLabel("Fire Trucks:"));
        formPanel.add(trucksField);

        panel.add(formPanel, BorderLayout.NORTH);

        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add Fire Dept.");
        JButton updateButton = new JButton("Update Fire Dept.");
        JButton deleteButton = new JButton("Delete Fire Dept.");
        JButton clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Table display

        JTable fireTable = new JTable(this.fireTableModel);
        JScrollPane scrollPane = new JScrollPane(fireTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action Listeners
        addButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String location = locationField.getText().trim();
                String status = (String) statusComboBox.getSelectedItem();
                int trucks = Integer.parseInt(trucksField.getText().trim());

                if (id.isEmpty() || location.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (cityResourceRepository.getResourceById(id) != null) {
                    JOptionPane.showMessageDialog(this, "Resource with this ID already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                FireDepartment newFire = new FireDepartment(id, location, status, trucks);
                cityResourceRepository.addResource(newFire);
                updateAllDisplays();
                clearFields(idField, locationField, trucksField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for fire trucks.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String location = locationField.getText().trim();
                String status = (String) statusComboBox.getSelectedItem();
                int trucks = Integer.parseInt(trucksField.getText().trim());

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter an ID to update.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                FireDepartment existingFire = (FireDepartment) cityResourceRepository.getResourceById(id);
                if (existingFire == null || !(existingFire instanceof FireDepartment)) {
                    JOptionPane.showMessageDialog(this, "Fire Department with ID " + id + " not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                existingFire.setLocation(location);
                existingFire.setStatus(status);
                existingFire.setNumberOfFireTrucks(trucks); // Add setter to FireDepartment class if not present

                cityResourceRepository.updateResource(existingFire);
                updateAllDisplays();
                clearFields(idField, locationField, trucksField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for fire trucks.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an ID to delete.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cityResourceRepository.removeResourceById(id)) {
                updateAllDisplays();
                clearFields(idField, locationField, trucksField);
            } else {
                JOptionPane.showMessageDialog(this, "Fire Department with ID " + id + " not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearFields(idField, locationField, trucksField));

        fireTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && fireTable.getSelectedRow() != -1) {
                int selectedRow = fireTable.getSelectedRow();
                idField.setText((String) fireTableModel.getValueAt(selectedRow, 0));
                locationField.setText((String) fireTableModel.getValueAt(selectedRow, 1));
                statusComboBox.setSelectedItem((String) fireTableModel.getValueAt(selectedRow, 2));
                trucksField.setText(String.valueOf(fireTableModel.getValueAt(selectedRow, 3)));
            }
        });
        return panel;
    }

    private JPanel createAmbulanceCrudPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField idField = new JTextField(10);
        JTextField locationField = new JTextField(10);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Operational", "Responding", "Idle"});
        JTextField medicsField = new JTextField(10);

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        formPanel.add(new JLabel("Medics:"));
        formPanel.add(medicsField);

        panel.add(formPanel, BorderLayout.NORTH);

        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add Ambulance");
        JButton updateButton = new JButton("Update Ambulance");
        JButton deleteButton = new JButton("Delete Ambulance");
        JButton clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Table display

        JTable ambulanceTable = new JTable(ambulanceTableModel);
        JScrollPane scrollPane = new JScrollPane(ambulanceTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action Listeners
        addButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String location = locationField.getText().trim();
                String status = (String) statusComboBox.getSelectedItem();
                int medics = Integer.parseInt(medicsField.getText().trim());

                if (id.isEmpty() || location.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (cityResourceRepository.getResourceById(id) != null) {
                    JOptionPane.showMessageDialog(this, "Resource with this ID already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Ambulance newAmbulance = new Ambulance(id, location, status, medics);
                cityResourceRepository.addResource(newAmbulance);
                updateAllDisplays();
                clearFields(idField, locationField, medicsField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for medics.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                String location = locationField.getText().trim();
                String status = (String) statusComboBox.getSelectedItem();
                int medics = Integer.parseInt(medicsField.getText().trim());

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter an ID to update.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Ambulance existingAmbulance = (Ambulance) cityResourceRepository.getResourceById(id);
                if (existingAmbulance == null || !(existingAmbulance instanceof Ambulance)) {
                    JOptionPane.showMessageDialog(this, "Ambulance with ID " + id + " not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                existingAmbulance.setLocation(location);
                existingAmbulance.setStatus(status);
                existingAmbulance.setNumberOfMedics(medics); // Add setter to Ambulance class if not present

                cityResourceRepository.updateResource(existingAmbulance);
                updateAllDisplays();
                clearFields(idField, locationField, medicsField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for medics.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an ID to delete.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cityResourceRepository.removeResourceById(id)) {
                updateAllDisplays();
                clearFields(idField, locationField, medicsField);
            } else {
                JOptionPane.showMessageDialog(this, "Ambulance with ID " + id + " not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearFields(idField, locationField, medicsField));

        ambulanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && ambulanceTable.getSelectedRow() != -1) {
                int selectedRow = ambulanceTable.getSelectedRow();
                idField.setText((String) ambulanceTableModel.getValueAt(selectedRow, 0));
                locationField.setText((String) ambulanceTableModel.getValueAt(selectedRow, 1));
                statusComboBox.setSelectedItem((String) ambulanceTableModel.getValueAt(selectedRow, 2));
                medicsField.setText(String.valueOf(ambulanceTableModel.getValueAt(selectedRow, 3)));
            }
        });
        return panel;
    }


    private JPanel createSmartGridPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top display for energy status
        panel.add(new JScrollPane(energyDisplayArea), BorderLayout.NORTH);

        // Consumers management (can be sub-tabs or combined)
        JTabbedPane consumerSubTabbedPane = new JTabbedPane();
        consumerSubTabbedPane.addTab("Household Consumers", createHouseholdConsumerCrudPanel());
        consumerSubTabbedPane.addTab("Industry Consumers", createIndustryConsumerCrudPanel());
        panel.add(consumerSubTabbedPane, BorderLayout.CENTER);


        // Buttons for Smart Grid actions
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton refreshButton = new JButton("Refresh Status");
        refreshButton.addActionListener(e -> updateSmartGridDisplay());
        controlPanel.add(refreshButton);

        JButton balanceGridButton = new JButton("Balance Grid");
        balanceGridButton.addActionListener(e -> {
            smartGrid.balanceGrid();
            updateSmartGridDisplay();
            JOptionPane.showMessageDialog(this, "Grid balancing initiated.", "Smart Grid", JOptionPane.INFORMATION_MESSAGE);
        });
        controlPanel.add(balanceGridButton);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createHouseholdConsumerCrudPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField idField = new JTextField(10);
        JTextField consumptionField = new JTextField(10);
        JTextField familySizeField = new JTextField(10);

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Consumption Rate (kWh):"));
        formPanel.add(consumptionField);
        formPanel.add(new JLabel("Family Size:"));
        formPanel.add(familySizeField);

        panel.add(formPanel, BorderLayout.NORTH);

        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add Household");
        JButton updateButton = new JButton("Update Household");
        JButton deleteButton = new JButton("Delete Household");
        JButton clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Table display
        String[] columnNames = {"ID", "Consumption", "Family Size"};
        householdConsumerTableModel = new DefaultTableModel(columnNames, 0);
        JTable householdConsumerTable = new JTable(householdConsumerTableModel);
        JScrollPane scrollPane = new JScrollPane(householdConsumerTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action Listeners
        addButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                double consumption = Double.parseDouble(consumptionField.getText().trim());
                int familySize = Integer.parseInt(familySizeField.getText().trim());

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (smartGrid.getConsumer(id) != null) {
                    JOptionPane.showMessageDialog(this, "Consumer with this ID already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                smartGrid.addConsumer(new HouseholdConsumer(id, consumption, familySize));
                updateAllDisplays();
                clearFields(idField, consumptionField, familySizeField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for consumption or family size.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                double consumption = Double.parseDouble(consumptionField.getText().trim());
                int familySize = Integer.parseInt(familySizeField.getText().trim());

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter an ID to update.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Consumer existingConsumer = smartGrid.getConsumer(id);
                if (existingConsumer == null || !(existingConsumer instanceof HouseholdConsumer)) {
                    JOptionPane.showMessageDialog(this, "Household consumer with ID " + id + " not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Need to remove and re-add to update consumption in smartGrid correctly
                smartGrid.removeConsumer(id);
                smartGrid.addConsumer(new HouseholdConsumer(id, consumption, familySize));
                updateAllDisplays();
                clearFields(idField, consumptionField, familySizeField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for consumption or family size.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an ID to delete.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (smartGrid.removeConsumer(id)) {
                updateAllDisplays();
                clearFields(idField, consumptionField, familySizeField);
            } else {
                JOptionPane.showMessageDialog(this, "Household consumer with ID " + id + " not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearFields(idField, consumptionField, familySizeField));

        householdConsumerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && householdConsumerTable.getSelectedRow() != -1) {
                int selectedRow = householdConsumerTable.getSelectedRow();
                idField.setText((String) householdConsumerTableModel.getValueAt(selectedRow, 0));
                consumptionField.setText(String.valueOf(householdConsumerTableModel.getValueAt(selectedRow, 1)));
                familySizeField.setText(String.valueOf(householdConsumerTableModel.getValueAt(selectedRow, 2)));
            }
        });

        return panel;
    }

    private JPanel createIndustryConsumerCrudPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField idField = new JTextField(10);
        JTextField consumptionField = new JTextField(10);
        JTextField industryTypeField = new JTextField(10);

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Consumption Rate (kWh):"));
        formPanel.add(consumptionField);
        formPanel.add(new JLabel("Industry Type:"));
        formPanel.add(industryTypeField);

        panel.add(formPanel, BorderLayout.NORTH);

        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add Industry");
        JButton updateButton = new JButton("Update Industry");
        JButton deleteButton = new JButton("Delete Industry");
        JButton clearButton = new JButton("Clear Fields");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Table display
        String[] columnNames = {"ID", "Consumption", "Industry Type"};
        industryConsumerTableModel = new DefaultTableModel(columnNames, 0);
        JTable industryConsumerTable = new JTable(industryConsumerTableModel);
        JScrollPane scrollPane = new JScrollPane(industryConsumerTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action Listeners
        addButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                double consumption = Double.parseDouble(consumptionField.getText().trim());
                String industryType = industryTypeField.getText().trim();

                if (id.isEmpty() || industryType.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (smartGrid.getConsumer(id) != null) {
                    JOptionPane.showMessageDialog(this, "Consumer with this ID already exists.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                smartGrid.addConsumer(new IndustryConsumer(id, consumption, industryType));
                updateAllDisplays();
                clearFields(idField, consumptionField, industryTypeField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for consumption.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            try {
                String id = idField.getText().trim();
                double consumption = Double.parseDouble(consumptionField.getText().trim());
                String industryType = industryTypeField.getText().trim();

                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter an ID to update.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Consumer existingConsumer = smartGrid.getConsumer(id);
                if (existingConsumer == null || !(existingConsumer instanceof IndustryConsumer)) {
                    JOptionPane.showMessageDialog(this, "Industry consumer with ID " + id + " not found.", "Update Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                smartGrid.removeConsumer(id);
                smartGrid.addConsumer(new IndustryConsumer(id, consumption, industryType));
                updateAllDisplays();
                clearFields(idField, consumptionField, industryTypeField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number for consumption.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an ID to delete.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (smartGrid.removeConsumer(id)) {
                updateAllDisplays();
                clearFields(idField, consumptionField, industryTypeField);
            } else {
                JOptionPane.showMessageDialog(this, "Industry consumer with ID " + id + " not found.", "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> clearFields(idField, consumptionField, industryTypeField));

        industryConsumerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && industryConsumerTable.getSelectedRow() != -1) {
                int selectedRow = industryConsumerTable.getSelectedRow();
                idField.setText((String) industryConsumerTableModel.getValueAt(selectedRow, 0));
                consumptionField.setText(String.valueOf(industryConsumerTableModel.getValueAt(selectedRow, 1)));
                industryTypeField.setText(String.valueOf(industryConsumerTableModel.getValueAt(selectedRow, 2)));
            }
        });

        return panel;
    }


    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }


    private JPanel createCityZonesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left: Zone List and controls
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        JLabel zoneListLabel = new JLabel("City Zones:");
        DefaultListModel<String> zoneListModel = new DefaultListModel<>();
        JList<String> zoneJList = new JList<>(zoneListModel);
        JScrollPane zoneScrollPane = new JScrollPane(zoneJList);

        leftPanel.add(zoneListLabel, BorderLayout.NORTH);
        leftPanel.add(zoneScrollPane, BorderLayout.CENTER);

        JPanel zoneControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton addZoneButton = new JButton("Add Zone");
        JButton removeZoneButton = new JButton("Remove Zone");
        zoneControlPanel.add(addZoneButton);
        zoneControlPanel.add(removeZoneButton);
        leftPanel.add(zoneControlPanel, BorderLayout.SOUTH);

        panel.add(leftPanel, BorderLayout.WEST);

        // Center: Resources in selected zone
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        JLabel zoneResourcesLabel = new JLabel("Resources in Selected Zone:");
        centerPanel.add(zoneResourcesLabel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(zoneResourcesDisplayArea), BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // --- Action Listeners ---
        addZoneButton.addActionListener(e -> {
            String zoneName = JOptionPane.showInputDialog(this, "Enter New City Zone Name:");
            if (zoneName != null && !zoneName.trim().isEmpty()) {
                if (cityZonesMap.containsKey(zoneName)) {
                    JOptionPane.showMessageDialog(this, "Zone '" + zoneName + "' already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                CityZone newZone = new CityZone(zoneName);
                cityZonesMap.put(zoneName, newZone);
                updateAllDisplays();
            }
        });

        removeZoneButton.addActionListener(e -> {
            String selectedZoneName = zoneJList.getSelectedValue();
            if (selectedZoneName == null) {
                JOptionPane.showMessageDialog(this, "Please select a zone to remove.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Check if any power stations are still assigned to this zone
            boolean hasPowerStations = false;
            for(CityResource res : cityResourceRepository.getAllResources()) {
                if (res instanceof PowerStation) {
                    PowerStation ps = (PowerStation) res;
                    if (ps.getCityZone() != null && ps.getCityZone().getZoneName().equals(selectedZoneName)) {
                        hasPowerStations = true;
                        break;
                    }
                }
            }
            if (hasPowerStations) {
                JOptionPane.showMessageDialog(this, "Cannot remove zone: Power stations are still assigned to it. Reassign or remove them first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove zone '" + selectedZoneName + "'?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                cityZonesMap.remove(selectedZoneName);
                updateAllDisplays();
            }
        });


        zoneJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedZoneName = zoneJList.getSelectedValue();
                if (selectedZoneName != null) {
                    CityZone selectedZone = cityZonesMap.get(selectedZoneName);
                    if (selectedZone != null) {
                        StringBuilder sb = new StringBuilder("Resources in Zone: " + selectedZone.getZoneName() + "\n\n");

                        // Populate ResourceHub dynamically for display
                        ResourceHub tempHub = new ResourceHub();
                        for (CityResource res : cityResourceRepository.getAllResources()) {
                            // This part assumes location implicitly relates to zone name for non-PowerStation resources
                            // For PowerStations, use their explicit cityZone reference
                            if (res instanceof PowerStation) {
                                if (((PowerStation) res).getCityZone() != null && ((PowerStation) res).getCityZone().getZoneName().equals(selectedZoneName)) {
                                     tempHub.addPowerStation((PowerStation) res);
                                }
                            } else if (res.getLocation().equalsIgnoreCase(selectedZoneName)) {
                                if (res instanceof TransportUnit) {
                                    tempHub.addTransportUnit((TransportUnit) res);
                                } else if (res instanceof EmergencyService) {
                                    tempHub.addEmergencyService((EmergencyService) res);
                                }
                            }
                        }

                        sb.append("--- Transport Units ---\n");
                        if (tempHub.getTransportUnits().isEmpty()) { sb.append("No transport units.\n"); }
                        for (TransportUnit t : tempHub.getTransportUnits()) { sb.append(t.toString()).append("\n"); }

                        sb.append("\n--- Power Stations ---\n");
                        if (tempHub.getPowerStations().isEmpty()) { sb.append("No power stations.\n"); }
                        for (PowerStation ps : tempHub.getPowerStations()) { sb.append(ps.toString()).append("\n"); }

                        sb.append("\n--- Emergency Services ---\n");
                        if (tempHub.getEmergencyServices().isEmpty()) { sb.append("No emergency services.\n"); }
                        for (EmergencyService es : tempHub.getEmergencyServices()) { sb.append(es.toString()).append("\n"); }

                        zoneResourcesDisplayArea.setText(sb.toString());
                    }
                } else {
                    zoneResourcesDisplayArea.setText("Select a zone to view its resources.");
                }
            }
        });

        // Initialize zone list
        updateZoneList((DefaultListModel<String>) zoneJList.getModel());

        return panel;
    }

    private void updateZoneComboBox(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        for (String zoneName : cityZonesMap.keySet()) {
            comboBox.addItem(zoneName);
        }
        if (comboBox.getItemCount() > 0) {
            comboBox.setSelectedIndex(0);
        }
    }

    // Helper method to get or create a CityZone.
    // Important: When creating a new zone here, it won't have initial resources.
    // Resources are linked via their `location` property (for most) or `cityZone` property (for PowerStation).
    private CityZone findOrCreateCityZone(String zoneName) {
        return cityZonesMap.computeIfAbsent(zoneName, k -> {
            CityZone newZone = new CityZone(zoneName);
            // Optionally, add to a display list immediately or ensure updateAllDisplays handles it
            return newZone;
        });
    }

    private void updateZoneList(DefaultListModel<String> model) {
        model.clear();
        for (String zoneName : cityZonesMap.keySet()) {
            model.addElement(zoneName);
        }
    }


    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JScrollPane(reportDisplayArea), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton generateAllReportsButton = new JButton("Generate All Reports");
        generateAllReportsButton.addActionListener(e -> generateAllReports());
        controlPanel.add(generateAllReportsButton);

        JButton generateMaintenanceReportButton = new JButton("Generate Maintenance Cost Report");
        generateMaintenanceReportButton.addActionListener(e -> generateMaintenanceReport());
        controlPanel.add(generateMaintenanceReportButton);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void generateAllReports() {
        StringBuilder sb = new StringBuilder("--- All Usage Reports ---\n\n");

        sb.append("--- Transport Unit Reports ---\n");
        for (CityResource res : cityResourceRepository.getAllResources()) {
            if (res instanceof TransportUnit) {
                sb.append(((TransportUnit) res).generateUsageReport()).append("\n");
            }
        }
        sb.append("\n--- Power Station Reports ---\n");
        for (CityResource res : cityResourceRepository.getAllResources()) {
            if (res instanceof PowerStation) {
                sb.append(((PowerStation) res).generateUsageReport()).append("\n");
            }
        }
        sb.append("\n--- Emergency Service Reports ---\n");
        for (CityResource res : cityResourceRepository.getAllResources()) {
            if (res instanceof EmergencyService) {
                sb.append(((EmergencyService) res).generateUsageReport()).append("\n");
            }
        }
        reportDisplayArea.setText(sb.toString());
    }

    private void generateMaintenanceReport() {
        StringBuilder sb = new StringBuilder("--- Maintenance Cost Report ---\n\n");
        double totalMaintenanceCost = 0;

        for (CityResource res : cityResourceRepository.getAllResources()) {
            double cost = res.calculateMaintenanceCost();
            sb.append(res.getResourceID()).append(" (").append(res.getClass().getSimpleName()).append("): $")
                    .append(String.format("%.2f", cost)).append("\n");
            totalMaintenanceCost += cost;
        }
        sb.append("\nTotal Estimated Maintenance Cost: $").append(String.format("%.2f", totalMaintenanceCost)).append("\n");
        reportDisplayArea.setText(sb.toString());
    }


    private JPanel createAlertsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Current System Alerts:");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        panel.add(new JScrollPane(alertDisplayArea), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton clearAlertsButton = new JButton("Clear Alerts");
        clearAlertsButton.addActionListener(e -> alertDisplayArea.setText(""));
        controlPanel.add(clearAlertsButton);

        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }


    // --- Update Methods for GUI Components ---

    private void updateAllDisplays() {
        updateResourceTables();
        updateAllResourcesDisplay();
        updateSmartGridDisplay();

    if (zoneComboBox != null) { // Check for null
        updateZoneComboBox(zoneComboBox);
    }
        // The list model for zones in createCityZonesPanel needs to be accessible to updateZoneList
        // This is a bit clunky, but necessary without making zoneListModel a field or passing it around.
        // A better approach would be to have a dedicated ZoneManagementPanel class.
        for (Component comp : tabbedPane.getComponents()) {
            if (comp instanceof JPanel && ((JPanel) comp).getBorder() != null && ((JPanel) comp).getBorder().getClass().getName().contains("EmptyBorder")) {
                // Heuristic to find the City Zones panel (assuming it has an EmptyBorder and unique content)
                JPanel mainZonePanel = (JPanel) comp;
                Component[] subComps = mainZonePanel.getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JPanel && ((JPanel) subComp).getLayout() instanceof BorderLayout) {
                        JPanel leftPanel = (JPanel) subComp;
                        Component[] leftPanelComps = leftPanel.getComponents();
                        for (Component lpc : leftPanelComps) {
                            if (lpc instanceof JScrollPane) {
                                JScrollPane sp = (JScrollPane) lpc;
                                if (sp.getViewport().getView() instanceof JList) {
                                    JList<?> list = (JList<?>) sp.getViewport().getView();
                                    if (list.getModel() instanceof DefaultListModel) {
                                        updateZoneList((DefaultListModel<String>) list.getModel());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private void updateResourceTables() {
        // Clear existing rows
        busTableModel.setRowCount(0);
        trainTableModel.setRowCount(0);
        powerStationTableModel.setRowCount(0);
        policeTableModel.setRowCount(0);
        fireTableModel.setRowCount(0);
        ambulanceTableModel.setRowCount(0);

        for (CityResource res : cityResourceRepository.getAllResources()) {
            if (res instanceof Bus) {
                Bus bus = (Bus) res;
                busTableModel.addRow(new Object[]{bus.getResourceID(), bus.getLocation(), bus.getStatus(), bus.getRoute(), bus.getNumberOfPassengers()});
            } else if (res instanceof Train) {
                Train train = (Train) res;
                trainTableModel.addRow(new Object[]{train.getResourceID(), train.getLocation(), train.getStatus(), train.getRoute(), train.getNumberOfCoaches()});
            } else if (res instanceof PowerStation) {
                PowerStation ps = (PowerStation) res;
                String zoneName = (ps.getCityZone() != null) ? ps.getCityZone().getZoneName() : "N/A";
                powerStationTableModel.addRow(new Object[]{ps.getResourceID(), ps.getLocation(), ps.getStatus(), ps.getProductionRate(), zoneName});
            } else if (res instanceof Police) {
                Police police = (Police) res;
                policeTableModel.addRow(new Object[]{police.getResourceID(), police.getLocation(), police.getStatus(), police.getNumberOfOfficers()});
            } else if (res instanceof FireDepartment) {
                FireDepartment fire = (FireDepartment) res;
                fireTableModel.addRow(new Object[]{fire.getResourceID(), fire.getLocation(), fire.getStatus(), fire.getNumberOfFireTrucks()});
            } else if (res instanceof Ambulance) {
                Ambulance ambulance = (Ambulance) res;
                ambulanceTableModel.addRow(new Object[]{ambulance.getResourceID(), ambulance.getLocation(), ambulance.getStatus(), ambulance.getNumberOfMedics()});
            }
        }
    }

    private void updateAllResourcesDisplay() {
        StringBuilder sb = new StringBuilder("--- All City Resources ---\n");
        for (CityResource res : cityResourceRepository.getAllResources()) {
            sb.append(res.toString()).append("\n");
        }
        allResourcesDisplayArea.setText(sb.toString());
    }

    private void updateSmartGridDisplay() {
        energyDisplayArea.setText(""); // Clear before updating
        StringBuilder sb = new StringBuilder();
        sb.append("Total Energy Produced: ").append(String.format("%.2f", smartGrid.getTotalEnergyProduced())).append(" kWh\n");
        sb.append("Total Energy Consumed: ").append(String.format("%.2f", smartGrid.getTotalEnergyConsumed())).append(" kWh\n");
        sb.append("Net Energy: ").append(String.format("%.2f", smartGrid.getCurrentNetEnergy())).append(" kWh\n\n");
        sb.append("--- Power Stations in Grid ---\n");
        if (smartGrid.getPowerStations().isEmpty()) {
            sb.append("No power stations in the grid.\n");
        } else {
            for (PowerStation ps : smartGrid.getPowerStations()) {
                sb.append(ps.toString()).append("\n");
            }
        }
        energyDisplayArea.setText(sb.toString());

        // Update consumer tables
        householdConsumerTableModel.setRowCount(0);
        industryConsumerTableModel.setRowCount(0);
        for (Consumer c : smartGrid.getConsumers()) {
            if (c instanceof HouseholdConsumer) {
                HouseholdConsumer hc = (HouseholdConsumer) c;
                householdConsumerTableModel.addRow(new Object[]{hc.getConsumerID(), hc.getConsumptionRate(), hc.getFamilySize()});
            } else if (c instanceof IndustryConsumer) {
                IndustryConsumer ic = (IndustryConsumer) c;
                industryConsumerTableModel.addRow(new Object[]{ic.getConsumerID(), ic.getConsumptionRate(), ic.getIndustryType()});
            }
        }
    }

    // Removed updateMapDisplay()


    // --- Persistence Methods ---

    private void saveAllData() {
        cityResourceRepository.saveToFile();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("city_zones.ser"))) {
            oos.writeObject(cityZonesMap);
            System.out.println("City zones saved to file.");
        } catch (IOException e) {
            System.err.println("Failed to save city zones: " + e.getMessage());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("smart_grid_consumers.ser"))) {
            oos.writeObject(smartGrid.getConsumers()); // Save consumers list
            System.out.println("Smart Grid Consumers saved to file.");
        } catch (IOException e) {
            System.err.println("Failed to save smart grid consumers: " + e.getMessage());
        }

        JOptionPane.showMessageDialog(this, "All data saved successfully!");
    }

    @SuppressWarnings("unchecked")
    private void loadAllData() {
        cityResourceRepository.loadFromFile();

        // Load city zones
        cityZonesMap.clear();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("city_zones.ser"))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?>) {
                cityZonesMap = (Map<String, CityZone>) obj;
                System.out.println("City zones loaded from file.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing city zones file found. Starting with empty map.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load city zones: " + e.getMessage());
        }

        // Re-associate PowerStations with loaded CityZone objects
        for (CityResource res : cityResourceRepository.getAllResources()) {
            if (res instanceof PowerStation) {
                PowerStation ps = (PowerStation) res;
                if (ps.getCityZone() != null && cityZonesMap.containsKey(ps.getCityZone().getZoneName())) {
                    ps.setCityZone(cityZonesMap.get(ps.getCityZone().getZoneName()));
                }
            }
        }

        // Re-initialize smartGrid components
        smartGrid = new SmartGrid();
        for (CityResource res : cityResourceRepository.getAllResources()) {
            if (res instanceof PowerStation) {
                smartGrid.addPowerStation((PowerStation) res);
            }
        }
        // Load consumers for SmartGrid
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("smart_grid_consumers.ser"))) {
            Object obj = ois.readObject();
            if (obj instanceof ArrayList<?>) {
                ArrayList<Consumer> loadedConsumers = (ArrayList<Consumer>) obj;
                for (Consumer c : loadedConsumers) {
                    smartGrid.addConsumer(c);
                }
                System.out.println("Smart Grid Consumers loaded from file.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("No existing smart grid consumers file found. Starting with empty list.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load smart grid consumers: " + e.getMessage());
        }

        updateAllDisplays();
        JOptionPane.showMessageDialog(this, "All data loaded successfully!");
    }
}
