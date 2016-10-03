package com.andresolarte.clustering;


import com.aliasi.cluster.CompleteLinkClusterer;
import com.aliasi.cluster.Dendrogram;
import com.aliasi.cluster.HierarchicalClusterer;
import com.aliasi.util.Distance;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatBot {

    static final Distance<Document> COSINE_DISTANCE = (doc1, doc2) -> {
        double oneMinusCosine = 1.0 - doc1.cosine(doc2);
        if (oneMinusCosine > 1.0) {
            return 1.0;
        } else if (oneMinusCosine < 0.0) {
            return 0.0;
        } else {
            return oneMinusCosine;
        }
    };

    static final Function<URL, String> LOAD_FROM_RES = (u) -> {
        {
            try {
                return Resources.toString(u, Charsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };


    public static void main(String... args) {

        try {
            new ChatBot().run(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(String question) throws Exception {
        Document questionDoc = new Document(question);
        Set<Document> docSet = setUpDocSet(questionDoc);

        HierarchicalClusterer<Document> clusterer
                = new CompleteLinkClusterer<>(COSINE_DISTANCE);
        Dendrogram<Document> completeLinkDendrogram
                = clusterer.hierarchicalCluster(docSet);

        //Ideally every answer is in a different cluster, and the question is one of those clusters
        int k = completeLinkDendrogram.size() - 1;
        String answer = null;
        while (answer == null) {
            Set<Set<Document>> sets = completeLinkDendrogram.partitionK(k); //Let group into k clusters
            Set<Document> matchingSet = findSet(sets, questionDoc);
            if (matchingSet.size() > 1) {
                answer = findRandomAnswer(matchingSet, questionDoc);
            }
            //If the question wasn't clustered with at least one answer, let's reduce the number of clusters.
            k = --k;
        }
        System.out.println("Final K:  " + (++k));
        System.out.println("Question: " + question);
        System.out.println("Answer:   " + answer);

    }

    private String findRandomAnswer(Set<Document> matchingSet, Document exclude) {
        List<Document> answers = matchingSet.stream()
                .filter(d -> d != exclude)
                .collect(Collectors.toList());
        Collections.shuffle(answers);
        return answers.get(0).getText();
    }

    private Set<Document> findSet(Set<Set<Document>> sets, Document questionDoc) {
        return sets.parallelStream()
                .filter(s -> s.contains(questionDoc))
                .findFirst().get();
    }

    private Set<Document> setUpDocSet(Document questionDoc) throws IOException {
        Set<Document> ret = ClassPath.from(this.getClass().getClassLoader()).getResources()
                .stream()
                .filter(r -> r.getResourceName().startsWith("answers"))
                .map(ClassPath.ResourceInfo::url)
                .map(LOAD_FROM_RES::apply)
                .map(s -> new Document(s))
                .collect(Collectors.toSet());
        ret = Stream.concat(ret.stream(), Stream.of(questionDoc))
                .collect(Collectors.toSet());
        return ret;
    }


}
