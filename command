import transformer.SpellCorrector

val inpath = "file:///home/user/Desktop/Data/english.txt"
val text = sc.textFile(inpath)
val spellcorrector = new SpellCorrector()
val cleaned_text = text.map(line => spellcorrector.correct_line(line, 6))
