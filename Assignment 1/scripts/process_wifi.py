# -*- coding: utf-8 -*-

# In[7]:
import pandas as pd
import csv
import numpy as np
path = "/Users/josefinulfenborg/Downloads/wifiSamples(4).csv"

# In[]:
data = pd.read_csv(path)
max_sample = data['Sample'].max()

macs = list(data['MAC'].unique())

max_cell = 4
all_cell_macs = []
for cell in range(1, max_cell + 1): 
    cell_name = 'c' + str(cell)
    for sample in range(1, max_sample + 1):
        rows = data.loc[(data['Cell'] == cell_name) & (data['Sample'] == sample)]
        cell_macs = rows['MAC'].unique()
        cell_macs_binary = [cell_name + '-' + str(sample)] + [1 if mac in cell_macs else 0 for mac in macs]
        all_cell_macs.append(cell_macs_binary)

macs = np.insert(macs, 0, [''], axis=0) # Top left cell should be empty
processed_data = pd.DataFrame(all_cell_macs, columns = macs)
save_path='/Users/josefinulfenborg/Downloads/wifi_processed(4).csv'
processed_data.to_csv(path_or_buf=save_path, index=False)


#%%
