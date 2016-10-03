This bot uses **unsupervised** clustering of text, to try to find an answer that is similar to a question provided by the user. It will choose from a set of answers, reducing the number of clusters until it finds an answer that is clustered together with the question. It uses LingPipe, a Java Natural Language Processing and Text Analytics libary.

    mvn package
    java -jar target/clustering-1.0.jar "On nuclear weapons, President Obama reportedly considered changing the nation's longstanding policy on first use. Do you support the current policy?"

The output will look like this:

    Final K:  11
    Question: On nuclear weapons, President Obama reportedly considered changing the nation's longstanding policy on first use. Do you support the current policy?
    Answer:   We have to get NATO to go into the Middle East with us, in addition to surrounding nations, and we have to knock the hell out of ISIS, and we have to do it fast, when ISIS formed in this vacuum created by Barack Obama and Secretary Clinton.
    
The K value is the number of clusters used to find a matching answer. A higher number means that the answer is more specific to the question. The bot seems to work better with longer questions.

Some things that might make this more efficient:
* Using stop words/soundex/etc
* Doing some supervided clustering. Right now the answers are somewhat organized in the filesystem, but that was only to make it easier to compile them. When we process them, they're all mixed together with no labelling at all.
