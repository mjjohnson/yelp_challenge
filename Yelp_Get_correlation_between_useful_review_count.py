import json
import requests
import nltk
data = []
print "Useful\tReview_count"
with open('yelp_academic_dataset_user.json') as f:
	for line in f:
		dat = json.loads(line)
		try:
			useful = dat["votes"]["useful"]
			review_count = dat["review_count"]
			print str(useful)+"\t"+str(review_count)
		except ValueError:
				continue
		
	
	

