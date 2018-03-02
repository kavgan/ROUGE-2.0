# ROUGE 2.0

ROUGE 2.0 is an easy to use evaluation toolkit for Automatic Summarization tasks. It uses the [ROUGE](http://www.aclweb.org/anthology/W04-1013) system of metrics which works by comparing an automatically produced summary or translation against a set of reference summaries (typically human-produced). ROUGE is one of the standard ways to compute effectiveness of auto generated summaries. To understand how ROUGE works you can [read this article](http://www.rxnlp.com/how-rouge-works-for-evaluation-of-summarization-tasks/).


## Features

The latest version of ROUGE 2.0 supports the following:

* Evaluation of ROUGE-N (unigram, bigrams, trigrams, etc)
* Evaluation of ROUGE-L (summary level LCS)
* Evaluation of ROUGE-S and ROUGE-SU (skip-gram and skip-gram with unigrams)
* Evaluation of multiple ROUGE metrics at one go
* Stemming for different languages
* Stopword removal with customizable stop words
* Evaluation of unicode texts (e.g. Persian)
* Minimal formatting requirements for system and reference summaries
* Output in CSV – this makes it super easy for score analysis
* Full documentation and support via [GitHub Issues](https://github.com/RxNLP/ROUGE-2.0/issues) and [RxNLP Q&A](http://www.rxnlp.com/ask-question/#.WpnDHpPwZTY)

This is an open-source project, so if you want to implement other measures, please read the [documentation](#contributing-to-rouge20) on how to contribute to this project.  

## Quick Start
* Ensure you have [Java 1.8](https://java.com/en/download/) and above installed. You only need the JRE.
* Download latest version of [ROUGE 2.0 distribution zip file](https://github.com/RxNLP/ROUGE-2.0/tree/master/versions)
* Unpack the zip file
* Move into the ROUGE 2.0 directory `cd <rouge2-1.x-runnable>`
* Do a test run: `java -jar rouge2-1.x.jar`. You should see results in the console and in `rouge2-1.x-runnable/results.csv`
* The above step uses the sample summarization tasks under `rouge2-1.x-runnable/projects/test-summarization` and the default settings in `rouge2-1.x-runnable/rouge.properties` file.

## Full Documentation
* [Usage instructions](http://www.rxnlp.com/rouge-2-0-usage-documentation/). 
* Please use Java 1.8 and above to run the ROUGE 2.0 package. 
* To build on the source code or make contributions, please read the installation instructions below.

## Installation

To build on the source code of ROUGE 2.0, [clone the repository](https://github.com/RxNLP/ROUGE-2.0.git) and import the project as a Maven project by selecting the `pom.xml` file in the root directory from Eclipse, IntelliJ or other IDE's. Next, add the jars under the `lib` directory to your build path for the code to compile. Note that you can also install the jars in the `lib` directory by creating a local maven repository for those jar files. All you would need to do after that is to add the local dependencies to your `pom.xml`.


## Reporting Issues & Feature Requests

When reporting issues please use "Github Issues" and include as much detail as possible about your operating system and java version. Whenever possible, please also include a brief, self-contained code example that demonstrates the problem. Visuals are appreciated!

If you need new features that would be useful to the community in general, please open up an issue and label it as `feature-request`.

## Contributing to ROUGE 2.0

Thanks for your interest in contributing code to ROUGE2.0! To contribute code to ROUGE2.0, please create a new branch and submit a pull request early on with your ideas and planned changes or enhancement. Once you get the thumbs up, you can use your usual workflow for development. Your changes will be reviewed and finally accepted for inclusion. Please use meaningful commit messages that intuitively explain what you are trying to achieve.

If you are proposing new evaluation measures, your changes will be included once your paper is accepted at a conference or journal.

## License
Please see our [license](https://github.com/RxNLP/ROUGE-2.0/blob/master/LICENSE) information.
