import pandas as pd
from wordcloud import WordCloud
import matplotlib.pyplot as plt
import nltk.sentiment as ns
import random
from textblob import TextBlob
import operator


def get_subjectivity(text):
    get_subjectivity.counter += 1
    if get_subjectivity.counter % 100 == 0:
        print "Finished parsing %d texts" % get_subjectivity.counter

    blob = TextBlob(text)
    return blob.sentiment[1]


get_subjectivity.counter = 0


def sort_reverse(dic):
    return sorted(dic.items(), key=operator.itemgetter(1), reverse=True)


def drop_keys(dic, list):
    for key in list:
        dic.pop(key)
    return dic




def print_quantiles(data, step=0.1):
    quantile = 0.0
    while quantile <= 1:
        print "The %.4f quantile is %.4f" % (quantile, data.quantile(quantile))
        quantile += step


def writeDict(dict, filename, sep):
    with open(filename, "a") as f:
        for i in dict.keys():
            f.write(i + " " + sep.join([str(x) for x in dict[i]]) + "\n")


def readDict(filename, sep):
    with open(filename, "r") as f:
        dict = {}
        for line in f:
            values = line.split(sep)
            dict[values[0]] = {int(x) for x in values[1:len(values)]}
        return (dict)


def grey_color_func(word, font_size, position, orientation, random_state=None,
                    **kwargs):
    return "hsl(0, 0%%, %d%%)" % random.randint(60, 100)


def get_date(string):
    date = string.split(" ")
    return date[0]


def get_no_words(string):
    return len(string.split(" "))


def return_text(text):
    result = ""
    for k, v in text.iteritems():
        result += (k + " ") * v
    return result



def print_quantile(data):
    print "20 percent %d " % data.quantile(0.20)
    print "40 percent %d " % data.quantile(0.40)
    print "60 percent %d " % data.quantile(0.60)
    print "80 percent %d " % data.quantile(0.80)


def print_basic_statistics(data):
    print "Min is %d " % data.min()
    print "Max is %d " % data.max()
    print "Average is %d" % data.mean()
    print "S.D is %d" % data.std()


def print_bs_quantile(data):
    print_basic_statistics(data)
    print_quantile(data)


##################################################################
inpath_reivew_data = "/home/user/Desktop/Data/cleanned.txt"
inpath_game_info = "/home/user/Desktop/Data/product.xlsx"
outpaht_review_with_genre = "/home/user/Desktop/Data/review_genre.txt"
review = pd.read_csv(inpath_reivew_data, sep="\t")
game_info = pd.read_excel(inpath_game_info)
game_genre_info = game_info[['ID_Game', 'Genre']]
review_with_game = pd.merge(review, game_genre_info, on='ID_Game')
review_with_game.to_csv(outpaht_review_with_genre, index=False, sep="\t")

# review_with_game_na = review_with_game.dropna()
# We found all the game have been properly assigned to a genre

review_without_horror = review_with_game[review_with_game['Genre'] != 'Horror']
review_without_horror['Day'] = review_without_horror['Date'].apply(get_date)

data = review_without_horror
#########################################################################
data = pd.read_csv("/home/user/Desktop/Data/review_genre.txt", sep="\t")
data['Day'] = data['Date'].apply(get_date)
data['Day'] = pd.to_datetime(data['Day'])
data['Length'] = data['Review_Texts'].apply(len)
data['Words'] = data['Review_Texts'].apply(get_no_words)
data['Words'] = pd.to_numeric(data['Words'])
data.to_csv("/home/user/Desktop/Data/time_length.txt", sep="\t", index=False)
data['Day'] = pd.to_datetime(data['Day'])
data['Out_of'] = pd.to_numeric(data['Out_of'])
data['Helpful'] = pd.to_numeric(data['Helpful'])
################################Information for each reviwer##############
review_count_user = data[['User', 'Review_Texts']].groupby(['User']).agg('count')
review_count_user = review_count_user.rename(columns={'Review_Texts': 'Count'})
review_count_user['User'] = review_count_user.index
review_count_user['Count'] = pd.to_numeric(review_count_user['Count'])
# review_count['Count'] = review_count['Review_Texts']
# review_count['User'] = review_count.index
# review_count_result = review_count['User','Count'].copy()
# review_count_result['User'] = review_count_result.index

#############################Information for Game#########################

review_count_game = data[['ID_Game', 'Review_Texts']].groupby(['ID_Game']).agg('count')
review_count_game = review_count_game.rename(columns={'Review_Texts': 'Count'})
review_count_game['ID_Game'] = review_count_game.index
review_count_game['Count'] = pd.to_numeric(review_count_game['Count'])
review_count_character = data['Length']
review_length_user = data[['User', 'Words']].groupby(['User']).agg('mean')
review_length_game = data[['ID_Game', 'Words']].groupby(['ID_Game']).agg('mean')
review_length_user = review_length_user.rename(columns={'Review_Texts': 'Count'})
review_length_user['User'] = review_length_user.index
review_length_user['Count'] = pd.to_numeric(review_length_user['Count'])

review_time = data[['ID_Game', 'Day']].groupby(['ID_Game']).agg('min')
review_time = review_time.rename(columns={"Day": "RDay"})
review_time['ID_Game'] = review_time.index
review_data_with_time = pd.merge(data, review_time, on="ID_Game")
##############################################################################
review_data_release = pd.read_csv("/home/user/Desktop/Data/review_with_release_date.csv", sep="\t")
data = review_data_release
data['Day'] = pd.to_datetime(data['Day'])
data['Out_of'] = pd.to_numeric(data['Out_of'])
data['Helpful'] = pd.to_numeric(data['Helpful'])
review_text = data['Review_Texts'].str.cat(sep="\n")
# review_text = review_text.encode('ascii', 'replace').decode()
review_text = unicode(review_text, 'utf-8')
blob = TextBlob(review_text)
np_count = blob.np_counts
#############################################################################
###Time Test
review_data_release = pd.read_csv("/home/user/Desktop/Data/review_with_release_date.csv", sep="\t")
data = review_data_release
data['Day'] = pd.to_datetime(data['Day'])
data['Out_of'] = pd.to_numeric(data['Out_of'])
data['Helpful'] = pd.to_numeric(data['Helpful'])
number_to_sample = 500
data_sampled = data.sample(n=number_to_sample)
review_text = data_sampled['Review_Texts'].str.cat(sep=" ")
review_text = unicode(review_text, 'utf-8')
blob = TextBlob(review_text)

% time
np_count = blob.np_counts

#########################Plotting Section####################################
import numpy as np
import matplotlib.mlab as mlab

review_data_total = pd.read_csv("/home/user/Desktop/Data/review_with_release_date.csv", sep="\t")
data = review_data_total
data['Day'] = pd.to_datetime(data['Day'])
data['Out_of'] = pd.to_numeric(data['Out_of'])
data['Helpful'] = pd.to_numeric(data['Helpful'])

review_data_rated = pd.read_csv("/home/user/Desktop/Data/voted.csv", sep="\t")
data = review_data_rated
data['Day'] = pd.to_datetime(data['Day'])
data['Out_of'] = pd.to_numeric(data['Out_of'])
data['Helpful'] = pd.to_numeric(data['Helpful'])
data['Ratio'] = pd.to_numeric(data['Ratio'])

helpful_total = review_data_total['Helpful']
helpful_rated = review_data_rated['Helpful']
ratio = review_data_rated['Ratio']


def apply_helpful_ness(helpfulness):
    if helpfulness == 0:
        return str(0)
    elif helpfulness == 1:
        return str(1)
    elif helpfulness == 2:
        return str(2)
    elif helpfulness == 3:
        return str(3)
    elif helpfulness >= 4 and helpfulness <= 5:
        return "4-5"
    elif helpfulness >= 6 and helpfulness <= 10:
        return "6-10"
    # elif helpfulness >=6 and helpfulness <=7:
    #     return "6-10"
    # elif helpfulness >=8 and helpfulness <=10:
    #     return "8-10"
    elif helpfulness >= 11 and helpfulness <= 20:
        return "11-20"
    else:
        return "larger than 20"


review_data_total['Class'] = review_data_total['Helpful'].apply(apply_helpful_ness)


def pie_chart_with_legend(data, column, outpath, title=" ", sort_legend=True):
    result_count = data.groupby(column).size()
    index = result_count.index
    value = result_count

    colors = ['yellowgreen', 'red', 'gold', 'lightskyblue', 'white', 'lightcoral', 'blue', 'pink', 'darkgreen',
              'yellow', 'grey', 'violet', 'magenta', 'cyan']
    percent = 100. * value / value.sum()
    labels = ['{0} - {1:1.2f} %'.format(i, j) for i, j in zip(index, percent)]
    patches, texts = plt.pie(value, colors=colors, startangle=90, radius=1.2)

    if sort_legend:
        patches, labels, dummy = zip(*sorted(zip(patches, labels, value),
                                             key=lambda index: index[2],
                                             reverse=True))

    plt.legend(patches, labels, loc='left center', bbox_to_anchor=(-0.1, 1.),
               fontsize=8, title=title)

    plt.savefig(outpath, bbox_inches='tight')


def bargram(data, column, xlab, ylab, outpath, sort=False):
    import matplotlib.pyplot as plt
    plt.rcdefaults()
    import matplotlib.pyplot as plt
    from matplotlib.ticker import FuncFormatter

    formatter = FuncFormatter(lambda y, pos: "%d%%" % (y))
    # ax.yaxis.set_major_formatter(formatter)
    result_count = data.groupby(column).size()
    if sort:
        result_count.sort_values(inplace=True, ascending=False)
    index = result_count.index
    value = 100.0 * result_count / result_count.sum()
    # value_tag = value.apply(lambda x: str(x)+"""%""")
    # value_tag = value
    plt.rcdefaults()
    fig, ax = plt.subplots()

    y_pos = np.arange(result_count.shape[0])

    ax.barh(y_pos, value, align='center')
    ax.set_yticks(y_pos)
    ax.set_yticklabels(index)
    ax.invert_yaxis()  # labels read top-to-bottom
    ax.set_xlabel(xlab)
    ax.set_ylabel(ylab)
    # ax.set_xticklabels(value_tag)
    ax.xaxis.set_major_formatter(formatter)
    plt.savefig(outpath, bbox_inches='tight')


bargram(review_data_total, 'Class', "Percentage", "Number of Votes as Helpful", "/home/user/Desktop/helpful.png",
        sort=True)


def get_percentage(percent):
    if percent == 0:
        return "0%"
    if percent > 0 and percent < 0.1:
        return "(0%, 10%)"
    elif percent >= 0.1 and percent < 0.2:
        return "[10%,20%)"
    elif percent >= 0.2 and percent < 0.3:
        return "[20%, 30%)"
    elif percent >= 0.3 and percent < 0.4:
        return "[30%, 40%)"
    elif percent >= 0.4 and percent < 0.5:
        return "[40%, 50%)"
    elif percent >= 0.5 and percent < 0.6:
        return "[50%, 60%)"
    elif percent >= 0.6 and percent < 0.7:
        return "[60%, 70%)"
    elif percent >= 0.7 and percent < 0.8:
        return "[70%, 80%)"
    elif percent >= 0.8 and percent < 0.9:
        return "[80%, 90%)"
    elif percent >= 0.9 and percent < 1.0:
        return "[90%, 100%)"
    elif percent == 1.0:
        return "100%"


review_data_rated['Class'] = review_data_rated['Ratio'].apply(get_percentage)

bargram(review_data_rated, 'Class', "Percentage", "Percentage of Votes as Helpful", "/home/user/Desktop/ratio.png",
        sort=True)
##################################Parsing Subjectivity##############################################################

review_data_release = pd.read_csv("/home/user/Desktop/Data/review_with_release_date.csv", sep="\t", encoding='utf-8')
data = review_data_release
# data = review_data_release
data['Day'] = pd.to_datetime(data['Day'])
data['Out_of'] = pd.to_numeric(data['Out_of'])
data['Helpful'] = pd.to_numeric(data['Helpful'])
data['Subjective'] = data['Review_Texts'].apply(get_subjectivity)

data.to_csv("/home/user/Desktop/Data/subjectivity.csv", sep="\t", encoding="utf-8", index=False)
##################################Final Data Set####################################################################
review_data_subject = pd.read_csv("/home/user/Desktop/Data/subjectivity.csv", sep="\t", encoding = 'utf-8')
data = review_data_subject
data['Day'] = pd.to_datetime(data['Day'])
data['Out_of'] = pd.to_numeric(data['Out_of'])
data['Helpful'] = pd.to_numeric(data['Helpful'])
data['Subjective'] = pd.to_numeric(data['Subjective'])