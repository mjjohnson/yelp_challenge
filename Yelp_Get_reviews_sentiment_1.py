import json
import requests
import nltk
data = []
print "review_id\tbusiness_id\tadj_only_label\tall_text_label\tstars"
with open('yelp_academic_dataset_review.json') as f:
	for line in f:
		dat = json.loads(line)
		try:
			text = nltk.word_tokenize(str(dat["text"]))
			taggedtext = nltk.pos_tag(text)
			adjectiveonly = " ".join(tup[0] for tup in taggedtext if tup[1]=='JJ')
		
			#print adjectiveonly
			requesttext = {'text' : adjectiveonly}
			requesttextwhole = {'text' : str(dat["text"]) }
			r = requests.post("http://text-processing.com/api/sentiment/",data=requesttext)
			r2 = requests.post("http://text-processing.com/api/sentiment/",data=requesttextwhole)
			print str(dat["review_id"]+"\t"+dat["business_id"])+"\t"+r.json()["label"]+"\t"+r2.json()["label"]+"\t"+str(dat["stars"])
		except ValueError:
				continue
		
	
	

