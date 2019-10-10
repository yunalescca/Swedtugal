# -*- coding: utf-8 -*-

# In[7]:
import pandas as pd
import csv
import numpy as np
import urllib.request as urllib2
import json
import codecs
import os
path = "/Users/josefinulfenborg/Downloads/wifiSamples.csv"
save_path = "/Users/josefinulfenborg/Desktop/wifi_processed.csv"
whitelist_manufacturer = ['Cisco Systems, Inc']
whitelist_ssid = ['eduroam', 'TUvisitor', 'tudelft-dastud', 'POAC2019']
url = "http://macvendors.co/api/"

# In[]:
# Only add valid rows to new CSV file (e.g. filter out hotspots)
data = pd.read_csv(path)
df = pd.DataFrame(data)

cells = []
samples = []
ssids = []
macs = []
rss = []

for index, row in df.iterrows():
    mac = (row['MAC']).upper()
    request = urllib2.Request(url + mac, headers = {'User-Agent' : "API Browser"})
    response = urllib2.urlopen(request)
    reader = codecs.getreader("utf-8")
    obj = json.load(reader(response))

    if 'company' in obj['result']:
        manufacturer = obj['result']['company']
        if manufacturer in whitelist_manufacturer or row['SSID'] in whitelist_ssid:
            cells.append(row['Cell'])
            samples.append(row['Sample'])
            ssids.append(row['SSID'])
            macs.append(row['MAC'])
            rss.append(row['RSS'])

# In[]:
# Save data to csv
processed_data = pd.DataFrame(
    {
        'Cell': cells,
        'Sample': samples,
        'SSID': ssids,
        'MAC': macs,
        'RSS': rss
    })
processed_data.to_csv(path_or_buf=save_path, index=False)

# In[]:
# Get histogram and save pmfs
data = pd.read_csv(save_path)
df = pd.DataFrame(data)
rss_header = list(range(-110,-29))

for mac in df['MAC'].unique():
    p = '/Users/josefinulfenborg/Desktop/pmfs/mac_' + (str(mac)).replace(':', '_') + '.csv'
    pmf_data = pd.DataFrame(rss_header).T
    pmf_data.to_csv(p, index=False, header=False)

    for cell in df['Cell'].unique():
        macs = []
        if not df.empty:
            rows = df.loc[(data['Cell'] == cell) & (data['MAC'] == mac)]
            histogram = np.zeros(81)
            for index, row in rows.iterrows():
                histogram[row['RSS']+110] += 1
        s = sum(histogram) or 1.0
        histogram = list(map(lambda x : x/s, histogram))
        #plot_histogram(histogram, str(mac), str(cell))
        pmf = get_pmf(histogram, str(mac), str(cell))
        pmf_data = pd.DataFrame(pmf).T
        pmf_data.to_csv(p, mode='a', index=False, header=False)

# In[]:
# plot histogram, plot and get pmf
from scipy.ndimage import gaussian_filter
import matplotlib.pyplot as plt

def plot_histogram(histogram, mac, cell):
    rss_range = range(-110,-29)
    plt.bar(list(rss_range), histogram)
    plt.xlabel('RSS values')
    plt.ylabel('Frequency')
    plt.title('Histogram for MAC: ' + mac + ', cell: ' + cell)
    plt.show()

def get_pmf(histogram, mac, cell):
    zeros = histogram.count(0)
    if zeros == 81:
        increase = 0.01/81
        decrease = 0
    else:
        non_zeros = len(histogram) - zeros
        increase = 0.01/zeros
        decrease = 0.01/non_zeros
    histogram = [x + increase if x == 0 else x - decrease for x in histogram]
    pmf = gaussian_filter(histogram, sigma=1)
    #plot_pmf(pmf, mac, cell)
    return np.ndarray.tolist(pmf)

def plot_pmf(pmf, mac, cell):
    rss_range = range(-110,-29)
    plt.bar(list(rss_range), pmf)
    plt.xlabel('RSS values')
    plt.ylabel('Probability')
    plt.title('PMF for MAC: ' + mac + ', cell: ' + cell)
    plt.show()


#%%
