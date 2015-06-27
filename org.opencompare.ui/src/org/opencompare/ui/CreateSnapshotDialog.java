package org.opencompare.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpringLayout;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.opencompare.ExploreApplication;
import org.opencompare.Snapshot;
import org.opencompare.WithProgress;
import org.opencompare.explorable.ApplicationConfiguration;
import org.opencompare.explorable.OptionDefinition;
import org.opencompare.explorable.OptionValue;
import org.opencompare.explorable.ProcessConfiguration;
import org.opencompare.ui.model.OptionsTableModel;

public class CreateSnapshotDialog extends javax.swing.JDialog implements WithProgress {

	private static final long serialVersionUID = 1L;

	private final MainWindow mainWindow;
	private List<OptionValue> options = new ArrayList<OptionValue>();

	private static final String STATUS_PREFIX = "Parsing file structure: ";

	public CreateSnapshotDialog(MainWindow parent, boolean modal) {
		super(parent, modal);
		this.mainWindow = parent;

		initComponents();

		buttonAction.setText(TEXT_START);
	}

	private void fillOptions(JPanel systemPanel) {
		// TODO: See if we can enable / disable factories as plugins
		options = ApplicationConfiguration.getInstance().initializeAllOptions();

		javax.swing.JTable table = new JTable(new OptionsTableModel(options)) {
			private static final long serialVersionUID = 1L;

			@Override
			public TableCellEditor getCellEditor(int row, int column) {
				if (column == 1) {
					OptionDefinition option = ((OptionsTableModel) getModel()).getOption(row).getDefinition();
					switch (option.getType()) {
					case Int:
						return getDefaultEditor(Integer.class);
					case List:
						JComboBox<String> dropDown = new JComboBox<String>(option.getAllowedValues());
						dropDown.setSelectedItem(option.getDefaultValue());
						return new DefaultCellEditor(dropDown);
					case Text:
						return getDefaultEditor(String.class);
					case YesNo:
						return getDefaultEditor(Boolean.class);
					}
				}
				return super.getCellEditor(row, column);
			}

			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				if (column == 1) {
					OptionDefinition option = ((OptionsTableModel) getModel()).getOption(row).getDefinition();
					switch (option.getType()) {
					case Int:
						return getDefaultRenderer(Integer.class);
					case List:
					case Text:
						return getDefaultRenderer(String.class);
					case YesNo:
						return getDefaultRenderer(Boolean.class);
					}
				}
				return super.getCellRenderer(row, column);
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return col == 1;
			}
		};
		
		table.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				enableOrDisableStart();
			}
		});
		
		SpringLayout layout = new SpringLayout();
		layout.putConstraint(SpringLayout.EAST, systemPanel, 5, SpringLayout.EAST, table);
		layout.putConstraint(SpringLayout.WEST, systemPanel, 5, SpringLayout.WEST, table);
		layout.putConstraint(SpringLayout.SOUTH, systemPanel, 5, SpringLayout.SOUTH, table);
		layout.putConstraint(SpringLayout.NORTH, systemPanel, 5, SpringLayout.NORTH, table);
		systemPanel.setLayout(layout);
		systemPanel.add(table);
		enableOrDisableStart();
	}

	private void enableOrDisableStart() {
		buttonAction.setEnabled(allRequiredOptionsFilled());
	}
	
	private boolean allRequiredOptionsFilled() {
		for (OptionValue optionValue : options) {
			if (optionValue.getDefinition().isRequired() && !optionValue.isFilled()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jSeparator1 = new javax.swing.JSeparator();
		buttonAction = new javax.swing.JButton();
		buttonCancel = new javax.swing.JButton();
		labelParseFileStatus = new javax.swing.JLabel();
		progressParseFile = new javax.swing.JProgressBar();
		systemPanel = new javax.swing.JPanel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Create snapshot");
		setLocationByPlatform(true);
		setModal(true);

		jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
		jLabel1.setText("Create shapshot");

		jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opencompare/ui/images/fingerprint.gif"))); // NOI18N

		buttonAction.setText("Action!");
		buttonAction.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonActionActionPerformed(evt);
			}
		});

		buttonCancel.setText("Cancel");
		buttonCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				buttonCancelActionPerformed(evt);
			}
		});

		labelParseFileStatus.setText("Progress");
		labelParseFileStatus.setToolTipText("");

		systemPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("System"));
		fillOptions(systemPanel);

		GroupLayout layout = new GroupLayout(getContentPane());
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(Alignment.TRAILING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jLabel2)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(systemPanel, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
								.addComponent(jLabel1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
								.addComponent(progressParseFile, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
								.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
									.addComponent(labelParseFileStatus)
									.addGap(259))))
						.addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(layout.createSequentialGroup()
							.addGap(0, 566, Short.MAX_VALUE)
							.addComponent(buttonCancel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(buttonAction)))
					.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(jLabel2)
						.addGroup(layout.createSequentialGroup()
							.addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(systemPanel, GroupLayout.PREFERRED_SIZE, 258, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(labelParseFileStatus)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(progressParseFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
					.addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(buttonAction)
						.addComponent(buttonCancel))
					.addContainerGap())
		);
		getContentPane().setLayout(layout);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void buttonActionActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonActionActionPerformed
		if (buttonAction.getText().equals(TEXT_FINISH)) {
			setVisible(false);
		} else if (buttonAction.getText().equals(TEXT_ABORT)) {
			// TODO: Implement abort
		} else {
			buttonAction.setText(TEXT_ABORT);
			buttonAction.setEnabled(false);
			buttonCancel.setEnabled(false);
			createSnapshot();
		}
	}// GEN-LAST:event_buttonActionActionPerformed

	public void complete(boolean success) {
		progressParseFile.setMaximum(1);
		progressParseFile.setValue(1);
		labelParseFileStatus.setText(STATUS_PREFIX + "Complete");

		buttonAction.setText(TEXT_FINISH);
		buttonAction.setEnabled(true);
	}

	public void setMaximum(int max) {
		progressParseFile.setMaximum(max);
	}

	public void setValue(int value) {
		progressParseFile.setValue(value);
	}

	public void start() {
		progressParseFile.setValue(0);
	}

	private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonCancelActionPerformed
		setVisible(false);
	}// GEN-LAST:event_buttonCancelActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton buttonAction;
	private javax.swing.JButton buttonCancel;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JLabel labelParseFileStatus;
	private javax.swing.JProgressBar progressParseFile;
	private javax.swing.JPanel systemPanel;

	// End of variables declaration//GEN-END:variables

	private void createSnapshot() {
		final WithProgress thiz = this;

		new Thread(new Runnable() {
			public void run() {
				try {
					ProcessConfiguration conf = new ProcessConfiguration();
					for (OptionValue o: options) {
						conf.getOption(o.getDefinition().getName()).setValue(o.getValue());
					}
					
					Snapshot snap = ExploreApplication.explore(conf, thiz);
					mainWindow.refreshSnapshotList();
					mainWindow.viewSnapshot(snap);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(mainWindow, "Error during exploration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}

		}).start();
	}
	
	public OptionValue getOptionValue(String name) {
		for (OptionValue optionValue : options) {
			if (optionValue.getDefinition().getName().equals(name)) {
				return optionValue;
			}
		}
		return null;
	}
}
