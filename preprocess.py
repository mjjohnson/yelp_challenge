#!/usr/bin/env python
from argparse import ArgumentParser
from bisect import bisect_right
import csv
import json
import math
import os
import sys

import numpy as np


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


def save_tab(filename, data, keys):
    """
    Save data as a tab-delimited file.
    """
    with open(filename, 'w') as f:
        for item in data:
            vals = []
            for key in keys:
                vals.append(unicode(item[key]))
            vals = '\t'.join(vals)
            f.write(vals)
            f.write('\n')


def calc_popularity(filtered):
    """
    Calculate a single popularity label for each item in the dataset.
    """
    for item in filtered:
        popularity = item['stars']
        if item['review_count']:
            popularity += math.log(item['review_count'])
        if item['checkins']:
            popularity += math.log(item['checkins'])

        for key in ('review_count', 'stars', 'checkins'):
            item.pop(key)
        item['popularity'] = popularity
    return filtered


def disc_attribute(filtered, attr, num_bins=3):
    """
    Discretize a given attribute into the given number of bins, by percentiles.
    """
    bin_edges = []
    values = [x[attr] for x in filtered]
    for i in range(1, num_bins):
        edge = np.percentile(values, float(i)/num_bins * 100)
        bin_edges.append(edge)

    for item in filtered:
        value = item[attr]
        value = bisect_right(bin_edges, value)
        item[attr] = value

    return filtered


def save_svmlight(filename, data, save_keys):
    """
    Save data in svmlight / libsvm format.
    """
    # Create the mapping between category names and their IDs first
    id_map = {}
    counter = 1
    for key in sorted(save_keys):
        if key == 'categories':
            continue
        id_map[key] = counter
        counter += 1

    with open(filename, 'w') as f:
        for item in data:
            attrs = [str(item['popularity'])]
            for key, value in item.iteritems():
                if key == 'categories':
                    for cat in value:
                        cat = 'c_' + cat
                        if cat not in id_map:
                            id_map[cat] = counter
                            counter += 1
                        attrs.append('{id}:1'.format(id=id_map[cat]))
                elif key in save_keys:
                    attrs.append('{id}:{v}'.format(id=id_map[key], v=value))
                else:
                    continue
            attrs = [attrs[0]] + sorted(attrs[1:],
                    key=lambda x: int(x.split(':')[0]))
            attrs = ' '.join(attrs)
            f.write(attrs)
            f.write('\n')

    with open('svmlight_mapping.txt', 'w') as f:
        for key, value in id_map.iteritems():
            f.write("{k}\t{v}\n".format(k=key, v=value))


def map_categories(filtered):
    """
    Take filtered data and map the categories to more general categories.
    """
    cats = set()
    with open('filtered_categories.txt', 'r') as f:
        for line in f:
            line = line.strip()
            cats.add(line)

    cat_map = {}
    with open('category_mapping.txt', 'r') as f:
        for line in f:
            line = line.strip()
            parts = line.split('\t')
            if len(parts) > 1:
                key = parts[0]
                vals = parts[1:]
                for val in vals:
                    assert val in cats
                cat_map[key] = vals

    for item in filtered:
        item_cats = item['categories']
        item_cat_mapped = set()
        for cat in item_cats:
            if cat in cat_map:
                item_cat_mapped.update(cat_map[cat])
        item_cats = sorted(item_cat_mapped)
        item['categories'] = item_cats

    return filtered


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


def process_businesses(data_dir, save_keys=None):
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

        # And we only want ones with price range attributes (13% filtered out)
        if not business['attributes'].get('Price Range'):
            continue

        attrs = dict(((a, business[a]) for a in ATTRS))
        attrs['price_range'] = business['attributes']['Price Range']
        attrs['checkins'] = checkins.get(business['business_id'], 0)
        filtered.append(attrs)

    filtered = map_categories(filtered)
    filtered = calc_popularity(filtered)
    filtered = disc_attribute(filtered, attr='popularity')

    if not save_keys:
        save_json('processed.json', filtered)
    else:
        save_svmlight('processed.svmlight', filtered, save_keys)
        #save_tab('processed.tsv', filtered, save_keys)


def calc_arunima_popularity(filtered):
    """
    Calculate a single popularity label for each item in the dataset.
    """
    for item in filtered:
        popularity = item['wtavgstarrating_without_1']
        if item['wtavgtotalreviews']:
            popularity += math.log(item['wtavgtotalreviews'])
        if item['checkins']:
            popularity += math.log(item['checkins'])

        for key in ('wtavgtotalreviews', 'wtavgstarrating_without_1',
                'checkins'):
            item.pop(key)
        item['popularity'] = popularity
    return filtered


def convert_number(text):
    # If only whitespace, or empty, just return.
    if not text.strip():
        return text

    try:
        value = float(text)
    except ValueError:
        return text

    if math.isnan(value):
        return 0
    elif value == int(value):
        return int(value)
    else:
        return value


def load_closed_businesses(data_dir):
    path = os.path.join(data_dir, 'yelp_academic_dataset_business.json')
    data = load_json(path)
    closed = set()
    for business in data:
        if not business['open']:
            closed.add(business['business_id'])
    return closed


def process_arunima(data_dir):
    ATTRS = ['review_count', 'longitude', 'latitude', 'price',
        'wtavgtotalreviews', 'wtavgstarrating_without_1', 'stars', 'cat1',
        'cat2', 'cat3', 'cat4', 'cat5', 'cat6', 'cat7', 'cat8']
    checkins = process_checkins(data_dir)
    closed = load_closed_businesses(data_dir)
    filtered = []

    filename = os.path.join(data_dir, 'Arunima_processed', 'wtagereview_details_without1')
    with open(filename, 'rb') as f:
        reader = csv.DictReader(f, delimiter='\t')
        for row in reader:
            if not row['price']:
                # Skip entries without a price group
                continue

            #if row['business_id'] in closed:
                # Skip the closed businesses
            #    continue

            attrs = dict(((a, convert_number(row[a])) for a in ATTRS))
            attrs['checkins'] = checkins.get(row['business_id'], 0)
            attrs['_latitude'] = attrs['latitude']
            attrs['_longitude'] = attrs['longitude']
            attrs.pop('latitude')
            attrs.pop('longitude')
            filtered.append(attrs)

    filtered = calc_arunima_popularity(filtered)
    filtered = disc_attribute(filtered, attr='popularity')

    save_svmlight('processed.svmlight', filtered, ['price', '_latitude', '_longitude', 'cat1', 'cat2', 'cat3', 'cat4', 'cat5', 'cat6', 'cat7', 'cat8'])


def main(argv):
    parser = ArgumentParser(description="Preprocess Yelp dataset.",
        prog=argv[0])
    parser.add_argument('input_dir', nargs='?',
        help="path to directory containing Yelp JSON files", default="data")
    parser.add_argument('output_file', nargs='?',
        help="path to the processed output file", default="processed.json")
    args = parser.parse_args(argv[1:])
    #process_businesses(args.input_dir, ['price_range', 'categories', 'latitude', 'longitude'])
    process_arunima(args.input_dir)


if __name__ == '__main__':
    main(sys.argv)

