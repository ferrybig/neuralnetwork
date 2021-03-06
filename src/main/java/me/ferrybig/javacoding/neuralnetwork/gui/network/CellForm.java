package me.ferrybig.javacoding.neuralnetwork.gui.network;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import me.ferrybig.javacoding.neuralnetwork.Cell;
import me.ferrybig.javacoding.neuralnetwork.Layer;
import me.ferrybig.javacoding.neuralnetwork.gui.DoubleFormatterFactory;

/**
 *
 * @author Fernando
 */
public class CellForm extends javax.swing.JPanel {

	private final Cell cell;
	private final Consumer<Layer> weigthUpdate;
	private final JFormattedTextField[] cells;
	private boolean throwEvents = true;

	/**
	 * Creates new form CellForm
	 * @param cell
	 * @param weigthUpdate
	 */
	@SuppressWarnings("OverridableMethodCallInConstructor")
	public CellForm(Cell cell, Consumer<Layer> weigthUpdate) {
		this.cell = cell;
		this.weigthUpdate = weigthUpdate;
		initComponents();
		cells = new JFormattedTextField[cell.getInputSize()];
		bias.setValue(cell.getBiasWeigth());

		addInputFields();
		setOutput(Double.NaN);
	}

	public Point[] getCalculatedLinePoints(boolean leftToRigthView) {
		Point[] arr = new Point[cells.length];
		if(leftToRigthView) {
			Arrays.fill(arr, new Point(0, this.getHeight() / 2));
		} else {
			Arrays.fill(arr, new Point(this.getWidth() / 2, 0));
		}
		return arr;
	}

	public void setOutput(double value) {
		if(Double.isNaN(value))
			this.bar.setIndeterminate(true);
		else
			this.bar.setIndeterminate(false);
		this.bar.setString(String.format("%.13f", value));
		this.bar.setValue((int) Math.round(value * 100));
	}

	private void addInputFields() {
		GridBagConstraints textConstraints = new java.awt.GridBagConstraints();
        textConstraints.gridx = 0;
        textConstraints.gridy = 3;
        textConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        textConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
		GridBagConstraints cellConstraints = new java.awt.GridBagConstraints();
        cellConstraints.gridx = 1;
        cellConstraints.gridy = 3;
        cellConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		cellConstraints.weightx = 0.1;
        cellConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
		for(Cell c : cell.getLayer().getPrevious().getCells()) {
			JLabel label = new JLabel(c.getSimpleName());
			JFormattedTextField field = new javax.swing.JFormattedTextField();
			field.setFormatterFactory(DoubleFormatterFactory.INSTANCE);
			field.setValue(cell.getWeigth(c));
			field.addPropertyChangeListener("value", (PropertyChangeEvent evt) -> {
				weigthEdited(c, (double) evt.getNewValue());
			});
			this.add(label, textConstraints);
			this.add(field, cellConstraints);
			textConstraints.gridy++;
			cellConstraints.gridy++;
			cells[c.getCellNumber()] = field;
		}
		bias.addPropertyChangeListener("value", (PropertyChangeEvent evt) -> {
			weigthEdited(null, (double) evt.getNewValue());
		});
	}

	public void weigthEdited(Cell c, double newValue) {
		if(!throwEvents) {
			return;
		}
		if(c == null) {
			cell.setBiasWeigth(newValue);
			this.weigthUpdate.accept(null);
		} else {
			cell.setWeigth(c.getCellNumber(), newValue);
			this.weigthUpdate.accept(cell.getLayer());
		}
		
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		dim.width = Math.max(dim.width, 200);
		return dim;
	}

	public void updateWeigth() {
		try {
			throwEvents = false;
			bias.setValue(cell.getBiasWeigth());
			for(Cell c : cell.getLayer().getPrevious().getCells()) {
				cells[c.getCellNumber()].setValue(cell.getWeigth(c));
			}
		} finally {
			throwEvents = true;
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

        biasLabel = new javax.swing.JLabel();
        bias = new javax.swing.JFormattedTextField();
        bar = new javax.swing.JProgressBar();

        setBorder(javax.swing.BorderFactory.createTitledBorder(cell.getSimpleName()));
        setMinimumSize(new java.awt.Dimension(200, 100));
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        biasLabel.setText("BIAS:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(biasLabel, gridBagConstraints);

        bias.setFormatterFactory(DoubleFormatterFactory.INSTANCE);
        bias.setText(DoubleFormatterFactory.INSTANCE_FORMATTER.valueToString(cell.getBiasWeigth())
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(bias, gridBagConstraints);

        bar.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 2);
        add(bar, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar bar;
    private javax.swing.JFormattedTextField bias;
    private javax.swing.JLabel biasLabel;
    // End of variables declaration//GEN-END:variables
}
