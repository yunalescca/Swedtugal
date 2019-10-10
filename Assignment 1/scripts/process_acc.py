# coding: utf-8
# In[7]:
import pandas as pd
if __name__ == "__main__":
    data = pd.read_csv("/Users/josefinulfenborg/Downloads/accelerometer(2).csv")

    samples = []
    labels = []
    windows = []
    diffs = []

    for sample in range(1,13):
        for window in range(20):
            rows = data.loc[(data['Window'] == window) & (data['Sample'] == sample)]
            max_mag = rows['Magnitude'].max()
            min_mag = rows['Magnitude'].min()
            difference = max_mag - min_mag
            samples.append(sample)
            labels.append(rows['Label'].values[0])
            windows.append(window)
            diffs.append(difference)

    processed_data = pd.DataFrame(
        {
            'Sample': samples,
            'Label': labels,
            'Window': windows,
            'Difference': diffs
        })
    
    path='/Users/josefinulfenborg/Downloads/acc_processed(2).csv'
    processed_data.to_csv(path_or_buf=path, index=False)

#%%
