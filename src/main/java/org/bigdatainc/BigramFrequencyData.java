package org.bigdatainc;

import static java.util.Optional.ofNullable;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class BigramFrequencyData {

    private final Map<String, LangProbabilities> wordToLangProbabilities;
    private final Set<String> languages;

    public BigramFrequencyData(final Set<LanguageStats> languagesStats) {
        final int bigramsAmount = languagesStats.stream()
                .map(LanguageStats::bigrams)
                .mapToInt(List::size)
                .max()
                .orElse(300);
        this.wordToLangProbabilities = new HashMap<>(bigramsAmount);
        this.languages = new HashSet<>(languagesStats.size());

        for (LanguageStats stats : languagesStats) {
            languages.add(stats.lang());
            for (Bigram bigram : stats.bigrams()) {
                wordToLangProbabilities.compute(
                        bigram.name(),
                        (bigramName, oldValue) -> {
                            final var probabilities = ofNullable(oldValue).orElseGet(LangProbabilities::of);
                            probabilities.add(stats.lang(), stats.totalOccurrences(), bigram);
                            return probabilities;
                        }
                );
            }
        }
    }

    public static BigramFrequencyData load(final Configuration configuration,
                                           final Path langFilesDir) throws IOException {

        final FileSystem fileSystem = FileSystem.get(configuration);
        final List<LoadedLanguage> loadedLanguages = new LinkedList<>();
        for (final FileStatus status : fileSystem.listStatus(langFilesDir)) {
            try (final FSDataInputStream is = fileSystem.open(status.getPath())) {
                loadedLanguages.add(new LoadedLanguage(status.getPath().getName(), new String(is.readAllBytes())));
            }
        }
        fileSystem.close();


        final Set<LanguageStats> stats = new HashSet<>();
        for (final LoadedLanguage language : loadedLanguages) {
            final AtomicInteger totalOccurrences = new AtomicInteger();

            final List<Bigram> bigrams = Arrays.stream(language.content().split(System.lineSeparator()))
                    .map(bigramStr -> bigramStr.split(":"))
                    .map(Bigram::of)
                    .peek(bigram -> totalOccurrences.addAndGet(bigram.occurrences()))
                    .toList();

            stats.add(new LanguageStats(language.name(), totalOccurrences.get(), bigrams));
        }
        return new BigramFrequencyData(stats);
    }

    public Set<String> languages() {
        return ImmutableSet.copyOf(languages);
    }

    public LangProbabilities getProbabilities(final String bigram) {
        return wordToLangProbabilities.getOrDefault(bigram, LangProbabilities.empty());
    }

    public static class LangProbabilities extends HashMap<String, Double> {
        private static final LangProbabilities EMPTY = new LangProbabilities(Map.of());

        public LangProbabilities() {super();}

        public LangProbabilities(final Map<String, Double> input) {super(input);}

        public static LangProbabilities of() {return new LangProbabilities();}

        public static LangProbabilities empty() {return EMPTY;}

        public void add(final String lang, final int totalOccurrences, final Bigram bigram) {
            final double probability = bigram.probability(totalOccurrences);
            put(lang, probability);
        }
    }

    public record LanguageStats(String lang, int totalOccurrences, List<Bigram> bigrams) {}

    public record Bigram(String name, int occurrences) {
        public static Bigram of(final String[] bigramParts) {
            final String bigramName = bigramParts[0].replace("\"", "");
            final int bigramOccurrences = Integer.parseInt(bigramParts[1]);
            return new Bigram(bigramName, bigramOccurrences);
        }

        public double probability(final int totalOccurrences) {
            return (double) occurrences / totalOccurrences;
        }
    }

    public record LoadedLanguage(String name, String content) {}
}
