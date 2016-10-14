
package me.ferrybig.javacoding.neuralnetwork.gui.training;

import java.beans.PropertyChangeEvent;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import me.ferrybig.javacoding.neuralnetwork.CompiledNetwork;
import me.ferrybig.javacoding.neuralnetwork.error.ErrorCalculator;
import me.ferrybig.javacoding.neuralnetwork.trainer.RandomNetworkTrainer;

public class WorkerFormRunner implements Runnable {
	private final float momentum;
	private final CompiledNetwork network;
	private final Random random;
	private final long maxIterations;
	private final Consumer<? super WorkerFormRunner> starter;
	private final Consumer<? super WorkerFormRunner> done;
	private final double targetError;
	private WorkerForm gui;
	private final long taskNumber;
	private final ErrorCalculator error;
	private volatile long iteration = 0;
	private volatile double errorRate = Double.POSITIVE_INFINITY;
	private volatile double initialErrorLog = 0;
	private boolean success = false;
	private Throwable exception = null;
	private final SwingWorker<Boolean, Double> worker = new SwingWorker<Boolean, Double>() {
		@Override
		protected Boolean doInBackground() throws Exception {
			RandomNetworkTrainer trainer = new RandomNetworkTrainer(random, momentum, targetError, maxIterations);
			boolean success = trainer.train(network, error, (i, err) -> {
				iteration = i;
				errorRate = err;
				if(i == 0) {
					initialErrorLog = Math.log(err);
				}
				setProgress((int)(i * 100 / maxIterations));
			});
			return success;
		}

		@Override
		protected void done() {
			super.done();
			try {
				success = this.get();
			} catch (InterruptedException | ExecutionException e) {
				exception = e;
			} finally {
				done.accept(WorkerFormRunner.this);
			}
		}

	};

	public WorkerFormRunner(long taskNumber, Random random,
			CompiledNetwork network, ErrorCalculator error,
			float momentum, double targetError, long maxIterations,
			Consumer<? super WorkerFormRunner> starter, Consumer<? super WorkerFormRunner> done) {
		this.maxIterations = maxIterations;
		this.starter = starter;
		this.done = done;
		this.taskNumber = taskNumber;
		this.random = random;
		this.network = network;
		this.error = error;
		this.momentum = momentum;
		this.targetError = targetError;
		initWorker();
	}

	public CompiledNetwork getNetwork() {
		return network;
	}

	public long getTaskNumber() {
		return taskNumber;
	}

	public boolean isSuccess() {
		return success;
	}

	public Throwable getException() {
		return exception;
	}
	
	public WorkerForm getGui() {
		return gui;
	}

	public void setGui(WorkerForm gui) {
		this.gui = gui;
	}

	private void propertyChange(PropertyChangeEvent evt) {
		if ("state".equals(evt.getPropertyName())) {
			if (evt.getOldValue().equals(SwingWorker.StateValue.PENDING)
					&& evt.getNewValue().equals(SwingWorker.StateValue.STARTED)) {
				starter.accept(this);
			}
		} else if ("progress".equals(evt.getPropertyName())) {
			if(this.gui == null || this.worker.isDone())
				return;
			long itr = this.iteration;
			double err = this.errorRate;
			this.gui.setError(err);
			this.gui.setIterations(itr);
		}
	}

	public double getErrorRate() {
		return errorRate;
	}

	public double getInitialErrorLog() {
		return initialErrorLog;
	}

	private void initWorker() {
		this.worker.addPropertyChangeListener(evt -> {
			if (SwingUtilities.isEventDispatchThread()) {
				propertyChange(evt);
			} else {
				SwingUtilities.invokeLater(() -> {
					propertyChange(evt);
				});
			}
		});
	}

	@Override
	public void run() {
		this.worker.run();
	}

	@Override
	public String toString() {
		return "WorkerForm [ Resolve task " + taskNumber + " ]";
	}

	public void sendTo(WorkerRunner.NetworkReceiver receiver) {
		receiver.processedNetwork(network, success, iteration, errorRate);
	}
}
