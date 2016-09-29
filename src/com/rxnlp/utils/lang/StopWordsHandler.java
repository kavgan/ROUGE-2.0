package com.rxnlp.utils.lang;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

public class StopWordsHandler {

	private static HashSet<String> mStopWords = new HashSet<String>();
	static Logger logger = Logger.getLogger(StopWordsHandler.class);

	public StopWordsHandler(String stopFile) {
		loadStopWords(stopFile);
	}

	public static boolean isStop(String str) {
		if (mStopWords.contains(str))
			return true;
		return false;
	}

	private static void loadStopWords(String stopFile) {

		try (Stream<String> stream = Files.lines(Paths.get(stopFile),StandardCharsets.ISO_8859_1)) {
			stream.forEach(line -> {
				String stopWord = line.trim().toLowerCase();
				mStopWords.add(stopWord);
			});

		} catch (IOException e) {
			logger.warn("Problem with stopwords file: " + e.getMessage());
			logger.warn("If you want to use stop words, please fix this issue first before proceeding.");
		}
	}
}
