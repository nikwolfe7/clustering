label = csvread('labels-spectral-results-aggregation-sig0.47.csv');
data = csvread('spectral-results-aggregation-sig0.47.csv');
visualize('results_spectral_aggregation.png',data,label);

label = csvread('labels-spectral-results-bridge-sig0.42.csv');
data = csvread('spectral-results-bridge-sig0.42.csv');
visualize('results_spectral_bridge.png',data,label);

label = csvread('labels-spectral-results-compound-sig0.152.csv');
data = csvread('spectral-results-compound-sig0.152.csv');
visualize('results_spectral_compound.png',data,label);

label = csvread('labels-spectral-results-flame-sig0.73.csv');
data = csvread('spectral-results-flame-sig0.73.csv');
visualize('results_spectral_flame.png',data,label);

label = csvread('labels-spectral-results-jain-sig0.302.csv');
data = csvread('spectral-results-jain-sig0.302.csv');
visualize('results_spectral_jain.png',data,label);

label = csvread('labels-spectral-results-spiral-sig0.2.csv');
data = csvread('spectral-results-spiral-sig0.2.csv');
visualize('results_spectral_spiral.png',data,label);

label = csvread('labels-spectral-results-two-diamonds-sig22.csv');
data = csvread('spectral-results-two-diamonds-sig22.csv');
visualize('results_spectral_two_diamonds.png',data,label);

label = csvread('labels-kmeans-results-aggregation.csv');
data = csvread('kmeans-results-aggregation.csv');
visualize('results_kmeans_aggregation.png',data,label);

label = csvread('labels-kmeans-results-bridge.csv');
data = csvread('kmeans-results-bridge.csv');
visualize('results_kmeans_bridge.png',data,label);

label = csvread('labels-kmeans-results-compound.csv');
data = csvread('kmeans-results-compound.csv');
visualize('results_kmeans_compound.png',data,label);

label = csvread('labels-kmeans-results-flame.csv');
data = csvread('kmeans-results-flame.csv');
visualize('results_kmeans_flame.png',data,label);

label = csvread('labels-kmeans-results-jain.csv');
data = csvread('kmeans-results-jain.csv');
visualize('results_kmeans_jain.png',data,label);

label = csvread('labels-kmeans-results-spiral.csv');
data = csvread('kmeans-results-spiral.csv');
visualize('results_kmeans_spiral.png',data,label);

label = csvread('labels-kmeans-results-two-diamonds.csv');
data = csvread('kmeans-results-two-diamonds.csv');
visualize('results_kmeans_two_diamonds.png',data,label);