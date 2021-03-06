package org.opencompare.ui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.opencompare.ExploreApplication;
import org.opencompare.Snapshot;
import org.opencompare.WithProgress;
import org.opencompare.database.Database;
import org.opencompare.database.DatabaseManagerFactory;
import org.opencompare.database.DescriptionsDatabase;
import org.opencompare.explorable.Explorable;
import org.opencompare.explore.ExplorationException;
import org.opencompare.ui.model.ConflictTreeTableModel;
import org.opencompare.ui.model.DescriptionTreeTableModel;
import org.opencompare.ui.model.EmptyTreeTableModel;
import org.opencompare.ui.model.ExplorableTreeTableModel;
import org.opencompare.ui.model.SnapshotTableDataModel;
import org.opencompare.ui.model.SnapshotTreeTableModel;

public class MainWindow extends javax.swing.JFrame implements WithProgress {

    private static final long serialVersionUID = 1L;
    private static final FileFilter SNAPSHOT_FILE_FILTER = new FileFilter() {
        
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().endsWith(".snapshot");
        }
        
        public String getDescription() {
            return "Snapshot (*.snapshot)";
        }
    };
    
    public MainWindow() {
        initComponents();
        refreshSnapshotList();
        refreshButtons();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        scrollPane = new javax.swing.JScrollPane();
        snapshotsTable = new javax.swing.JTable();
        toolBar = new javax.swing.JToolBar();
        buttonNew = new javax.swing.JButton();
        buttonCompare = new javax.swing.JButton();
        buttonView = new javax.swing.JButton();
        buttonClose = new javax.swing.JButton();
        separator2 = new javax.swing.JToolBar.Separator();
        buttonImport = new javax.swing.JButton();
        buttonExport = new javax.swing.JButton();
        separator3 = new javax.swing.JToolBar.Separator();
        buttonDelete = new javax.swing.JButton();
        buttonRefresh = new javax.swing.JButton();
        separator6 = new javax.swing.JToolBar.Separator();
        buttonFilter = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuNew = new javax.swing.JMenuItem();
        menuCompare = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenuItem();
        menuClose = new javax.swing.JMenuItem();
        separator4 = new javax.swing.JPopupMenu.Separator();
        menuDelete = new javax.swing.JMenuItem();
        menuRefresh = new javax.swing.JMenuItem();
        separator5 = new javax.swing.JPopupMenu.Separator();
        menuFilter = new javax.swing.JMenuItem();
        separator7 = new javax.swing.JPopupMenu.Separator();
        menuImport = new javax.swing.JMenuItem();
        menuExport = new javax.swing.JMenuItem();
        separator1 = new javax.swing.JPopupMenu.Separator();
        menuExit = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Open Compare");
        setLocationByPlatform(true);

        tabs.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabsStateChanged(evt);
            }
        });

        snapshotsTable.setModel(new SnapshotTableDataModel());
        snapshotsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(snapshotsTable);

        tabs.addTab("Snapshots", scrollPane);

        toolBar.setRollover(true);

        buttonNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opencompare/ui/images/new.gif"))); // NOI18N
        buttonNew.setText("New...");
        buttonNew.setFocusable(false);
        buttonNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNewActionPerformed(evt);
            }
        });
        toolBar.add(buttonNew);

        buttonCompare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opencompare/ui/images/compare.gif"))); // NOI18N
        buttonCompare.setText("Compare...");
        buttonCompare.setFocusable(false);
        buttonCompare.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonCompare.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonCompare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCompareActionPerformed(evt);
            }
        });
        toolBar.add(buttonCompare);

        buttonView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opencompare/ui/images/view.gif"))); // NOI18N
        buttonView.setText("View");
        buttonView.setFocusable(false);
        buttonView.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonViewActionPerformed(evt);
            }
        });
        toolBar.add(buttonView);

        buttonClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opencompare/ui/images/close.gif"))); // NOI18N
        buttonClose.setText("Close");
        buttonClose.setFocusable(false);
        buttonClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });
        toolBar.add(buttonClose);
        toolBar.add(separator2);

        buttonImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opencompare/ui/images/import.gif"))); // NOI18N
        buttonImport.setText("Import...");
        buttonImport.setFocusable(false);
        buttonImport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonImport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonImportActionPerformed(evt);
            }
        });
        toolBar.add(buttonImport);

        buttonExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opencompare/ui/images/export.gif"))); // NOI18N
        buttonExport.setText("Export...");
        buttonExport.setFocusable(false);
        buttonExport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonExport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonExportActionPerformed(evt);
            }
        });
        toolBar.add(buttonExport);
        toolBar.add(separator3);

        buttonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opencompare/ui/images/delete.gif"))); // NOI18N
        buttonDelete.setText("Delete");
        buttonDelete.setFocusable(false);
        buttonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteActionPerformed(evt);
            }
        });
        toolBar.add(buttonDelete);

        buttonRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opencompare/ui/images/refresh.gif"))); // NOI18N
        buttonRefresh.setText("Refresh");
        buttonRefresh.setFocusable(false);
        buttonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRefreshActionPerformed(evt);
            }
        });
        toolBar.add(buttonRefresh);
        toolBar.add(separator6);

        buttonFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opencompare/ui/images/filter.gif"))); // NOI18N
        buttonFilter.setText("Filter...");
        buttonFilter.setEnabled(false);
        buttonFilter.setFocusable(false);
        buttonFilter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonFilter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buttonFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonFilterActionPerformed(evt);
            }
        });
        toolBar.add(buttonFilter);

        menuFile.setText("File");

        menuNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuNew.setIcon(buttonNew.getIcon());
        menuNew.setText(buttonNew.getText());
        menuNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewActionPerformed(evt);
            }
        });
        menuFile.add(menuNew);

        menuCompare.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        menuCompare.setIcon(buttonCompare.getIcon());
        menuCompare.setText(buttonCompare.getText());
        menuCompare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCompareActionPerformed(evt);
            }
        });
        menuFile.add(menuCompare);

        menuView.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        menuView.setIcon(buttonView.getIcon());
        menuView.setText(buttonView.getText());
        menuView.setEnabled(buttonView.isEnabled());
        menuView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewActionPerformed(evt);
            }
        });
        menuFile.add(menuView);

        menuClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        menuClose.setIcon(buttonClose.getIcon());
        menuClose.setText(buttonClose.getText());
        menuClose.setEnabled(buttonClose.isEnabled());
        menuClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCloseActionPerformed(evt);
            }
        });
        menuFile.add(menuClose);
        menuFile.add(separator4);

        menuDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuDelete.setIcon(buttonDelete.getIcon());
        menuDelete.setText(buttonDelete.getText());
        menuDelete.setEnabled(buttonDelete.isEnabled());
        menuDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDeleteActionPerformed(evt);
            }
        });
        menuFile.add(menuDelete);

        menuRefresh.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        menuRefresh.setIcon(buttonRefresh.getIcon());
        menuRefresh.setText(buttonRefresh.getText());
        menuRefresh.setEnabled(buttonRefresh.isEnabled());
        menuRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRefreshActionPerformed(evt);
            }
        });
        menuFile.add(menuRefresh);
        menuFile.add(separator5);

        menuFilter.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        menuFilter.setIcon(buttonFilter.getIcon());
        menuFilter.setText("Filter...");
        menuFilter.setEnabled(false);
        menuFilter.setText(buttonFilter.getText());
        menuFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFilterActionPerformed(evt);
            }
        });
        menuFile.add(menuFilter);
        menuFile.add(separator7);

        menuImport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        menuImport.setIcon(buttonImport.getIcon());
        menuImport.setText(buttonImport.getText());
        menuImport.setEnabled(buttonImport.isEnabled());
        menuImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuImportActionPerformed(evt);
            }
        });
        menuFile.add(menuImport);

        menuExport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        menuExport.setIcon(buttonExport.getIcon());
        menuExport.setText(buttonExport.getText());
        menuExport.setEnabled(buttonExport.isEnabled());
        menuExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExportActionPerformed(evt);
            }
        });
        menuFile.add(menuExport);
        menuFile.add(separator1);

        menuExit.setText("Exit");
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        menuFile.add(menuExit);

        menuBar.add(menuFile);

        menuHelp.setText("Help");

        menuAbout.setText("About...");
        menuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAboutActionPerformed(evt);
            }
        });
        menuHelp.add(menuAbout);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(tabs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1130, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(toolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tabs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private Component createSnapshotTree(Snapshot snapshot) throws ExplorationException {
        DescriptionsDatabase descDb = null;
        
        if (snapshot.getType() != Snapshot.Type.Descriptions) {
            try {
                descDb = DatabaseManagerFactory.get().newDescriptionsConnection();
            } catch (ExplorationException e) {
            	if (ExploreApplication.ENABLE_EXPERIMENTAL_FUNCTIONALITY) {
	                JOptionPane.showMessageDialog(this, "Warning: descriptions database is not available.", "Warning", JOptionPane.WARNING_MESSAGE);
	                // TODO: Ask to download and import complete descriptions snapshot
            	}
            }
        }
        
        final Database database = DatabaseManagerFactory.get().newConnection(snapshot);
        
        ExplorableTreeTableModel model;
        if (snapshot.getType() == Snapshot.Type.Conflicts) {
            model = new ConflictTreeTableModel(database, descDb);
        } else if (snapshot.getType() == Snapshot.Type.Snapshot) {
            model = new SnapshotTreeTableModel(database, descDb);
        } else if (snapshot.getType() == Snapshot.Type.Descriptions) {
            model = new DescriptionTreeTableModel(database);
        } else {
            throw new ExplorationException("Trying to open unsupported type of snapshot: " + snapshot.getType());
        }
        
        final Frame frame = this;
        final JXTreeTable treeTable = new JXTreeTable();
        treeTable.setTreeTableModel(model);
        
        JScrollPane treeTableScrollPane = new JScrollPane();
        treeTableScrollPane.setViewportView(treeTable);
        treeTableScrollPane.setName(snapshot.getType().toString() + ": " + snapshot.getName());

        final MainWindow thiz = this;
        
        treeTable.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent me) {
                    if (me.getClickCount() == 2) {
                        int sel = treeTable.getSelectedRow();
                        if (sel >= 0) {
                            TreePath tp = treeTable.getTreeSelectionModel().getSelectionPath();
                            Object o = tp.getLastPathComponent();
                            if (o instanceof Explorable) {
                                Explorable e = (Explorable) o;
                                try {
                                    DetailsDialog dlg = new DetailsDialog(frame, false, e, database.getFullId(e));
                                    dlg.setVisible(true);
                                } catch (ExplorationException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(
                                            thiz, 
                                            "Unable to display the details: " + ex.getLocalizedMessage(), 
                                            "Error", 
                                            JOptionPane.ERROR_MESSAGE
                                        );
                                }
                            }
                        }
                    }
                }
            });
        
        return treeTableScrollPane;
    }
    
    private void buttonViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonViewActionPerformed
        int[] selected = snapshotsTable.getSelectedRows();
        if (selected.length > 0) {
            SnapshotTableDataModel model = (SnapshotTableDataModel) snapshotsTable.getModel();
            for (int i : selected) {
                viewSnapshot(model.get(i));
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a snapshot", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_buttonViewActionPerformed

    public void refreshSnapshotList() {
        SnapshotTableDataModel model = (SnapshotTableDataModel) snapshotsTable.getModel();
        model.refresh();
    }
    
    public void viewSnapshot(Snapshot snapshot) {
        try {
            Component tree = createSnapshotTree(snapshot);
            int index = tabs.getTabCount();
            tabs.insertTab(tree.getName(), null, tree, null, index);
            tabs.setSelectedIndex(index);
            refreshButtons();
        } catch (ExplorationException ex) {
            JOptionPane.showMessageDialog(this, "Unable to view snapshot " + snapshot, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buttonImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonImportActionPerformed
        JFileChooser dlg = new JFileChooser();
        dlg.setDialogTitle("Import snapshot");
        dlg.setMultiSelectionEnabled(false);
        dlg.setFileFilter(SNAPSHOT_FILE_FILTER);
        
        if (dlg.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        	DatabaseManagerFactory.get().importSnapshot(
                    dlg.getSelectedFile(),
                    new ProgressDialog(this, "Importing snapshot", null, this)
                );
        }
    }//GEN-LAST:event_buttonImportActionPerformed
    
    private void buttonExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonExportActionPerformed
        Snapshot snapshot = getCurrentSnapshot();
        if (snapshot != null) {
            
            JFileChooser dlg = new JFileChooser();
            dlg.setDialogTitle("Export snapshot");
            dlg.setMultiSelectionEnabled(false);
            dlg.setFileFilter(SNAPSHOT_FILE_FILTER);
            
            if (dlg.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            
                // Modify filename if necessary
                File out = dlg.getSelectedFile();
                if (dlg.getFileFilter() == SNAPSHOT_FILE_FILTER) {
                    if (!out.getName().endsWith(".snapshot")) {
                        out = new File(out.getAbsolutePath() + ".snapshot");
                    }
                }

                DatabaseManagerFactory.get().exportSnapshot(
                        snapshot,
                        out,
                        new ProgressDialog(null, "Exporting snapshot", null, this)
                    );
            }
        }
    }//GEN-LAST:event_buttonExportActionPerformed

    /**
     * This method returns currently selected snapshot. If we are on the
     * Snapshots tab, it looks at the selected snapshot. If there's zero or more
     * than one selected snapshots, it will display the error message and return
     * null. If we are on one of the snapshots tabs, it will return the
     * corresponding snapshot.
     */
    private Snapshot getCurrentSnapshot() {
        int tab = tabs.getSelectedIndex();
        
        if (tab == 0) {
            int[] selected = snapshotsTable.getSelectedRows();
            if (selected.length == 1) {
                SnapshotTableDataModel model = (SnapshotTableDataModel) snapshotsTable.getModel();
                return model.get(selected[0]);
            } else {
                JOptionPane.showMessageDialog(this, "Please select one snapshot", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } else {
            return getTabModel(tab).getDatabase().getSnapshot();
        }
    }
    
    private ExplorableTreeTableModel getTabModel(int tab) {
        if (tab == 0) {
            return null;
        } else {
            return (ExplorableTreeTableModel) getTreeTable(tab).getTreeTableModel();
        }
    }
    
    private JXTreeTable getTreeTable(int tab) {
        if (tab == 0) {
            return null;
        } else {
            JScrollPane sp = (JScrollPane) tabs.getComponentAt(tab);
            return (JXTreeTable) sp.getViewport().getView();
        }
    }
    
    private void buttonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteActionPerformed
        Snapshot snapshot = getCurrentSnapshot();
        if (snapshot != null) {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the snapshot? This action cannot be undone.", "Confirmation", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                closeAllTabsWithSnapshot(snapshot);
                snapshot.delete();
                refreshSnapshotList();
            }
        }
    }//GEN-LAST:event_buttonDeleteActionPerformed

    private void closeAllTabsWithSnapshot(Snapshot snapshot) {
        boolean closedSomething;
        do {
            closedSomething = false;
            for (int i = 1; i < tabs.getTabCount(); ++i) {
                ExplorableTreeTableModel model = getTabModel(i);
                if (model.getDatabase().getSnapshot().equals(snapshot)) {
                    closeTab(i);
                    closedSomething = true;
                    break;
                }
            }
        } while (closedSomething);
    }
    
    private void buttonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRefreshActionPerformed
        refreshSnapshotList();
    }//GEN-LAST:event_buttonRefreshActionPerformed
    
    private void menuViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewActionPerformed
        buttonViewActionPerformed(evt);
    }//GEN-LAST:event_menuViewActionPerformed
    
    private void menuImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuImportActionPerformed
        buttonImportActionPerformed(evt);
    }//GEN-LAST:event_menuImportActionPerformed
    
    private void menuExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExportActionPerformed
        buttonExportActionPerformed(evt);
    }//GEN-LAST:event_menuExportActionPerformed
    
    private void menuDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDeleteActionPerformed
        buttonDeleteActionPerformed(evt);
    }//GEN-LAST:event_menuDeleteActionPerformed
    
    private void menuRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRefreshActionPerformed
        buttonRefreshActionPerformed(evt);
    }//GEN-LAST:event_menuRefreshActionPerformed
    
    private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloseActionPerformed
        closeTab(tabs.getSelectedIndex());
    }//GEN-LAST:event_buttonCloseActionPerformed

    private void closeTab(int tab) {
        ExplorableTreeTableModel model = getTabModel(tab);
        try {
            model.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "There was an error closing database connection", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        tabs.remove(tab);
        refreshButtons();
    }
    
    private void refreshButtons() {
        int tab = tabs.getSelectedIndex();
        
        buttonClose.setEnabled(tab != 0);
        buttonView.setEnabled(tab == 0);
        buttonRefresh.setEnabled(tab == 0);
        buttonFilter.setEnabled(tab != 0 && getTabModel(tab).getDatabase().getSnapshot().getType() == Snapshot.Type.Conflicts);

        menuClose.setEnabled(buttonClose.isEnabled());
        menuView.setEnabled(buttonView.isEnabled());
        menuRefresh.setEnabled(buttonRefresh.isEnabled());
        menuFilter.setEnabled(buttonFilter.isEnabled());
    }
    
    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuExitActionPerformed

    private void menuCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCloseActionPerformed
        buttonCloseActionPerformed(evt);
    }//GEN-LAST:event_menuCloseActionPerformed

    private void tabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabsStateChanged
        refreshButtons();
    }//GEN-LAST:event_tabsStateChanged

    private void buttonNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNewActionPerformed
        CreateSnapshotDialog wizard = new CreateSnapshotDialog(this, true);
        wizard.setVisible(true);
    }//GEN-LAST:event_buttonNewActionPerformed

    private void buttonCompareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCompareActionPerformed
        CompareSnapshotsDialog wizard = new CompareSnapshotsDialog(this, true);
        wizard.setVisible(true);
    }//GEN-LAST:event_buttonCompareActionPerformed

    private void menuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAboutActionPerformed
        new AboutDialog(this, true).setVisible(true);
    }//GEN-LAST:event_menuAboutActionPerformed

    private void menuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewActionPerformed
        buttonNewActionPerformed(evt);
    }//GEN-LAST:event_menuNewActionPerformed

    private void menuCompareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCompareActionPerformed
        buttonCompareActionPerformed(evt);
    }//GEN-LAST:event_menuCompareActionPerformed

    private void buttonFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonFilterActionPerformed
        // We assume there's a conflict snapshot currently selected, otherwise this button won't be available
        JXTreeTable treeTable = getTreeTable(tabs.getSelectedIndex());
        ConflictTreeTableModel model = (ConflictTreeTableModel) treeTable.getTreeTableModel();
            
        FilterDialog fd = new FilterDialog(this, true, model.getFilter());
        fd.setVisible(true);
        
        if (fd.isUserConfirmed()) {
            model.setFilter(fd.isDisplayIdentical());
            treeTable.setTreeTableModel(new EmptyTreeTableModel());
            treeTable.setTreeTableModel(model);
        }
    }//GEN-LAST:event_buttonFilterActionPerformed

    private void menuFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFilterActionPerformed
        buttonFilterActionPerformed(evt);
    }//GEN-LAST:event_menuFilterActionPerformed

    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClose;
    private javax.swing.JButton buttonCompare;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonExport;
    private javax.swing.JButton buttonFilter;
    private javax.swing.JButton buttonImport;
    private javax.swing.JButton buttonNew;
    private javax.swing.JButton buttonRefresh;
    private javax.swing.JButton buttonView;
    private javax.swing.JMenuItem menuAbout;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuClose;
    private javax.swing.JMenuItem menuCompare;
    private javax.swing.JMenuItem menuDelete;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuExport;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuFilter;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuImport;
    private javax.swing.JMenuItem menuNew;
    private javax.swing.JMenuItem menuRefresh;
    private javax.swing.JMenuItem menuView;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JPopupMenu.Separator separator1;
    private javax.swing.JToolBar.Separator separator2;
    private javax.swing.JToolBar.Separator separator3;
    private javax.swing.JPopupMenu.Separator separator4;
    private javax.swing.JPopupMenu.Separator separator5;
    private javax.swing.JToolBar.Separator separator6;
    private javax.swing.JPopupMenu.Separator separator7;
    private javax.swing.JTable snapshotsTable;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

    public void start() {
        // Ignore
    }
    
    public void complete(boolean success) {
        refreshSnapshotList();
    }
    
    public void setMaximum(int max) {
        // Ignore
    }
    
    public void setValue(int value) {
        // Ignore
    }
    
}
