package me.ferrybig.javacoding.neuralnetwork.gui.network;

import java.awt.GridBagConstraints;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Fernando
 */
public final class ResizeLayerForm extends javax.swing.JComponent {

	private Runnable action;
	private boolean removeable = true;
	private final TitledBorder border = javax.swing.BorderFactory.createTitledBorder(" ");
	private String title = " ";

	/**
	 * Creates new form ResizeLayerForm
	 */
	public ResizeLayerForm() {
		unsetRemoveAction();
		initComponents();
		setTitle(" ");
	}
	
	public void unsetRemoveAction() {
		this.action = () -> {};
	}

	public void setRemoveAction(Runnable action) {
		this.action = action;
	}

	public Runnable getRemoveAction() {
		return this.action;
	}

	public int getValue() {
		return (Integer) size.getValue();
	}

	public void setValue(int value) {
		size.setValue(value);
	}
	
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 * @beaninfo
     *        bound: true
     *  description: title of the form
	 */
	public void setTitle(String title) {
		this.firePropertyChange("title", this.title, title);
		border.setTitle(this.title = title);
		this.setBorder(border);
	}

	public boolean getRemoveable() {
		return removeable;
	}

	public void setRemoveable(boolean removeable) {
		if(this.removeable == removeable) {
			return;
		}
		this.removeable = removeable;
		if(!removeable) {
			this.remove(remove);
		} else {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 0.1;
			gridBagConstraints.weighty = 0.1;
			gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
			add(remove, gridBagConstraints);
		}
	}

	

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        remove = new javax.swing.JButton();
        size = new javax.swing.JSpinner();

        setLayout(new java.awt.GridBagLayout());

        remove.setText("Remove layer");
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(remove, gridBagConstraints);

        size.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(size, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
		action.run();
    }//GEN-LAST:event_removeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton remove;
    private javax.swing.JSpinner size;
    // End of variables declaration//GEN-END:variables
}
