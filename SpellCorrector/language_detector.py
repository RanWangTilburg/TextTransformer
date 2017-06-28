from langdetect import detect
import pandas as pd
import re


def is_english(string):
    is_english.counter += 1

    if is_english.counter % 100 == 0:
        print is_english.counter
    try:
        language = detect(string)
        if 'en' == language:
            return True
        else:
            return False
    except:
        return False


is_english.counter = 0




def is_ascii(s):
    return bool(re.match(r'[\x00-\x7F]+$', s))


def detect_non_english(data):
    result = []

    nrow = data.shape[0]
    non_english_count = 0

    for row in range(nrow):
        if 'en' != detect(data[row]):
            result.append(row)
            non_english_count += 1

    print "Examined %d entries" % nrow
    print "Detected %d non-English entries" % non_english_count

    return result


def run_language_detector():
    inpath = "/home/user/Desktop/Data/replaced.txt"
    outpath = "/home/user/Desktop/Data/english.txt"
    sep = "\t"
    data = pd.read_csv(inpath, sep=sep)
    text = data["Review_Texts"]
    data_ascii = data[text.apply(is_ascii)]
    text = data_ascii["Review_Texts"]
    data_english = data_ascii[text.apply(is_english)]

    data_english.to_csv(outpath, sep=sep, index=False)
