# skill-data-mine

Analyzes which skills are required by public posts. 

For example, if I search a job site for "java" and "clojure", what other skills do those
jobs required. Is there any trends worth noting, any substantive correlations, etc.

Project is written in Clojure. Uses Datomic for data persistence and query services.

This is a standalone command line application. Thus, I am using the free version of Datomic.

See my website article [Data Mining with Clojure and Datomic] (http://public-action.org/mob/polyglot-data-mining-clojure-and-datomic.html)
for more details. Articla and project is part of a series on Polyglot programming.

## Process
* Parses an RSS feed (of job postings).
* Stores the job title, unique identifier and skill list in Datomic.
* Generates several reports.
    * Filter the job posts further (E.G. show me jobs that include "Datomic")
    * Top 10 skills found, display percent per skill. E.G. sql was found in 10% of the jobs, jpa in 8%, etc.
    * Report history of particular skill. E.G. Dec 1, 2013 Clojure found in 10% of jobs, Dec 2, 2013 Clojure found in 12% of jobs, etc.
* NOTE! I am using the RSS feed published by [Careers 2.0 by stackoverflow] (http://careers.stackoverflow.com/). Skills such as "clojure" or "java" are published as RSS categories. That what we need to analyze skill coorelation.


## Basic Usage
* Download an RSS xml file. (Recommended: store in the "data/" disk directory of this project). Hint! Make sure you've stored an XML file, not an HTML represention of the RSS feed.
* Verify Datomic is running on localhost (see below Prepare Database).

* From the root directory of this project, run the following command line:
    * $> lein run -in INPUTFILE -name DESCRIPTION -d yyyy-MM-dd
* Example
    * $> lein run -in "data/12-21-13-rss.xml" -name "12-21-13-rss.xml" -d "2013-12-21"
* Output
    * Program displays a horizontal bar chart. 
        * Total number of jobs found is RSS xml file.
        * For each skill, how many jobs categorized this skill, what percentage of all jobs.

## Producing Reports

* Report All Snapshot Descriptions
    * $> lein -report

* Report Snaphot Details ( displays each job title, job key  and skill list)
    * $> lein run -report jobs SNAPSHOT-DESCRIPTION

* Report SKill History (for each snaphot reports the percent of jobs this skill appeared)
    * $> lein run -report skill-history SKILL
* Example:
    * $> lein run -report skill-history python

* Report Skill History - list (for each snapshot, reports the percent all skills (in list) are present in a job.
    * $> lein run -report skill-history SKILL-LIST
* Example:
    * $> lein run -report skill-history python sql linux 
    Reports the percent of jobs that contained all 3 skill (python sql linux) in each snapshot.

## License
Code licensed under [GNU General Public License, version 2] (http://www.gnu.org/licenses/gpl-2.0.html")

## Author
Written by Lorin M Klugman

## Development Notes

You should have the free version of Datomic running your localhost.

See [Datomic's website for details] (http://www.datomic.com/)

## Prepare Database (one time process)
To install this projects schema. Run lein repl from the projects root directory. Note! Datomic should be running on your locahost.
From the command line with Leinegen:

    * $> lein run -init-db

That function installs the schema and a custom function. 

Before running Peristence and Reporting tests, you need to create a test database instance and load the test database instance with data.
To create and load the test database, execute the following command line once. Note! Datomic must be running on locahost.

    * $> lein test skill-data-mine.load-test

The test suite suite is divided into:
* RSS xml processing
* Peristence and Query
* Reporting
* Command Line Processing
* Filter Functions

Persistence and Reporting tests require Datomic running on localhost. Make sure you've followed the above note (installed and loaded test database). 
RSS xml processing requires the test files located in the data/ disk directory.

To run RSS tests, issue the following command line:
    * $> lein test skill-data-mine.rss-test

To run Persisence and Quert test, issue the following command line:
    * $> lein test skill-data-mine.persist-test

To run Reporting tests, issue the following command line:

    * $> lein test skill-data-mine.report-test

To run Command Line Processing tests, issue the following command line:

    * $> lein test skill-data-mine.core-test

To run Filter functions tests, issue the following command line:

    * $> lein test skill-data-mine.filters-test
