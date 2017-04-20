# Neuralnetwork
This is a neural network implementation in java, it uses double arrays in the background to provide quick calculations.

The network is build to simulate a simple model of a network, it supports almost unlimited layers of processing cells, and is only bound by the computation limit of the integer size of connections, because it uses standard java arrays to store the data.

## Design
The design of this network is pretty simple, instead of going the classes way, we have a few large double arrays, this provides increased performance and decreased memory usage, at the expance of readability.

These double arrays are then read in simple for loops, the indexes are calculated by the layer sizes, and this gives very efficient array access times.

## Usage

### Gui

Start the gui using `java -cp neuralnetwork.jar me.ferrybig.javacoding.neuralnetwork.gui.network.ApplicationForm`

### API

This project is designed to be used from the api, a few examples are given below.

#### training example:

    CompiledNetwork network = constructNetwork();

    List<SupervisedTrainingSet> training = Arrays.asList(
        new SupervisedTrainingSet(new double[]{1, 1}, new double[]{0}),
        new SupervisedTrainingSet(new double[]{1, 0}, new double[]{1}),
        new SupervisedTrainingSet(new double[]{0, 1}, new double[]{1}),
        new SupervisedTrainingSet(new double[]{0, 0}, new double[]{0})
    );
  
  
    double[] out = new double[network.getOutputSize()];
    // Create a calculation cache, so we don't produce any garbage during our run
    double[] cache = new double[network.getMinCacheSize()];
    Random random = new Random();
    
    RandomNetworkTrainer trainer = new RandomNetworkTrainer(random, 0.1f,
        0.0001, 10_000);
    SupervisedErrorCalculator errorCalculator = new SupervisedErrorCalculator(training);
        boolean success = trainer.train(network, errorCalculator, (i, err) -> {
        if ((i % (Math.pow(10, (int) Math.log10(i)) * 5) == 0) || (i == 0)) {
            System.out.println("Iteration " + i + " error " + err);
        }
    });
    if (success) {
        System.out.println("Succeded iteration");
    } else {
        System.out.println("Failed iteration");
    }
    System.out.println("Final error: " + errorCalculator.calculate(network, out, cache));
    System.out.println("Network: " + network.debug());
  
  More training examples: https://github.com/ferrybig/neuralnetwork/tree/master/src/test/java/me/ferrybig/javacoding/neuralnetwork/data

## Examples of networks:

### XOR with 4 inputs solved by the network
[![](https://i.imgur.com/8Hmz3hc.png)](https://i.imgur.com/8Hmz3hc.png)

### XOR with 2 inputs solved by the network
[![](https://i.imgur.com/IN8Rz7B.png)](https://i.imgur.com/IN8Rz7B.png)
