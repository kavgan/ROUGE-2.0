# ROUGE-2.0
ROUGE 2.0 is a Java Package for Evaluation of Summarization Tasks building on the Perl Implementation of ROUGE. ROUGE 2.0 currently supports the following:

- Evaluation of ROUGE-N (unigram, bigrams, trigrams, etc)
- Stemming for different languages
- Stopword removal with customizable stop words
- Evaluation of unicode texts (e.g. Persian)
- Synonyms capture for better agreement between system and reference summaries [English only and requires WordNet installation]
- Evaluation of specific parts of speech (e.g. NN) [Uses Stanford POS Tagger]

This is an open-source project, so if you want to implement other measures, please read the [documentation](#contributing-to-rouge20) on how to contribute to this project.  

## Quick Start
To start evaluating your summaries using ROUGE, just download the [complete ROUGE 2.0 distribution](distribute/downloads) and follow the [usage instructions](http://www.rxnlp.com/rouge-2-0-usage-documentation/). Please use Java 1.8 and above to run the ROUGE 2.0 package. 

To build on the source code or make contributions, please read the installation instructions below.

## Installation

To build on the source code of ROUGE 2.0, [clone the repository](https://github.com/RxNLP/ROUGE-2.0.git) and import the project as a Maven project by selecting the `pom.xml` file in the root directory from Eclipse, IntelliJ or other IDE's. Next, add the jars under the `lib` directory to your build path for the code to compile. Note that you can also install the jars in the `lib` directory by creating a local maven repository for those jar files. All you would need to do after that is to add the local dependencies to your `pom.xml`.

## Download / Links
- [ROUGE 2.0 Downloadable Package](distribute/downloads)
- [ROUGE 2.0 Usage Documentation](http://www.rxnlp.com/rouge-2-0-usage-documentation/)
- [Source Code](https://github.com/RxNLP/ROUGE-2.0)

## Contributing to ROUGE 2.0

### Reporting issues

When reporting issues please use "Github Issues" and include as much detail as possible about your operating system and java version. Whenever possible, please also include a brief, self-contained code example that demonstrates the problem. Visuals are appreciated!

### Contributing code

Thanks for your interest in contributing code to ROUGE2.0! To contribute code to ROUGE2.0, please create a new branch and submit a pull request early on with your ideas and planned changes or enhancement. Once you get the thumbs up, you can use your usual workflow for development. Your changes will be reviewed and finally accepted for inclusion. Please use meaningful commit messages that intuitively explain what you are trying to achieve.

If you are proposing new evaluation measures, your changes will be included once your paper is accepted at a conference or journal. 
