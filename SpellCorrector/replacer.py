import pandas as pd
import re
from collections import namedtuple
import functools
ptn_rpl = namedtuple('ptn_rpl', ['ptn', 'rpl'])
counter = 0


def read_pattern(inpath, sep="\t"):
    patterns_replacement = pd.read_csv(inpath, sep=sep)

    nrow = patterns_replacement.shape[0]

    results = []
    for i in range(nrow):
        results.append(ptn_rpl(ptn=re.compile(patterns_replacement['pattern'][i], re.IGNORECASE),
                               rpl=patterns_replacement['replace'][i]))

    return results


def place_string(string, ptn_rpls):
    place_string.counter += 1
    for ptn_rpl in ptn_rpls:
        string = ptn_rpl.ptn.sub(ptn_rpl.rpl, string)
        if place_string.counter % 100 == 0:
            print "Cleaning row %d" % place_string.counter
    return string
place_string.counter = 0


def replace_with_pattern(data, ptn_rpls):
    nrow = data.shape[0]
    partial = functools.partial(place_string, ptn_rpls=ptn_rpls)
    data.loc[:] = data.apply(partial)

    return data

def is_long_enough(string, minimum_length=150):
    length = len(string)
    if length > minimum_length:
        return True
    else:
        return False

def run_replacer():
    data_in = "/home/user/Desktop/Data/Table_GC_Details.txt"
    ptn_in = "/home/user/Desktop/Data/pr.txt"
    out_path = "/home/user/Desktop/Data/replaced.txt"
    sep = "\t"

    data = pd.read_csv(data_in, sep)
    data = data.dropna()
    data.drop_duplicates(inplace=True)
    ptn = read_pattern(ptn_in)

    text = data["Review_Texts"]

    replace_with_pattern(text, ptn)
    data_long_enough =  data[text.apply(is_long_enough)]
    data_long_enough.to_csv(out_path, sep=sep, index=False)
