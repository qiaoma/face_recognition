

package edu.calstatela.cs560.qiao.hw4;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Range;

public class FaceDetection {

	private JFrame userFrame;
	private JPanel oriFacePanel;
	private JPanel aveFacePanel;
	private JPanel eigFacePanel;
	private JPanel userPanel, displayPanel;
	private JTextField jtfEigen, jtfFaceNo, jtfImageNo, jtfTest;
	private JComboBox<String> jcbMethod;
	private JLabel jblSelectedFace;
	private JButton jbtDisplay;
	private Mat eigenvectors, diffMat, mean;
	private int rows, cols;
	private int numClick;

	public FaceDetection() {
		numClick = 0;
		oriFacePanel = new JPanel();
		oriFacePanel.setLayout(new GridLayout(0, 8));

		aveFacePanel = new JPanel();
		aveFacePanel.setLayout(new GridLayout());

		eigFacePanel = new JPanel();
		eigFacePanel.setLayout(new GridLayout(0, 5));

		userPanel = new JPanel();
		userPanel.setLayout(new GridLayout(0, 2));
		
		displayPanel = new JPanel();
		displayPanel.setLayout(new GridLayout(0, 4));

		addComponent();
		headerInfo("att_faces/s1/1.pgm");
		faceDetection();
		setOriFaceFrame();
		setAveFaceFrame();
		setEigFaceFrame();
		setUserFrame();
	}

	// -------------------DisplayListener class---------------------
	class DisplayListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(numClick < 10){
				numClick++;
			}
			String userFaceNum = jtfFaceNo.getText();
			String userImageNum = jtfImageNo.getText();
			faceRecognition(true, userFaceNum, userImageNum);
		}
	}

	// -------------------DisplayListener class---------------------
		class TestListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String numTestString = jtfTest.getText();
				String method = (String) jcbMethod.getSelectedItem();
				int numTest = Integer.parseInt(numTestString);			
				int accurate = 0;
				System.out.println(method);
				for(int i = 0; i < numTest; i++){
					int userFaceNum = 1+(int)(Math.random()*40);
					int userImageNum = 1+(int)(Math.random()*10);
					int resultNo = faceRecognition(false, userFaceNum+"", userImageNum+"");
					System.out.println("userFaceNum: "+userFaceNum+" userImageNum: "+userImageNum+" result: "+resultNo);
					
					if(resultNo == userFaceNum){						
						accurate++;
					}	
								
				}
				double accurateRate = ((double)accurate)/numTest;
				
				System.out.println("Accurate rate: "+accurateRate);
			}
		}

		// ---------------------------------------------------------------
		
	public void addComponent() {
		JLabel jblEigen = new JLabel("Please enter a value for M (1-40):");
		JLabel jblFace = new JLabel("Please enter a person number (1-40):");
		JLabel jblImage = new JLabel("Please enter an image number of that person (1-10):");
		JLabel jblMethod = new JLabel("Please select a classification method:");
		JLabel jblTest = new JLabel("Number of testing samples: ");

		jtfEigen = new JTextField("15");
		jtfFaceNo = new JTextField("5");
		jtfImageNo = new JTextField("1");
		jtfTest = new JTextField("20");

		String[] methods = { "Nearest Mean", "Nearest Neighbor" };
		jcbMethod = new JComboBox<String>(methods);

		jbtDisplay = new JButton("Display");
		jbtDisplay.addActionListener(new DisplayListener());
		
		JButton jbtTest = new JButton("Testing with random sumples");
		jbtTest.addActionListener(new TestListener());

		userPanel.add(jblEigen);
		userPanel.add(jtfEigen);
		userPanel.add(jblFace);
		userPanel.add(jtfFaceNo);
		userPanel.add(jblImage);
		userPanel.add(jtfImageNo);
		userPanel.add(jblMethod);
		userPanel.add(jcbMethod);
		userPanel.add(jblTest);
		userPanel.add(jtfTest);
		userPanel.add(jbtDisplay);
		userPanel.add(jbtTest);
	}

	public void setOriFaceFrame() {
		JFrame frame = new JFrame("Original Face Images");
		frame.add(oriFacePanel);
		frame.setSize(900, 650);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void setAveFaceFrame() {
		JFrame frame = new JFrame("Average Face Image");
		frame.add(aveFacePanel);
		frame.setSize(500, 500);
		frame.setLocation(50, 0);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void setEigFaceFrame() {
		JFrame frame = new JFrame("Eigenface Images");
		frame.add(eigFacePanel);
		frame.setSize(500, 500);
		frame.setLocation(200, 0);
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public void setUserFrame() {
		userFrame = new JFrame("User Control Panel");
		userFrame.setLayout(new GridLayout(2, 1));
		userFrame.add(userPanel);
		userFrame.add(displayPanel);
		userFrame.setSize(800, 500);
		userFrame.setLocation(300, 0);
		userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		userFrame.setVisible(true);
	}

	public void headerInfo(String filename) {

		try {
			FileInputStream fileInput = new FileInputStream(filename);
			Scanner scanner = new Scanner(fileInput);
			scanner.nextLine();
			cols = scanner.nextInt();
			rows = scanner.nextInt();
			System.out.println("image width: " + cols + ", height: " + rows);
			fileInput.close();
			scanner.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int[][] readPGMFile(String filename) {

		int[][] pixels = null;
		try {
			FileInputStream fileInput = new FileInputStream(filename);
			DataInputStream dataInput = new DataInputStream(fileInput);
			int lineNumber = 3;
			for (int i = 0; i < lineNumber; i++) {
				dataInput.readUnsignedByte();
			}

			pixels = new int[rows][cols];
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					pixels[r][c] = dataInput.readUnsignedByte();
				}
			}

			fileInput.close();
			dataInput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pixels;
	}

	public void faceDetection() {
		System.out.println("Face detecting...");

		DrawingCanvas canvas;

		// -----------------get image sample data-------------------

		int samples_col_size = rows * cols;
		int[][] samplesData = new int[40][samples_col_size];
		int r_count = 0;
		for (int i = 1; i <= 10; i++) {
			int person = (int) (Math.random() * 40) + 1;
			for (int j = 1; j <= 4; j++) {
				String filename = "att_faces/s" + person + "/" + j + ".pgm";
				int[][] pixels = readPGMFile(filename);
				canvas = new DrawingCanvas(pixels, rows, cols);
				oriFacePanel.add(canvas);
				int c_count = 0;
				for (int r = 0; r < rows; r++) {
					for (int c = 0; c < cols; c++) {
						samplesData[r_count][c_count] = pixels[r][c];
						c_count++;
					}
				}
				r_count++;
			}
		}

		// -------------------- get average face -------------------------
		Mat samples = getMatData(samplesData, 40, samples_col_size);

		mean = new Mat();
		Mat covar = new Mat();
		Core.calcCovarMatrix(samples, covar, mean, Core.COVAR_NORMAL
				| Core.COVAR_ROWS);

		int[][] aveFace = getMeanArray(mean, rows, cols);
		canvas = new DrawingCanvas(aveFace, rows, cols);
		aveFacePanel.add(canvas);

		// -------------- get covariance------------------
		int num = 0;
		int[][] diffFace = new int[40][samples_col_size];
		for (int r = 0; r < 40; r++) {
			for (int c = 0; c < samples_col_size; c++) {
				diffFace[r][c] = samplesData[r][c]
						- ((int) (mean.get(0, num)[0]));
				num++;
			}
			num = 0;
		}
		covar = new Mat();
		diffMat = getMatData(diffFace, 40, samples_col_size);
		Core.mulTransposed(diffMat, covar, false);

		// -------------------- get eigenvalues and eigenvectors----------------
		Mat eigenvalues = new Mat();
		eigenvectors = new Mat();
		Core.eigen(covar, true, eigenvalues, eigenvectors);

		Mat finalData = new Mat();
		Core.gemm(eigenvectors, diffMat, 1, new Mat(), 0, finalData);

		for (int a = 0; a < 5; a++) {
			int[][] eigFace = new int[rows][cols];
			num = 0;
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					eigFace[r][c] = (int) finalData.get(a, num)[0];
					num++;
				}
			}
			// --------------- normalize face -----------------------------
			Mat eigFaceMat = getMatData(eigFace, rows, cols);
			Mat normFaceMat = new Mat();
			Core.normalize(eigFaceMat, normFaceMat, 0, 255, Core.NORM_MINMAX,
					CvType.CV_8UC1);

			int[][] normFace = getIntArray(normFaceMat, rows, cols);
			canvas = new DrawingCanvas(normFace, rows, cols);
			eigFacePanel.add(canvas);
		}

		// System.out.println( highEigenMat.dump() );
	}

	public int faceRecognition(boolean userDisplay, String userFaceNum, String userImageNum) {
		// ----------------------High dimensional space----------------
		
		String userEigenNum = jtfEigen.getText();			
		int eigenNum = Integer.parseInt(userEigenNum);
				
		Mat eigenvectors2 = eigenvectors.rowRange(new Range(0, eigenNum));
		Mat highEigenMat = new Mat();
		Core.gemm(eigenvectors2, diffMat, 1, new Mat(), 0, highEigenMat);

		// ---------------------Step 2: Create class information----------
		Mat[][] patternMat = new Mat[40][7];
		Mat[] meanPatternMat = new Mat[40];
		int num = 0;
		int samples_col_size = rows * cols;
		for (int i = 1; i <= 40; i++) {
			int[][] diffData = new int[1][samples_col_size];
			for (int j = 1; j <= 7; j++) {
				String filename = "att_faces/s" + i + "/" + j + ".pgm";
				int[][] pixels = readPGMFile(filename);

				for (int r = 0; r < rows; r++) {
					for (int c = 0; c < cols; c++) {
						diffData[0][num] = pixels[r][c] - (int) (mean.get(0, num)[0]);
						num++;
					}
				}
				num = 0;
				Mat diffDataMat = getMatData(diffData, 1, samples_col_size);

				patternMat[i - 1][j - 1] = new Mat();
				// Mat diffDataMatTran = diffDataMat.t();

				Mat diffDataMatTran = new Mat();
				Core.transpose(diffDataMat, diffDataMatTran);
				Core.gemm(highEigenMat, diffDataMatTran, 1, new Mat(), 0, patternMat[i - 1][j - 1]);
			}

			Mat sumMat = new Mat();
			Core.add(patternMat[i - 1][0], patternMat[i - 1][1], sumMat);
			Core.add(sumMat, patternMat[i - 1][2], sumMat);
			Core.add(sumMat, patternMat[i - 1][3], sumMat);
			Core.add(sumMat, patternMat[i - 1][4], sumMat);
			Core.add(sumMat, patternMat[i - 1][5], sumMat);
			Core.add(sumMat, patternMat[i - 1][6], sumMat);

			meanPatternMat[i - 1] = new Mat(sumMat.rows(), 1, CvType.CV_64FC1);
			for (int r = 0; r < sumMat.rows(); r++) {
				double m = sumMat.get(r, 0)[0] / 7;
				meanPatternMat[i - 1].put(r, 0, m);
			}

			//System.out.println(meanPatternMat[i - 1].dump());
		}

		// ---------------------Step 3: Query Input---------------------
		String filename = "att_faces/s" + userFaceNum + "/" + userImageNum+".pgm";
		jblSelectedFace = new JLabel("User selected face:");
		
		int[][] pixels = readPGMFile(filename);
		if(userDisplay){
			DrawingCanvas canvas = new DrawingCanvas(pixels, rows, cols);	
			displayPanel.add(jblSelectedFace);
			displayPanel.add(canvas);	
			userFrame.setSize(800, 300+100*numClick);
		}
		int[][] diffData = new int[1][samples_col_size];
		num = 0;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				diffData[0][num] = pixels[r][c] - (int) (mean.get(0, num)[0]);
				num++;
			}
		}
		
		Mat diffDataMat = getMatData(diffData, 1, samples_col_size);
		
		Mat selectPatternMat = new Mat();
		Mat diffDataMatTran = new Mat();
		Core.transpose(diffDataMat, diffDataMatTran);
		Core.gemm(highEigenMat, diffDataMatTran, 1, new Mat(), 0, selectPatternMat);
		
		//System.out.println(selectPatternMat.dump());
		
		//--------------------------Step 4: Recognition--------------------
		int methodNum = jcbMethod.getSelectedIndex();
		int recResultNo = 0;
		if(methodNum == 0){
			Mat[] diMat = new Mat[40];
			double[] di = new double[40];		
			for(int i = 0; i < 40; i++){
				diMat[i] = new Mat();
				Core.subtract(selectPatternMat, meanPatternMat[i], diMat[i]);	
				Core.pow(diMat[i], 2, diMat[i]);			
				double sum = 0;
				for(int j = 0; j < diMat[i].rows(); j++){					
					sum += diMat[i].get(j, 0)[0];
				}
				di[i] = Math.sqrt(sum);
			//	System.out.println(di[i]);
			}
			double mindi = di[0];
			int resultNo = 0;
			for(int i = 1; i < 40; i++){			
				if(mindi > di[i]){
					mindi = di[i];
					resultNo = i;
				}
			}
			
			JLabel jblResult;
			recResultNo = resultNo + 1;
			double threshold = (double) 1E7;
			if(mindi < threshold){
				if(userDisplay){
					jblResult = new JLabel("Person is identified as person "+recResultNo);
					filename = "att_faces/s" + recResultNo + "/1.pgm";
					pixels = readPGMFile(filename);		
					DrawingCanvas canvas = new DrawingCanvas(pixels, rows, cols);	
					displayPanel.add(jblResult);
					displayPanel.add(canvas);	
				}
			}else{
				if(userDisplay){
					jblResult = new JLabel("Unknow");
					JPanel spacePanel = new JPanel();
					displayPanel.add(jblResult);
					displayPanel.add(spacePanel);
				}
			}
		//	System.out.println(mindi+" "+resultImageNo );
			
			
		}else{
			int count = 0;
			Mat[] diMat = new Mat[280];
			double[] di = new double[280];		
			for(int i = 0; i < 40; i++){
				for(int j = 0; j < 7; j++){
					diMat[count] = new Mat();
					Core.subtract(selectPatternMat, patternMat[i][j], diMat[count]);	
					Core.pow(diMat[count], 2, diMat[count]);			
					double sum = 0;
					for(int r = 0; r < diMat[count].rows(); r++){					
						sum += diMat[count].get(r, 0)[0];
					}
					di[count] = Math.sqrt(sum);
					count++;
					//System.out.println(di[i][j]);
				}
			}
			Mat diMat2 = getMatData(di, 280);
			Mat diSort = new Mat(1, 280, CvType.CV_64FC1);
			String resultImageNo = "";
			Core.sort(diMat2, diSort, Core.SORT_ASCENDING);
			int[] classIndex = new int[10];
			for(int i = 0; i < classIndex.length; i++){
				for(int j = 0; j < 280; j++){
					if( diSort.get(0, i)[0] == di[j] ){
						classIndex[i] = 1+((int)(j/7));
						resultImageNo += classIndex[i]+" ";
					//	System.out.println(classIndex[i]);
					}
				}
			}
			
			int[] countArray = new int[41];
			double[] percentage = new double[41];
			for(int i = 0; i < countArray.length; i++){
				countArray[i] = 0;
				percentage[i] = 0;
			}
			for(int i = 0; i < classIndex.length; i++){
					countArray[classIndex[i]] += 1;						
			}
			
			for(int i = 0; i < countArray.length; i++){
				if (countArray[i] > 0){	
					percentage[i] = (double)countArray[i]/10;
				}
			}
			
			double maxPercent = percentage[0];
			for(int i = 0; i < percentage.length; i++){
				if(maxPercent < percentage[i]){
					maxPercent = percentage[i];
					recResultNo = i;
				}
			}
			
			if(userDisplay){
				JLabel jblResultText = new JLabel("10 nearest person number: ");
				JLabel jblResult = new JLabel(resultImageNo);
				JLabel jblResultImage = new JLabel("Person is identified as: "+recResultNo);
				
				displayPanel.add(jblResultText);
				displayPanel.add(jblResult);
				displayPanel.add(jblResultImage);
			}
		//	System.out.println(diMat2.dump());
		//	System.out.println(diSort.dump());
		}	
		return recResultNo;
	}
	
	public Mat getMatData(int[][] data, int row_size, int col_size) {
		Mat samples = new Mat(row_size, col_size, CvType.CV_64FC1);
		for (int r = 0; r < row_size; r++) {
			for (int c = 0; c < col_size; c++) {
				samples.put(r, c, data[r][c]);
			}
		}
		return samples;
	}
	
	public Mat getMatData(double[] data, int col_size) {
		Mat samples = new Mat(1, col_size, CvType.CV_64FC1);
		
			for (int c = 0; c < col_size; c++) {
				samples.put(0, c, data[c]);
			}
		
		return samples;
	}

	public int[][] getMeanArray(Mat mean, int row_size, int col_size) {
		int num = 0;
		int[][] meanArray = new int[row_size][col_size];
		for (int r = 0; r < row_size; r++) {
			for (int c = 0; c < col_size; c++) {
				meanArray[r][c] = (int) (mean.get(0, num)[0]);
				num++;
			}
		}
		return meanArray;
	}

	public int[][] getIntArray(Mat mat, int row_size, int col_size) {
		int[][] intArray = new int[row_size][col_size];
		for (int r = 0; r < row_size; r++) {
			for (int c = 0; c < col_size; c++) {
				intArray[r][c] = (int) (mat.get(r, c)[0]);
			}
		}
		return intArray;
	}

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		new FaceDetection();
	}
}
