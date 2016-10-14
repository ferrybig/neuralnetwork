package me.ferrybig.javacoding.neuralnetwork.gui.network;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import me.ferrybig.javacoding.neuralnetwork.Cell;
import me.ferrybig.javacoding.neuralnetwork.CompiledNetwork;
import me.ferrybig.javacoding.neuralnetwork.Layer;
import me.ferrybig.javacoding.neuralnetwork.gui.palete.LineColor;

/**
 * @beaninfo
 *   attribute: isContainer false
 * description: A component that displays a view of a neural network
 */
public final class NetworkForm extends JComponent {

	static final String ERROR_TEXT_PROPERTY = "errorText";
	static final String HOVER_EFFECT_PROPERTY = "hoverEffect";
	static final String CENTERED_CELLS_PROPERTY = "centeredCells";
	static final String LEFT_TO_RIGHT_VIEW_PROPERTY = "leftToRightView";
	static final String SHOW_LINES_PROPERTY = "showLines";
	static final String NETWORK_PROPERTY = "network";
	static final String LINE_COLORS_PROPERTY = "lineColors";

	private boolean centeredCells;

	private boolean leftToRightView;
	private CompiledNetwork network;

	private InputCellForm[] input;
	private double[] inputArray;
	private double[] outputArray;

	private CellForm[] outputCells;
	private Point[][][] targetPoints;
	private Point[][][] sendingPoints;
	private boolean pointsRequiresUpdates = true;
	private boolean runningUpdate = false;
	private boolean shouldUpdate = false;
	private double[] calculationCache;
	private JPanel[] panelLayers;
	private int hoverIndex = -1;
	private Layer hoverLayer = null;
	private final JLabel errorLabel = new JLabel("No network provided");
	private final GridBagConstraints errorConstrains = new GridBagConstraints();
	{
		errorConstrains.insets = new Insets(2, 2, 2, 2);
	}

	private boolean showLines = true;
	private boolean hoverEfect = false;
	private Map<LineColor, Color> lineColors = new EnumMap<>(LineColor.class);

	public NetworkForm() {
		this(null);
	}

	@SuppressWarnings("OverridableMethodCallInConstructor")
	public NetworkForm(CompiledNetwork network) {
		this.errorLabel.addPropertyChangeListener("text", evt -> {
			this.firePropertyChange(ERROR_TEXT_PROPERTY, evt.getOldValue(), evt.getNewValue());
		});
		setLayout(new GridBagLayout());
		setOpaque(false);
		setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240)));
		add(errorLabel, errorConstrains);
		setNetwork(network);
	}

	private void checkPoints() {
		if (!pointsRequiresUpdates) {
			return;
		}
		if (!showLines) {
			return;
		}
		List<Layer> layers = network.getLayers();
		int currentLayerCount = layers.get(0).getSize();
		int previousLayerCount = 0;
		for (int i = 1; i < layers.size(); i++) {
			Layer layer = layers.get(i);
			this.targetPoints[i] = new Point[layer.getSize()][];
			this.sendingPoints[i] = new Point[layer.getSize()][];
			List<Cell> cells = layer.getCells();
			for (int j = 0; j < cells.size(); j++) {
				Cell cell = cells.get(j);
				CellForm outputCell = outputCells[currentLayerCount + j];
				this.targetPoints[i][j] = Arrays.stream(outputCell
						.getCalculatedLinePoints(leftToRightView))
						.map(p -> SwingUtilities.convertPoint(outputCell, p, this)).toArray(Point[]::new);
				this.sendingPoints[i][j] = new Point[cell.getInputSize()];
				for (int k = 0; k < cell.getInputSize(); k++) {
					JComponent com = outputCells[previousLayerCount + k];
					if (com == null) {
						assert previousLayerCount == 0;
						com = input[k];
					}
					if (leftToRightView) {
						this.sendingPoints[i][j][k] = SwingUtilities.convertPoint(com,
								new Point(com.getWidth(), com.getHeight() / 2), this);
					} else {
						this.sendingPoints[i][j][k] = SwingUtilities.convertPoint(com,
								new Point(com.getWidth() / 2, com.getHeight()), this);
					}
				}
			}
			previousLayerCount = currentLayerCount;
			currentLayerCount += layer.getSize();
		}
	}

	private void initComponents() {
		int totalCellCount = 0;
		for (Layer l : network.getLayers()) {
			JPanel cellContainer = new JPanel();
			cellContainer.setLayout(new GridBagLayout());
			cellContainer.setOpaque(false);
			for (Cell c : l.getCells()) {
				JComponent container;
				if (l.getIndex() == 0) {
					container = input[totalCellCount] = new InputCellForm(c, this::inputChanged);
				} else {
					container = outputCells[totalCellCount] = new CellForm(c, this::updateWeigth);
					container.addMouseListener(new HoverMarker(c.getCellNumber(), c.getLayer()));
				}
				container.addComponentListener(invalidator);
				totalCellCount++;
			}
			cellContainer.addComponentListener(invalidator);
			panelLayers[l.getIndex()] = cellContainer;

		}
	}

	private void removeLayout() {
		int totalCellCount = 0;
		for (int layerNumber = 0; layerNumber < panelLayers.length; layerNumber++) {
			JPanel cellContainer = panelLayers[layerNumber];
			int cellAmount = network.getLayer(layerNumber).getSize();
			for (int cellNumber = 0; cellNumber < cellAmount; cellNumber++) {
				JComponent container;
				if (layerNumber == 0) {
					container = input[totalCellCount];
				} else {
					container = outputCells[totalCellCount];
				}
				cellContainer.remove(container);
				totalCellCount++;
			}
			this.remove(cellContainer);
		}
	}

	private void makeLayout() {
		int xMultiplier = leftToRightView ? 0 : 1;
		int yMultiplier = leftToRightView ? 1 : 0;
		GridBagConstraints layer = new GridBagConstraints();
		layer.insets = new Insets(
				xMultiplier * 30 + 4, yMultiplier * 30 + 4,
				xMultiplier * 30 + 4, yMultiplier * 30 + 4);
		layer.gridx = 0;
		layer.gridy = 0;
		layer.anchor = centeredCells ? GridBagConstraints.CENTER : GridBagConstraints.NORTHWEST;
		GridBagConstraints cellConstrains = new GridBagConstraints();
		cellConstrains.insets = new Insets(2, 2, 2, 2);
		int totalCellCount = 0;
		for (int layerNumber = 0; layerNumber < panelLayers.length; layerNumber++) {
			JPanel cellContainer = panelLayers[layerNumber];
			int cellAmount = network.getLayer(layerNumber).getSize();
			for (int cellNumber = 0; cellNumber < cellAmount; cellNumber++) {
				JComponent container;
				if (layerNumber == 0) {
					container = input[totalCellCount];
				} else {
					container = outputCells[totalCellCount];
				}
				cellConstrains.gridy = yMultiplier * cellNumber;
				cellConstrains.gridx = xMultiplier * cellNumber;
				cellConstrains.fill = GridBagConstraints.BOTH;
				cellContainer.add(container, cellConstrains);
				totalCellCount++;
			}
			layer.gridx = yMultiplier * layerNumber;
			layer.gridy = xMultiplier * layerNumber;
			this.add(cellContainer, layer);
		}
		this.revalidate();
	}

	private void remakeLayout() {
		if (network == null) {
			return;
		}
		removeLayout();
		makeLayout();
	}

	private void updateNetwork() {
		if (shouldUpdate) {
			return;
		}
		if (runningUpdate) {
			shouldUpdate = true;
			return;
		}
		runningUpdate = true;
		startUpdateTask();
	}
	
	public void updateWeigth(Layer layer) {
		if(layer != null)
			updateLines(layer);
		updateNetwork();
	}

	public void updateLines(Layer layer) {
		if (!showLines)
			return;
		JPanel panel = this.panelLayers[layer.getIndex()];
		JPanel previousPanel = this.panelLayers[layer.getIndex() - 1];
		if (leftToRightView) {
			this.repaint(
					previousPanel.getX() + previousPanel.getWidth(),
					Math.min(previousPanel.getY(), panel.getY()),
					panel.getX() - previousPanel.getX() + previousPanel.getWidth(),
					Math.max(previousPanel.getHeight(), panel.getHeight()));
		} else {
			this.repaint(
					Math.min(previousPanel.getX(), panel.getX()),
					previousPanel.getY() + previousPanel.getHeight(),
					Math.max(previousPanel.getWidth(), panel.getWidth()),
					panel.getY() - previousPanel.getY() + previousPanel.getHeight());
		}
	}

	public void inputChanged() {
		for (int i = 0; i < input.length; i++) {
			this.inputArray[i] = this.input[i].getValue();
		}
		updateNetwork();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (network == null) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g;
		Stroke previousStroke = g2.getStroke();
		Color previousColor = g2.getColor();
		try {
			g.setColor(new Color(180, 180, 180));
			g.drawString("http://github.com/ferrybig/neuralnetwork", 5, getHeight() - 10);

			if (!showLines) {
				return;
			}
			checkPoints();

			Iterator<Layer> layers = network.getLayers().iterator();
			layers.next(); // Skip input layer
			Iterator<Cell> cells;
			do {
				Layer layer = layers.next();
				cells = layer.getCells().iterator();
				do {
					Cell cell = cells.next();
					Point[] toPoints = this.targetPoints[layer.getIndex()][cell.getCellNumber()];
					Point[] fromPoints = this.sendingPoints[layer.getIndex()][cell.getCellNumber()];
					for (int i = 0; i < cell.getInputSize(); i++) {
						double weigth = cell.getWeigth(i);
						Color color;
						double strokeSize = Math.max(Math.log10(Math.abs(weigth * 10)), 1);
						if(hoverEfect && layer.equals(hoverLayer)) {
							if(hoverIndex == cell.getCellNumber()) {
								if (weigth > 1) {
									color = LineColor.POSITIVE_OUTPUT_SELECTED.getFromMap(lineColors);
								} else if (weigth < -1) {
									color = LineColor.NEGATIVE_OUTPUT_SELECTED.getFromMap(lineColors);
								} else {
									color = LineColor.NEUTRAL_OUTPUT_SELECTED.getFromMap(lineColors);
								}
							} else {
								if (weigth > 1) {
									color = LineColor.POSITIVE_OUTPUT_UNSELECTED.getFromMap(lineColors);
								} else if (weigth < -1) {
									color = LineColor.NEGATIVE_OUTPUT_UNSELECTED.getFromMap(lineColors);
								} else {
									color = LineColor.NEUTRAL_OUTPUT_UNSELECTED.getFromMap(lineColors);
								}
							}
						} else {
							if (weigth > 1) {
								color = LineColor.POSITIVE_OUTPUT.getFromMap(lineColors);
							} else if (weigth < -1) {
								color = LineColor.NEGATIVE_OUTPUT.getFromMap(lineColors);
							} else {
								color = LineColor.NEUTRAL_OUTPUT.getFromMap(lineColors);
							}
						}
						g2.setColor(color);
						g2.setStroke(new BasicStroke((float) strokeSize));
						g2.drawLine(
								(int) toPoints[i].getX(), (int) toPoints[i].getY(),
								(int) fromPoints[i].getX(), (int) fromPoints[i].getY());
					}
				} while (cells.hasNext());
			} while (layers.hasNext());
		} finally {
			g2.setStroke(previousStroke);
			g2.setColor(previousColor);
		}
	}

	private void startUpdateTask() {
		if (network != null) {
			new UpdateWorker().execute();
		}
	}

	public void updateNetworkWeigths() {
		this.inputChanged();
		Arrays.stream(this.outputCells).filter(Objects::nonNull).forEach(CellForm::updateWeigth);
		this.repaint();
	}

	@Override
	public void print(Graphics g) {
		boolean opaque = this.isOpaque();
		Color color = this.getBackground();
		setOpaque(true);
		setBackground(Color.white);
		try {
			super.print(g);
		} finally {
			setOpaque(opaque);
			setBackground(color);
		}
	}

	public String getErrorText() {
		return errorLabel.getText();
	}

	/**
	 * @param text 
	 * @beaninfo
     *        bound: true
     *  description: String that displays when the network == null
	 */
	public void setErrorText(String text) {
		errorLabel.setText(text);
		// PCS delegated in constructor
	}

	public CompiledNetwork getNetwork() {
		return network;
	}

	/**
	 * @param network
	 * @beaninfo
     *        bound: true
     *  description: Compiled network that this NetworkForm shows
	 */
	public void setNetwork(CompiledNetwork network) {
		if (network == this.network) {
			return;
		}
		CompiledNetwork old = this.network;
		this.network = network;
		hoverIndex = -1;
		if (old != null) {
			this.removeLayout();
		} else {
			this.remove(errorLabel);
		}
		if (network != null) {
			this.input = new InputCellForm[network.getInputSize()];
			this.inputArray = new double[network.getInputSize()];
			this.outputCells = new CellForm[network.getMaxCacheSize()];
			this.calculationCache = new double[network.getMaxCacheSize()];
			this.outputArray = new double[network.getOutputSize()];
			this.targetPoints = new Point[network.getLayerCount()][][];
			this.sendingPoints = new Point[network.getLayerCount()][][];
			this.panelLayers = new JPanel[network.getLayerCount()];
			this.initComponents();
			this.updateNetwork();
			this.makeLayout();
		} else {
			this.input = null;
			this.inputArray = null;
			this.outputCells = null;
			this.calculationCache = null;
			this.outputArray = null;
			this.targetPoints = null;
			this.sendingPoints = null;
			this.panelLayers = null;
			this.add(errorLabel, errorConstrains);
		}
		this.firePropertyChange(NETWORK_PROPERTY, old, network);
	}

	public Map<LineColor, Color> getLineColors() {
		return lineColors;
	}

	public void setLineColors(Map<LineColor, Color> lineColors) {
		if(this.lineColors == lineColors)
			return;
		Map<LineColor, Color> old = this.lineColors;
		this.lineColors = lineColors;
		this.firePropertyChange(LINE_COLORS_PROPERTY, old, lineColors);
		if(network != null && showLines)
			this.network.getLayers().stream().skip(1).forEach(this::updateLines);
	}

	public boolean isCenteredCells() {
		return centeredCells;
	}

	/**
	 * @param centeredCells
	 * @beaninfo
     *        bound: true
     *  description: Should this NetworkForm try to center the neural network cells
	 */
	public void setCenteredCells(boolean centeredCells) {
		if (this.centeredCells == centeredCells) {
			return;
		}
		this.centeredCells = centeredCells;
		this.firePropertyChange(CENTERED_CELLS_PROPERTY, !centeredCells, centeredCells);
		remakeLayout();

	}

	public boolean isLeftToRightView() {
		return leftToRightView;
	}

	/**
	 * @param leftToRightView
	 * @beaninfo
     *        bound: true
     *  description: Should this NetworkForm try show the cells from the
	 *               left to rigth instead of top to bottom
	 */
	public void setLeftToRightView(boolean leftToRightView) {
		if (this.leftToRightView == leftToRightView) {
			return;
		}
		this.leftToRightView = leftToRightView;
		this.firePropertyChange(LEFT_TO_RIGHT_VIEW_PROPERTY, !leftToRightView, leftToRightView);
		remakeLayout();
	}

	public boolean isShowLines() {
		return showLines;
	}

	/**
	 * @param showLines
	 * @beaninfo
     *        bound: true
     *  description: Show the connections between cells
	 */
	public void setShowLines(boolean showLines) {
		if (this.showLines == showLines) {
			return;
		}
		this.showLines = showLines;
		this.firePropertyChange(SHOW_LINES_PROPERTY, !showLines, showLines);
		this.repaint();
	}

	public boolean isHoverEfect() {
		return hoverEfect;
	}

	public void setHoverEfect(boolean hoverEfect) {
		if (this.hoverEfect == hoverEfect) {
			return;
		}
		this.hoverEfect = hoverEfect;
		this.firePropertyChange(HOVER_EFFECT_PROPERTY, !hoverEfect, hoverEfect);
		repaint();
	}

	private final ComponentListener invalidator = new ComponentListener() {

		@Override
		public void componentHidden(ComponentEvent e) {
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			pointsRequiresUpdates = true;
			if (showLines) {
				NetworkForm.this.repaint();
			}
		}

		@Override
		public void componentResized(ComponentEvent e) {
			pointsRequiresUpdates = true;
			if (showLines) {
				NetworkForm.this.repaint();
			}
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}
	};

	private class UpdateWorker extends SwingWorker<Object, Object> {

		private final CompiledNetwork network = NetworkForm.this.network;

		@Override
		protected Object doInBackground() throws Exception {
			network.resolveNetworkSequential(inputArray, outputArray, calculationCache);
			return null;
		}

		@Override
		protected void done() {
			super.done();
			try {
				this.get();
				if (NetworkForm.this.network == network) {
					for (int i = 0; i < calculationCache.length; i++) {
						if (outputCells[i] != null) {
							outputCells[i].setOutput(calculationCache[i]);
						}
					}
				}
			} catch (InterruptedException | ExecutionException ex) {
				JOptionPane.showMessageDialog(NetworkForm.this, ex, "Error while calculation", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (shouldUpdate) {
					shouldUpdate = false;
					startUpdateTask();
				} else if (runningUpdate) {
					runningUpdate = false;
				}
			}
		}
	}

	@Override
	public Dimension getMinimumSize() {
		if(!this.isMinimumSizeSet())
			return super.getPreferredSize();
		else
			return super.getMinimumSize();
	}

	private class HoverMarker extends MouseAdapter {

		private final int index;
		private final Layer layer;

		public HoverMarker(int index, Layer layer) {
			this.index = index;
			this.layer = layer;
		}

		@Override
		public void mouseExited(MouseEvent e) {
			super.mouseExited(e);
			if(NetworkForm.this.hoverIndex == index) {
				NetworkForm.this.hoverIndex = -1;
				NetworkForm.this.hoverLayer = null;
				NetworkForm.this.updateLines(layer);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			super.mouseEntered(e);
			NetworkForm.this.hoverIndex = index;
			NetworkForm.this.hoverLayer = layer;
			NetworkForm.this.updateLines(layer);
		}

	}
}
