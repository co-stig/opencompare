/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opencompare.ui;

import org.opencompare.explorable.Conflict;
import org.opencompare.explorable.Explorable;

public class DetailsDialog extends javax.swing.JDialog {

	private static final long serialVersionUID = 1L;

	private final Explorable explorable;
    private final String fullId;
    
    /**
     * Creates new form DetailsDialog
     */
    public DetailsDialog(java.awt.Frame parent, boolean modal, Explorable explorable, String fullId) {
        super(parent, modal);
        
        this.explorable = explorable;
        this.fullId = fullId;
        
        initComponents();
        
        refreshFields();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonClose = new javax.swing.JButton();
        labelName = new javax.swing.JLabel();
        editName = new javax.swing.JTextField();
        labelFullName = new javax.swing.JLabel();
        editFullName = new javax.swing.JTextField();
        labelReferenceValue = new javax.swing.JLabel();
        editReferenceValue = new javax.swing.JTextField();
        labelActualValue = new javax.swing.JLabel();
        editActualValue = new javax.swing.JTextField();
        editReferenceCrc = new javax.swing.JTextField();
        labelReferenceCrc = new javax.swing.JLabel();
        labelActualCrc = new javax.swing.JLabel();
        editActualCrc = new javax.swing.JTextField();
        editSha = new javax.swing.JTextField();
        labelSha = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Details");
        setLocationByPlatform(true);

        buttonClose.setText("Close");
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });

        labelName.setText("Name:");

        editName.setEditable(false);

        labelFullName.setText("Full name:");

        editFullName.setEditable(false);

        labelReferenceValue.setText("Reference value:");

        editReferenceValue.setEditable(false);

        labelActualValue.setText("Actual value:");

        editActualValue.setEditable(false);

        editReferenceCrc.setEditable(false);

        labelReferenceCrc.setText("CRC:");

        labelActualCrc.setText("CRC:");

        editActualCrc.setEditable(false);

        editSha.setEditable(false);

        labelSha.setText("SHA:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(labelFullName)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(editFullName))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(labelReferenceValue)
                            .add(labelActualValue))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(editActualValue)
                            .add(editReferenceValue, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(labelReferenceCrc)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(editReferenceCrc, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(labelActualCrc)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(editActualCrc, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(layout.createSequentialGroup()
                        .add(labelName)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(editName)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(labelSha)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(editSha, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(buttonClose)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labelName)
                    .add(editName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labelSha)
                    .add(editSha, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labelFullName)
                    .add(editFullName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labelReferenceValue)
                    .add(editReferenceValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(editReferenceCrc, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labelReferenceCrc))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labelActualValue)
                    .add(editActualValue, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(editActualCrc, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labelActualCrc))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 18, Short.MAX_VALUE)
                .add(buttonClose)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloseActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClose;
    private javax.swing.JTextField editActualCrc;
    private javax.swing.JTextField editActualValue;
    private javax.swing.JTextField editFullName;
    private javax.swing.JTextField editName;
    private javax.swing.JTextField editReferenceCrc;
    private javax.swing.JTextField editReferenceValue;
    private javax.swing.JTextField editSha;
    private javax.swing.JLabel labelActualCrc;
    private javax.swing.JLabel labelActualValue;
    private javax.swing.JLabel labelFullName;
    private javax.swing.JLabel labelName;
    private javax.swing.JLabel labelReferenceCrc;
    private javax.swing.JLabel labelReferenceValue;
    private javax.swing.JLabel labelSha;
    // End of variables declaration//GEN-END:variables

    private void refreshFields() {
        editName.setText(explorable.getRelativeId());
        editSha.setText(explorable.getSha());
        editFullName.setText(fullId);

        if (explorable instanceof Conflict) {
            Conflict conflict = (Conflict) explorable;
            
            Explorable actual = conflict.getActual();
            if (actual != null) {
                editActualValue.setText(actual.getValue());
                editActualCrc.setText(Long.toString(actual.getValueHashCode()));
            }

            Explorable ref = conflict.getReference();
            if (ref != null) {
                editReferenceValue.setText(ref.getValue());
                editReferenceCrc.setText(Long.toString(ref.getValueHashCode()));
            }
        } else {
            editActualValue.setText(explorable.getValue());
            editActualCrc.setText(Long.toString(explorable.getValueHashCode()));
            
            editReferenceValue.setEnabled(false);
            editReferenceCrc.setEnabled(false);
        }
    }
}
