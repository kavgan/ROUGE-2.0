package com.rxnlp.tools.rouge;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import com.rxnlp.tools.rouge.ROUGESettings.RougeType;
import com.rxnlp.utils.lang.StopWordsHandler;
import com.rxnlp.utils.lang.WordNetDict;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * 
 * @author Kavita Ganesan
 * www.rxnlp.com
 *
 */
public class ROUGECalculator {

	DecimalFormat df = new DecimalFormat("0.00000");

	static Logger logger = Logger.getLogger(ROUGECalculator.class);
	static MaxentTagger tagger;
	static SnowballStemmer stemmer;

	private String POS_SEP = "(_|/|#)";
	private static String SEP = "__";

	WordNetDict wrdnet = new WordNetDict();
	private StopWordsHandler stopHandler;
	private ROUGESettings settings;
	BufferedWriter resultsWriter;

	public class Result {

		double precision = 0;
		double recall = 0;
		double f = 0;
		int count = 0;
		String name;
		
		public void resetROUGE(){
			precision=0;
			recall=0;
			count=0;
			f=0;
		}

	}

	public class TaskFile {

		String taskName;
		HashMap<Path, Result> systemFiles = new HashMap<Path, Result>();
		List<Path> referenceFiles = new ArrayList<Path>();

		@Override
		public boolean equals(Object obj) {
			TaskFile tf1 = (TaskFile) obj;
			return tf1.taskName.equals(taskName);
		}

		@Override
		public int hashCode() {
			return taskName.hashCode();
		}

	}
	
	

	public static void main(String args[]) {

		ROUGECalculator rc = new ROUGECalculator();
		rc.start(args);
	}

	private void start(String[] args) {

		settings = new ROUGESettings();
		SettingsUtil.loadProps(settings);

		if (settings.REMOVE_STOP_WORDS) {
			stopHandler = new StopWordsHandler(settings.STOP_WORDS_FILE);
		}

		if (settings.USE_STEMMER) {
			try {
				Class stemClass = Class.forName("org.tartarus.snowball.ext." + settings.STEMMER);
				stemmer = (SnowballStemmer) stemClass.newInstance();
			} catch (ClassNotFoundException e) {
				logger.error("Stemmer not found " + e.getMessage());
				logger.error("Default englishStemmer will be used");
				stemmer = new englishStemmer();
			} catch (InstantiationException e) {
				logger.error("Problem instantiating stemmer..." + e.getMessage());
			} catch (IllegalAccessException e) {
				logger.error("Illegal Access " + e.getMessage());
			}
		}

		/** load POS tagger only if ROUGE topic or synonyms are true */
		if (settings.ROUGE_TYPE.equals(RougeType.topic) || settings.ROUGE_TYPE.equals(RougeType.topicUniq)
				|| settings.USE_SYNONYMS) {
			tagger = new MaxentTagger("resources/taggers/" + settings.TAGGER_NAME);
			logger.info("Loaded...POS tagger.");
		}

		

		if (settings.OUTPUT_TYPE.equalsIgnoreCase("file")) {
			try {

				resultsWriter = new BufferedWriter(new FileWriter(settings.RESULTS_FILE));
				resultsWriter.write("ROUGE-Type");
				resultsWriter.write(",");
				resultsWriter.write("Task Name");
				resultsWriter.write(",");
				resultsWriter.write("System Name");
				resultsWriter.write(",");
				resultsWriter.write("Avg_Recall");
				resultsWriter.write(",");
				resultsWriter.write("Avg_Precision");
				resultsWriter.write(",");
				resultsWriter.write("Avg_F-Score");
				resultsWriter.write(",");
				resultsWriter.write("Num Reference Summaries");
				resultsWriter.newLine();

			} catch (IOException e) {
				logger.error("There was a problem creating or writing to the results file. Make sure the file is not in use");
				logger.error(e.getMessage());
				System.exit(-1);
			}
		}

		String projectDir = settings.PROJ_DIR;
		String references = projectDir + "/reference";
		String system = projectDir + "/system";

		HashMap<String, TaskFile> hmEvalTasks = new HashMap<String, TaskFile>();

		Path refPath = Paths.get(references);
		Path sysPath = Paths.get(system);

		try {
			if (Files.exists(refPath) && Files.exists(sysPath)) {
				List<Path> refFiles = Files.list(refPath).collect(Collectors.toList());
				List<Path> sysFiles = Files.list(sysPath).collect(Collectors.toList());
				initTasks(refFiles, sysFiles, hmEvalTasks);
				evaluate(hmEvalTasks);
			} else {
				logger.error("A valid 'references' and 'system' folder not found..Check rouge.properties file.");
				logger.error("\nYou need to have a valid 'references' and 'system' folder under " + projectDir
						+ ". Please check your rouge.properties file");
				System.exit(-1);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error("\nYou need to have a valid 'references' and 'system' folder under " + projectDir
					+ ". Please check your rouge.properties file");
			logger.error(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void evaluate(HashMap<String, TaskFile> hmEvalTasks) {

		Set<String> tasks = hmEvalTasks.keySet();
		StringBuffer b = new StringBuffer();
		List<String> resultList=new ArrayList<String>();
		
			for (String task : tasks) {

				TaskFile t = hmEvalTasks.get(task);
				HashMap<Path, Result> systems = t.systemFiles;
				Set<Path> sysKeys = systems.keySet();
			
			for (String ngram : settings.NGRAM) {
				
				logger.info("Working on "+task+" ngram="+ngram);
				

				for (Path system : sysKeys) {

					// get all sentences from the system file
					List<String> systemSents = getSystemSents(system);

					// the result object for sys file
					Result r = systems.get(system);
					computeRouge(r, ngram, systemSents, t.referenceFiles);
					String str = getROUGEName(settings,ngram);

					// Print results to console
					String resultStr=str + "\t" + t.taskName.toUpperCase() + "\t" + r.name.toUpperCase()
							+ "\tAverage_R:" + df.format(r.recall) + "\tAverage_P:" + df.format(r.precision)
							+ "\tAverage_F:" + df.format(r.f) + "\tNum Reference Summaries:" + r.count;
					resultList.add(resultStr);
					// Write to file if specified by settings
					if (resultsWriter != null) {
						try {
							b.delete(0, b.length());
							b.append(str).append(",")
									.append(t.taskName.toUpperCase()).append(",")
									.append(r.name.toUpperCase()).append(",").append(df.format(r.recall)).append(",")
									.append(df.format(r.precision)).append(",").append(df.format(r.f)).append(",")
									.append(r.count);

							resultsWriter.write(b.toString());
							resultsWriter.newLine();
							
						} catch (IOException e) {
							logger.error(e.getMessage());
						}

					}
				}
				try {
					resultsWriter.newLine();
					resultList.add("\n");
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}//end of task
			
			
		}
			
		printResults(resultList);
		cleanUp();
	}

	private void printResults(List<String> resultList) {
		System.out.println("\n========Results Summary=======\n");
		
		for (String res:resultList){
			System.out.println(res);
		}
		
		System.out.println("======Results Summary End======\n");
	}

	private void cleanUp() {
		// Close output file
		try {
			if (resultsWriter != null) {
				logger.info("Results written to " + settings.RESULTS_FILE);
				System.out.println("\n\nDone! Find results file in: " + (new File(settings.RESULTS_FILE)).getAbsolutePath());
				resultsWriter.close();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

	}

	private String getROUGEName(ROUGESettings settings, String ngram) {

		StringBuffer sb = new StringBuffer();
		sb.append("ROUGE-" + ngram);

		// TYPE - TOPIC, NORMAL
		if (!settings.ROUGE_TYPE.equals(RougeType.normal))
			sb.append("-").append(settings.ROUGE_TYPE);

		if (settings.REMOVE_STOP_WORDS)
			sb.append("+").append("StopWordRemoval");

		if (settings.USE_STEMMER)
			sb.append("+").append("Stemming");

		if (settings.USE_SYNONYMS)
			sb.append("+").append("Synonyms");

		return sb.toString();
	}

	private List<String> getSystemSents(Path system) {
		/*
		 * try { BufferedReader r=new BufferedReader(new FileReader(system));
		 * List<String> sentList=new ArrayList<>(); String line="";
		 * 
		 * while((line=r.readLine())!=null){ line=cleanSent(line);
		 * sentList.add(line); }
		 */

		// read file into stream, try-with-resources
		List<String> sentList = new ArrayList<>();
		try (Stream<String> stream = Files.lines(system, StandardCharsets.ISO_8859_1)) {

			stream.forEach(line -> {
				line = cleanSent(line);
				sentList.add(line);
			});

		} catch (IOException e) {
			logger.error("Problem reading system sentences");
		}

		return sentList;
		/*
		 * } catch (IOException e) { e.printStackTrace(); return null; }
		 */
	}

	private String cleanSent(String line) {
		line = line.replaceAll("[0-9]+", " @ ").toLowerCase();
		line = line.replaceAll("(!|\\.|\"|'|;|:|,)", " ").toLowerCase();

		return line;
	}

	private void computeRouge(String system, List<String> referenceFiles) {

	}

	private static void initTasks(List<Path> refFiles, List<Path> sysFiles, HashMap<String, TaskFile> hmEvalTasks) {

		// INITIALIZE SYSTEM FILES FOR EACH TASK
		for (Path sysFile : sysFiles) {

			String fileName = sysFile.getFileName().toFile().getName();
			String[] fileToks = fileName.split("_");

			if (fileNamingOK(fileToks, fileName)) {
				TaskFile theEvalTask = getEvalTask(fileToks, hmEvalTasks);

				Result r = new ROUGECalculator().new Result();
				r.name = fileToks[1];

				theEvalTask.systemFiles.put(sysFile, r);
			}

		}

		// INITIALIZE REFERENCE FILES FOR EACH TASK
		for (Path refFile : refFiles) {

			String fileName = refFile.getFileName().toFile().getName();
			String[] fileToks = fileName.split("_");

			if (fileNamingOK(fileToks, fileName)) {
				TaskFile theEvalTask = getEvalTask(fileToks, hmEvalTasks);
				theEvalTask.referenceFiles.add(refFile);
			}
		}

	}

	/**
	 * Each unique text, is considered a unique summarization task. Create a
	 * summarization task and add evaluation tasks into hmEvalTasks
	 * 
	 * @param fileToks
	 * @param hmEvalTasks
	 * @return
	 */
	private static TaskFile getEvalTask(String[] fileToks, HashMap<String, TaskFile> hmEvalTasks) {
		String taskName = fileToks[0].toLowerCase();
		TaskFile theEvalTask = hmEvalTasks.get(taskName);

		if (theEvalTask == null) {
			theEvalTask = new ROUGECalculator().new TaskFile();
			theEvalTask.taskName = taskName;
			hmEvalTasks.put(taskName, theEvalTask);
		}
		return theEvalTask;
	}

	private static boolean fileNamingOK(String[] fileToks, String fileName) {
		if (fileToks.length < 2) {
			logger.error("Something seems to be wrong with your file naming convention: " + fileName);
			System.exit(-1);
		}
		return true;
	}

	private void getPOSTagged(List<String> reference, boolean restrictByTopic) {

		List<String> newList = new ArrayList<String>();
		for (String sent : reference) {
			sent = sent.toLowerCase();
			String tagged = tagger.tagString(sent);

			if (restrictByTopic)
				tagged = getRelevantPOS(tagged.toLowerCase());

			if (tagged.trim().length() > 0)
				newList.add(tagged.toLowerCase());
		}

		reference.clear();
		reference.addAll(newList);
	}

	/**
	 * Pick out words that have the relevant POS tags if ROUGE-Topic selected
	 * 
	 * @param tagged
	 * @return
	 */
	private String getRelevantPOS(String tagged) {
		String[] tokens = tagged.split("\\s+");
		StringBuffer b = new StringBuffer();

		for (String t : tokens) {
			if (t.matches(".*(" + settings.SELECTED_TOPICS + ").*")) {
				// b.append(t.split("_")[0]).append(" ");
				b.append(t).append(" ");
			}
		}
		return b.toString().trim();
	}

	void getCombiFor(List<HashSet<String>> li, List<String> daList) {

		for (int i = 0; i < li.size(); i++) {

			ArrayList<String> toRemove = new ArrayList<String>();
			ArrayList<String> toAdd = new ArrayList<String>();

			HashSet<String> hs = li.get(i);

			if (daList.size() > 0) {

				for (String tempStr : daList) {
					for (String s : hs) {
						toRemove.add(tempStr);
						toAdd.add(tempStr + SEP + s);
						// tempStr=tempStr+"$$;"+s;
					}
				}

			} else {
				daList.addAll(hs);
			}

			daList.addAll(toAdd);
			daList.removeAll(toRemove);
		}
	}

	public void computeRouge(Result r, String ngram, List<String> sysSents, List<Path> refSentFileNames) {

		List<String> refSents = null;
		r.resetROUGE();

		// For each reference summary
		for (Path refSentFile : refSentFileNames) {

			refSents = getSystemSents(refSentFile);

			if (refSents == null) {

				logger.warn("No reference sentences in " + refSentFile);

			} else {

				/** ROUGE topic , POS TAG and set n-gram to 1 */
				if (settings.ROUGE_TYPE.equals(RougeType.topic) || settings.ROUGE_TYPE.equals(RougeType.topicUniq)) {
					ngram="1";
					getPOSTagged(refSents, true);
					getPOSTagged(sysSents, true);
				}

				/** ROUGE+Synonyms, POS TAG */
				else if (settings.USE_SYNONYMS) {
					getPOSTagged(refSents, false);
					getPOSTagged(sysSents, false);
				}

				/** Remove Stop Words */
				if (settings.REMOVE_STOP_WORDS) {
					removeStopWords(refSents);
					removeStopWords(sysSents);
				}

				/** Stem Words */
				if (settings.USE_STEMMER) {
					applyStemming(refSents);
					applyStemming(sysSents);
				}

				double overlap = 0;
				double ROUGE = 0;

				Collection<String> hsrefOriginal = getNGramTokens(ngram,refSents);
				Collection<String> refSummaryTokens = getNGramTokens(ngram,refSents);
				Collection<String> sysSummaryTokens = getNGramTokens(ngram,sysSents);

				HashSet<String> theSynonyms = new HashSet<>();

				/*
				 * if(s.LOOSEN_WORD_ORDER){ rearrange(referenceSummaryTokens);
				 * rearrange(systemSummaryTokens); }
				 */

				if (settings.USE_SYNONYMS) {
					removePos(refSummaryTokens);

					// PERFORM COMPARISONS
					if (sysSummaryTokens.size() > 0 && refSummaryTokens.size() > 0) {
						for (String sysTok : sysSummaryTokens) {

							String[] unigramList = sysTok.split(SEP);

							// get list of synonyms for individual words in the
							// system summary
							List<HashSet<String>> listOfSynonyms = new ArrayList<HashSet<String>>();
							for (String unigram : unigramList) {
								theSynonyms = getSynonymList(unigram);
								listOfSynonyms.add(theSynonyms);
							}

							// combine synonyms (combinatorial)
							ArrayList<String> synTokens = new ArrayList<String>();
							getCombiFor(listOfSynonyms, synTokens);

							// for each synonym, check if the reference summary
							// contains it. if at least one matches, then this
							// is a hit
							boolean isMatch = false;

							// now check if the reference summary has any of the
							// synonyms
							for (String syntok : synTokens) {

								if (refSummaryTokens.contains(syntok)) {
									refSummaryTokens.remove(syntok);
									overlap++;
								}
							}
						}
					}
				} else {
					// WE DONT CARE ABOUT SYNONYMS

					for (String sysTok : sysSummaryTokens) {

						if (refSummaryTokens.contains(sysTok)) {
							refSummaryTokens.remove(sysTok);
							overlap++;
						}
					}
				}
				r.count=r.count+1;
				computePrecisionRecall(r,ngram, overlap, hsrefOriginal, sysSummaryTokens, refSents, sysSents);
				
				

			} // SENTS FOUND
		}
		
		finalizePrecisionRecall(r);
	}

	/**
	 * Average the precision and recall and compute f-score
	 * 
	 * @param r
	 */
	private void finalizePrecisionRecall(Result r) {
		// AVERAGE THE P/R
		r.precision = r.precision / r.count;
		r.recall = r.recall / r.count;

		if (r.precision > 0 || r.recall > 0)
			r.f = ((1 + Math.pow(settings.BETA, 2)) * r.recall * r.precision)
					/ ((Math.pow(settings.BETA, 2) * r.precision) + r.recall);

	}

	private void computePrecisionRecall(Result r, String ngram, double overlap, Collection<String> hsrefOriginal,
			Collection<String> sysSummaryTokens, List<String> refSents, List<String> sysSents) {

		// ROUGE-L
		// special case, rouge L, we need to get lcs
		if (ngram.toLowerCase().equals("l")) {

			computeROUGEL(r, refSents, sysSents);
		}

		else
		// EVERYTHING ELSE
		if (overlap > 0) {
			double rougeRecall = overlap / (hsrefOriginal.size());
			double rougePrecision = (overlap) / (sysSummaryTokens.size());
			r.recall = r.recall + rougeRecall;
			r.precision = r.precision + rougePrecision;
		}
	}

	private void computeROUGEL(Result r, List<String> refSents, List<String> sysSents) {
		double total_lcs = 0;
		Set<String> unionLCSWords = new HashSet<>();
		Set<String> allRefWords = new HashSet<>();
		Set<String> allSysWords = new HashSet<>();

		// Get unique sys summary words
		for (String sys : sysSents) {
			allSysWords.addAll(Arrays.asList(sys.split("\\s+")));
		}

		// Get unique reference summary words
		for (String ref : refSents) {
			allRefWords.addAll(Arrays.asList(ref.split("\\s+")));
		}

		// Get the union LCS
		for (String ref : refSents) {
			// get union of words from reference into a hashset
			for (String sys : sysSents) {
				// get union of all LCS words into hashset
				unionLCSWords.addAll(getLCSSequence(ref, sys));
			}
		}
		double rougeRecall = unionLCSWords.size() / (double) allRefWords.size();
		double rougePrecision = unionLCSWords.size() / (double) allSysWords.size();
		r.recall = r.recall + rougeRecall;
		r.precision = r.precision + rougePrecision;

	}

	private void rearrange(Collection<String> originalTokens) {
		Collection<String> str = new ArrayList<String>();
		Collection<String> newStr = new ArrayList<String>();
		StringBuffer b = new StringBuffer();

		for (String ref : originalTokens) {
			List<String> theTokens = Arrays.asList(ref.split(SEP));
			Collections.sort(theTokens);

			for (String s : theTokens) {
				b.append(s).append(SEP);
			}
			newStr.add(b.toString());
			b.delete(0, b.length());
		}

		originalTokens.clear();
		originalTokens.addAll(newStr);

	}

	private void removePos(Collection<String> nGrams) {

		List<String> removeList = new ArrayList<String>();
		List<String> addList = new ArrayList<String>();

		for (String theNGram : nGrams) {

			// get individual words
			String[] words = theNGram.split(SEP);
			StringBuffer b = new StringBuffer();

			// for each individual word, get the word without POS tags
			// create n-gram tokens with SEP as the separator
			for (String w : words) {

				String[] wordPOSTokens = w.split(POS_SEP);
				if (wordPOSTokens.length == 2) {
					String wordToken = wordPOSTokens[0];
					String posToken = wordPOSTokens[1];
					b.append(wordToken.trim()).append(SEP);
				}
			}

			addList.add(b.substring(0, b.length() - SEP.length()).toString().trim());
			removeList.add(theNGram);
		}

		nGrams.addAll(addList);// add tokens without the POS tags
		nGrams.removeAll(removeList);// remove tokens with the POS tags. At this
										// point we only want the words and not
										// the POS tags
	}

	private HashSet<String> getSynonymList(String sysTok) {
		String[] toks = sysTok.split(POS_SEP);
		HashSet<String> synonymList = new HashSet<>();

		if (toks[1].contains("nn")) {
			synonymList = wrdnet.getNounSynonyms(toks[0]);
		}

		else if (toks[1].contains("jj")) {
			synonymList = wrdnet.getAdjectiveSynonyms(toks[0]);
		}

		else if (toks[1].contains("vb")) {
			synonymList = wrdnet.getVerbSynonyms(toks[0]);
		}

		// System.err.println(toks[0]+":"+synonymList);
		synonymList.add(toks[0].trim());
		return synonymList;
	}

	private void applyStemming(List<String> sents) {

		List<String> updatedSents = new ArrayList<String>();
		StringBuffer b = new StringBuffer();

		for (String s : sents) {
			b.delete(0, b.length());
			String[] tokens = s.toLowerCase().split("\\s+");

			for (String t : tokens) {

				String[] daToks = t.split("_");

				// IF NOT STOP WORD KEEP
				stemmer.setCurrent(daToks[0]);

				if (stemmer.stem()) {
					b.append(stemmer.getCurrent());

					if (daToks.length == 2) {
						b.append(daToks[1]);
					}
					b.append(" ");
				}
			}

			updatedSents.add(b.toString().trim());
		}

		sents.clear();
		sents.addAll(updatedSents);
	}

	private void removeStopWords(List<String> sents) {

		List<String> updatedSents = new ArrayList<String>();
		StringBuffer b = new StringBuffer();

		for (String s : sents) {
			b.delete(0, b.length());
			String[] tokens = s.toLowerCase().split("\\s+");

			for (String t : tokens) {

				String[] daToks = t.split("_");


				// IF NOT STOP WORD, KEEP
				if (daToks.length<1 || !StopWordsHandler.isStop(daToks[0].trim())) {
					b.append(t).append(" ");
				}
			}
			updatedSents.add(b.toString().trim());
		}

		sents.clear();
		sents.addAll(updatedSents);
	}

	private void printSupportedNgrams() {
		System.out.println("The following n-gram settings are supported:");
		System.out.println("ROUGE-N  (e.g. 1,2,3,..)");
		System.out.println("ROUGE-S  (e.g. S1,S2,S3,..)");
		System.out.println("ROUGE-SU (e.g. SU1,SU2,SU3,..);");
		System.out.println("ROUGE-L  (only option: LCS)");
	}

	/**
	 * Get list of n-grams
	 * 
	 * @param sents
	 * @param traditional
	 * @return
	 */
	private Collection<String> getNGramTokens(String ngram, List<String> sents) {

		Collection<String> ngramList = null;

		if (settings.ROUGE_TYPE.equals(RougeType.topicUniq) || settings.ROUGE_TYPE.equals(RougeType.normalCompressed))
			ngramList = new HashSet<String>();
		else
			ngramList = new ArrayList<String>();

		for (String sent : sents) {

			// ROUGE-1
			if (ngram.equals("1")) {
				String[] tokens = sent.split("\\s+");
				ngramList.addAll(Arrays.asList(tokens));
			}

			else if (ngram.matches("[0-9]+")) {
				int ngramVal = Integer.parseInt(ngram);
				generateNgrams(ngramVal, sent, ngramList);
			}

			else// ROUGE-N
			if (ngram.matches("SU?[0-9]+")) {
				generateSkipGrams(ngram, sent, ngramList);
			}

			else // ROUGE-L
			if (ngram.toLowerCase().equals("l")) {
				// just generate unigrams for now
				generateNgrams(1, sent, ngramList);
			}

			else {
				logger.error("Please check your N-GRAM settings. NGRAM=" + settings.NGRAM
						+ " doesn't seem to be supported.");
				printSupportedNgrams();
				System.exit(-1);
			}
		}

		return ngramList;
	}

	private static void generateSkipGrams(String gram, String summary, Collection<String> ngramList) {
		String[] tokens = summary.split("\\s+");

		// split to get the skip-gram size
		String[] gramTokens = gram.split("SU?");

		// is the last part does not contain a number, there is some error
		if (gramTokens.length < 2 && !gramTokens[1].matches("[0-9]+")) {
			logger.error("There is an error in your ROUGE-S setting. Please check documentation.");
			System.exit(-1);
		}

		int skip = Integer.parseInt(gramTokens[1]);

		// GENERATE THE SKIP-N-GRAMS
		for (int k = 0; k < tokens.length; k++) {
			String s = tokens[k] + "__";
			int start = k + 1;
			int end = Math.min(k + skip + 1, tokens.length);
			for (int j = start; j < end; j++) {
				String theSkipGram = s + tokens[j] + "__";
				ngramList.add(theSkipGram);
			}

		}

		// if SU specified then also include unigrams
		if (gram.toLowerCase().contains("su")) {
			generateNgrams(1, summary, ngramList);
		}

	}

	private static void generateNgrams(int gram, String summary, Collection<String> ngramList) {
		String[] tokens = summary.split("\\s+");

		// GENERATE THE N-GRAMS
		for (int k = 0; k < (tokens.length - gram + 1); k++) {
			String s = "";
			int start = k;
			int end = k + gram;
			for (int j = start; j < end; j++) {
				s = s + tokens[j] + SEP;
			}

			ngramList.add(s);
		}

	}

	/**
	 * Ensures symmetric LCS results
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static List<String> getLCSSequence(String s1, String s2) {
		List<String> l1 = getLCSInternal(s1, s2);
		List<String> l2 = getLCSInternal(s2, s1);

		if (l1.size() > l2.size())
			return l1;
		else
			return l2;

	}

	private static List<String> getLCSInternal(String s1, String s2) {
		List<String> lcsWords = new ArrayList<String>();
		int internal_start = 0;

		String[] s1Toks = s1.split("\\s+");
		String[] s2Toks = s2.split("\\s+");

		for (int k = 0; k < s1Toks.length; k++) {

			String s1_word = s1Toks[k];

			// find the longest subsequence
			for (int j = internal_start; j < s2Toks.length; j++) {
				String s2_word = s2Toks[j];
				if (s1_word.equals(s2_word)) {
					lcsWords.add(s1_word);
					internal_start = j + 1; // force starting position
				}
			}
		}

		return lcsWords;
	}

}
