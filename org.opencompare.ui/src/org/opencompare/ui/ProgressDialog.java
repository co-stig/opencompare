package org.opencompare.ui;

import javax.swing.JOptionPane;

import org.opencompare.WithProgress;

public class ProgressDialog extends javax.swing.JDialog implements WithProgress {

	private static final long serialVersionUID = 1L;

    private final Object lock = new Object();
    private final String abortMessage;
    private final WithProgress parent;
    
    private boolean running = false;

    /**
     * Creates new form ProgressDialog
     */
    public ProgressDialog(java.awt.Frame parentFrame, String title, String abortMessage, WithProgress parent) {
        super(parentFrame, false);
        this.parent = parent;
        this.abortMessage = abortMessage;
        initComponents();
        setTitle(title);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progressBar = new javax.swing.JProgressBar();
        closeButton = new javax.swing.JButton();
        progressLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Progress");
        setLocationByPlatform(true);
        setResizable(false);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        progressLabel.setText("Initializing...");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(progressLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(closeButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(closeButton)
                    .add(progressLabel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        if (running) {
            if (JOptionPane.showConfirmDialog(
                    this, 
                    abortMessage == null ? "Are you sure you want to abort?" : abortMessage, 
                    "Confirmation", 
                    JOptionPane.OK_CANCEL_OPTION
                ) == JOptionPane.OK_OPTION) {
                // TODO: Abort somehow
                setVisible(false);
            }
        } else {
            setVisible(false);
        }
    }//GEN-LAST:event_closeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel progressLabel;
    // End of variables declaration//GEN-END:variables

    public void start() {
        synchronized(lock) {
            running = true;
            setVisible(true);
            progressLabel.setText("Running...");
            closeButton.setText("Abort");
        }
    }

    public void complete(boolean success) {
        synchronized(lock) {
            if (running) {
                if (parent != null) {
                    parent.complete(success);
                }
                running = false;
                progressBar.setMaximum(1);
                progressBar.setValue(1);
                progressLabel.setText(success ? "Completed successfully" : "Failed");
                closeButton.setText("Close");
            }
        }
    }

    public void setMaximum(int max) {
        synchronized(lock) {
            progressBar.setMaximum(max);
        }
    }

    public void setValue(int value) {
        synchronized(lock) {
            progressBar.setValue(value);
        }
    }
}
