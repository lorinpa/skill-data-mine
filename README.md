# skill-data-mine

Analyzes which skills are required by public posts. 

For example, if I search a job site for "java" and "clojure", what other skills do those
jobs required. Is there any trends worth noting, any substantive correlations, etc.

Project is written in Clojure. Uses Datomic for data persistence and query services.

This is a standalone command line application. Thus, I am using the free version of Datomic.

I'll add a blog post on my website which details the implentation. Part of a series on Polyglot programming.

See my [website, Public-Action.org] (http://public-action.org/mob/polyglot-index.html)  to read the articles series.

## Process
Parses an RSS feed (of job postings).
Stores the job title, unique identifier and skill list in Datomic.
Generates severak reports.
-- Filter the job posts further (E.G. show me jobs that include "Datomic")
-- Top 10 skills found, display percent per skill. E.G. sql was found in 10% of the jobs, jpa in 8%, etc.
-- Report history of particular skill. E.G. Dec 1, 2013 Clojure found in 10% of jobs, Dec 2, 2013 Clojure found in 12% of jobs, etc.


## License
Code licensed under [GNU General Public License, version 2] (http://www.gnu.org/licenses/gpl-2.0.html")

## License
Written by Lorin M Klugman

## Development Notes

To run tests, issue the following command line:
$> lein test


