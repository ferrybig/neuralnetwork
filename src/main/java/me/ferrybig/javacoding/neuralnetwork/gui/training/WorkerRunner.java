package me.ferrybig.javacoding.neuralnetwork.gui.training;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import me.ferrybig.javacoding.neuralnetwork.CompiledNetwork;
import me.ferrybig.javacoding.neuralnetwork.error.ErrorCalculator;

public class WorkerRunner {

	private final IntConsumer attempts;
	private int attemptsField;
	private int attemptsStartedField;
	private final IntConsumer attemptsRunning;
	private int attemptsRunningField;
	private NetworkReceiver done;
	private final ErrorCalculator error;
	private final long maxAttempts;
	private final long maxIterations;
	private final float momentum;
	private final CompiledNetwork network;
	private final JPanel panel;
	private ThreadPoolExecutor pool;
	private final Iterator<Random> random;
	private boolean stopped = false;
	private final double targetError;
	private final AtomicInteger taskNumber = new AtomicInteger(0);
	private final int threads;
	private final WorkerForm[] forms;
	private final boolean[] hasThread;

	public WorkerRunner(CompiledNetwork network, float momentum, double targetError,
			Iterator<Random> random, JPanel panel,
			long maxIterations, long maxAttempts, int threads, ErrorCalculator error,
			IntConsumer attempts, IntConsumer attemptsRunning
	) {
		this.network = network;
		this.momentum = momentum;
		this.targetError = targetError;
		this.random = random;
		this.panel = panel;
		this.maxIterations = maxIterations;
		this.maxAttempts = maxAttempts;
		this.threads = threads;
		this.error = error;
		this.attempts = attempts;
		this.attemptsRunning = attemptsRunning;
		this.forms = new WorkerForm[threads];
		this.hasThread = new boolean[threads];
		double initialError = error.calculate(network, new double[network.getOutputSize()],
				new double[network.getMinCacheSize()]);
		for (int i = 0; i < threads; i++) {
			WorkerForm form = new WorkerForm();
			this.forms[i] = form;
			this.panel.add(form);
			form.setTargetError(targetError);
			form.setInitialError(initialError);
			form.setError(initialError);
			form.setMaxIterations(maxIterations);
		}

	}

	public ThreadPoolExecutor getPool() {
		return pool;
	}

	public void start(NetworkReceiver done) {
		assert SwingUtilities.isEventDispatchThread();
		this.done = done;
		if (pool != null || stopped) {
			throw new IllegalStateException();
		}
		final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
		pool = new ThreadPoolExecutor(threads, threads, 0, TimeUnit.DAYS, new LinkedBlockingQueue<>(),
				(r) -> {
					Thread t = defaultFactory.newThread(r);
					t.setName(this.getClass().getSimpleName() + "-" + t.getName());
					t.setDaemon(true);
					t.setPriority(t.getPriority() - 1);
					return t;
				});
		for (int i = 0; i < threads; i++) {
			startNewTask();
		}
	}

	private void startNewTask() {
		assert SwingUtilities.isEventDispatchThread();
		long task = taskNumber.incrementAndGet();
		if (task > maxAttempts) {
			return;
		} else if (task == maxAttempts) {
			this.pool.shutdown(); // Shutdown normaly so all pending tasks are still processed
			return;
		}
		++attemptsStartedField;
		WorkerFormRunner form = new WorkerFormRunner(task, random.next(), network.copy(), error, momentum,
				targetError, maxIterations, this::workerStart, this::workerDone);
		try {
			this.pool.execute(form);
		} catch (RejectedExecutionException e) {
			if (!this.pool.isShutdown()) {
				throw e;
			}
		}
	}

	public void stop() {
		assert SwingUtilities.isEventDispatchThread();
		if (this.pool != null) {
			this.pool.shutdownNow();
		}
		this.stopped = true;
		if (this.done != null) {
			this.done.finished();
		}
	}

	private void workerDone(WorkerFormRunner w) {
		assert SwingUtilities.isEventDispatchThread();
		assert attemptsRunningField > 0;
		int running = --attemptsRunningField;
		assert running >= 0;
		WorkerForm gui = w.getGui();
		Throwable exception = w.getException();
		// Loop should never be broken because there SHOULD always be a used gui
		for (int i = 0; true; i++) {
			if (forms[i] == gui) {
				hasThread[i] = false;
				break;
			}
		}
		if (exception != null) {
			exception.printStackTrace();
		}
		this.attemptsRunning.accept(running);
		if (!stopped) {
			w.sendTo(done);
		}
		if (running == 0 && this.attemptsStartedField >= this.maxAttempts) {
			if (!stopped) {
				stop();
			}
		}

	}

	private void workerStart(WorkerFormRunner w) {
		assert SwingUtilities.isEventDispatchThread();
		assert threads == hasThread.length;
		// Loop should never be broken normally because there SHOULD always be a free space
		for (int i = 0; true; i++) {
			if (!hasThread[i]) {
				hasThread[i] = true;
				WorkerForm form = forms[i];
				w.setGui(form);
				form.setTitle("Task " + w.getTaskNumber());
				break;
			}
		}
		startNewTask();
		this.attempts.accept(++attemptsField);
		this.attemptsRunning.accept(++attemptsRunningField);
	}

	public interface NetworkReceiver {

		public void processedNetwork(CompiledNetwork network, boolean success,
				long iteration, double error);

		public default void finished() {
		}
	}
}
