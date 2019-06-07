package com.sathya.extractinformation.imageprocess;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;

public class PreProcessImage {
	public  static void preProcess(String imgPath) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println(imgPath);
	Mat img = new Mat();
	img = Imgcodecs.imread(imgPath); 
	Imgcodecs.imwrite("preprocess/True_Image.png", img);
	// Gray Scale
	
	Mat imgGray = new Mat();
	Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
	Imgcodecs.imwrite("preprocess/Gray.png", imgGray);
	// Gaussian Blur
	
	Mat imgGaussianBlur = new Mat(); 
	Imgproc.GaussianBlur(imgGray,imgGaussianBlur,new Size(3, 3),0);
	Imgcodecs.imwrite("preprocess/gaussian_blur.png", imgGaussianBlur);  
	// Adaptive Threshold
	Mat imgAdaptiveThreshold = new Mat();
	Imgproc.adaptiveThreshold(imgGaussianBlur, imgAdaptiveThreshold, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C ,Imgproc.THRESH_BINARY, 99, 4);
	Imgcodecs.imwrite("preprocess/adaptive_threshold.png", imgAdaptiveThreshold);
	
	
}}