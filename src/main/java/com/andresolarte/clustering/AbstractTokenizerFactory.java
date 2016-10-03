package com.andresolarte.clustering;

import com.aliasi.tokenizer.EnglishStopTokenizerFactory;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;

public class AbstractTokenizerFactory {
    public static TokenizerFactory tokenizerFactory() {
        TokenizerFactory factory = IndoEuropeanTokenizerFactory.INSTANCE;
        factory = new LowerCaseTokenizerFactory(factory);
        factory = new EnglishStopTokenizerFactory(factory);
        factory = new PorterStemmerTokenizerFactory(factory);
        return factory;
    }
}
