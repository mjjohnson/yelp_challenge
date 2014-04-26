import com.aliasi.util.Files;
import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramProcessLM;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import edu.illinois.cs.cogcomp.lbj.pos.POSTagger;
import LBJ2.nlp.seg.Token;
import LBJ2.nlp.SentenceSplitter;
import LBJ2.nlp.WordSplitter;
import LBJ2.nlp.seg.PlainToTokenParser;
import LBJ2.parse.ChildrenFromVectors;

public class Prediction {

    File mPolarityDir;
    String[] mCategories;
    DynamicLMClassifier<NGramProcessLM> mClassifier;
    File mPolarityDir1;
    String[] mCategories1;
    DynamicLMClassifier<NGramProcessLM> mClassifier1;
    Prediction(String[] args) {
        System.out.println("\nBASIC POLARITY DEMO");
        mPolarityDir = new File("C:\\Users\\Arunima\\Desktop\\test_predictor2");
        System.out.println("\nData Directory=" + mPolarityDir);
        mCategories = mPolarityDir.list();
        int nGram = 8;
        mClassifier 
            = DynamicLMClassifier
            .createNGramProcess(mCategories,nGram);
        mPolarityDir1 = new File("C:\\Users\\Arunima\\Desktop\\test_predictor2");
        System.out.println("\nData Directory=" + mPolarityDir1);
        mCategories1 = mPolarityDir1.list();
        int nGram1 = 8;
        mClassifier1 
            = DynamicLMClassifier
            .createNGramProcess(mCategories1,nGram1);
    }

    void run() throws Exception {
        train();
        evaluate();
        trainadj();
        evaluateadj();
    }
    
    private static String onlyadjectives(String filename)
    {
    	String onlyadj="";
    	POSTagger tagger = new POSTagger();
        
        PlainToTokenParser parser =
          new PlainToTokenParser(
            new WordSplitter(
              new SentenceSplitter(filename)));
        Token w = (Token) parser.next();
        while(w!=null){
        	String tag = tagger.discreteValue(w);
        	
        	if(tag.equals("JJ")||tag.equals("JJS")||tag.equals("JJR"))
        	{
        		onlyadj+=w.form+" ";
            //System.out.println(w.form+" "+tag);
        	}
        w = (Token) parser.next();
        }
     // System.out.println(onlyadj);
    	return onlyadj;
    }
    private static String sendPost(String revtext) throws Exception {
		final String USER_AGENT = "Mozilla/5.0";
		String url = "http://text-processing.com/api/sentiment/";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		//add reuqest header
		con.setRequestMethod("POST");
	//	con.setRequestProperty("User-Agent", USER_AGENT);
		//con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		String urlParameters = "text="+revtext;
 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		if(responseCode!=200)
		{
			System.out.println("RESPONSE ERROR"+responseCode);
			System.exit(0);
		}
		//System.out.println("\nSending 'POST' request to URL : " + url);
		//System.out.println("Post parameters : " + urlParameters);
		//System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		//System.out.println(response.toString());
		String s = "["+response+"]";
		Object ob=JSONValue.parse(s);
		JSONArray array=(JSONArray)ob;
		//System.out.println("=========================");
		//System.out.println(array.get(0));
		JSONObject ob2=(JSONObject)array.get(0);
		
			return ob2.get("label").toString();
	}

    boolean isTrainingFile(File file) {
    	int beg = file.getName().indexOf("(");
    	int end = file.getName().indexOf(")");
    	
    	int num = Integer.parseInt(file.getName().substring(beg+1, end));
    	
        return (num%2!= 0);  // test on fold 9
    }

    void train() throws IOException {
        int numTrainingCases = 0;
        int numTrainingChars = 0;
        System.out.println("\nTraining.");
        for (int i = 0; i < mCategories.length; ++i) {
            String category = mCategories[i];
            Classification classification
                = new Classification(category);
            File file = new File(mPolarityDir,mCategories[i]);
            File[] trainFiles = file.listFiles();
            for (int j = 0; j < trainFiles.length; ++j) {
                File trainFile = trainFiles[j];
                if (isTrainingFile(trainFile)) {
                    ++numTrainingCases;
                    String review = Files.readFromFile(trainFile,"ISO-8859-1");
                    
                    numTrainingChars += review.length();
                    Classified<CharSequence> classified
                        = new Classified<CharSequence>(review,classification);
                    mClassifier.handle(classified);
                }
            }
        }
        System.out.println("  # Training Cases=" + numTrainingCases);
        System.out.println("  # Training Chars=" + numTrainingChars);
    }

    void evaluate() throws Exception {
        System.out.println("\nEvaluating.");
        int numTests = 0;
        int numCorrect = 0;
        int numcorrectother =0;
        for (int i = 0; i < mCategories.length; ++i) {
            String category = mCategories[i];
            File file = new File(mPolarityDir,mCategories[i]);
            File[] trainFiles = file.listFiles();
            for (int j = 0; j < trainFiles.length; ++j) {
                File trainFile = trainFiles[j];
                if (!isTrainingFile(trainFile)) {
                    String review = Files.readFromFile(trainFile,"ISO-8859-1");
                    ++numTests;
                    Classification classification
                        = mClassifier.classify(review);
                    if (classification.bestCategory().equals(category))
                        ++numCorrect;
                    if(sendPost(review).equals(category))
                    	++numcorrectother;
                }
            }
        }
        System.out.println("  # Test Cases=" + numTests);
        System.out.println("  # Correct=" + numCorrect);
        System.out.println("  % Correct=" 
                           + ((double)numCorrect)/(double)numTests);
        System.out.println("  % Correctother=" 
                + ((double)numcorrectother)/(double)numTests);
    }

    void trainadj() throws IOException {
        int numTrainingCases = 0;
        int numTrainingChars = 0;
        System.out.println("\nTraining.");
        for (int i = 0; i < mCategories1.length; ++i) {
            String category = mCategories1[i];
            Classification classification
                = new Classification(category);
            File file = new File(mPolarityDir1,mCategories1[i]);
            File[] trainFiles = file.listFiles();
            for (int j = 0; j < trainFiles.length; ++j) {
                File trainFile = trainFiles[j];
                if (isTrainingFile(trainFile)) {
                    ++numTrainingCases;
                 //   String review = Files.readFromFile(trainFile,"ISO-8859-1");
                    String review = onlyadjectives(trainFile.getAbsolutePath());
                    numTrainingChars += review.length();
                    Classified<CharSequence> classified
                        = new Classified<CharSequence>(review,classification);
                    mClassifier1.handle(classified);
                }
            }
        }
        System.out.println("  # Training Cases=" + numTrainingCases);
        System.out.println("  # Training Chars=" + numTrainingChars);
    }

    void evaluateadj() throws Exception {
        System.out.println("\nEvaluating.");
        int numTests = 0;
        int numCorrect = 0;
        int numcorrectother =0;
        for (int i = 0; i < mCategories1.length; ++i) {
            String category = mCategories1[i];
            File file = new File(mPolarityDir1,mCategories1[i]);
            File[] trainFiles = file.listFiles();
            for (int j = 0; j < trainFiles.length; ++j) {
                File trainFile = trainFiles[j];
                if (!isTrainingFile(trainFile)) {
                    //String review = Files.readFromFile(trainFile,"ISO-8859-1");
                    String review = onlyadjectives(trainFile.getAbsolutePath());
                    if(review.length()==0)
                    {
                    	review="ABCD";
                    }
                    ++numTests;
                    Classification classification
                        = mClassifier1.classify(review);
                    if (classification.bestCategory().equals(category))
                        ++numCorrect;
                    if(sendPost(review).equals(category))
                    	++numcorrectother;
                }
            }
        }
        System.out.println("  # Test Cases=" + numTests);
        System.out.println("  # Correct=" + numCorrect);
        System.out.println("  % Correct=" 
                           + ((double)numCorrect)/(double)numTests);
        System.out.println("  % Correctother=" 
                + ((double)numcorrectother)/(double)numTests);
    }
    public static void main(String[] args) {
        try {
        	
        	//onlyadjectives("C:\\Users\\Arunima\\Desktop\\test_predictor\\neutral\\__0G7C28bnbwxdtanv-1Bg(2728)");
            new Prediction(args).run();
        } catch (Throwable t) {
            System.out.println("Thrown: " + t);
            t.printStackTrace(System.out);
        }
    }

}
