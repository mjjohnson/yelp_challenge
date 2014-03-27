#!/usr/bin/env python
from argparse import ArgumentParser
import json
import os
import sys


def load_json(filename):
    """
    Load JSON from a file.
    """
    data = []
    with open(filename, 'r') as f:
        for line in f:
            line_data = json.loads(line)
            data.append(line_data)
    return data


def save_json(filename, data):
    """
    Save JSON to a file.
    """
    with open(filename, 'w') as f:
        for item in data:
            json.dump(item, f, separators=(',',':'))
            f.write("\n")


def process_checkins(data_dir):
    """
    Get the total number of checkins for each business and return a dict.
    """
    businesses = {}
    path = os.path.join(data_dir, 'yelp_academic_dataset_checkin.json')
    data = load_json(path)

    for business in data:
        total_tips = sum(business['checkin_info'].values())
        businesses[business['business_id']] = total_tips

    return businesses


def process_businesses(data_dir):
    """
    Load business data and save a filtered feature set for relevant businesses.
    """
    # The attributes we want to directly copy from the input feature set.
    ATTRS = ['review_count', 'name', 'business_id', 'stars', 'latitude',
        'longitude', 'categories']

    checkins = process_checkins(data_dir)

    path = os.path.join(data_dir, 'yelp_academic_dataset_business.json')
    data = load_json(path)
    filtered = []
    for business in data:
        # We only care about open businesses
        if not business['open']:
            continue

        # We only want restaurants
        if not 'Restaurants' in business['categories']:
            continue

        if business['type'] != 'business':
            print business['type']

        attrs = dict(((a, business[a]) for a in ATTRS))
        attrs['price_range'] = business['attributes'].get('Price Range', -1)
        attrs['checkins'] = checkins.get(business['business_id'], 0)
        filtered.append(attrs)
    save_json('processed.json', filtered)
    

def main(argv):
    parser = ArgumentParser(description="Preprocess Yelp dataset.",
        prog=argv[0])
    parser.add_argument('input_dir', nargs='?',
        help="path to directory containing Yelp JSON files", default="data")
    parser.add_argument('output_file', nargs='?',
        help="path to the processed output file", default="processed.json")
    args = parser.parse_args(argv[1:])
    process_businesses(args.input_dir)


if __name__ == '__main__':
    main(sys.argv)

