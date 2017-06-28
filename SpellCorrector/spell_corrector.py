import re
import enchant
import pandas as pd

# dic = enchant.Dict("en_US")
# regex = re.compile('[.?]|\w+')
from nltk.stem import WordNetLemmatizer
from nltk.tokenize import word_tokenize
from nltk.stem import PorterStemmer
def split_with_punc(string):
    return split_with_punc.regex.findall(string)


split_with_punc.regex = re.compile('[.?]|\w+')


# def spell_check_data(data):
#     nrow = data.shape[0]
#     for row in range(nrow):
#         words = split_with_punc(data[row])
#
#         for word in words:
#             if dic.check(word) is not True:
#                 word = dic.suggest(word)[0]
#
#         data[row] = " ".join(words)
#
#     return data


def spell_check(string):
    spell_check.counter += 1
    # if spell_check.counter % 100 ==0:
    # print spell_check.counter
    print spell_check.counter
    words = split_with_punc(string)

    for elem in range(len(words)):
        if spell_check.dic.check(words[elem]) is not True:
            if len(spell_check.dic.suggest(words[elem])) != 0:
                words[elem] = spell_check.dic.suggest(words[elem])[0]

    return " ".join(words)


spell_check.counter = 0
spell_check.dic = enchant.Dict("en_US")


def lemmatize(string):
    lemmatize.counter += 1
    if lemmatize.counter % 50 == 0:
        print "Finished processing %d items" % lemmatize.counter
    try:
        words = split_with_punc(string)
        words = map(lemmatize.lemmatizer.stem, words)
        return " ".join(words)
    except:
        return string
lemmatize.lemmatizer = PorterStemmer()
lemmatize.counter = 0


def phony3():
    inpath = "/home/user/Desktop/Data/english.txt"
    sep = "\t"
    outpath = "/home/user/Desktop/Data/cleaned.txt"
    data = pd.read_csv(inpath, sep=sep)
    text = data['Review_Texts']

    text.loc[:] = text.apply(spell_check)
    data.to_csv(outpath, sep=sep, index=False)


def lemma(inpath="/home/user/Desktop/Data/cleanned.txt", outpath="/home/user/Desktop/Data/lemma.txt", sep="\t"):
    data = pd.read_csv(inpath, sep=sep)
    text = data['Review_Texts']
    text.loc[:] = text.apply(lemmatize)
    data.to_csv(outpath, sep=sep, index=False)
