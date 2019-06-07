package com.sathya.extractinformation;

import org.bytedeco.javacpp.*;

import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.Classification;
import com.sathya.extractinformation.imageprocess.PreProcessImage;

import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ExpenseTaggerController {
	static ArrayList<String> items = new ArrayList<String>();
	static ArrayList<Double> itemAmt = new ArrayList<Double>();///
	static ArrayList<String> classes = new ArrayList<String>();///
	static Double totalBillAMt;
	static Map<String, Double> expenseClassesAmt = new HashMap<String, Double>();
	static {
		expenseClassesAmt.put("Beverages", 0.0);
		expenseClassesAmt.put("Cosmetics", 0.0);
		expenseClassesAmt.put("Dairy", 0.0);
		expenseClassesAmt.put("Fruits", 0.0);
		expenseClassesAmt.put("Grocery", 0.0);
		expenseClassesAmt.put("Liquor", 0.0);
		expenseClassesAmt.put("Meat", 0.0);
		expenseClassesAmt.put("Medicines", 0.0);
		expenseClassesAmt.put("Misc", 0.0);
		expenseClassesAmt.put("Pet", 0.0);
		expenseClassesAmt.put("Seafood", 0.0);
		expenseClassesAmt.put("cloth", 0.0);
		expenseClassesAmt.put("Vegetable", 0.0);
		expenseClassesAmt.put("Toilletries/Cleaners", 0.0);
	}

	public static void main(String[] args) {
		BytePointer outText;
		NaturalLanguageClassifier service = new NaturalLanguageClassifier();
		service.setUsernameAndPassword("4cd2533d-7537-4b9e-afd6-1779df0a48c6", "eiPNS2HeYBx6");

		PreProcessImage.preProcess("images/walmart.jpeg");

		@SuppressWarnings("resource")
		TessBaseAPI api = new TessBaseAPI();
		// Initialize tesseract-ocr with English, without specifying tessdata path

		if (api.Init("/Users/sathya/Downloads/informationextraction/tessdata", "eng") != 0) {
			System.err.println("Could not initialize tesseract.");
			System.exit(1);
		}
		// Open input image with leptonica library
		PIX image = pixRead(args.length > 0 ? args[0] : "preprocess/adaptive_threshold.png");
		// System.out.println(image);
		api.SetImage(image);
		// Get OCR result
		outText = api.GetUTF8Text();
		String[] itemisedBill = outText.getString().split("\n");
		System.out.println(outText.getString());
		totalBillAMt = 0.0;
		for (String sr : itemisedBill) {

			if (sr.startsWith("E")) {
				String[] billElements = sr.split(" ");
				items.add(billElements[2]);
				Double itemAmount = Double.parseDouble(billElements[3]);
				itemAmt.add(itemAmount);
				totalBillAMt += itemAmount;
				Classification classification = service.classify("33badcx272-nlc-24169", billElements[2]).execute();
				//System.out.println(classification.toString());
				String tag = classification.getTopClass();
				expenseClassesAmt.put(tag, (expenseClassesAmt.get(tag) + itemAmount));
				;
				classes.add(tag);
			}
		}
		System.out.println("************* ************ ************");
		System.out.println("************* ************ ************");
		System.out.println("************* Classified Bill ************");
		for (int i = 0; i < items.size(); i++)
			System.out.println(items.get(i) + " >> " + itemAmt.get(i) + " >> " + classes.get(i));
		// Destroy used object and release memory
		System.out.println("************* ************ ************");
		System.out.println("************* ************ ************");

		System.out.println("************* Expensify Report ************");
		for (Entry<String, Double> entry : expenseClassesAmt.entrySet()) {
			if (entry.getValue().intValue() != 0)
				System.out.println(entry.getKey() + " : " + entry.getValue());
		}
		System.out.println( "Total Before Tax : " + totalBillAMt);

		System.out.println("************* ************ ************");
		System.out.println("************* ************ ************");
		api.End();
		outText.deallocate();
		pixDestroy(image);

	}
}