# ROUGE 2.0 Usage Documentation

This tutorial walks you through using the ROUGE 2.0 toolkit to start evaluating automatic summarization tasks. Please check the [features](/readme.md) of this toolkit before you start this tutorial.

## Preparatory Steps

*   Ensure you have [Java 1.8][2] and above installed. You only need the JRE.
*   Make sure Java works by typing `java -version` from the command line
*   Next, download the most recent version of [ROUGE 2.0][3] distribution

**Notes on terminology:**

*   **Reference summary** refers to the "gold standard" or human summaries.
*   **System summary** refers to the machine generated or candidate summaries.
*   You can have **any number of reference summaries** for each summarization task.
*   You can have **any number of system summaries** for each summarization task.

* * *

## Step 1: Unpack ROUGE 2.0

The first step is to unpack the ROUGE 2.0 distribution to any location on your machine. After unpacking ROUGE 2.0, this is the resulting directory structure that you would see:

[![][4]][5]

*   **resources/** - This directory contains replaceable stop words, POS taggers, language related resources.
*   **test-summarization/**- This is a test project containing "references" and "system" folders. It currently has samples for english and persian.
*   **results.csv** - This is a sample results file produced by ROUGE 2.0 
*   **rouge.properties**- This is where you configure all your settings. This would be the file you would be working with extensively. (see step 4 for details)
*   **rouge2_xx.jar**- This is the runnable ROUGE 2.0 jar file that you can use to compute ROUGE scores.  

* * *

## Step 2: Getting Started - Create a project directory

To get started, create a directory structure as follows anywhere on your system:

    your-project-name/ 
        reference/  ---- contains all reference summaries that will be used for evaluation. 
        system/  ---  contains all system generated summaries. 
    

Since we avoid any complex HTML formatting for evaluation. We evaluate summaries based on file naming convention. Please follow instructions in the next step for naming convention.

* * *

## Step 3: Generate your system and reference summaries

In generating your system and reference summaries, please adhere to the following naming convention for evaluation. The naming convention for ROUGE 2.0 is actually very simple. You basically use the task name, reference summary name and system summary names.

#### Reference Summary Naming Convention

Your reference summary should be named as follows:

    (task_name)_(reference_name)
    

So if you have a summarization document called news1 and you have 3 human composed reference summaries, the files would be similar to:

    news1_reference1.txt
    news1_reference2.txt
    news1_reference3.txt
    

The underscore is mandatory, and the first part of the file name always refers to the summarization task (i.e. document or text to be summarized). Please see samples under **test-summarization/reference/**

#### System Summary Naming Convention

Your system summary should be named as follows:

    (task_name)_(system_name)
    

If you have a document called news1 (as earlier) and you have 4 systems (e.g. variation of the same summarization algorithm), each system would generate a separate summary for the same summarization task. The system files would thus be similar to:

    news1_system1.txt
    news1_system2.txt
    news1_system3.txt
    news1_system4.txt
    

The underscore is mandatory, and the first part of the file name always refers to the summarization task (i.e. document or text to be summarized). Note that news1 in the reference folder matches up with news1 in the system folder. Each of the system summaries for news1 would be individually evaluated against all reference summaries for news1 in the reference folder (scores averaged). Please see samples under **test-summarization/system/**

#### Reference and System Summary Formatting

You would need to use one file for each reference or system summary. Each sentence in your system or reference summary should be on a separate line. There is no need for numbering the sentences or placing the text in a html file. Any file extension is permitted as long as each sentence in the file is on a separate line. For example, if your system summary file has 3 sentences, the file would contain sentences like this:

<!--img src="http://www.rxnlp.com/wp-content/uploads/2016/09/formatting.png" align="center" -->

    lightweight phone.
    the screen is very clear.
    bright screen.
    

More examples can be found in **[test-summarization/system][6]** or **[test-summarization/reference][7]**

* * *

## Step 4: Configure rouge.properties

Now, before you start evaluating your summaries you need to configure the **rouge.properties** file located in the root of ROUGE 2.0. This is where you specify the ROUGE-N type that you want to evaluate, stop words to use, output file, synonyms and etc. You would be working with this file quite extensively. The table below describes the rouge 2.0 parameters and how you should set it. The most important parameters are:

*   project.dir
*   rouge.ngram (and parameters related to this selection)
*   output

| **Property Name**  | **Description of Property**                                                                                                                                                                                                                                                                                                                                                                                                                            |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **project.dir**    | Location of your reference and system summaries. Default is `test-summarization` within the root folder. This folder contains sample reference and system summaries.                                                                                                                                                                                                                                                                                   |
| **rouge.type**     | topic, topicUniq or normal. Select normal for the typical ROUGE-N,L,S,SU evaluation. If you are not too sure what this does, just leave the default.                                                                                                                                                                                                                                                                                                   |
| **ngram**          | What n-gram size? This is only applicable if `rouge.type=normal`. Options: 1...N, SN, SUN, L (where N is an integer). Separate by comma if you want to evaluate more than one ngram type.                                                                                                                                                                                                                                                              |
| **stopwords.use**  | Do you want to use stop words? true/false . Default is `true`.                                                                                                                                                                                                                                                                                                                                                                                         |
| **stopwords.file** | Location of stop words file. This can be changed based on language. By default it uses what the perl version of ROUGE uses.                                                                                                                                                                                                                                                                                                                            |
| **stemmer.use**    | Use stemming? (true/false). Default is `false`                                                                                                                                                                                                                                                                                                                                                                                                         |
| **stemmer.name**   | Which stemmer to use? englishStemmer, turkishStemmer, frenchStemmer...? Default is `englishStemmer`                                                                                                                                                                                                                                                                                                                                                    |
| **topic.type**     | Only set this if topic of topicUniq are used. This should be the POS form in lowercase (based on Stanford's POS Tagger). For example, "nn"                                                                                                                                                                                                                                                                                                             |
| **synonyms.use**   | Use synonyms? true/false . Default is false.                                                                                                                                                                                                                                                                                                                                                                                                           |
| **beta**           | The beta value to use for F-measure computation. Use a large value, for e.g. `1000` to give more weight to recall. Use a value `< 1` to give more weight to precision. Default is 1 which is equivalent to the harmonic mean. | 
| **output** | How to generate results, to screen or file? file or console. Default is `file` | 
| **outputFile** | What file to output results to? This is to be set only if output=file. Default is `results.csv`. |

* * *

## Step 5: Running ROUGE 2.0 Jar File

Once you have generated your system summaries and formatted your reference summaries as specified in Step 3 and configured the rouge property file as in Step 4, the next step would be to run the evaluation package. Assuming ROUGE 2.0 was unpacked into C:\projects\rouge2.0, you can execute ROUGE 2.0 from any Linux or Windows machine as follows:

    java -jar rouge2-xx.jar
    

This step uses all the system summaries and corresponding reference summaries from your project directory defined in Step 2 and computes the appropriate ROUGE scores (as specified in rouge.properties). By default, the rouge.properties file in the root of the rouge 2.0 installation will be used. You could either modify this version of rouge.properties or you can also use a rouge.properties file located elsewhere on disk as follows:

    -Drouge.prop="path_to_properties_file"
    

The output of evaluation would be printed to console and also to file if `output=file` in rouge.properties. Here is an example of output printed to screen:

<img src="http://www.rxnlp.com/wp-content/uploads/2016/09/Screen-Shot-2018-03-02-at-10.26.02-PM.png" alt=""  class="size-full wp-image-1203" border="1" />


Here is an example of the CSV results file produced:

<!--img src="http://www.rxnlp.com/wp-content/uploads/2016/09/rouge2.0-results-file.png" alt="" width="921" height="149" class="alignleft size-full wp-image-1057" -->

**ROUGE-Type**|**Task Name**|**System Name**|**Avg\_Recall**|**Avg\_Precision**|**Avg\_F-Score**|**Num Reference Summaries**
:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|:-----:
ROUGE-L+StopWordRemoval|TASK1|ENGLISHSYSSUM2.TXT|0.80000|0.21053|0.33333|2
ROUGE-L+StopWordRemoval|TASK1|ENGLISHSYSSUM1.TXT|0.60000|0.60000|0.60000|2
ROUGE-1+StopWordRemoval|TASK1|ENGLISHSYSSUM2.TXT|0.80000|0.17391|0.28571|2
ROUGE-1+StopWordRemoval|TASK1|ENGLISHSYSSUM1.TXT|0.80000|0.66667|0.72727|2
ROUGE-2+StopWordRemoval|TASK1|ENGLISHSYSSUM2.TXT|0.00000|0.00000|0.00000|2
ROUGE-2+StopWordRemoval|TASK1|ENGLISHSYSSUM1.TXT|0.00000|0.00000|0.00000|2
ROUGE-SU4+StopWordRemoval|TASK1|ENGLISHSYSSUM2.TXT|0.55556|0.05882|0.10638|2
ROUGE-SU4+StopWordRemoval|TASK1|ENGLISHSYSSUM1.TXT|0.44444|0.44444|0.44444|2
ROUGE-L+StopWordRemoval|TASK2|PERSIANSYSSUM1.TXT|0.25993|0.51799|0.34615|1
ROUGE-1+StopWordRemoval|TASK2|PERSIANSYSSUM1.TXT|0.38475|0.97309|0.55146|1
ROUGE-2+StopWordRemoval|TASK2|PERSIANSYSSUM1.TXT|0.37138|0.96244|0.53595|1
ROUGE-SU4+StopWordRemoval|TASK2|PERSIANSYSSUM1.TXT|0.36185|0.96256|0.52598|1

By default if outputFile in rouge.properties is not changed, the output would be in `results.csv` in the root of rouge 2.0.

* * *

## Running ROUGE 2.0 for Unicode Texts (Persian, Tamil, etc)

One of the problems with the original perl version of ROUGE is that it does not support evaluation of unicode based texts because of the way the text is tokenized. This package has been tested with Persian texts and would thus work in cases where the original Perl package fails. To make sure that this package works for unicode texts please ensure that:

*   In the `rouge.properties` file, `use_synonyms` is set to false. When this is true, ROUGE 2.0 tries to POS tag the text and if there isn't a suitable POS tagger from the Stanford POS tagging libraries, this will cause issues.

Other than this setting, you are actually ready to go! Just follow Steps 1 - 5 as specified above.

## Support

For technical questions, feedback and feature requests, please create an issue on [GitHub Issues][8].

 [1]: http://www.rxnlp.com/rouge-2-0/
 [2]: https://java.com/en/download/
 [3]: https://github.com/RxNLP/ROUGE-2.0/tree/master/versions
 [4]: https://images-blogger-opensocial.googleusercontent.com/gadgets/proxy?url=http%3A%2F%2F2.bp.blogspot.com%2F-uSuXVaQ4vYU%2FVO4AB_Nh9VI%2FAAAAAAAAHjI%2F9TZL6KUWCpY%2Fs1600%2Frouge2.0-directory-structure.png&container=blogger&gadget=a&rewriteMime=image%2F*
 [5]: http://2.bp.blogspot.com/-uSuXVaQ4vYU/VO4AB_Nh9VI/AAAAAAAAHjI/9TZL6KUWCpY/s1600/rouge2.0-directory-structure.png
 [6]: https://github.com/RxNLP/ROUGE-2.0/tree/master/projects/test-summarization/system
 [7]: https://github.com/RxNLP/ROUGE-2.0/tree/master/projects/test-summarization/reference
 [8]: https://github.com/RxNLP/ROUGE-2.0/issues
 [9]: http://www.rxnlp.com/ask-question/
