package com.rxnlp.lang.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;




public class WordNetDict {
	
	
	public WordNetDict(String path){
		System.setProperty("wordnet.database.dir","C:\\Program Files (x86)\\WordNet\\2.1\\dict\\");
	}
	
	public WordNetDict(){
		System.setProperty("wordnet.database.dir","C:\\Program Files (x86)\\WordNet\\2.1\\dict\\");
	}
	
	static WordNetDatabase database = WordNetDatabase.getFileInstance(); 

	public static void main(String[] args) {
		AdjectiveSynset nounSynset; 
		AdjectiveSynset[] hyponyms; 
		
		
		WordNetDict wdn=new WordNetDict("");
		
		wdn.getAdjectiveSynonyms("awesome");
		wdn.getVerbSynonyms("fell");
		
		System.out.println(wdn.getNounSynonyms("phone"));


	}
	
	/**
	 * 
	 * @param adjWord
	 * @return
	 */
	public HashSet<String> getAdjectiveSynonyms(String adjWord) {

		Synset[] synsets1 = database.getSynsets(adjWord, SynsetType.ADJECTIVE); 
		Synset[] synsets2 = database.getSynsets(adjWord, SynsetType.ADJECTIVE_SATELLITE);
		HashSet<String> finalSyns=new HashSet<String>();
		

		List<Synset> syns=new ArrayList<Synset>();
		syns.addAll(Arrays.asList(synsets1));
		syns.addAll(Arrays.asList(synsets2));


		for (Synset ss:syns) { 

			AdjectiveSynset synset = (AdjectiveSynset)ss;
			AdjectiveSynset[] words = synset.getSimilar();

			if(ss.getWordForms().length>0){

				//Only use senses where the tag count > 3
				if(ss.getTagCount(ss.getWordForms()[0])>3){

					for(AdjectiveSynset adj:words){

						//int usage=nounSynset.getTagCount(adj.getWordForms()[0]);
						List<String> synList=getCompleteAdjSet(adj);
						finalSyns.addAll(synList);
					}
				}
			}
		}
		
		return finalSyns;
	}

	
	/**
	 * For verbs we use troponyms and hypernymes along with synset
	 * troponym - A verb that indicates more precisely the manner of doing something by replacing a verb of a more generalized meaning.
	 * @param verb
	 * @return
	 */
	public HashSet<String> getVerbSynonyms(String verb) {

		Synset[] synsets1 = database.getSynsets(verb, SynsetType.VERB); 
		HashSet<String> finalSyns=new HashSet<>();

		List<Synset> syns=new ArrayList<Synset>();
		List<Synset> syns2=new ArrayList<Synset>();
		List<String> synList=new ArrayList<String>();
		syns.addAll(Arrays.asList(synsets1));


		for (Synset ss:syns) { 

			VerbSynset synset = (VerbSynset)ss;

			//For verbs we use troponyms and hypernymes along with synset 
			//troponym - A verb that indicates more precisely the manner of doing something by replacing a verb of a more generalized meaning.
			if(ss.getTagCount(ss.getWordForms()[0])>3){
				VerbSynset[] hypernyms = synset.getHypernyms();
				syns2.addAll(Arrays.asList(hypernyms));

				/*VerbSynset[] entails = synset.getEntailments();
				syns2.addAll(Arrays.asList(entails));*/

				VerbSynset[] tropo = synset.getTroponyms();
				syns2.addAll(Arrays.asList(tropo));

			}
		}

		syns.addAll(syns2);

		for (Synset ss:syns) { 
			//VerbSynset[] words = synset.getSimilar();

			if(ss.getWordForms().length>0){

				//Only use senses where the tag count > 3
				if(ss.getTagCount(ss.getWordForms()[0])>3){

					//for(VerbSynset adj:words){

					//int usage=nounSynset.getTagCount(adj.getWordForms()[0]);
					synList.addAll(Arrays.asList(ss.getWordForms()));

				}
			}
		}

		finalSyns.addAll(synList);
		return finalSyns;
	}

	/**
	 * We use original synsets, hyponyms and hypernyms for nouns
	 * hypernym is a parent of hyponym
	 * color ==> red
	 * @param noun
	 * @return
	 */
	public HashSet<String> getNounSynonyms(String noun) {

		HashSet<String> finalSyns=new HashSet<>();
		Synset[] synsets = database.getSynsets(noun, SynsetType.NOUN); 

		List<Synset> syns=new ArrayList<Synset>();
		List<Synset> syns2=new ArrayList<Synset>();
		List<String> synList=new ArrayList<String>();
		
		//FIRST ADD THE SYNSET TO THE SYNONYM LIST
		syns.addAll(Arrays.asList(synsets));


		//THEN ADD HYPONYMS AND HYPERNYMS TO THE SYNONYM LIST
		//for each synset in the list, get the word forms, and add the synsets of hypernyms and hyponyms of this word
		for (Synset ss:syns) { 

			NounSynset synset = (NounSynset)ss;
			String[] wordForms = synset.getWordForms();

			for(String wf:wordForms){
				//if(ss.getTagCount(wf)>3){
					NounSynset[] hypernyms = synset.getHypernyms();
					syns2.addAll(Arrays.asList(hypernyms));

					NounSynset[] hypos = synset.getHyponyms();
					syns2.addAll(Arrays.asList(hypos));
				//}
			}
		}
		syns.addAll(syns2);

		//NEXT, ONLY SELECT THOSE THAT HAVE A TAG COUNT OF > 3
		//for each synset in the list, get the word forms, and add the hypernyms and hyponyms of this word
		for (Synset ss:syns) { 
		
			if(ss.getWordForms().length>0){

				String[] wordForms = ss.getWordForms();
				for(String wf:wordForms){
					//Only use senses where the tag count > 3
					if(ss.getTagCount(wf)>0){
						synList.add(wf);
					}
				}
			}
		}
		
		finalSyns.addAll(synList);
		return finalSyns;
	}

	public List<String> getCompleteAdjSet(AdjectiveSynset adj) {

		List<String> words=new ArrayList<String>();

		String[] wf = adj.getWordForms();
		for(String w:wf){
			words.add(w);
		}

		AdjectiveSynset[] syms = adj.getSimilar();
		for(AdjectiveSynset sym:syms){
			wf = sym.getWordForms();

			for(String w:wf){
				if(sym.getTagCount(w)>0)
					words.add(w);
			}
		}

		return words;
	}

}
