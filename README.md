Car Evaluation
======

This is a Mahout example of classification using the NaiveBayesClassifier.
I'm using the dataset found here: https://archive.ics.uci.edu/ml/datasets/Car+Evaluation
After running this Java program, use the following commands to run and evaluate the classifier:


### push it to hdfs
hadoop fs -put car-seq car-seq

### convert it to sparse
mahout seq2sparse -i car-seq -o car-sparse

### split into training and test test (70-30 split)
mahout split -i car-sparse/tfidf-vectors --trainingOutput car-train-vectors --testOutput car-test-vectors --randomSelectionPct 40 --overwrite --sequenceFiles -xm sequential


### train it on the train-data
mahout trainnb -i car-train-vectors -el -li labelindex -o model -ow -c

### test it on the train-data
mahout testnb -i car-train-vectors -m model -l labelindex -ow -o car-testing -c

### finally, test it on the test-data
mahout testnb -i car-test-vectors -m model -l labelindex -ow -o car-testing -c
