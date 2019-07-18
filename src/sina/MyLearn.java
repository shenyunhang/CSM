package sina;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;

public class MyLearn {
	public weka.classifiers.lazy.IBk knn;
	public weka.classifiers.functions.SMO smo;
	public weka.classifiers.meta.AdaBoostM1 adaboost;
	public weka.classifiers.bayes.NaiveBayes naivebayes;
	public weka.classifiers.trees.J48 j48;

	void KNN(MyData mydata) {
		knn = new IBk();
		// String[] options = new String[2];
		// options[0] = "-K";
		// options[1] = "3";
		knn.setKNN(3);
		try {
			knn.buildClassifier(mydata.dataset);
			Evaluation eval = new Evaluation(mydata.dataset);
			eval.evaluateModel(knn, mydata.dataset);
			System.out.println(eval.toSummaryString("\nResults\n\n", false));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void SMO(MyData mydata) {
		smo = new SMO();

		try {
			smo.buildClassifier(mydata.dataset);
		
			Evaluation eval = new Evaluation(mydata.dataset);
			eval.evaluateModel(smo, mydata.dataset);
			System.out.println(eval.toSummaryString("\nResults\n\n", false));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void AdaBoost(MyData mydata) {
		adaboost = new AdaBoostM1();

		String[] options = new String[4];
		options[0] = "-W";
		options[1] = "weka.classifiers.lazy.IBk";
		options[2] = "-- -K";
		options[3] = "3";

		try {
			adaboost.setOptions(options);

			adaboost.buildClassifier(mydata.dataset);
			//Evaluation eval = new Evaluation(mydata.dataset);
			//eval.evaluateModel(adaboost, mydata.dataset);
			//System.out.println(eval.toSummaryString("\nResults\n\n", false));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void TreeJ48(MyData mydata) {
		j48 = new J48();

		try {
			j48.buildClassifier(mydata.dataset);
			Evaluation eval = new Evaluation(mydata.dataset);
			eval.evaluateModel(j48, mydata.dataset);
			System.out.println(eval.toSummaryString("\nResults\n\n", false));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void NaiveBayes(MyData mydata) {
		naivebayes = new weka.classifiers.bayes.NaiveBayes();

		try {
			naivebayes.buildClassifier(mydata.dataset);
			Evaluation eval = new Evaluation(mydata.dataset);
			eval.evaluateModel(naivebayes, mydata.dataset);
			System.out.println(eval.toSummaryString("\nResults\n\n", false));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
