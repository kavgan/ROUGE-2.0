package com.rxnlp.tools.rouge;

import org.tartarus.snowball.SnowballStemmer;

public class ROUGESettings {
	
	public enum RougeType{
		normal,
		normalCompressed,
		topic,
		topicUniq
	}

	public boolean USE_SYNONYMS = false;
	
	
	/** ROUGE-N */
	public int NGRAM=2;
	
	/**
	 * Is this ROUGE-N, ROUGE-Topic or ROUGE-TopicUniq
	 * ROUGE-Topic and ROUGE-TopicUniq are by default unigrams
	 * */
	public RougeType ROUGE_TYPE=RougeType.topic;
	
	//nn|jj|vb$|vb[zgbpn|prp]
	public String SELECTED_TOPICS="nn|jj";
	
	/** Do stop word removal */
	public boolean REMOVE_STOP_WORDS=false;
	
	public String STOP_WORDS_FILE="stopwords-terrier-en.txt";


	/** POS Tagger name - based on Stanford -*/
	public String TAGGER_NAME="english-left3words-distsim.tagger";


	/** console,file */
	public String OUTPUT_TYPE="console";


	/** name of the results file */
	public String RESULTS_FILE="results.txt";


	/**root of the reference and system summaries */
	public String PROJ_DIR="projects.txt";


	public String STEMMER;


	public boolean USE_STEMMER;
		

}
