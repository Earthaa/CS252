import re, sys, operator

# Mileage may vary. If this crashes, make it lower
RECURSION_LIMIT = 9000
# We add a few more, because, contrary to the name,
# this doesn't just rule recursion: it rules the
# depth of the call stack

def count(f):
    def g(word_list, stopwords, wordfreqs):
        # What to do with an empty list
        if word_list == []:
            return
        # The inductive case, what to do with a list of words
        else:
            # Process the head word
            word = word_list[0]
            if word not in stopwords:
                if word in wordfreqs:
                    wordfreqs[word] += 1
                else:
                    wordfreqs[word] = 1
        # Process the tail
            f(word_list[1:], stopwords, wordfreqs)
    return g


def wf_print(f):
    def g(word_freq):
        if word_freq == []:
            return
        else:
            (w, c) = word_freq[0]
            print(w, '-', c)
            return f(word_freq[1:])
    return g
# Y Combinator with multiple arguments

Y = lambda f: (lambda g: g(g))(lambda h: lambda *args: f(h(h))(*args))

stop_words = set(open('../stop_words.txt').read().split(','))
words = re.findall('[a-z]{2,}', open(sys.argv[1]).read().lower())
word_freqs = {}
# Theoretically, we would just call count(words, word_freqs)
# Try doing that and see what happens.
# Using Y combinator to do implicit recursion
for i in range(0, len(words), RECURSION_LIMIT):
     Y(count)(words[i:i+RECURSION_LIMIT], stop_words, word_freqs)
Y(wf_print)(sorted(word_freqs.iteritems(), key=operator.itemgetter(1), reverse=True)[:25])

