## ROUGE 2.0

ROUGE 2.0 is a Java Package for Evaluation of Summarization Tasks building on the Perl Implementation of ROUGE. This is a lightweight open-source tool that allows for easy evaluation of summaries or translation by limiting the amount of formatting needed in terms of reference summaries as well as system summaries. In addition, it also allows for evaluation of unicode texts known to be an issue with other implementations of ROUGE. One can also add new evaluation metrics to the existing code base or improve on existing ones.

ROUGE stands for Recall-Oriented Understudy for Gisting Evaluation. It consists of a set of metrics for evaluating automatic summarization of texts as well as machine translation. It works by comparing an automatically produced summary or translation against a set of reference summaries (typically human-produced) or translations. If you want to learn more about how ROUGE works you can [read this article](http://www.rxnlp.com/how-rouge-works-for-evaluation-of-summarization-tasks/).


# Features

ROUGE 2.0 currently supports the following:

* Evaluation of ROUGE-N (unigram, bigrams, trigrams, etc)
* Stemming for different languages
* Stopword removal with customizable stop words
* Evaluation of unicode texts (e.g. Persian)

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
