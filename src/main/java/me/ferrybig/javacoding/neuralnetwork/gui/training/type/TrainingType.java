
package me.ferrybig.javacoding.neuralnetwork.gui.training.type;

import java.util.Objects;
import java.util.function.Function;
import me.ferrybig.javacoding.neuralnetwork.CompiledNetwork;

public class TrainingType {

	private final String name;
	private final Function<CompiledNetwork, Training> createComponent;

	public TrainingType(String name, Function<CompiledNetwork, Training> createComponent) {
		this.name = Objects.requireNonNull(name, "name == null");
		this.createComponent = Objects.requireNonNull(createComponent, "createComponent == null");
	}

	public String getName() {
		return name;
	}

	public Function<CompiledNetwork, Training> createComponent() {
		return createComponent;
	}

	@Override
	public String toString() {
		return name;
	}
}
