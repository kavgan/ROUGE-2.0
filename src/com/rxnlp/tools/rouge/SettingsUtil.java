package com.rxnlp.tools.rouge;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.danishStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import org.tartarus.snowball.ext.frenchStemmer;
import org.tartarus.snowball.ext.germanStemmer;
import org.tartarus.snowball.ext.portugueseStemmer;
import org.tartarus.snowball.ext.turkishStemmer;

import com.rxnlp.tools.rouge.ROUGESettings.RougeType;

public class SettingsUtil {

	static Logger log=Logger.getLogger(SettingsUtil.class);

	
	/**
	 * Load by property file location
	 * @param propfile
	 * @return
	 */
	public static void loadProps(ROUGESettings rs) {


		try {
			// Read properties file.
			String propFile=System.getProperty("rouge.prop", "rouge.properties");
			
			log.info("Using rouge.properties file specified as '"+propFile+"'");
			
			InputStream stream=new FileInputStream(propFile);
			loadProps(stream,rs);

		} catch (IOException exception) {
			
			System.err.println("Properties file not found. Please specify -Drouge.prop=<path_to_rouge_prop> in the command line or place 'rouge.properties' file in the root of the project.");
			System.exit(-1);
			// TODO Auto-generated catch block
			//exception.printStackTrace();
		}
		
		
	}

	public static void loadProps(InputStream stream, ROUGESettings rs){

		Properties properties = new Properties();
		
		try {
			properties.load(stream);


			String val=properties.getProperty("ngram","1");
			rs.NGRAM=Integer.parseInt(val);
			
			val=properties.getProperty("project.dir","projects");
			rs.PROJ_DIR=val;
			
			val=properties.getProperty("synonyms.use","false");
			rs.USE_SYNONYMS=Boolean.parseBoolean(val);
			
			val=properties.getProperty("rouge.type","normal");
			rs.ROUGE_TYPE=RougeType.valueOf(val);
			
			val=properties.getProperty("stopwords.use","false");
			rs.REMOVE_STOP_WORDS=Boolean.parseBoolean(val);
			
			val=properties.getProperty("stopwords.file","resources/stopwords-terrier-en.txt");
			rs.STOP_WORDS_FILE=val;
			
			val=properties.getProperty("topic.type","nn");
			rs.SELECTED_TOPICS=val;
			
			val=properties.getProperty("pos_tagger_name","english-left3words-distsim.tagger");
			rs.TAGGER_NAME=val;
			
			val=properties.getProperty("stemmer_name","englishStemmer");
			rs.STEMMER=val;
			
			val=properties.getProperty("stemmer.use","false");
			rs.USE_STEMMER=Boolean.parseBoolean(val);
			
			
			val=properties.getProperty("output","console");
			rs.OUTPUT_TYPE=val;
			
			val=properties.getProperty("outputFile","results.txt");
			rs.RESULTS_FILE=val;
			
			//property=properties.getProperty("type","normal");
			//ROUGESettings.TYPE= property;

				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	

}
