import json
import requests
import nltk
data = []
with open('yelp_academic_dataset_review.json') as f:
	for line in f:
		dat = json.loads(line)
		try:
			text = nltk.word_tokenize(str(dat["text"]))
			taggedtext = nltk.pos_tag(text)
			adjectiveonly = " ".join(tup[0] for tup in taggedtext if tup[1]=='JJ')
			print adjectiveonly
		except ValueError:
				continue
		
	
	

