package com.andresolarte.clustering;

import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.util.Counter;
import com.aliasi.util.ObjectToCounterMap;

public class Document {
    final String text;
    final ObjectToCounterMap<String> tokenCounter
            = new ObjectToCounterMap<>();
    final double length;


    public Document(String text) {

        this.text = text;
        char[] chars = text.toCharArray();

        Tokenizer tokenizer = AbstractTokenizerFactory.tokenizerFactory().tokenizer(chars, 0, chars.length);
        String token;
        while ((token = tokenizer.nextToken()) != null) {
            this.tokenCounter.increment(token.toLowerCase());
        }
        this.length = length(tokenCounter);
    }

    public double cosine(Document thatDoc) {
        return product(thatDoc) / (length * thatDoc.length);
    }

    public double product(Document thatDoc) {
        double sum = 0.0;
        for (String token : tokenCounter.keySet()) {
            int count = thatDoc.tokenCounter.getCount(token);
            if (count == 0) {
                continue;
            }
            // tf = sqrt(count); sum += tf1 * tf2
            sum += Math.sqrt(count * tokenCounter.getCount(token));
        }
        return sum;
    }

    static double length(ObjectToCounterMap<String> otc) {
        double sum = 0.0;
        for (Counter counter : otc.values()) {
            double count = counter.doubleValue();
            sum += count;  // tf =sqrt(count); sum += tf * tf
        }
        return Math.sqrt(sum);
    }

    public String getText() {
        return text;
    }
}
